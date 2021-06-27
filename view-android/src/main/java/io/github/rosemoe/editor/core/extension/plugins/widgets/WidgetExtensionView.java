package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import io.github.rosemoe.editor.core.Adaptater;
import io.github.rosemoe.editor.core.Rect;

public abstract class WidgetExtensionView extends LinearLayout {

    /**
     * Defines which class control the view (useful for instanciation from xml).
     */
    public Class controllerBuilder = null;
    public String controllerName = "";
    public void initialize() {
        setWillNotDraw(false); // https://stackoverflow.com/questions/10727225/drawing-something-on-my-linearlayout
    }
    public WidgetExtensionView(Context context) {
        super(context);
        setControllerName(context);
    }

    public WidgetExtensionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setControllerName(context);
    }

    public WidgetExtensionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setControllerName(context);
    }

    public WidgetExtensionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setControllerName(context);
    }
    public static void drawColor(Object canvasContainer, int color, Rect rect) {
        Canvas canvas = (Canvas) canvasContainer;
        if (color != 0) {
            Paint mPaint = new Paint();
            mPaint.setColor(color);
            canvas.drawRect(Adaptater.getRectF(rect), mPaint);
        }
    }

    /**
     * Tell which view instanciate which controller.
     * ex: io.github.rosemoe. ... .widgets.linenumber.LineNumberPanelController
     * @param ctx
     */
    public abstract void setControllerName(Context ctx);
}
