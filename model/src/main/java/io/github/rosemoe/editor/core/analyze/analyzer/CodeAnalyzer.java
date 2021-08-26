/*
 *   Copyright 2020-2021 Rosemoe
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
package io.github.rosemoe.editor.core.analyze.analyzer;

import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.util.CEObject;
import io.github.rosemoe.editor.core.analyze.ResultStore;
import io.github.rosemoe.editor.core.analyze.result.AnalysisDoneCallback;
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.analyze.signal.Routes;
import io.github.rosemoe.editor.core.util.CallStack;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * This could be :
 *  a color analysis (display color (spans at screen)),
 *  a content analysis (display text at screen).
 *  a spellcheck result (display misspelled words at screen).
 *
 * @author Release Standard
 */
public abstract class CodeAnalyzer extends Analyzer {

    @Override
    public boolean route(Routes action, Object... args) {
        throw new RuntimeException("You do not have overrided this method so nothing will happen at runtime !");
    }

    /**
     * This switch is setup by the analyzer to prevent the view from painting non displayed regions.
     * Set suppress switch for editor
     * What is 'suppress switch' ?:
     * Suppress switch is a switch size for code block line drawing
     * and for the process to find out which code block the cursor is in.
     * Because the code blocks are not saved by the order of both start line and
     * end line,we are unable to know exactly when we should stop the process.
     * So without a suppress switch,it will cost a large of time to search code
     * blocks.So I added this switch.
     * A suppress switch is the code block count in the first layer code block
     * (as well as its sub code blocks).
     * If you are unsure,do not set it.
     * The default value if Integer.MAX_VALUE
     *
     * @param suppressSwitch Suppress switch
     */
    public int mSuppressSwitch = Integer.MAX_VALUE;


    public CodeAnalyzer(ResultStore resultStore) {
        super(resultStore);
    }
    /**
     * Method responsible from building results in inProcessResults HashMap.
     * Each implementation of CodeAnalyzer is free to provide as much CodeAnalyzerResult as it want or none.
     * eg : CodeAnalyzerResultColor, CodeAnalyzerResultSpellCheck, CodeAnalyzerResultContent.
     *      CodeAnalyzerResultUnderlineAbcWords, CodeAnalyzerResultSyntaxeChecking.
     *
     * @param content ContentCell to analyze
     * @param delegate
     */
    protected abstract void analyze(CharSequence content, CodeAnalyzerThread.Delegate delegate);

    @Override
    public void analyze() {
        analyze(resultStore.mText);
    }

    /**
     * Launch a clear for all result listener inside this analyzer.
     */
    public void clear() {
        mSuppressSwitch = Integer.MAX_VALUE;
    }

    private void unlock(ReentrantLock lock) {
        if ( lock != null &&
                lock.isHeldByCurrentThread() &&
                lock.isLocked() ) {
            lock.unlock();
        } else {
            Logger.debug("Warning somethin strange has happened on unlocking");
            CallStack.printStackTrace();
        }
    }
    private void lock(ReentrantLock lock) {
        lock.lock();
    }

    /**
     * Start an analysis thread of a given text/code.
     */
    public CodeAnalyzerThread mThread = null;
    public synchronized void analyze(CodeAnalyzerResultContent origin) {
        CodeAnalyzerThread thread = this.mThread;
        if (thread == null || !thread.isAlive()) {
            thread = this.mThread = CodeAnalyzerThread.newInstance(origin, this);
        } else {
            thread.restartWith(origin);
            synchronized (thread.lock) {
                thread.lock.notify();
            }
        }
    }

    /**
     * Stopping analysis thread.
     */
    public void shutdown() {
        final CodeAnalyzerThread thread = mThread;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            mThread = null;
        }
    }

    /**
     * Is analyzer's thread running ?
     * @return
     */
    public boolean isRunning() {
        return mThread != null;
    }

    /**
     * Dump analyzer content.
     */
    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        CEObject.dump(this, offset);
    }

    /**
     * Set callback of analysis
     *
     * @param cb New callback
     */
    public void setCallback(AnalysisDoneCallback cb) {
        if ( isRunning() ) {
            mThread.mAnalysisDoneCallback = cb;
        }
    }

}

