package io.github.rosemoe.editor.core.analyze.result.instances;

import io.github.rosemoe.editor.core.analyze.result.AnalyzerResult;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.IdentifierAutoCompleteModel;

public class CodeAnalyzerResultCompletion implements AnalyzerResult {

    public IdentifierAutoCompleteModel.Identifiers identifiers = new IdentifierAutoCompleteModel.Identifiers();

    @Override
    public CodeAnalyzerResultCompletion clone() {
        return new CodeAnalyzerResultCompletion();
    }

    @Override
    public void clear() {
        identifiers.identifiers.clear();
    }
}
