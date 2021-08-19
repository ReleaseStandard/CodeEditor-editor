package io.github.rosemoe.editor.core.extension.extensions.widgets.scrollbar;

import java.util.ArrayList;
import java.util.Vector;

import io.github.rosemoe.editor.core.Rect;

/**
 * A scrollbar is modelled as:
 *  - a bar
 *  - a bartrack
 */
public class ScrollBarModel {

    public Rect bar = new Rect();
    public Rect barTrack = new Rect();
    public float length = 0;                    // length of the scrollbar
    public float width = 0;                     // width of the scrollbar
    public boolean holding = false;             // is user holding the bar
    public final static int O_VERTICAL = 0;
    public final static int O_HORIZONTAL = 90;
    public float orientation = O_VERTICAL;


    /**
     * Compute a rectangle position on the screen from given:
     *  params, barWidth, barTrackLength, orientation
     *
     * @param lenAllContent length of all the content to which the scrollbar is connected.
     *                      ex: length of the content in X
     *                          or length of the content in Y
     * @param offset offset in the scrollbar.
     * @param position position of the scroll bar on its 1D axis (ex: vertical, horizontal)
     * @return
     */
    public float prepareBarRect(float lenAllContent, int offset, int position) {
        float scrollPercent = offset / lenAllContent;
        float barLength = (length * length) / lenAllContent;
        bar.top = scrollPercent * (length - barLength);
        bar.bottom = bar.top + barLength;
        bar.left = position;
        bar.right = position + width;
        bar.orientation = orientation;
        bar.rotate(orientation);
        return 0;
    }
    /**
     * Compute rectangle of the track bar.
     * @param eWidth
     * @param eHeight
     */
    public void prepareTrackBarRect(int eWidth, int eHeight) {
        if (holding) {
            barTrack.top  = 0;
            barTrack.right = eWidth;
            barTrack.bottom = eHeight;
            barTrack.left = eWidth - width;
            barTrack.orientation = orientation;
            bar.rotate(orientation);
        }
    }
    /**
     * Rotation counter clock wise of vectors.
     * @param deg andle in degree
     */
    public void matrixRotation(ArrayList<Vector<Float>> coord, float deg) {
        int i = 0;
        for(Vector<Float> c : coord) {
            float x = c.get(0);
            float y = c.get(1);
            float xx = (float) (x * Math.cos(Math.toRadians(deg)) - y * Math.sin(Math.toRadians(deg)));
            float yy = (float) (x * Math.sin(Math.toRadians(deg)) + y * Math.cos(Math.toRadians(deg)));
            c.set(0, xx);
            c.set(1, yy);
        }
    }
    public void matrixRotation(Vector<Float> coord, float deg) {
        matrixRotation(new ArrayList<Vector<Float>>(){{
            add(coord);
        }}, deg);
    }

    /**
     * Get maximum offset that could be scrolled inside this.
     * @return
     */
    public float getMaxScroll() {
        return length - bar.length();
    }
    public void dump() {
        dump("");
    }
    public void dump(String off) {
        String noff = "     " + off;
        System.out.println(off  + "{");
        System.out.println(noff + "barLength=" + bar.length());
        System.out.println(noff + "barTrackLength=" + length);
        System.out.println(noff + "width=" + width);
        System.out.println(noff + "holding=" + holding);
        System.out.println(noff + "bar=");
        bar.dump(off);
        System.out.println(noff + "barTrack=");
        barTrack.dump(off);
        System.out.println(off  + "}");
    }
}
