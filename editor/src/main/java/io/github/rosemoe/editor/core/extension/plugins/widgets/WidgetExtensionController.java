package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.graphics.Canvas;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionController;

public class WidgetExtensionController extends SystemExtensionController {
    public WidgetExtensionController(CodeEditor editor) {
        super(editor);
    }

    /**
     * Feed the widget with new data.
     */
    public final void refresh(Canvas canvas, Object ...args) {
        if ( isDisabled() ) {
            return ;
        }
        handleRefresh(canvas,args);
    }
    protected void handleRefresh(Canvas canvas, Object ...args) {
    }
}
