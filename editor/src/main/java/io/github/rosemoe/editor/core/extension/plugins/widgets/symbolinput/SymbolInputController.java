package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Observable;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput.handles.SymbolInputViewHandle;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * We can consider that a SymbolInputController have only one
 * view. (Nobody wants two symbol view in one codeeditor).
 */
public class SymbolInputController extends WidgetExtensionController {

    private SymbolChannelController channel;
    private ArrayList<SymbolController> symbols = new ArrayList<>();
    private SymbolInputModel model = new SymbolInputModel();
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
        view.setWillNotDraw(false);
        this.view = (WidgetExtensionView) view;
        SymbolInputView siv = getView();
        getView().setOrientation(LinearLayout.HORIZONTAL);

        channel = new SymbolChannelController();
        siv.handles = new SymbolInputViewHandle() {
            @Override
            public void handleOnClickSymbol(String symbol, String insertText) {
                channel.insertSymbol(editor, insertText, 1);
            }
        };
        addSymbols(new String[]{"TAB", "{", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"},
                new String[]{"\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"});
        Logger.debug("View attached");
    }

    /**
     * @param state
     */
    @Override
    public void setEnabled(boolean state) {
        /*
        TODO
        if ( state ) {
            for(View v : getView().views) {
                if ( ! v.isShown() ) {
                    getView().addButton(v);
                }
            }
        }*/
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
            String symbol = symbolsDisplayIcon[i];
            String actionText = symbolsTextAction[i];
            SymbolController s = new SymbolController(editor,view.getContext(), symbol, actionText, prefixColor("text"),prefixColor("bg")) {
                @Override
                public void handleOnClick() {
                    channel.insertSymbol(editor, actionText, 1);
                }
            };
            editor.colorManager.attach(s);
            symbols.add(s);
            getView().addView(s.view,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        view.invalidate();
        Logger.debug("symbols added");
    }

    @Override
    public void update(Observable o, Object arg) {
        editor.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ( view != null ) {
                    view.setBackgroundColor(getPrefixedColor("bg"));
                }
            }
        });
    }
}
