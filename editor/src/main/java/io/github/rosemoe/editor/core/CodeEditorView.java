package io.github.rosemoe.editor.core;

import android.graphics.Canvas;
import android.graphics.Rect;

import io.github.rosemoe.editor.core.util.shortcuts.A;

/**
 * android view of CodeEditor.
 */
public class CodeEditorView {
    CodeEditor editor = null;
    public CodeEditorView(CodeEditor editor) {
        this.editor = editor;
    }
    public void drawBackground(Canvas canvas) {
        editor.drawColor(canvas, editor.colorManager.getColor("wholeBackground"), A.getRectF(editor.model.background));
    }
}
