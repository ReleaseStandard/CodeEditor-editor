package io.github.rosemoe.editor.core.extension.extensions.widgets.completion.analysis;

import io.github.rosemoe.editor.core.analyzer.result.CodeAnalyzerResult;
import io.github.rosemoe.editor.core.analyzer.result.TokenEmitterResult;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.IdentifierAutoCompleteModel;

public class CodeAnalyzerResultCompletion extends TokenEmitterResult {

    public IdentifierAutoCompleteModel.Identifiers identifiers = new IdentifierAutoCompleteModel.Identifiers();

    @Override
    public CodeAnalyzerResult clone() {
        return new CodeAnalyzerResultCompletion();
    }

    @Override
    public void dispatchResult(Object... args) {
        for(Object arg : args) {
            identifiers.addIdentifier((String) arg);
        }
    }
}
