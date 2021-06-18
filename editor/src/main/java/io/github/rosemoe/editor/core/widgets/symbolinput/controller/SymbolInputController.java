package io.github.rosemoe.editor.core.widgets.symbolinput.controller;

import android.view.View;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.widgets.WidgetController;
import io.github.rosemoe.editor.core.widgets.symbolinput.view.SymbolInputView;

/**
 * We can consider that a SymbolInputController have only one
 * view. (Nobody wants two symbol view in one codeeditor).
 */
public class SymbolInputController extends WidgetController {

    public SymbolInputView view = null;

    public SymbolInputController(CodeEditor editor) {
        super(editor);
        name        = "symbolinput";
        description = "Symbol input widget";
    }

    /**
     * Used to attach inflated view to Controller.
     * @param view
     */
    public void attachView(SymbolInputView view) {
        this.view = view;
        view.channel = new SymbolChannelController(editor);
    }

    /**
     * @param state
     */
    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if ( state ) {
            for(View v : view.views) {
                if ( ! v.isShown() ) {
                    view.addButton(v);
                }
            }
        } else {
            view.removeAllViewsInLayout();
        }
        view.invalidate();
    }

    /**
     * Add symboles into the view.
     * @param symbolsDisplayIcon What text to display as an icon
     * @param symbolsTextAction What text to insert when clicked
     */
    public void addSymbols(String[] symbolsDisplayIcon, final String[] symbolsTextAction) {
        int count = Math.max(symbolsDisplayIcon.length, symbolsTextAction.length);
        for (int i = 0; i < count; i++) {
            view.addSymbol(symbolsDisplayIcon[i], symbolsTextAction[i]);
        }
    }
}
