package io.github.rosemoe.editor.core.analyze;

import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.analyze.analyzer.Analyzer;
import io.github.rosemoe.editor.core.analyze.analyzer.CodeAnalyzer;
import io.github.rosemoe.editor.core.analyze.analyzer.content.ContentActionStackAnalyzer;
import io.github.rosemoe.editor.core.analyze.analyzer.content.ContentAnalyzer;
import io.github.rosemoe.editor.core.analyze.signal.Router;
import io.github.rosemoe.editor.core.analyze.signal.Routes;

/**
 * A pipeline is a collection of analyzer.
 */
public class Pipeline extends ConcurrentSkipListMap<Integer, Analyzer> implements Router {

    public final ResultStore resultStore;

    public final static int ANALYZER_CONTENT_STACK = 0;
    public final static int ANALYZER_CONTENT = 1;
    public final static int ANALYZER_LANG = 2;

    @Override
    public boolean route(Routes action, Object... args) {
        return flow1(action, args);
    }

    public Pipeline(ResultStore resultStore) {
        this.resultStore = resultStore;
        put(ANALYZER_CONTENT,new ContentAnalyzer(resultStore));
        put(ANALYZER_CONTENT_STACK, new ContentActionStackAnalyzer(resultStore));
    }

    public void stopAllFlow() {
        /**
         *         if (analyzer != null) {
         *             analyzer.setCallback(null);
         *             analyzer.shutdown();
         *         }
         */
        new RuntimeException("Not implemented");
    }

    /**
     * An action in the workflow.
     * @param action
     * @param args
     * @return
     */
    public boolean action1(Routes action, Object ...args) {
        switch (action) {
            case ACTION_CONTENT_ACTION_STACK: {
                Routes nextAction = (Routes) args[0];
                Router r = (Router) get(ANALYZER_CONTENT_STACK);
                if ( r.route(nextAction) ) return true;
                break;
            }
        }
        return false;
    }
    /**
     * This flow just transform content in response to user actions on the stack.
     * e.g. context menu, shortcuts
     * @param action
     * @param args
     * @return
     */
    private boolean flow1(Routes action, Object ...args) {
        if ( action1(action, args) ) {
            return true;
        }
        return false;
    }
    /**
     * This should be the final flow for code analysis.
     * @param action
     * @param args
     * @return
     */
    private boolean flow2(Routes action, Object ...args) {
        if ( action1(action, args) ) {
            return true;
        }
        /*CodeAnalyzer ca = getLanguageAnalyzer();
        ca.route(action,args);*/
        /**
         * for(CodeAnlyser ca : analyszer) {
         *      ca.route(action,args);
         * }
         */
        return false;
    }

    // DSL
    public CodeAnalyzer getLanguageAnalyzer() { return (CodeAnalyzer) get(ANALYZER_LANG); }
    public void setLanguageAnalyzer(CodeAnalyzer a) { put(ANALYZER_LANG,a); }
}
