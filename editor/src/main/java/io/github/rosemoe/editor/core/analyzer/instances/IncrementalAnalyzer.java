package io.github.rosemoe.editor.core.analyzer.instances;

import io.github.rosemoe.editor.core.analyzer.Analyzer;
import io.github.rosemoe.editor.core.analyzer.ResultStore;

/**
 * Incremental analyzer are supposed to have better performances than the general case.
 */
public abstract class IncrementalAnalyzer extends Analyzer {
    public IncrementalAnalyzer(ResultStore resultStore) {
        super(resultStore);
    }
}
