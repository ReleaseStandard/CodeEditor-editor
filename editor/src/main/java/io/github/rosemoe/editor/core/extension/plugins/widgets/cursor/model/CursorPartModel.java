package io.github.rosemoe.editor.core.extension.plugins.widgets.cursor.model;

import android.graphics.RectF;

import io.github.rosemoe.editor.core.extension.plugins.widgets.cursor.controller.CursorPartController;

/**
 * Hold data for a cursor part.
 */
public class CursorPartModel {
    /**
     * RowModel position
     */
    public int row;

    /**
     * Center x offset
     */
    public float centerX;

    /**
     * Handle rectangle
     */
    public RectF outRect;

    /**
     * Draw as insert cursor
     */
    public boolean insert;

    public int handleType = -1;

    public void initialize(int row, float centerX, RectF outRect, boolean insert) {
        this.row = row;
        this.centerX = centerX;
        this.outRect = outRect;
        this.insert = insert;
    }
    public CursorPartModel(int row, float centerX, RectF outRect, boolean insert) {
        initialize(row,centerX,outRect,insert);
    }

    public CursorPartModel(int row, float centerX, RectF outRect, boolean insert, int handleType) {
        initialize(row,centerX,outRect,insert);
        this.handleType = handleType;
    }
}
