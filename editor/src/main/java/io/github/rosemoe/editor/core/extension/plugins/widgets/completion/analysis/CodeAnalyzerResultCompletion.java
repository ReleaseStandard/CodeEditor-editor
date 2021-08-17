package io.github.rosemoe.editor.core.extension.plugins.widgets.completion.analysis;

import io.github.rosemoe.editor.core.codeanalysis.analyzer.CodeAnalyzerResult;
import io.github.rosemoe.editor.core.codeanalysis.analyzer.tokenemitter.TokenEmitterResult;
import io.github.rosemoe.editor.core.extension.plugins.widgets.completion.IdentifierAutoCompleteModel;

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
