package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.Nullable;

import io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput.handles.SymbolViewHandle;


/**
 * View for an individual Symbol.
 */
public class SymbolView extends androidx.appcompat.widget.AppCompatButton {

    SymbolViewHandle handles = new SymbolViewHandle();

    public SymbolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(0,0,0,0);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handles.handleOnClick();
            }
        });
    }
    @Override
    public void onDraw(Canvas canvas) {
        handles.handleOnDraw(canvas);
        super.onDraw(canvas);
    }
}

