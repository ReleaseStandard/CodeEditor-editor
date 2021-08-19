package io.github.rosemoe.editor.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {
    @Test
    public void testCell() {
        {
            Cell<Integer> c = new Cell<Integer>(0, 2);
            assertTrue(c.column == 0);
            assertTrue(c.getStart() == 0);
            assertTrue(c.getStop() == 2);
        }
        {
            Cell<Integer> c = new Cell<Integer>(2, 2);
            assertTrue(c.column == 2);
            assertTrue(c.getStart() == 2);
            assertTrue(c.getStop() == 4);
        }
    }
}