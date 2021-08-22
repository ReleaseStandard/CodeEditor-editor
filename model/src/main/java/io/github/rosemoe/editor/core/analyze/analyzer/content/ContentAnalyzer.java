package io.github.rosemoe.editor.core.analyze.analyzer.content;

import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.analyze.signal.Router;
import io.github.rosemoe.editor.core.analyze.analyzer.Analyzer;
import io.github.rosemoe.editor.core.analyze.ResultStore;
import io.github.rosemoe.editor.core.analyze.signal.Routes;

public class ContentAnalyzer extends Analyzer implements Router {

    public ContentAnalyzer(ResultStore resultStore) {
        super(resultStore);
    }

    @Override
    public void analyze() {

    }

    /**
     * This method is responsible from analyze content produced by the View.
     */
    public void analyze(int textAction, Object ...args) {
        CodeAnalyzerResultContent result = (CodeAnalyzerResultContent) resultStore.getResultInBuild(ResultStore.RES_CONTENT);
    }

    @Override
    public boolean route(Routes action, Object ...args) {
        switch (action) {

        }
        return false;
    }
}
