package io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.controller;

import android.graphics.Canvas;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.CursorPartModel;
import io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.view.CursorPartView;
import io.github.rosemoe.editor.core.util.shortcuts.A;

public class CursorPartController {

    public CursorPartView view;
    public CursorPartModel model;

    public CursorPartController(CodeEditor editor, int row, float centerX, Object outRect, boolean insert) {
        view = new CursorPartView(editor);
        model = new CursorPartModel(row,centerX, A.getRect(outRect),insert);
    }
    public CursorPartController(CodeEditor editor, int row, float centerX, Object outRect, boolean insert, int handleType) {
        view = new CursorPartView(editor);
        model = new CursorPartModel(row,centerX,A.getRect(outRect),insert,handleType);
    }

    /**
     * Paint a cursor subpart.
     */
    public void refresh(Canvas canvas, Object ...args) {
        view.exec(canvas, model.centerX, model.row, model.outRect, model.insert, model.handleType);
    }
}
