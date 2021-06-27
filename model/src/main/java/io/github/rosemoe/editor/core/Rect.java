package io.github.rosemoe.editor.core;

/**
 * A Rect on the screen.
 *  underlying interface could be :
 *   - RectF for android.
 *
 * It does not retains informations on the position on the screen.
 */
public class Rect {

    public float bottom = 0;
    public float top = 0;
    public float right = 0;
    public float left = 0;
    public float orientation = 0;

    public static Rect DEFAULT() { return new Rect(); }
    public Rect(float left, float top, float right, float bottom) {
        this.bottom = bottom;
        this.left = left;
        this.top = top;
        this.right = right;
    }

    public Rect() {

    }
    public void rotate(float degrees) {
        rotateRight(degrees);
    }
    /**
     * rotate the rectangle.
     * @param degrees at the time, it must be *90 degree angle.
     */
    private void rotateRight(float degrees) {
        float aux;
        for(int a = 0; a < degrees/90; a = a + 1) {
            aux = right; right = top;
            top = left; left = bottom;
            bottom = aux;
        }
    }
    private void rotateLeft(float degrees) {
        rotateRight(-degrees);
    }
    /**
     * return length of the Rect in its main orientation.
     * @return
     */
    public float length() {
        if ( (orientation % 90) == 0 ) {
            return left - right;
        } else {
            return bottom - top;
        }
    }
    public float width() {
        if ( (orientation % 90) != 0 ) {
            return left - right;
        } else {
            return bottom - top;
        }
    }
    public void dump() {
        dump("");
    }
    public void dump(String o) {
        String no = "    " + o;
        System.out.println(o + "{" +
                           no + "bottom=" + bottom +
                           no + "top=" + top +
                           no + "left=" + left +
                           no + "right=" + right +
                           o + "}");
    }
    public void clear() {
        bottom = 0;
        top = 0;
        left = 0;
        right = 0;
        orientation = 0;
    }
}
