package io.github.rosemoe.editor.core;

import org.junit.Test;

import io.github.rosemoe.editor.core.grid.BaseCell;
import io.github.rosemoe.editor.core.grid.Cell;

import static org.junit.Assert.*;

public class CellTest {
    @Test
    public void testCell() {
        {
            Cell c = new BaseCell(0, 2);
            assertTrue(c.column == 0);
            assertTrue(c.getStart() == 0);
            assertTrue(c.getStop() == 2);
        }
        {
            BaseCell c = new BaseCell(2, 2);
            assertTrue(c.column == 2);
            assertTrue(c.getStart() == 2);
            assertTrue(c.getStop() == 4);
        }
    }
}