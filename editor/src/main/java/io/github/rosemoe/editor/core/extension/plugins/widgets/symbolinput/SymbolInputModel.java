package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import java.util.HashMap;

public class SymbolInputModel {
    public HashMap<String,Symbol> symbols = new HashMap<>();
    public static class Symbol {
        public String displayText;
        public String actionText;
        public Symbol(String displayText, String actionText) {
            this.actionText = actionText;
            this.displayText = displayText;
        }
    }
    public Symbol addSymbol(String displayText, String actionText) {
        return symbols.put(displayText, new Symbol(displayText, actionText));
    }
}
