package io.github.rosemoe.editor.core.grid.instances.color;

import org.junit.Test;

import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class SpanMapTest {

    Random r = new Random();
    @Test
    public void appendLine() {
        SpanMap s = new SpanMap();
        int sz = r.nextUint(400);
        s.appendLines(sz);
        assertTrue(sz == s.size());
    }

    @Test
    public void insertLines() {
    }
}












