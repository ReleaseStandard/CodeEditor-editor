package io.github.rosemoe.editor.core.analyzer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.rosemoe.editor.core.BlockLineModel;
import io.github.rosemoe.editor.core.analyzer.result.CodeAnalyzerResult;
import io.github.rosemoe.editor.core.analyzer.result.instances.CodeAnalyzerResultColor;
import io.github.rosemoe.editor.core.analyzer.result.instances.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.content.controller.ContentGrid;
import io.github.rosemoe.editor.core.grid.Grid;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * A Result store is used to manage stack of results.
 * Theses results are then passed to the analyzer.
 * The ResultStore must must thread safe, many analyzer on different thread could request it.
 */
public class ResultStore {

    // Content result object
    public ContentGrid mText;
    
    // Below color result object
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
    /// HACKISH easiers, they mut be removed
    public Grid getSpanMap() {
        CodeAnalyzerResultColor color = (CodeAnalyzerResultColor)getResult("color");
        return color == null ? null : color.map;
    }

    public List<BlockLineModel> getContent() {
        CodeAnalyzerResultContent content = (CodeAnalyzerResultContent)getResult("content");
        return content == null ? null : content.mBlocks;
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
}
