package io.github.rosemoe.editor.core.analyzer;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.analyzer.result.AnalyzerResult;
import io.github.rosemoe.editor.core.analyzer.result.CodeAnalyzerResult;
import io.github.rosemoe.editor.core.analyzer.result.instances.CodeAnalyzerResultColor;
import io.github.rosemoe.editor.core.content.controller.ContentGrid;
import io.github.rosemoe.editor.core.grid.Cell;
import io.github.rosemoe.editor.core.grid.Grid;
import io.github.rosemoe.editor.core.grid.instances.SpanCell;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * A Result store is used to manage stack of results.
 * Theses results are then passed to the analyzer.
 * The ResultStore must must thread safe, many analyzer on different thread could request it.
 */
public class ResultStore extends ConcurrentSkipListMap<String, ConcurrentLinkedQueue<AnalyzerResult>> implements Iterable<ConcurrentLinkedQueue<AnalyzerResult>>{

    // Content result object
    public ContentGrid mText;

    public ResultStore() {
        put("color", new ConcurrentLinkedQueue<>());
        get("color").offer(new CodeAnalyzerResultColor());
        get("color").offer(new CodeAnalyzerResultColor());
        put("content", new ConcurrentLinkedQueue<>());
    }


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

    /**
     * Get the result listener, in mean in build results
     *
     * @param name
     * @return
     */
    public AnalyzerResult getResultInBuild(String name) {
        ConcurrentLinkedQueue<AnalyzerResult> result = get(name);
        if ( true ) { throw new RuntimeException("Not yet implemented"); }
        return null;
    }

    /**
     * caller is responsible from removing the lock.
     * @param name
     * @return
     */
    public AnalyzerResult getResult(String name) {
        ConcurrentLinkedQueue<AnalyzerResult> result = get(name);
        return result.peek();
    }

    /**
     * This call will put inProcessResults to results and create an
     * empty inProcessResults.
     */
    public void updateView() {
        for(ConcurrentLinkedQueue<AnalyzerResult> clq : this) {
            if ( clq.size() > 1 ) {
                AnalyzerResult trashCan = clq.poll();
                trashCan.clear();
                clq.offer(trashCan);
            } else {
                Logger.debug("WARNING: clq.size() = " + clq.size() + " so not able to refresh the view.");
            }
        }
    }

    /// HACKISH easiers, they mut be removed
    public CodeAnalyzerResultColor getSpanMap() {
        return (CodeAnalyzerResultColor) getResult("color");
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
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Iterator<ConcurrentLinkedQueue<AnalyzerResult>> iterator() {
        return values().iterator();
    }
}
