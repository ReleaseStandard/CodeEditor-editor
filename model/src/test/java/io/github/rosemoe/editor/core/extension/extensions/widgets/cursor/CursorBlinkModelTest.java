package io.github.rosemoe.editor.core.extension.extensions.widgets.cursor;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class CursorBlinkModelTest {

    Random r = new Random();
    @Test
    public void onSelectionChanged() {
        for(int a = 0; a < 10; a = a + 1) {
            CursorBlinkModel b = new CursorBlinkModel(0);
            b.onSelectionChanged();
        }
    }

    @Test
    public void setPeriod() {
        CursorBlinkModel a = new CursorBlinkModel(0);
        for(int b = 0; b < 100; b = b+1) {
            int c = r.nextInt();
            a.setPeriod(c);
            assertTrue(a.period == c);
        }
    }
}