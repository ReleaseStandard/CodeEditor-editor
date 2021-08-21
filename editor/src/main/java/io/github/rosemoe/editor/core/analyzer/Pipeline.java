package io.github.rosemoe.editor.core.analyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.analyzer.analyzer.CodeAnalyzer;

/**
 * A pipeline is a collection of analyzer,
 * and dependencies between them.
 */
public class Pipeline extends ConcurrentSkipListMap<Integer, Analyzer> implements Iterable<Analyzer> {

    public final ResultStore resultStore;
    
    public final static int ANALYZER_CONTENT = 1;
    public final static int ANALYZER_LANG = 2;

    /**
     * A flow is a run order between different Analyzers.
     * In order to process user input.
     */
    class Flow extends ArrayList<Integer> { }
    final Flow classicUserInput = new Flow();
    public Flow getDefaultFlow() { return classicUserInput; }

    public Pipeline(ResultStore resultStore) {
        this.resultStore = resultStore;
        classicUserInput.add(ANALYZER_CONTENT);
        classicUserInput.add(ANALYZER_LANG);
    }

    public void run() {
        for(Integer i : getDefaultFlow()) {
            Analyzer a = get(i);
            //a.runAnalysis
        }
    }

    @Override public Iterator<Analyzer> iterator() { return super.values().iterator(); }
    @Override public Object call(Class<?> iface, String name, String actualName, Class<?> returnType, Class<?>[] paramTypes, Object[] args) { return super.call(iface, name, actualName, returnType, paramTypes, args); }

    // DSL
    public CodeAnalyzer getLanguageAnalyzer() { return (CodeAnalyzer) get(ANALYZER_LANG); }
    public void setLanguageAnalyzer(CodeAnalyzer a) { put(ANALYZER_LANG,a); }
}
