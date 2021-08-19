package io.github.rosemoe.editor.core.extension.extensions.color.analysis.spans;

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

    @Test
    public void testRecycler() {
        int sz = 50 + r.nextUint(500);
        for( int a = 0; a < sz ; a = a + 1 ) {
            @Jailbreak Span s = new @Jailbreak Span(r.nextUint(100), 0xFFFF0000);
            s.recycle();
            s.color = 0xFFFF00;
            int col = r.nextUint(500);
            assertTrue(Span.obtain(col, 0xFF00FF00) == s);
            assertTrue(s.color == 0xFF00FF00);
            assertTrue(s.column == col);
        }

    }

}