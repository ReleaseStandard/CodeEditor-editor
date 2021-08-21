package io.github.rosemoe.editor.core.analyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.analyzer.analyzer.CodeAnalyzer;
import io.github.rosemoe.editor.core.content.controller.ContentGrid;

/**
 * A pipeline is a collection of analyzer,
 * and dependencies between them.
 */
public class Pipeline extends ConcurrentSkipListMap<Integer, Analyzer> {

    public final ResultStore resultStore;

    public final static int ANALYZER_CONTENT = 1;
    public final static int ANALYZER_LANG = 2;

    /**
     * A flow is a run order between different Analyzers.
     * In order to process user input.
     */
    class Flow extends ArrayList<Integer> { }
    final Flow classicUserInput = new Flow();

    public Pipeline(ResultStore resultStore) {
        this.resultStore = resultStore;
        classicUserInput.add(ANALYZER_CONTENT);
        classicUserInput.add(ANALYZER_LANG);
    }
    /**
     * Run flow on the Pipeline.
     * @param f flow schedule to run.
     */
    public void run(Flow f) {
        for(Integer i : f) {
            Analyzer a = get(i);
            //a.runAnalysis
        }
    }
    public void run() {
        run(classicUserInput);
    }
    public void runHack(ContentGrid content) {
        //ContentAnalyzer content = get(ANALYZER_CONTENT);
        // content.run();
        CodeAnalyzer lang = (CodeAnalyzer) get(ANALYZER_LANG);
        lang.analyze(resultStore.mText);
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

    // DSL
    public CodeAnalyzer getLanguageAnalyzer() { return (CodeAnalyzer) get(ANALYZER_LANG); }
    public void setLanguageAnalyzer(CodeAnalyzer a) { put(ANALYZER_LANG,a); }
}
