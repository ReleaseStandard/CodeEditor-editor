package io.github.rosemoe.editor.core.extension.plugins.widgets.scrollbar;

import android.graphics.Canvas;
import android.graphics.RectF;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetCanvasPartView;

public class ScrollBarView extends WidgetCanvasPartView {
    public RectF bar = new RectF();
    public RectF barTrack = new RectF();

    public ScrollBarView(CodeEditor editor) {
        super(editor);
    }

    @Override
    public void paint(Canvas canvas, CodeEditor editor) {

    }
}
