package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.graphics.Canvas;

import io.github.rosemoe.editor.core.CodeEditor;

/**
 * This type of widget view is some pixel to draw on an instanciated canvas, ex: CodeEditor's View.
 */
public abstract class WidgetCanvasPartView extends WidgetView {

    /**
     * Paint the canvas part on the canvas.
     * @param canvas
     * @param editor
     */
    public abstract void paint(Canvas canvas, CodeEditor editor);

    public final CodeEditor editor;
    public WidgetCanvasPartView(CodeEditor editor) {
        this.editor = editor;
    }
}
