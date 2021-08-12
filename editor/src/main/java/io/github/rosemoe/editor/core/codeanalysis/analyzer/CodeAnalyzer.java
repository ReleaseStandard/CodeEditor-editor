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
package io.github.rosemoe.editor.core.codeanalysis.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.codeanalysis.results.Callback;
import io.github.rosemoe.editor.core.util.CallStack;
import io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.codeanalysis.CodeAnalyzerResultColor;
import io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans.SpanMapController;
import io.github.rosemoe.editor.core.extension.plugins.widgets.contentAnalyzer.controller.ContentMapController;
import io.github.rosemoe.editor.core.extension.plugins.widgets.contentAnalyzer.codeanalysis.CodeAnalyzerResultContent;


import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.BlockLineModel;

/**
 * This could be :
 *  a color analysis (display color (spans at screen)),
 *  a content analysis (display text at screen).
 *  a spellcheck result (display misspelled words at screen).
 *
 * @author Release Standard
 */
public abstract class CodeAnalyzer {

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


    /**
     * This is the view, all results in this hashmap are already processed results, they will not change
     * until background thread is calling updateView method.
     * results could be : CodeAnalyzerResultColor (display color on the screen), CodeAnalyzerResultContent (display content on the screen).
     *                    CodeAnalyzerResultSpellCheck, CodeAnalyzerResultSyntaxeErrors ...
     */
    public ConcurrentHashMap<String, CodeAnalyzerResult> results = new ConcurrentHashMap<>();
    /**
     * This is in processing results in the analyzer.
     */
    public ConcurrentHashMap<String, CodeAnalyzerResult> inProcessResults = new ConcurrentHashMap<>();

    public CodeAnalyzer() {
    }
    /**
     * Method responsible from building results in inProcessResults HashMap.
     * Each implementation of CodeAnalyzer is free to provide as much CodeAnalyzerResult as it want or none.
     * eg : CodeAnalyzerResultColor, CodeAnalyzerResultSpellCheck, CodeAnalyzerResultContent.
     *      CodeAnalyzerResultUnderlineAbcWords, CodeAnalyzerResultSyntaxeChecking.
     *
     * @param content Content to analyze
     * @param delegate
     */
    protected abstract void analyze(CharSequence content, CodeAnalyzerThread.Delegate delegate);

    /**
     * Dispatch (partial) results of an analysis on result objects, to show the result of processing, use updateView()
     * (No matter what type is expected by results)
     */
    public void dispatchResultPart(Object ...args) {
        for(CodeAnalyzerResult result : inProcessResults.values()) {
            if ( result != null ) {
                result.dispatchResult(args);
            } else {
                Logger.debug("Cannot given results to the object since it is null");
            }
        }
    }

    /**
     * This add a result listener on the code analyzer.
     * @param listener
     */
    public void addResultListener(String name, CodeAnalyzerResult listener) {
        Logger.v("name=",name,",listener=",listener);
        results.put(name,listener);
        inProcessResults.put(name, listener.clone());
    }
    /**
     * Remove any CodeAnalyzerResult from the analysis process.
     */
    public void rmResultsListener() {
        for(String key : results.keySet()) {
            results.remove(key);
            inProcessResults.remove(key);
        }
    }
    /**
     * Remove a given CodeAnalyzerResult from the analysis process.
     * @param name name of the result to remove.
     */
    public void rmResultListener(String name) {
        results.remove(name);
        inProcessResults.remove(name);
    }

    /**
     * Launch a clear for all result listener inside this analyzer.
     */
    public void clear() {
        mSuppressSwitch = Integer.MAX_VALUE;
        clearBuilded();
        clearInBuild();
    }

    /**
     * Clear what have been done in the analyzer (view).
     */
    public void clearBuilded() {
        for(CodeAnalyzerResult result : results.values()) {
            if ( result != null ) {
                result.clear();
            }
        }
    }
    /**
     * Clear what is being done in the analyzer.
     */
    public void clearInBuild() {
        for(CodeAnalyzerResult inProcessResult : inProcessResults.values()) {
            if ( inProcessResult != null ) {
                inProcessResult.clear();
            }
        }
    }

    /**
     * Get the result listener, in mean in build results
     *
     * @param name
     * @return
     */
    public CodeAnalyzerResult getResultInBuild(String name) {
        return inProcessResults.get(name);
    }

    /**
     * caller is responsible from removing the lock.
     * @param name
     * @return
     */
    public CodeAnalyzerResult getResult(String name) {
        return results.get(name);
    }

    /**
     * Recycle the content of all analysis result.
     */

    // TODO : initially was named notify recycle
    public void recycle() {
        Logger.debug("recycle results");
        for(CodeAnalyzerResult result : results.values()) {
            if ( result != null ) {
                result.recycle();
            }
        }
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
     * This call will put inProcessResults to results and create an
     * empty inProcessResults.
     */
    public void updateView() {
        Logger.debug("Update the view");
        for(Map.Entry<String, CodeAnalyzerResult> e : results.entrySet()) {
            CodeAnalyzerResult result = e.getValue();
            String key = e.getKey();
            CodeAnalyzerResult newResult = inProcessResults.get(key);
            results.put(key, newResult);
            if ( result != null ) {
                result.recycler.putToDigest(result);
                result.clear();
            }
            inProcessResults.put(key, result);
        }
    }

    /**
     * Start an analysis thread of a given text/code.
     */
    public CodeAnalyzerThread mThread = null;
    public synchronized void analyze(ContentMapController origin) {
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
        Logger.debug(offset,"CodeAnalyzer:");
        Logger.debug(offset,"  results = " + results.size());
        for(Map.Entry<String, CodeAnalyzerResult> entry : results.entrySet()) {
            Logger.debug(offset, entry.getKey());
            if ( entry.getValue() != null ) {
                entry.getValue().dump(offset);
            } else {
                Logger.debug("entry getValue is null");
            }
        }
        Logger.debug(offset,"  inProcessResults = " + inProcessResults.size());
        for(Map.Entry<String, CodeAnalyzerResult> entry : results.entrySet()) {
            Logger.debug(offset, entry.getKey());
            if ( entry.getValue() != null ) {
                entry.getValue().dump(offset);
            } else {
                Logger.debug("entry getValue is null");
            }
        }
    }

    /**
     * Set callback of analysis
     *
     * @param cb New callback
     */
    public void setCallback(Callback cb) {
        if ( isRunning() ) {
            mThread.mCallback = cb;
        }
    }



    /// HACKISH easiers, they mut be removed
    public SpanMapController getSpanMap() {
        CodeAnalyzerResultColor color = (CodeAnalyzerResultColor)getResult("color");
        return color == null ? null : color.map;
    }

    public List<BlockLineModel> getContent() {
        CodeAnalyzerResultContent content = (CodeAnalyzerResultContent)getResult("content");
        return content == null ? null : content.mBlocks;
    }
}

