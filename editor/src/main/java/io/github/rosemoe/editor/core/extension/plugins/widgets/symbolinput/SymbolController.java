package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Button;

import java.util.Observable;
import java.util.Observer;

import io.github.rosemoe.editor.R;
import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput.handles.SymbolViewHandle;

public class SymbolController implements Observer {

    private CodeEditor editor;
    private SymbolModel model = new SymbolModel();
    protected final SymbolView view;

    public SymbolController(CodeEditor editor, Context ctx, String displayIcon, String actionText) {
        this.editor = editor;
        model.displayIcon = displayIcon;
        model.actionText = actionText;
        view = new SymbolView(ctx, null, android.R.attr.buttonStyleSmall);
        view.handles = new SymbolViewHandle() {
            @Override
            public void handleOnClick() {
                SymbolController.this.handleOnClick();
            }
            @Override
            public void handleOnDraw(Canvas canvas) {
                SymbolController.this.handleOnDraw();
            }
        };
        view.setText(displayIcon);
        view.setTextColor(editor.symbolInputController.getPrefixedColor("text"));
        view.setBackgroundColor(editor.symbolInputController.getPrefixedColor("bg"));

    }
    public void handleOnClick() {

    }
    public void handleOnDraw() {

    }

    @Override
    public void update(Observable o, Object arg) {
        int text = editor.symbolInputController.getPrefixedColor("text");
        view.setTextColor(text);
        int bg = editor.symbolInputController.getPrefixedColor("bg");
        view.setBackgroundColor(bg);
    }
}
