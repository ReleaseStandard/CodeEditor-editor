package io.github.rosemoe.editor.core;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class IntPairTest {

    Random r = new Random();

    @Test
    public void pack() {
        for(int a = 0; a < 100 ; a = a + 1) {
            int firstP = r.nextUint();
            int secondP = r.nextUint();
            assertTrue(IntPair.pack(firstP, secondP) >= firstP);
        }
    }

    @Test
    public void getFirst() {
        for(int a = 0; a < 100 ; a = a + 1) {
            int firstP = r.nextUint();
            int secondP = r.nextUint();
            long pack = IntPair.pack(firstP, secondP);
            assertTrue(IntPair.getFirst(pack) == firstP);
        }
    }

    @Test
    public void getSecond() {
        for(int a = 0; a < 100; a = a + 1) {
            int firstP = r.nextUint();
            int secondP = r.nextUint();
            long pack = IntPair.pack(firstP, secondP);
            assertTrue(IntPair.getSecond(pack) == secondP);        }
    }
}