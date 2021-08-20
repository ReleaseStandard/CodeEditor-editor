package io.github.rosemoe.editor.core.grid.instances.color;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

public class SpanCellTest {

    Random r = new Random();

    @Test
    public void clear() {
        @Jailbreak SpanCell s = new @Jailbreak SpanCell(0,0xFFFF0000);
        s.clear();
    }

}