package io.github.rosemoe.editor.core.extension.extensions.widgets.scrollbar;

import android.graphics.Canvas;
import android.view.View;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.extensions.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.shortcuts.A;

/**
 * A scroll bar with it's track.
 * It is a widget but it does'nt require to be used by plugins.
 * A scrollbar move on it's 1D axis
 */
public class ScrollBarController extends WidgetExtensionController {
    public final ScrollBarView view;
    public ScrollBarModel model = new ScrollBarModel();

    public ScrollBarController(CodeEditor editor, float orientation) {
        super(editor);
        view = new ScrollBarView(editor);
        model.orientation = orientation;
        registerColorIfNotIn("scrollBarThumb", "base1");
        registerColorIfNotIn("scrollBarThumbPressed", "base2");
        registerColorIfNotIn("scrollBarTrack", "wholeBackground");
    }
    protected void handleRefresh(Canvas canvas, Object ...args) {
        int eWidth = editorController.view.getWidth();
        int eHeight = editorController.view.getHeight();

        Float lenAllContent    = (Float) args[0];
        Integer offset         = (Integer) args[1]; // offsetX
        Integer position       = (Integer) args[2]; // offsetY
        Integer trackBarLength = (Integer) args[3];
        Logger.debug("lenAllContent=",lenAllContent,",offset=",offset,",position=",position,",trackaBarLength=",trackBarLength);

        // compute in the model
        model.length = trackBarLength;
        updateWidth();
        model.prepareTrackBarRect(eWidth, eHeight);
        model.prepareBarRect(lenAllContent, offset, position);

        // painting
        view.barTrack = A.getRectF(model.barTrack);
        view.bar      = A.getRectF(model.bar);
        if ( isHolding() ) {
            editorController.drawColor(canvas, getColor("scrollBarTrack"), view.barTrack);
            //if ( isVertical ) {
            //    editorController.drawLineInfoPanel(canvas, 0, 0); // TODO : centerX
            //}
        }
        editorController.drawColor(canvas, isHolding() ? getColor("scrollBarThumbPressed") : getColor("scrollBarThumb"), view.bar);
    }

    @Override
    public void attachView(View v) {

    }

    /**
     * Test if user is holding the scroll bar.
     * @return
     */
    public boolean isHolding() {
        return model.holding;
    }
    /**
     * Update width of the scrollbar, this is required by the model is order to process dimensions.
     * @return the new width
     */
    public float updateWidth() {
        return model.width = editorController.mDpUnit * 10;
    }
    /**
     * Get width of the scroll bar.
     * @return
     */
    public float getWidth() {
        return model.width;
    }
}
