package io.github.rosemoe.editor.core.analyzer;

/**
 * Any analyzer has a goal : analyse an input signal and produce a result.
 * e.g. signal : text, stream of token.
 * e.g. result : ContentGrid, SpanMap.
 */
public abstract class Analyzer/*<T>*/ {

    final public ResultStore resultStore;

    protected Analyzer(ResultStore resultStore) {
        this.resultStore = resultStore;
    }

    /**
     * Process input signal of type T and fill result a result.
     * @param es
     */
    //protected abstract void processSignal(T es);
}
