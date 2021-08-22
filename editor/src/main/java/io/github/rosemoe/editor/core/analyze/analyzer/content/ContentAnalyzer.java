package io.github.rosemoe.editor.core.analyze.analyzer.content;

import io.github.rosemoe.editor.core.content.controller.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.signal.Router;
import io.github.rosemoe.editor.core.analyze.analyzer.Analyzer;
import io.github.rosemoe.editor.core.analyze.ResultStore;
import io.github.rosemoe.editor.core.signal.Routes;

public class ContentAnalyzer extends Analyzer implements Router {

    public ContentActionStackAnalyzer contentManager = new ContentActionStackAnalyzer();

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
            case ACTION_CONTENT_ACTION_STACK: {
                Routes nextAction = (Routes) args[0];
                contentManager.route(nextAction, resultStore.getResult(ResultStore.RES_CONTENT));
                return true;
            }
        }
        return false;
    }
}
