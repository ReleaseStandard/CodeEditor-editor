package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class WidgetExtensionView extends View {
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
