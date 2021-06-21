package io.github.rosemoe.editor.core.extension.plugins.widgets.scrollbar;

import android.graphics.Canvas;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetController;
import io.github.rosemoe.editor.core.Rect;
import io.github.rosemoe.editor.core.util.shortcuts.A;

/**
 * A scroll bar with it's track.
 * It is a widget but it does'nt require to be used by plugins.
 */
public class ScrollBarController extends WidgetController {
    public final ScrollBarView view;
    public ScrollBarModel model = new ScrollBarModel();

    public ScrollBarController(CodeEditor editor, String orientation) {
        super(editor);
        view = new ScrollBarView(editor);
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
        float all = isVertical ? editor.mLayout.getLayoutHeight() + eHeight / 2f : editor.getScrollMaxX() + eWidth;
        int offsetY = editor.getOffsetY();
        int offsetX = editor.getOffsetX();
        float centerY = 0;

        centerY = model.computeBarRect( all, offsetX, offsetY, eWidth, eHeight, dpUnit);


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
