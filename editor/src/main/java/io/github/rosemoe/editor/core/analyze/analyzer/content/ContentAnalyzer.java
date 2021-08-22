package io.github.rosemoe.editor.core.analyze.analyzer.content;

import java.util.Arrays;

import io.github.rosemoe.editor.core.signal.Router;
import io.github.rosemoe.editor.core.analyze.analyzer.Analyzer;
import io.github.rosemoe.editor.core.analyze.ResultStore;
import io.github.rosemoe.editor.core.signal.Routes;
import io.github.rosemoe.editor.core.analyze.result.instances.CodeAnalyzerResultContent;

public class ContentAnalyzer extends Analyzer implements Router {

    public ContentActionStack contentManager = new ContentActionStack();

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
