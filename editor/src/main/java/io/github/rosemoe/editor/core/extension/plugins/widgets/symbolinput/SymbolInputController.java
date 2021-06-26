package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.view.View;
import android.widget.LinearLayout;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput.handles.SymbolInputViewHandles;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * We can consider that a SymbolInputController have only one
 * view. (Nobody wants two symbol view in one codeeditor).
 */
public class SymbolInputController extends WidgetExtensionController {

    public SymbolInputModel model = new SymbolInputModel();
    public SymbolInputView getView() {
        return (SymbolInputView) view;
    }
    public SymbolInputController(CodeEditor editor) {
        super(editor);
        name        = "symbolinput";
        description = "Symbol input widget";
        builderClass = SymbolInputView.class;
        registerPrefixedColor("text", "textNormal");
        registerPrefixedColor("bg","base2");
    }

    /**
     * Used to attach inflated view to Controller.
     * @param view
     */
    @Override
    public void attachView(View view) {
        this.view = (WidgetExtensionView) view;
        SymbolInputView siv = getView();
        siv.ll = new LinearLayout(siv.getContext());
        siv.ll.setOrientation(LinearLayout.HORIZONTAL);

        siv.handles = new SymbolInputViewHandles() {
            @Override
            public void handleOnClickSymbol(SymbolChannelController channel, String symbol, String insertText) {
                super.handleOnClickSymbol(channel, symbol, insertText);
                channel.insertSymbol(editor, insertText, 1);
            }
        };
        siv.channel = new SymbolChannelController();
        siv.textcolor = getPrefixedColor( "text");
        siv.bgColor = getPrefixedColor("bg");

        addSymbols(new String[]{"TAB", "{", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"},
                new String[]{"\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"});
        Logger.debug("View attached");
    }

    /**
     * @param state
     */
    @Override
    public void setEnabled(boolean state) {
        if ( state ) {
            for(View v : getView().views) {
                if ( ! v.isShown() ) {
                    getView().addButton(v);
                }
            }
        }
        super.setEnabled(state);
    }

    /**
     * Add symboles into the view.
     * @param symbolsDisplayIcon What text to display as an icon
     * @param symbolsTextAction What text to insert when clicked
     */
    public void addSymbols(String[] symbolsDisplayIcon, final String[] symbolsTextAction) {
        int count = Math.max(symbolsDisplayIcon.length, symbolsTextAction.length);
        for (int i = 0; i < count; i++) {
            getView().addSymbol(symbolsDisplayIcon[i], symbolsTextAction[i]);
        }
        view.invalidate();
        Logger.debug("symbols added");
    }
}
