package io.github.rosemoe.editor.core;

import junit.framework.TestCase;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

public class RectTest extends TestCase {

    @Test
    public void testInit() {
        Random r = new Random();
        for(int a = 0; a < 20; a = a +1 ) {
            Integer b[] = new Integer[]{r.nextInt(), r.nextInt(), r.nextInt(), r.nextInt()};
            Rect rect = new Rect(b[0],b[1],b[2],b[3]);
            assertTrue(rect.left == b[0] && rect.top == b[1] && rect.right == b[2] && rect.bottom == b[3]);
        }
    }
}