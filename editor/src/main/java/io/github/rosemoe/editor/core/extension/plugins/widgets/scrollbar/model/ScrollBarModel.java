package io.github.rosemoe.editor.core.extension.plugins.widgets.scrollbar.model;

import io.github.rosemoe.editor.core.Rect;

public class ScrollBarModel {
    public Rect bar = new Rect();
    public Rect barTrack = new Rect();
    public boolean holding = false;
    public final static String O_VERTICAL = "vertical";
    public String orientation = O_VERTICAL;

    /**
     *
     * @param all
     * @param length
     * @param offsetX
     * @param offsetY
     * @param eWidth
     * @param eHeight
     * @param dpUnit
     * @return centerY value
     */
    public float computeBarRect(float all, int offsetX, int offsetY, int eWidth, int eHeight, float dpUnit) {
        boolean isVertical = orientation.equals(O_VERTICAL);
        int page = isVertical ? eHeight : eWidth;
        float length = isVertical ? (page / all * eHeight) : page / all * eWidth;

        if ( isVertical ) {
            float margin = 0;
            if ( length < dpUnit * 30 ) {
                length = dpUnit * 30;
                margin = (offsetY + page / 2f) / all * (eHeight - length);
            } else {
                margin = offsetY / all * eHeight;
            }
            if ( holding ) {
                return margin + length / 2f;
            }
            bar.top = margin;
            bar.bottom = margin + length;
            bar.right = eWidth;
            bar.left = eWidth - dpUnit * 10;
        } else {
            float margin = offsetX / all * eWidth;
            bar.top = eHeight - dpUnit * 10;
            bar.bottom = eHeight;
            bar.right = margin + length;
            bar.left = margin;
        }
        return 0;
    }
}
