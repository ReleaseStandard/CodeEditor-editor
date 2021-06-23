package io.github.rosemoe.editor.core.extension.plugins.widgets.linenumberpanel;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class LineNumberPanelModelTest {

    Random r = new Random();

    @Test
    public void computeAndGetText() {
        LineNumberPanelModel a = new LineNumberPanelModel();
        int c = a.computeAndGetText(10);
        assertTrue(c == 2);
        for(int b = 0; b < 20 ; b = b + 1) {
            assertTrue(a.computeAndGetText(r.nextInt() % 100) <= 3);
        }
    }
}