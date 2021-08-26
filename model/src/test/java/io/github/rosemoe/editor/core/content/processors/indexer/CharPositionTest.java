package io.github.rosemoe.editor.core.content.processors.indexer;

import org.junit.Test;

import io.github.rosemoe.editor.core.content.processors.indexer.CharPosition;
import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class CharPositionTest {
    Random r = new Random();

    @Test
    public void cloneTest() {
        CharPosition cp = new CharPosition();
        for(int a = 0; a < 50; a = a + 1) {
            cp.column = r.nextInt();
            cp.index = r.nextInt();
            cp.line = r.nextInt();
            CharPosition clone = cp.clone();
            assertTrue(cp.equals(clone));
        }
    }
}