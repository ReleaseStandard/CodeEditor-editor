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

    public OverScroller scroller;
    public Paint lineNumberPaint = new Paint();

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
        lineNumberPaint.setAntiAlias(true);
        lineNumberPaint.setTypeface(Typeface.MONOSPACE);
        setWillNotDraw(false); // https://stackoverflow.com/questions/10727225/drawing-something-on-my-linearlayout
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
     * @param canvas
     * @param row
     * @param textWidth
     * @param count
     * @param color
     * @param computedText
     * @param margin
     * @param mLineNumberAlign
     */
    protected void drawLineNumber(Canvas canvas, int row, float textWidth, int count, int color, char[]computedText, float margin, Paint.Align mLineNumberAlign, int bottomRow, int topRow, int yOffset) {
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
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        handles.handleOnDraw(canvas);
    }
}
