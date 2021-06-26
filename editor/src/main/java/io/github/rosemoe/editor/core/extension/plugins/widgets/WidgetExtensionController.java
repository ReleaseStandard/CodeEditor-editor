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
public abstract class WidgetExtensionController extends SystemExtensionController {

    public Class builderClass = null;
    public WidgetExtensionView view;
    public WidgetExtensionController(CodeEditor editor) {
        super(editor);
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if( view == null ) {
            Logger.debug("View is not ready, cannot paint");
            return;
        }

        // part of the view
        if ( state ) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        view.invalidate();
    }

    /**
     * Feed the widget with new data, it will modify model and view.
     */
    public final void refresh(Canvas canvas, Object... args) {
        if (isDisabled()) {
            return;
        }
        handleRefresh(canvas, args);
    }

    protected void handleRefresh(Canvas canvas, Object... args) {
    }

    /**
     * Method that attach View to the widget.
     * @param v View to attach.
     */
    public abstract void attachView(View v);

    /**
     * Clear data in the model and invalidate the view.
     */
    public void clear() {
    }

    /**
     * Get the width of the widget.
     * @return
     */
    public float width() {
        return 0;
    }

    /**
     * An other method to init a widget.
     */
    public void initFromAttrs(){}
}
