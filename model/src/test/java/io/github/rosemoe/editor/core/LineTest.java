package io.github.rosemoe.editor.core;

import org.junit.Test;

import io.github.rosemoe.editor.core.grid.BaseCell;
import io.github.rosemoe.editor.core.grid.Cell;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.util.Random;

import static io.github.rosemoe.editor.core.grid.Line.SPAN_SPLIT_INVALIDATE;
import static io.github.rosemoe.editor.core.grid.Line.SPAN_SPLIT_SPLITTING;
import static org.junit.Assert.*;

public class LineTest {

    Random r = new Random();

    @Test
    public void testPut() {
        {
            // *--
            Line l = new Line();
            BaseCell c1 = new BaseCell(0, 1);
            BaseCell c2 = new BaseCell(1, 2);
            l.put(c1);
            l.put(c2);
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size == 1);
            assertTrue(l.get(1).size == 2);
        }
        {
            // ------**+
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            assertTrue(s.size() == 3);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 2);
            assertTrue(s.get(8).size == 1);
        }
    }

    @Test
    public void testSplitBug1() {
        {
            // +++-|--m
            //
            // +++-
            // --m
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            s.put(new BaseCell(0,3));
            s.put(new BaseCell(3,3));
            s.put(new BaseCell(6,1));
            Line[] lines = s.split(4);
            assertTrue(lines[0].size() == 2);
            assertTrue(lines[0].get(0).size == 3);
            assertTrue(lines[0].get(3).size == 1);
            assertTrue(lines[1].size() == 2);
            assertTrue(lines[1].get(0).size == 2);
            assertTrue(lines[1].get(1).size == 1);
        }
        {
            // +++-|--m
            //
            // +++
            //   m
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(new BaseCell(0,3));
            s.put(new BaseCell(3,3));
            s.put(new BaseCell(6,1));
            Line[] lines = s.split(4);
            assertTrue(lines[0].size() == 2);
            assertTrue(lines[0].get(0).size == 3);
            assertTrue(lines[0].get(1).size == 1);
            assertTrue(lines[1].size() == 2);
            assertTrue(lines[1].get(0).size == 2);
            assertTrue(lines[1].get(1).size == 1);
        }
    }

    @Test
    public void testValidConcurrentEnvironment() {
        // *--+
        Line l = new Line() {{
           put(0,new BaseCell(0,1));
           put(1, new BaseCell(1,2));
           put(2, new BaseCell(3,1));
        }};
        int nt = r.nextUint(20);
        Thread[] threads = new Thread[nt];
        for(int a = 0; a < nt; a=a+1) {
            threads[a] = new Thread() {
                @Override
                public void run() {
                    switch (r.nextUint(3)) {
                        case 0: {
                            int newSZ = r.nextUint(30);
                            for (int b = 0; b < newSZ ; b=b+1) {
                                for (Cell i : l) {
                                    i = i;
                                }
                            }
                        } break;
                        case 1: {
                            int newSZ = r.nextUint(30);
                            for (int b = 0; b < newSZ; b=b+1) {
                                l.get(0);
                            }
                        } break;
                        case 2: {
                            int newSZ = r.nextUint(30);
                            for(int b = 0; b < newSZ; b=b+1) {
                                l.put(new BaseCell(b,1));
                            }
                        }
                    }
                }
            };
            threads[a].start();
        }
        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}