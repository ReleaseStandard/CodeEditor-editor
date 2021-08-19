package io.github.rosemoe.editor.core.grid.instances.color;

import org.junit.Test;

import io.github.rosemoe.editor.core.grid.instances.color.Span;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class SpanTest {

    Random r = new Random();

    @Test
    public void clear() {
        @Jailbreak Span s = new @Jailbreak Span(0,0xFFFF0000);
        s.clear();
    }

}