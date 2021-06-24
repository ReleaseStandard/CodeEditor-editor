package io.github.rosemoe.editor.core;

import android.graphics.RectF;

/**
 * This adapt data from the model to the view.
 *  ex: data from the model is putted into the view (android RectF)
 */
public class Adaptater {
    /**
     * conversion view RectF to Rect from the vuew
     * Effort to make it cross View (ex: android, then other).
     * @param o
     * @return
     */
    public static Rect getRect(Object o) {
        if ( o == null ) {
            return null;
        }
        RectF r = (RectF) o;
        return new Rect(r.left, r.top, r.right, r.bottom);
    }
    /**
     * get RectF (view) from Rect model.
     * @param r
     * @return
     */
    public static RectF getRectF(Rect r) {
        if ( r == null ) { return null; }
        return new RectF(r.left,r.top,r.right,r.bottom);
    }
}
