package io.github.rosemoe.editor.core.analyze.analyzer;

import io.github.rosemoe.editor.core.analyze.analyzer.Analyzer;
import io.github.rosemoe.editor.core.analyze.ResultStore;

/**
 * Incremental analyzer are supposed to have better performances than the general case.
 */
public abstract class IncrementalAnalyzer extends Analyzer {
    public IncrementalAnalyzer(ResultStore resultStore) {
        super(resultStore);
    }
}
