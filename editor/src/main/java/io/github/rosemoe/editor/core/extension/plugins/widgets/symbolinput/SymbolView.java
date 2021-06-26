package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput.handles.SymbolViewHandle;

public class SymbolView extends androidx.appcompat.widget.AppCompatButton {

    SymbolViewHandle handles = new SymbolViewHandle();

    public SymbolView(CodeEditor editor, Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handles.handleOnClick();
            }
        });
    }
}

