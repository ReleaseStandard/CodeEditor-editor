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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.IntPair;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.handles.LineNumberPanelViewHandles;
import io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.extension.UserInputEvent;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.extension.LineNumberPanelEvent;

import static io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel.LineNumberPanelModel.*;

/**
 * Class responsible for displaying a line number panel on the editor.
 * This is a widget.
 *
 */
public class LineNumberPanelController extends WidgetExtensionController {

    private LineNumberPanelModel model = new LineNumberPanelModel();

    public LineNumberPanelView getView() {
        return (LineNumberPanelView) view;
    }
    public LineNumberPanelController(CodeEditor editor) {
        super(editor);
        subscribe(LineNumberPanelEvent.class);
        subscribe(UserInputEvent.class);
        name        = "linenumberpanel";
        description = "This widget is responsible from displaying the linenumber panel";
        builderClass = LineNumberPanelView.class;
        registerColorIfNotIn("lineNumberPanel", "base2");
        registerColorIfNotIn("lineNumberBackground", "base2");
        registerColorIfNotIn("lineNumberPanelText", "base1");
    }

    @Override
    public void attachView(View v) {
        view = (WidgetExtensionView) v;
        getView().scroller = new OverScroller(v.getContext());
        ((LineNumberPanelView)v).initialize();
        getView().handles = new LineNumberPanelViewHandles() {
            @Override
            public void handleOnDraw(Object canvas) {
                refresh(canvas);
            }
        };
        model.dividerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, Resources.getSystem().getDisplayMetrics());
        model.margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    protected void handleEventDispatch(Event e, String subtype) {
        switch(subtype) {
            case LineNumberPanelEvent.CHANGE_ALIGN:
                Integer align = (Integer) e.getArg(0);
                if ( align == null ) {
                    Logger.v("No arguments given to change align");
                }
                setLineNumberAlign(align);
                break;
            case LineNumberPanelEvent.DIVIDER:
                String prop = (String) e.getArg(0);
                if( prop != null ) {
                    if ( prop.equals("width") ) {
                        Float width = (Float) e.getArg(1);
                        model.dividerWidth = width;
                        view.invalidate();
                    } else if ( prop.equals("margin") ) {
                        Float margin = (Float) e.getArg(1);
                        model.margin = margin;
                    }
                }
                break;
            case UserInputEvent.ONSCROLL: {
                android.view.MotionEvent e1 = (MotionEvent) e.getArg(0);
                android.view.MotionEvent e2 = (MotionEvent) e.getArg(1);
                Float distanceX = (Float) e.getArg(2);
                Float distanceY = (Float) e.getArg(3);
                Integer endX = (Integer) e.getArg(4);
                Integer endY = (Integer) e.getArg(5);
                Logger.debug("HERE : endX=", endX, ",endY=", endY);
                OverScroller mScroller = getView().scroller;
                mScroller.startScroll(mScroller.getCurrX(),
                        mScroller.getCurrY(),
                        endX - mScroller.getCurrX(),
                        endY - mScroller.getCurrY(), 0);
                view.invalidate();
                break;
            }
        }
    }

    @Override
    public void setEnabled(boolean state) {
        if (editor.isWordwrap()) {
            editor.mLayout.createLayout(editor);
        }
        super.setEnabled(state);
    }

    @Override
    public void clear() {
        model.postDrawLineNumbers.clear();
        view.invalidate();
    }

    /**
     * Generic method : all widgets will have it (all canvas widget).
     * Paint the widget on the screen in its state.
     * @param canvas to paint on
     */
    @Override
    protected void handleRefresh(Object canvas, Object ...args) {
        ColorManager colorManager = editor.colorManager;
        int lineNumberColor = colorManager.getColor("lineNumberPanelText");
        int lineNumberBackgroundColor = colorManager.getColor("lineNumberBackground");
        int dividerColor = colorManager.getColor("completionPanelBackground");

        drawLineNumberBackground(canvas, lineNumberBackgroundColor);
        drawDivider(canvas, dividerColor);

        for (int i = 0; i < model.postDrawLineNumbers.size(); i++) {
            long packed = model.postDrawLineNumbers.get(i);
            Logger.debug("Drawing : ",i);
            drawLineNumber(canvas, IntPair.getFirst(packed), IntPair.getSecond(packed), lineNumberColor );
        }
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
        getView().lineNumberPaint.setTypeface(typefaceLineNumber);
        view.invalidate();
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
        getView().setTextAlign(getViewLineNumber());
        view.invalidate();
    }

    /**
     * Draw divider line
     *
     * @param canvas  Canvas to draw
     * @param color   Color to draw divider
     */
    private void drawDivider(Object canvas, int color) {

        int offY = editor.getOffsetY();
        int editorHeight = editor.view.getHeight();

        // model compute
        model.divider.bottom = editorHeight;
        model.divider.top = 0;
        if (offY < 0) {
            model.divider.bottom = model.divider.bottom - offY;
            model.divider.top = model.divider.top - offY;
        }
        model.divider.left = width() - model.dividerWidth;
        model.divider.right = model.divider.left + model.dividerWidth;

        // display view
        WidgetExtensionView.drawColor(canvas, color, model.divider);
    }

    /**
     * Draw line number background
     *
     * @param canvas  Canvas to draw
     * @param color   Color of line number background
     */
    private void drawLineNumberBackground(Object canvas, int color) {
        float left = 0;
        float right = width();
        if (right < 0) {
            return;
        }
        Logger.debug("color=",color);

        model.panelBg.bottom = editor.view.getHeight();
        model.panelBg.top = 0;
        int offY = editor.getOffsetY();
        if (offY < 0) {
            model.panelBg.bottom = model.panelBg.bottom - offY;
            model.panelBg.top = model.panelBg.top - offY;
        }
        model.panelBg.left = left;
        model.panelBg.right = right;

        // display view
        WidgetExtensionView.drawColor(canvas, color, model.panelBg);
    }

    /**
     * Draw single line number
     */
    private void drawLineNumber(Object canvas, int line, int row, int color) {
        float textWidth = width() - model.dividerWidth - model.margin;
        int count = model.computeAndGetText(line);
        getView().drawLineNumber(canvas,row,textWidth,count,color,model.computedText,model.margin,getViewLineNumber(),editor.getRowBottom(row),editor.getRowTop(row),editor.getOffsetY());
    }

    public void addNumber(int line, int row) {
        model.postDrawLineNumbers.add(IntPair.pack(line, row));
    }

    @Override
    public float width() {
        return getView().getWidth();
    }
}
