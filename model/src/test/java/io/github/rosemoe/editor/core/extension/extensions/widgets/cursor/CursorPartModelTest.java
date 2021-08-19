package io.github.rosemoe.editor.core.extension.extensions.widgets.cursor;

import org.junit.Test;

import io.github.rosemoe.editor.core.Rect;
import io.github.rosemoe.editor.core.extension.extensions.widgets.userinput.UserInputModel;

public class CursorPartModelTest {
    @Test
    public void instanciate() {
        int row = 10;
        float centerX = 0;
        Rect outRect = new Rect();
        boolean insert = false;
        int handleType = UserInputModel.NONE;
        CursorPartModel a = new CursorPartModel(row, centerX, outRect, insert, handleType);
    }
}