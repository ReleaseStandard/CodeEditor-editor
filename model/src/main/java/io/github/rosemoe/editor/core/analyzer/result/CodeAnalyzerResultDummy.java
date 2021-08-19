package io.github.rosemoe.editor.core.analyzer.result;

/**
 * Empty result, nothing appends.
 */
public class CodeAnalyzerResultDummy extends CodeAnalyzerResult {
    @Override
    public void dispatchResult(Object... args) {

    }

    @Override
    public CodeAnalyzerResult clone() {
        return null;
    }
}
