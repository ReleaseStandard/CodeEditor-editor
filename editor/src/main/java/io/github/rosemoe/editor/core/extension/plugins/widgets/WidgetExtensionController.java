package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.graphics.Canvas;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionController;

/**
 * A widget is a subtype of extension.
 * it has facilities for painting on the screen.
 */
public class WidgetExtensionController extends SystemExtensionController {
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

    public static final int A_NONE = 0;
    public static final int A_LEFT = 1;
    public static final int A_RIGHT = 2;
    public static final int A_TOP = 4;
    public static final int A_BOTTOM = 8;
    public static final int A_STARTOF = 16;
    public static final int A_ENDOF = 32;
    public int align = A_LEFT;
    public String alignOn = null;
    /**
     * Set alignement of the widget.
     * @param type
     * @param value
     */
    public void setAlignment(int type, String value) {
            align = type;
            if ( value != null ) {
                alignOn = value;
            }
    }
    public void setAlignment(int type) {
        setAlignment(type, null);
    }
}
