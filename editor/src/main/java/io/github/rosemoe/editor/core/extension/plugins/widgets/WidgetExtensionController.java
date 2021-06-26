package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.graphics.Canvas;
import android.view.View;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionController;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * A widget is a subtype of extension.
 * it has facilities for painting on the screen.
 */
public class WidgetExtensionController extends SystemExtensionController {
    public WidgetExtensionView view;
    public WidgetExtensionController(CodeEditor editor) {
        super(editor);
    }

    /**
     * Feed the widget with new data.
     */
    public final void refresh(Canvas canvas, Object... args) {
        if (isDisabled()) {
            return;
        }
        handleRefresh(canvas, args);
    }

    protected void handleRefresh(Canvas canvas, Object... args) {
    }

    public void clear() {
    }

    /**
     * set text size inside this widget.
     */
    public void setTextSize(float size) {

    }

    /**
     * Get the width of the widget.
     * @return
     */
    public float width() {
        return 0;
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if( view == null ) {
            Logger.debug("View is not ready, cannot paint");
            return;
        }
        if ( state ) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        view.invalidate();
    }
}
