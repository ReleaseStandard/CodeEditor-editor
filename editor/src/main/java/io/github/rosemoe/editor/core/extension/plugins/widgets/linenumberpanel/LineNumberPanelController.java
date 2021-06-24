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
package io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.IntPair;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionController;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.extension.LineNumberPanelEvent;
import io.github.rosemoe.editor.core.util.shortcuts.A;

import static io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.LineNumberPanelModel.*;

/**
 * Class responsible for displaying a line number panel on the editor.
 * This is a widget.
 *
 */
public class LineNumberPanelController extends SystemExtensionController {

    public LineNumberPanelModel model = new LineNumberPanelModel();
    public final LineNumberPanelView  view;

    public LineNumberPanelController(CodeEditor editor) {
        super(editor);
        subscribe(LineNumberPanelEvent.class);
        view = new LineNumberPanelView(editor);
        model.dividerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, Resources.getSystem().getDisplayMetrics());
        model.dividerMargin = model.dividerWidth;
        name        = "linenumberpanel";
        description = "This widget is responsible from displaying the linenumber panel";
        editor.colorManager.register("lineNumberPanel", "base2");
        editor.colorManager.register("lineNumberBackground", "base2");
        editor.colorManager.register("lineNumberPanelText", "base1");
    }

    @Override
    public void handleEventDispatch(Event e, String subtype) {
        LineNumberPanelEvent uie = (LineNumberPanelEvent) e;
        switch(subtype) {
            case LineNumberPanelEvent.CHANGE_ALIGN:
                Integer align = (Integer) uie.getArg(0);
                if ( align == null ) {
                    Logger.v("No arguments given to change align");
                }
                setLineNumberAlign(align);
                break;
            case LineNumberPanelEvent.DIVIDER:
                String prop = (String) uie.getArg(0);
                if( prop != null ) {
                    if ( prop.equals("width") ) {
                        Float width = (Float) uie.getArg(1);
                        setDividerWidth(width);
                    } else if ( prop.equals("margin") ) {
                        Float margin = (Float) uie.getArg(1);
                        setDividerMargin(margin);
                    }
                }
        }
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        editor.invalidate();
    }

    /**
     * Get the width of line number region
     *
     * @return width of line number region
     */
    public float measureLineNumber(int lineCount) {
        if (!isEnabled()) {
            return 0f;
        }
        int count = 0;
        while (lineCount > 0) {
            count++;
            lineCount /= 10;
        }
        final String[] charSet = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        float single = 0f;
        for (String ch : charSet) {
            single = Math.max(single, view.lineNumberPaint.measureText(ch));
        }
        return single * count;
    }

    /**
     * @return Typeface of line number
     */
    public Typeface getTypefaceLineNumber() {
        return view.lineNumberPaint.getTypeface();
    }

    /**
     * Set line number's typeface
     *
     * @param typefaceLineNumber New typeface
     */
    public void setTypefaceLineNumber(Typeface typefaceLineNumber) {
        if (typefaceLineNumber == null) {
            typefaceLineNumber = Typeface.MONOSPACE;
        }
        view.lineNumberPaint.setTypeface(typefaceLineNumber);
        editor.invalidate();
    }

    /**
     * Get align state from the model.
     * @return Line number align
     */
    private Paint.Align getViewLineNumber() {
        switch (model.alignment) {
            case ALIGN_LEFT:
                return Paint.Align.LEFT;
            case ALIGN_CENTER:
                return Paint.Align.CENTER;
            case ALIGN_RIGHT:
                return Paint.Align.RIGHT;
        }
        model.alignment = ALIGN_DEFAULT;
        return getViewLineNumber();
    }
    /**
     * Set line number align
     *
     * @param align Align for line number
     */
    public void setLineNumberAlign(int align) {
        model.alignment = align;
        view.setTextAlign(getViewLineNumber());
        editor.invalidate();
    }

    /**
     * Set divider line's left and right margin
     *
     * @param dividerMargin Margin for divider line
     */
    public void setDividerMargin(float dividerMargin) {
        if (dividerMargin < 0) {
            throw new IllegalArgumentException("margin can not be under zero");
        }
        model.dividerMargin = dividerMargin;
        editor.invalidate();
    }

    /**
     * @return Width of divider line
     */
    public float getDividerWidth() {
        return model.dividerWidth;
    }

    /**
     * Set divider line's width
     *
     * @param dividerWidth Width of divider line
     */
    public void setDividerWidth(float dividerWidth) {
        if (dividerWidth < 0) {
            throw new IllegalArgumentException("width can not be under zero");
        }
        model.dividerWidth = dividerWidth;
        editor.invalidate();
    }

    /**
     * Get the width of line number and divider line
     * (width of the all panel).
     *
     * @return The width
     */
    public float getPanelWidth() {
        return isEnabled() ? measureLineNumber(editor.getLineCount()) + model.dividerMargin * 2 + getDividerWidth() : editor.mDpUnit * 5;
    }

    @Override
    protected void initFromJson(JsonNode extension) {

    }

    /**
     * Generic method : all widgets will have it (all canvas widget).
     * Paint the widget on the screen in its state.
     * @param canvas to paint on
     */
    @Override
    protected void handleRefresh(Canvas canvas, Object ...args) {
        ColorManager colorManager = editor.colorManager;
        int lineNumberColor = colorManager.getColor("lineNumberPanelText");
        int lineNumberBackgroundColor = colorManager.getColor("lineNumberBackground");
        Float offsetX = (Float) args[0];
        Integer lineCount = (Integer) args[1];
        float lineNumberWidth = measureLineNumber(lineCount);

        drawLineNumberBackground(canvas, offsetX, lineNumberWidth + model.dividerMargin, lineNumberBackgroundColor);
        drawDivider(canvas, offsetX + lineNumberWidth + model.dividerMargin, colorManager.getColor("completionPanelBackground"));

        for (int i = 0; i < model.postDrawLineNumbers.size(); i++) {
            long packed = model.postDrawLineNumbers.get(i);
            drawLineNumber(canvas, IntPair.getFirst(packed), IntPair.getSecond(packed), offsetX, lineNumberWidth, lineNumberColor);
        }
    }

    /**
     * Draw divider line
     *
     * @param canvas  Canvas to draw
     * @param offsetX End x of line number region
     * @param color   Color to draw divider
     */
    private void drawDivider(Canvas canvas, float offsetX, int color) {
        float right = offsetX + getDividerWidth();
        if (right < 0) {
            return;
        }
        float left = Math.max(0f, offsetX);
        int offY = editor.getOffsetY();
        int editorHeight = editor.getHeight();

        // model compute
        model.divider.bottom = editorHeight;
        model.divider.top = 0;
        if (offY < 0) {
            model.divider.bottom = model.divider.bottom - offY;
            model.divider.top = model.divider.top - offY;
        }
        model.divider.left = left;
        model.divider.right = right;

        // display view
        editor.drawColor(canvas, color, A.getRectF(model.divider));
    }
    /**
     * Draw line number background
     *
     * @param canvas  Canvas to draw
     * @param offsetX Start x of line number region
     * @param width   Width of line number region
     * @param color   Color of line number background
     */
    private void drawLineNumberBackground(Canvas canvas, float offsetX, float width, int color) {
        float right = offsetX + width;
        if (right < 0) {
            return;
        }
        Logger.debug("color=",color);
        float left = Math.max(0f, offsetX);

        model.panelBg.bottom = editor.getHeight();
        model.panelBg.top = 0;
        int offY = editor.getOffsetY();
        if (offY < 0) {
            model.panelBg.bottom = model.panelBg.bottom - offY;
            model.panelBg.top = model.panelBg.top - offY;
        }
        model.panelBg.left = left;
        model.panelBg.right = right;

        // display view
        editor.drawColor(canvas, color, A.getRectF(model.panelBg));
    }
    /**
     * Draw single line number
     */
    private void drawLineNumber(Canvas canvas, int line, int row, float offsetX, float width, int color) {
        if (width + offsetX <= 0) {
            Logger.debug("aborted ...");
            return;
        }
        Logger.debug("color=",color,",model.computedText=",model.computedText.length,",c1=",model.computedText[0]);
        int count = model.computeAndGetText(line);
        view.drawLineNumber(canvas,row,offsetX,width,count,color,model.computedText,model.dividerMargin,getViewLineNumber());
    }
}
