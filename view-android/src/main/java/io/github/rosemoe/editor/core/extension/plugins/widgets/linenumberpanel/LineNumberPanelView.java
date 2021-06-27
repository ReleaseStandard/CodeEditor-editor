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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.OverScroller;

import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.handles.LineNumberPanelViewHandles;

/**
 * This view is a canvas part.
 */
public class LineNumberPanelView extends WidgetExtensionView {

    protected OverScroller scroller;
    protected Paint lineNumberPaint = new Paint();

    public LineNumberPanelViewHandles handles = new LineNumberPanelViewHandles();

    public LineNumberPanelView(Context context) {
        super(context);
    }
    public LineNumberPanelView(Context context, android.util.AttributeSet attrs) {
        super(context,attrs);

    }
    public LineNumberPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public LineNumberPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void initialize() {
        super.initialize();
        lineNumberPaint.setAntiAlias(true);
        lineNumberPaint.setTypeface(Typeface.MONOSPACE);
    }

    /**
     * Set text alignment in this view.
     * @param align
     */
    protected void setTextAlign(Paint.Align align) {
        if (lineNumberPaint.getTextAlign() != align) {
            lineNumberPaint.setTextAlign(align);
        }
    }

    /**
     * Draw text of line number panel.
     * @param canvasContainer canvas to draw on.
     * @param row
     * @param textWidth
     * @param count
     * @param color
     * @param computedText
     * @param margin
     * @param mLineNumberAlign
     */
     protected void drawLineNumber(Object canvasContainer, int row, float textWidth, int count, int color, char[]computedText, float margin, Paint.Align mLineNumberAlign, int bottomRow, int topRow, int yOffset) {
        Canvas canvas = (Canvas) canvasContainer;
        setTextAlign(mLineNumberAlign);
        lineNumberPaint.setColor(color);
        int sz = (int) Math.min((bottomRow - topRow), textWidth);
        float marginLeft = margin/2;
        float marginRight = margin/2;
        lineNumberPaint.setTextSize(sz);
        // Line number center align to text center //

        Paint.FontMetrics metrics = lineNumberPaint.getFontMetrics();
        float y = (bottomRow + topRow) / 2f
                - (metrics.descent - metrics.ascent) / 2f - metrics.ascent
                - yOffset;
        switch (mLineNumberAlign) {
            case LEFT:
                canvas.drawText(computedText, 0, count, marginLeft, y - marginRight, lineNumberPaint);
                break;
            case RIGHT:
                canvas.drawText(computedText, 0, count, marginLeft + textWidth, y - marginRight, lineNumberPaint);
                break;
            case CENTER:
                canvas.drawText(computedText, 0, count, marginLeft + textWidth / 2f, y - marginRight, lineNumberPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        handles.handleOnDraw(canvas);
    }
    @Override
    public void setControllerName(Context ctx) {
        controllerName = getClass().getPackage().getName() + ".LineNumberPanelController";
    }
}
