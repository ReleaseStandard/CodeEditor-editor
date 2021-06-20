package io.github.rosemoe.editor.core.model;

/**
 * A Rect on the screen.
 *  underlying interface could be :
 *   - RectF for android.
 */
public class Rect {
    public float bottom = 0;
    public float top = 0;
    public float right = 0;
    public float left = 0;

    public Rect(float left, float top, float right, float bottom) {
        this.bottom = bottom;
        this.left = left;
        this.top = top;
        this.right = right;
    }
    public Rect() {

    }
}
