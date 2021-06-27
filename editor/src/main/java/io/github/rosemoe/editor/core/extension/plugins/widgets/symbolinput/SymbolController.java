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
    protected String textColorKey = "";
    protected String backgroundColorKey = "";
    public SymbolController(CodeEditor editor, Context ctx, String displayIcon, String actionText,String textColorKey, String backgroundColorKey) {
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
        this.textColorKey = textColorKey;
        this.backgroundColorKey = backgroundColorKey;
        view.setText(displayIcon);
        update(null, null);
    }
    public void handleOnClick() {

    }
    public void handleOnDraw() {

    }

    @Override
    public void update(Observable o, Object arg) {
        view.setTextColor(editor.colorManager.getColor(textColorKey));
        view.setBackgroundColor(editor.colorManager.getColor(backgroundColorKey));
    }
}
