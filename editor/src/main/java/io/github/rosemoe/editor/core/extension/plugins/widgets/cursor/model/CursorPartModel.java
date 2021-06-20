package io.github.rosemoe.editor.core.extension.plugins.widgets.cursor.model;
import android.graphics.RectF;

import io.github.rosemoe.editor.core.extension.plugins.widgets.cursor.view.CursorPartView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.controller.UserInputController;
import io.github.rosemoe.editor.core.model.Rect;

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
    public Rect outRect;

    /**
     * Draw as insert cursor
     */
    public boolean insert;

    /**
     * Handle type.
     * TODO WD
     */
    public int handleType = UserInputController.SelectionHandle.NONE;


    /**
     * Initialize model
     * @param row
     * @param centerX
     * @param outRect
     * @param insert
     */
    private void initialize(int row, float centerX, Rect outRect, boolean insert) {
        this.row = row;
        this.centerX = centerX;
        this.outRect = outRect;
        this.insert = insert;
    }
    public CursorPartModel(int row, float centerX, Rect outRect, boolean insert) {
        initialize(row,centerX, outRect,insert);
    }

    public CursorPartModel(int row, float centerX, Rect outRect, boolean insert, int handleType) {
        initialize(row,centerX,outRect,insert);
        this.handleType = handleType;
    }
}
