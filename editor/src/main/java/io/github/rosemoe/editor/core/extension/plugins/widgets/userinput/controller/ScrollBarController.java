package io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.controller;

import android.graphics.Canvas;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetController;
import io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.model.ScrollBarModel;
import io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.view.ScrollBarView;
import io.github.rosemoe.editor.core.model.Rect;
import io.github.rosemoe.editor.core.util.shortcuts.A;

/**
 * A scroll bar with it's track.
 * it seem to be also a widget, maybe we can link it editor later.
 */
public class ScrollBarController extends WidgetController {
    public ScrollBarView view = new ScrollBarView();
    public ScrollBarModel model = new ScrollBarModel();

    public ScrollBarController(CodeEditor editor, String orientation) {
        super(editor);
        model.orientation = orientation;
        registerColor("scrollBarThumb", "base1");
        registerColor("scrollBarThumbPressed", "base2");
        registerColor("scrollBarTrack", "wholeBackground");
    }
    public void paint(Canvas canvas, Object ...args) {
        int eWidth = editor.getWidth();
        int eHeight = editor.getHeight();
        float dpUnit = editor.mDpUnit;
        boolean isVertical = model.orientation.equals("vertical");

        // paint the track
        if (isHolding()) {
            if ( isVertical ) {
                model.barTrack = new Rect(eWidth - dpUnit * 10, 0, eWidth, eHeight);
            } else {
                model.barTrack = new Rect(0, eHeight - dpUnit * 10, eWidth, eHeight);
            }
        }

        // paint the bar
        int page = isVertical ? eHeight : eWidth;
        float all = isVertical ? editor.mLayout.getLayoutHeight() + eHeight / 2f : editor.getScrollMaxX() + eWidth;
        float length = isVertical ? (page / all * eHeight) : page / all * eWidth;
        int offsetY = editor.getOffsetY();
        int offsetX = editor.getOffsetX();
        float centerY = 0;

        if ( isVertical ) {
            float margin = 0;
            if ( length < dpUnit * 30 ) {
                length = dpUnit * 30;
                margin = (offsetY + page / 2f) / all * (eHeight - length);
            } else {
                margin = offsetY / all * eHeight;
            }
            if ( isHolding() ) {
                centerY = margin + length / 2f;
            }
            model.bar.top = margin;
            model.bar.bottom = margin + length;
            model.bar.right = eWidth;
            model.bar.left = eWidth - dpUnit * 10;
        } else {
            float margin = offsetX / all * eWidth;
            model.bar.top = eHeight - dpUnit * 10;
            model.bar.bottom = eHeight;
            model.bar.right = margin + length;
            model.bar.left = margin;
        }
        view.barTrack = A.getRectF(model.barTrack);
        view.bar      = A.getRectF(model.bar);

        // painting
        if ( isHolding() ) {
            editor.drawColor(canvas, getColor("scrollBarTrack"), view.barTrack);
            if ( isVertical ) {
                editor.drawLineInfoPanel(canvas, centerY, 0);
            }
        }
        editor.drawColor(canvas, isHolding() ? getColor("scrollBarThumbPressed") : getColor("scrollBarThumb"), view.bar);
    }
    public boolean isHolding() {
        return model.holding;
    }
}
