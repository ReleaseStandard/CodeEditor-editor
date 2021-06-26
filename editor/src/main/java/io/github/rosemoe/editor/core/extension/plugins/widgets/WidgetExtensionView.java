package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class WidgetExtensionView extends LinearLayout {
    public void initialize() {
        setWillNotDraw(false); // https://stackoverflow.com/questions/10727225/drawing-something-on-my-linearlayout
    }
    public WidgetExtensionView(Context context) {
        super(context);
    }

    public WidgetExtensionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WidgetExtensionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WidgetExtensionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public static void drawColor(Canvas canvas, int color, RectF rect) {
        if (color != 0) {
            Paint mPaint = new Paint();
            mPaint.setColor(color);
            canvas.drawRect(rect, mPaint);
        }
    }
}
