/*
 *   Copyright 2020 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.text;

import android.util.Log;

import io.github.rosemoe.editor.interfaces.CodeAnalyzer;
import io.github.rosemoe.editor.struct.BlockLine;
import io.github.rosemoe.editor.struct.Span;

import java.util.List;

/**
 * This is a manager of analyzing text
 *
 * @author Rose
 */
public class TextAnalyzer {

    private static int sThreadId = 0;

    private synchronized static int nextThreadId() {
        sThreadId++;
        return sThreadId;
    }

    /**
     * Debug:Start time
     */
    public long mOpStartTime;
    private TextAnalyzeResult mResult;
    private Callback mCallback;
    private AnalyzeThread mThread;
    private CodeAnalyzer mCodeAnalyzer;
    private final Object mLock = new Object();

    /**
     * Create a new manager for the given codeAnalyzer
     *
     * @param codeAnalyzer0 Target codeAnalyzer
     */
    public TextAnalyzer(CodeAnalyzer codeAnalyzer0) {
        if (codeAnalyzer0 == null) {
            throw new IllegalArgumentException();
        }
        mResult = new TextAnalyzeResult();
        mResult.addNormalIfNull();
        mCodeAnalyzer = codeAnalyzer0;
    }

    /**
     * Set callback of analysis
     *
     * @param cb New callback
     */
    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    public void shutdown() {
        final AnalyzeThread thread = mThread;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            mThread = null;
        }
    }

    /**
     * Analyze the given text
     *
     * @param origin The source text
     */
    public synchronized void analyze(Content origin) {
        AnalyzeThread thread = this.mThread;
        if (thread == null || !thread.isAlive()) {
            Log.d("TextAnalyzer", "Starting a new thread for analyzing");
            thread = this.mThread = new AnalyzeThread(mLock, mCodeAnalyzer, origin);
            thread.setName("TextAnalyzeDaemon-" + nextThreadId());
            thread.setDaemon(true);
            thread.start();
        } else {
            thread.restartWith(origin);
            synchronized (mLock) {
                mLock.notify();
            }
        }
    }

    /**
     * Get analysis result
     *
     * @return Result of analysis
     */
    public TextAnalyzeResult getResult() {
        return mResult;
    }

    public interface Callback {

        void onAnalyzeDone(TextAnalyzer provider);

    }

    /**
     * AnalyzeThread to control
     */
    public class AnalyzeThread extends Thread {

        private volatile boolean waiting = false;
        private Content content;
        private final CodeAnalyzer codeAnalyzer;
        private final Object lock;

        /**
         * Create a new thread
         *
         * @param a       The CodeAnalyzer to call
         * @param content The Content to analyze
         */
        public AnalyzeThread(Object lock, CodeAnalyzer a, Content content) {
            this.lock = lock;
            codeAnalyzer = a;
            this.content = content;
        }

        @Override
        public void run() {
            try {
                do {
                    TextAnalyzeResult colors = new TextAnalyzeResult();
                    Delegate d = new Delegate();
                    mOpStartTime = System.currentTimeMillis();
                    do {
                        waiting = false;
                        StringBuilder c = content.toStringBuilder();
                        codeAnalyzer.analyze(c, colors, d);
                        if (waiting) {
                            colors.mSpanMap.clear();
                            colors.mLast = null;
                            colors.mBlocks.clear();
                            colors.mSuppressSwitch = Integer.MAX_VALUE;
                            colors.mLabels = null;
                            colors.mExtra = null;
                        }
                    } while (waiting);

                    List<BlockLine> blockLines = mResult.mBlocks;
                    List<List<Span>> spanMap = mResult.mSpanMap;
                    mResult = colors;
                    colors.addNormalIfNull();
                    try {
                        if (mCallback != null)
                            mCallback.onAnalyzeDone(TextAnalyzer.this);
                        ObjectAllocator.recycleBlockLine(blockLines);
                        SpanRecycler.getInstance().recycle(spanMap);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    try {
                        synchronized (lock) {
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        Log.d("AnalyzeThread", "Analyze daemon is being interrupted -> Exit");
                        break;
                    }
                } while (true);
            } catch (Exception ex) {
                Log.i("AnalyzeThread", "Analyze daemon got exception -> Exit", ex);
            }
        }

        /**
         * New content has been sent
         * Notify us to restart
         *
         * @param content New source
         */
        public synchronized void restartWith(Content content) {
            waiting = true;
            this.content = content;
        }

        /**
         * A delegate for token stream loop
         * To make it stop in time
         */
        public class Delegate {

            /**
             * Whether new input is set
             * If it returns true,you should stop your tokenizing at once
             *
             * @return Whether re-analyze required
             */
            public boolean shouldAnalyze() {
                return !waiting;
            }

        }

    }
}

