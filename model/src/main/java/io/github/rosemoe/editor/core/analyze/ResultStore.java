package io.github.rosemoe.editor.core.analyze;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.analyze.result.AnalyzerResult;
import io.github.rosemoe.editor.core.color.CodeAnalyzerResultColor;
import io.github.rosemoe.editor.core.analyze.result.instances.CodeAnalyzerResultCompletion;
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * A Result store is used to manage stack of results.
 * Theses results are then passed to the analyzer.
 * The ResultStore must thread safe, many analyzer on different thread could request it.
 */
public class ResultStore extends ConcurrentSkipListMap<String, ConcurrentLinkedQueue<AnalyzerResult>> implements Iterable<ConcurrentLinkedQueue<AnalyzerResult>>{

    // Content result object
    public CodeAnalyzerResultContent mText;

    public final static String RES_COLOR = "color";
    public final static String RES_COMPLETION = "completion";
    public final static String RES_CONTENT = "content";
    public final static Integer initialQueueSize = 2;

    public ResultStore() {
        init(initialQueueSize);
    }

    public void init(int queueSz) {
        clear();
        put(RES_COLOR, new ConcurrentLinkedQueue<>());
        put(RES_COMPLETION, new ConcurrentLinkedQueue<>());
        put(RES_CONTENT, new ConcurrentLinkedQueue<>());

        for(int a = 0; a < queueSz; a=a+1) {
            get(RES_COLOR).offer(new CodeAnalyzerResultColor());
            get(RES_COMPLETION).offer(new CodeAnalyzerResultCompletion());
            get(RES_CONTENT).offer(new CodeAnalyzerResultContent());
        }
    }
    /**
     * Get the result listener, in mean in build results
     *
     * @param name
     * @return AnalyzerResult or null if there is no work copy.
     */
    public AnalyzerResult getResultInBuild(String name) {
        ConcurrentLinkedQueue<AnalyzerResult> q = get(name);
        Iterator it = q.iterator();
        if ( ! it.hasNext() ) {
            return null;
        }
        it.next();
        if ( ! it.hasNext() ) {
            return null;
        }
        return (AnalyzerResult) it.next();
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

    public void clearBuilded() {
        for(ConcurrentLinkedQueue<AnalyzerResult> ar : this) {
            ar.poll();
        }
    }

    /**
     * Clear what is being done in the analyzer.
     */
    public void clearInBuild() {
        for(ConcurrentLinkedQueue<AnalyzerResult> ar : this) {
            Iterator<AnalyzerResult> elements = ar.iterator();
            if ( ! elements.hasNext() ) { continue; }
            elements.next();
            while ( elements.hasNext() ) {
                elements.remove();
                elements.next();
            }
        }
    }

    @Override
    public Iterator<ConcurrentLinkedQueue<AnalyzerResult>> iterator() {
        return values().iterator();
    }
}
