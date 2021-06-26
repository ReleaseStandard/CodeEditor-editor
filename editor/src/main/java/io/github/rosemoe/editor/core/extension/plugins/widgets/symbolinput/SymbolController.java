package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.content.Context;
import android.widget.Button;

import io.github.rosemoe.editor.R;
import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput.handles.SymbolViewHandle;

public class SymbolController {
    public SymbolModel model = new SymbolModel();
    public final SymbolView view;

    public SymbolController(CodeEditor editor, Context ctx, String displayIcon, String actionText) {
        model.displayIcon = displayIcon;
        model.actionText = actionText;
        view = new SymbolView(editor, ctx, null, android.R.attr.buttonStyleSmall);
        view.handles = new SymbolViewHandle() {
            @Override
            public void handleOnClick() {
                SymbolController.this.handleOnClick();
            }
        };
        view.setPadding(0,0,0,0);
        view.setText(displayIcon);
        view.setTextColor(editor.symbolInputController.getPrefixedColor("text"));
        view.setBackgroundColor(editor.symbolInputController.getPrefixedColor("bg"));

    }
    public void handleOnClick() {

    }
}
