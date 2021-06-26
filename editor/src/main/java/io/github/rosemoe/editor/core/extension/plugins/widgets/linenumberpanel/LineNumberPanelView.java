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
import android.view.View;
import android.widget.OverScroller;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionCanvasPartView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.handles.LineNumberPanelViewHandles;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * This view is a canvas part.
 */
public class LineNumberPanelView extends WidgetExtensionView {
    CodeEditor editor;
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

    public void initialize(CodeEditor editor) {
        this.editor = editor;
        lineNumberPaint.setAntiAlias(true);
        lineNumberPaint.setTypeface(Typeface.MONOSPACE);
    }
    protected void setTextAlign(Paint.Align align) {
        if (lineNumberPaint.getTextAlign() != align) {
            lineNumberPaint.setTextAlign(align);
        }
    }

    /**
     * Draw text of line number panel.
     * @param canvas
     * @param row
     * @param width
     * @param count
     * @param color
     * @param computedText
     * @param mDividerMargin
     * @param mLineNumberAlign
     */
    protected void drawLineNumber(Canvas canvas, int row, float width, int count, int color, char[]computedText, float mDividerMargin, Paint.Align mLineNumberAlign) {
        setTextAlign(mLineNumberAlign);
        lineNumberPaint.setColor(color);
        int sz = (int) Math.min((editor.getRowBottom(row) - editor.getRowTop(row)), width);
        lineNumberPaint.setTextSize(sz);
        // Line number center align to text center //

        Paint.FontMetrics metrics = lineNumberPaint.getFontMetrics();
        float y = (editor.getRowBottom(row) + editor.getRowTop(row)) / 2f
                - (metrics.descent - metrics.ascent) / 2f - metrics.ascent
                - editor.getOffsetY();
        switch (mLineNumberAlign) {
            case LEFT:
                canvas.drawText(computedText, 0, count, 0, y, lineNumberPaint);
                break;
            case RIGHT:
                canvas.drawText(computedText, 0, count, 0 + width, y, lineNumberPaint);
                break;
            case CENTER:
                canvas.drawText(computedText, 0, count, 0 + width / 2f, y, lineNumberPaint);
        }
    }
//
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        handles.handleOnDraw(canvas);
        //lineNumberPaint.setColor(0xFFFF0000);
        //canvas.drawText("TEST",0,4,0,0,lineNumberPaint);
    }
    //
    /**
     * PUBLIC
     */
    /*@Override
    public void paint(Canvas canvas, CodeEditor editor) {

    }*/
}
