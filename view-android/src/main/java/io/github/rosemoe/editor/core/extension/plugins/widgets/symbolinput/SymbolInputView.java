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
package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.LineNumberPanelView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput.handles.SymbolInputViewHandle;

/**
 * Class responsible from displaying symbol input to user.
 */
public class SymbolInputView extends WidgetExtensionView {

    protected SymbolInputViewHandle handles = new SymbolInputViewHandle();

    public SymbolInputView(Context context, int textColor, int backgroundColor) {
        super(context);
    }

    public SymbolInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SymbolInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SymbolInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setControllerName(Context ctx) {
        controllerName = getClass().getPackage().getName() + ".SymbolInputController";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        handles.handleOnDraw(canvas);
        super.onDraw(canvas);
    }
}
