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

    @Test
    public void testCompareTo() {
        {
            CharPosition cp1 = new CharPosition(0, 0);
            CharPosition cp2 = new CharPosition(0, 5);
            assertTrue(""+cp1.compareTo(cp2),cp1.compareTo(cp2) < 0);
        }
        {
            CharPosition cp1 = new CharPosition(0, 5);
            CharPosition cp2 = new CharPosition(0, 0);
            assertTrue(cp1.compareTo(cp2) > 0);
        }
        {
            CharPosition cp1 = new CharPosition(1, 0);
            CharPosition cp2 = new CharPosition(0, 0);
            assertTrue(cp1.compareTo(cp2) > 0);
        }
        {
            CharPosition cp1 = new CharPosition(0, 0);
            CharPosition cp2 = new CharPosition(1, 0);
            assertTrue(cp1.compareTo(cp2) < 0);
        }
        {
            CharPosition cp1 = new CharPosition(0, 0);
            CharPosition cp2 = new CharPosition(0, 0);
            assertTrue(cp1.compareTo(cp2) == 0);
        }
        {
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(0);
            assertTrue(cp1.compareTo(cp2) == 0);
        }
        {
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(1);
            assertTrue(cp1.compareTo(cp2) < 0);
        }
        {
            CharPosition cp1 = new CharPosition(1);
            CharPosition cp2 = new CharPosition(0);
            assertTrue(cp1.compareTo(cp2) > 0);
        }
        {
            CharPosition cp1 = new CharPosition(0);
            assertTrue(cp1.compareTo(0)==0);
        }
        {
            CharPosition cp1 = new CharPosition(10);
            assertTrue(cp1.compareTo(0)>0);
        }
        {
            CharPosition cp1 = new CharPosition(10);
            assertTrue(cp1.compareTo(11)<0);
        }
    }

    @Test
    public void nearest() {
        {
            // |   +   |
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(5);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3) == cp1);
        }
        {
            // |+      |
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(2);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3) == cp1);
        }
        {
            // |     +|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(10);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3) == cp3);
        }
        {
            // |     +|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(9);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3) == cp3);
        }
        {
            // |+|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(0);
            CharPosition cp3 = new CharPosition(0);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3) == cp1);
        }
        {
            // |+|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(0);
            CharPosition cp3 = new CharPosition(1);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3) == cp1);
        }
        {
            // | +|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(1);
            CharPosition cp3 = new CharPosition(1);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3) == cp3);
        }
        {
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(1);
            assertTrue(CharPosition.nearest(cp1,cp2,null)==cp1);
        }
        {
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(1);
            assertTrue(CharPosition.nearest(null,cp1,cp2)==cp2);
        }
        {
            assertTrue(CharPosition.nearest(null,null,null)==null);
        }
        {
            // |     +|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(9);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp3,cp2,cp1) == cp3);
        }
        {
            // |+      |
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(2);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp3,cp2,cp1) == cp1);
        }
    }
}