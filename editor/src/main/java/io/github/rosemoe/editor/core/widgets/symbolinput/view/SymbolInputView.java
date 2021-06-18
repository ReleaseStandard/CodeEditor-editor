/*
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.core.widgets.symbolinput.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.widgets.symbolinput.controller.SymbolChannelController;

/**
 * Class responsible from displaying symbol input to user.
 */
public class SymbolInputView extends LinearLayout {

    int textcolor = Color.BLACK;
    int bgColor = Color.WHITE;

    /**
     * View initialization.
     */
    public void init() { init(null, null); }
    public void init(Integer textColor, Integer backgroundColor) {
        setBackgroundColor(bgColor);
        setOrientation(HORIZONTAL);
        if ( textColor != null ) {
            this.textcolor = textColor;
        }
        if ( backgroundColor != null ) {
            this.bgColor = backgroundColor;
        }
    }
    public SymbolInputView(Context context, int textColor, int backgroundColor) {
        super(context);
        init(textColor, backgroundColor);
    }

    public SymbolInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SymbolInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SymbolInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private SymbolChannelController channel;

    public void bindEditor(CodeEditor editor) {
        channel = editor.createNewSymbolChannel();
    }
    public void removeSymbols() {
        removeAllViews();
    }
    public void addSymbol(String symbol, final String insertText) {
        Button btn = new Button(getContext(), null, android.R.attr.buttonStyleSmall);
        btn.setText(symbol);
        btn.setTextColor(textcolor);
        btn.setBackgroundColor(bgColor);
        addView(btn, new LinearLayout.LayoutParams(-2, -1));
        btn.setOnClickListener((view) -> {
            channel.insertSymbol(insertText, 1);
        });
    }


}
