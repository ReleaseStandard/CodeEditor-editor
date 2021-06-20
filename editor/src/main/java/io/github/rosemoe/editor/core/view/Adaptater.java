package io.github.rosemoe.editor.core.view;

import android.graphics.RectF;
import io.github.rosemoe.editor.core.model.Rect;

/**
 * This adapt data from the model to the view.
 *  ex: data from the model is putted into the view (android RectF)
 */
public class Adaptater {
    /**
     * Effort to make it cross View (ex: android, then other).
     * @param o
     * @return
     */
    public static io.github.rosemoe.editor.core.model.Rect getRect(Object o) {
        if ( o == null ) {
            return null;
        }
        RectF r = (RectF) o;
        return new io.github.rosemoe.editor.core.model.Rect(r.left, r.top, r.right, r.bottom);
    }
    public static RectF getRectF(Rect r) {
        if ( r == null ) { return null; }
        return new RectF(r.left,r.top,r.right,r.bottom);
    }
}
