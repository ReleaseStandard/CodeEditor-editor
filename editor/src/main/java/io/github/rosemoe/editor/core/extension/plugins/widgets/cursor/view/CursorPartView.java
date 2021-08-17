package io.github.rosemoe.editor.core.extension.plugins.widgets.cursor.view;

import android.graphics.Canvas;
import android.graphics.RectF;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionCanvasPartView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.UserInputModel;
import io.github.rosemoe.editor.core.Rect;
import io.github.rosemoe.editor.core.util.shortcuts.A;

public class CursorPartView extends SystemExtensionCanvasPartView {
    public CursorPartView(CodeEditor editor) {
        super(editor);
    }

    @Override
    public void paint(Canvas canvas, CodeEditor editor) {

    }

    /**
     * Execute painting on the given editor and canvas
     */
    public void exec(Canvas canvas, float centerX, int row, Rect handle, boolean insert, int handleType) {
        if (!insert || editor.cursor.blink == null || editor.cursor.blink.model.visibility) {
            // OK
            RectF mRect = new RectF();
            mRect.top = editor.getRowTop(row) - editor.getOffsetY();
            mRect.bottom = editor.getRowBottom(row) - editor.getOffsetY();
            mRect.left = centerX - editor.cursor.mInsertSelWidth / 2f;
            mRect.right = centerX + editor.cursor.mInsertSelWidth / 2f;
            editor.drawColor(canvas, editor.model.colorManager.getColor("selectionInsert"), mRect);
        }
        if (handle != null) {
            drawHandle(canvas, row, centerX, handle, handleType);
        }
    }
    /**
     * Draw a handle.
     * The handle can be insert handle,left handle or right handle
     *
     * @param canvas     The Canvas to draw handle
     * @param row        The row you want to attach handle to its bottom (Usually the selection line)
     * @param centerX    Center x offset of handle
     * @param rect The rect of handle this method drew
     * @param handleType The selection handle type (LEFT, RIGHT,BOTH or -1)
     */
    private void drawHandle(Canvas canvas, int row, float centerX, Rect rect, int handleType) {
        RectF resultRect = A.getRectF(rect);
        float radius = editor.mDpUnit * 12;

        // TODO WD
        if (handleType != UserInputModel.NONE && handleType == editor.userInput.getTouchedHandleType()) {
            radius = editor.mDpUnit * 16;
        }

        float top = editor.getRowBottom(row) - editor.getOffsetY();
        float bottom = top + radius * 2;
        float left = centerX - radius;
        float right = centerX + radius;
        if (right < 0 || left > editor.view.getWidth() || bottom < 0 || top > editor.view.getHeight()) {
            resultRect.setEmpty();
            return;
        }
        resultRect.left = left;
        resultRect.right = right;
        resultRect.top = top;
        resultRect.bottom = bottom;
        editor.mPaint.setColor(editor.model.colorManager.getColor("selectionHandle"));
        canvas.drawCircle(centerX, (top + bottom) / 2, radius, editor.mPaint);
    }
}
