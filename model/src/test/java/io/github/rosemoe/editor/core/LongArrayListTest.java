package io.github.rosemoe.editor.core;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class LongArrayListTest {
    Random r = new Random();

    @Test
    public void add() {
        LongArrayList lal = new LongArrayList();
        int sz = r.nextUint()%20;
        for(int a = 0; a < sz ; a = a + 1) {
            lal.add(0);
        }
        assertTrue(lal.size() == sz);
    }

    @Test
    public void size() {
        LongArrayList lal = new LongArrayList();
        int sz = r.nextUint()%20;
        for(int a = 0; a < sz ; a = a + 1) {
            lal.add(0);
        }
        assertTrue(lal.size() == sz);
    }

    @Test
    public void get() {
        LongArrayList lal = new LongArrayList();

        int sz = r.nextUint()%500;
        for(int a = 0; a < sz; a = a + 1) {
            lal.add(r.nextUint());
        }
        for(int a = 0; a < sz; a = a + 1) {
            lal.get(a);
        }
        try {
            lal.get(-1);
            assertTrue(false);
        } catch (ArrayIndexOutOfBoundsException e) {
            assertTrue(true);
        }
        LongArrayList lal1 = new LongArrayList();
        assertTrue(sz == lal.size());
    }

    @Test
    public void clear() {
        LongArrayList lal = new LongArrayList();
        for(int a = 0; 10 + a < r.nextUint() % 100; a = a + 1) {
            lal.add(r.nextUint());
        }
        assertFalse(lal.size() == 0);
        lal.clear();
        assertTrue(lal.size() == 0);
        lal.clear();
        assertTrue(lal.size() == 0);
    }
}