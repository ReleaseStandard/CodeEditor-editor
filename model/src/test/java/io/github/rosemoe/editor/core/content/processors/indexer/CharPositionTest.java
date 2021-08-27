package io.github.rosemoe.editor.core.content.processors.indexer;

import org.checkerframework.checker.units.qual.C;
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
            cp.column = r.nextUint();
            cp.index = r.nextUint();
            cp.line = r.nextUint();
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
            assertTrue(CharPosition.nearest(cp1,cp2,cp3).equals(cp1));
        }
        {
            // |+      |
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(2);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3).equals(cp1));
        }
        {
            // |     +|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(10);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3).equals(cp3));
        }
        {
            // |     +|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(9);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3).equals(cp3));
        }
        {
            // |+|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(0);
            CharPosition cp3 = new CharPosition(0);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3).equals(cp1));
        }
        {
            // |+|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(0);
            CharPosition cp3 = new CharPosition(1);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3).equals(cp1));
        }
        {
            // | +|
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(1);
            CharPosition cp3 = new CharPosition(1);
            assertTrue(CharPosition.nearest(cp1,cp2,cp3).equals(cp3));
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
            assertTrue(CharPosition.nearest(cp3,cp2,cp1).equals(cp3));
        }
        {
            // |+      |
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(2);
            CharPosition cp3 = new CharPosition(10);
            assertTrue(CharPosition.nearest(cp3,cp2,cp1).equals(cp1));
        }
    }

    @Test
    public void testEquals() {
        {
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(2);
            assertFalse(cp1.equals(cp2));
        }
        {
            CharPosition cp1 = new CharPosition(2);
            CharPosition cp2 = new CharPosition(1);
            assertFalse(cp1.equals(cp2));
        }
        {
            CharPosition cp1 = new CharPosition(0);
            CharPosition cp2 = new CharPosition(0);
            cp1.dump();
            cp2.dump();
            assertTrue(cp1.equals(cp2));
        }
        {
            CharPosition cp1 = new CharPosition(0,0);
            CharPosition cp2 = new CharPosition(0,1);
            assertFalse(cp1.equals(cp2));
        }
        {
            CharPosition cp1 = new CharPosition(0,0);
            CharPosition cp2 = new CharPosition(0,0);
            assertTrue(cp1.equals(cp2));
        }
        {
            CharPosition cp1 = new CharPosition(0);
            assertTrue(cp1.equals(0));
        }
        {
            CharPosition cp1 = new CharPosition(6);
            assertFalse(cp1.equals(0));
        }
    }

    @Test
    public void testIsValid() {
        {
            CharPosition cp1 = new CharPosition(0);
            assertTrue(cp1.isValid());
        }
        {
            CharPosition cp1 = new CharPosition(0, 0);
            assertTrue(cp1.isValid());
        }
        {
            CharPosition cp1 = new CharPosition(0, CharPosition.INVALID);
            assertFalse(cp1.isValid());
        }
        {
            CharPosition cp1 = new CharPosition();
            assertFalse(cp1.isValid());
        }
        {
            CharPosition cp1 = new CharPosition(CharPosition.INVALID, 0);
            assertFalse(cp1.isValid());
        }
        {
            CharPosition cp1 = new CharPosition(CharPosition.INVALID);
            assertFalse(cp1.isValid());
        }
    }
}