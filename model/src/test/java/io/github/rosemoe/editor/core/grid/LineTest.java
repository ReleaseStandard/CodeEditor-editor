package io.github.rosemoe.editor.core.grid;

import org.junit.Test;

import io.github.rosemoe.editor.core.grid.instances.color.Span;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static io.github.rosemoe.editor.core.grid.Line.SPAN_SPLIT_EXTENDS;
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
            assertTrue(lines[1].get(2).size == 1);
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
            assertTrue(lines[0].get(3).size == 1);
            assertTrue(lines[0].get(3).enabled == false);
            assertTrue(lines[1].size() == 2);
            assertTrue(lines[1].get(0).size == 2);
            assertTrue(lines[1].get(0).enabled == false);
            assertTrue(lines[1].get(2).size == 1);
            assertTrue(lines[1].get(2).enabled);
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

    @Test
    public void testSimpleMethods() {
        int sz = r.nextUint(20);
        for(int a = 0; a < sz; a=a+1) {
            Line sl = new Line();
            int dex = 0;
            int sz2 = 1+r.nextUint(50);
            for(int b = 0; b < sz2; b = b + 1 ) {
                final int choice = r.nextUint(2);
                int sz3 = 1+r.nextUint(10);
                int off = r.nextUint(3);
                dex += off;
                switch (choice) {
                    case 0:
                        sl.put(dex, Span.obtain(dex, sz3, 0));
                        break;
                    case 1:
                        sl.put(Span.obtain(dex, sz3, 0));
                        break;
                }
                sl.get(dex);
                dex += sz3;
            }
            assertTrue(sl.size() == sz2);
            assertFalse(sl.size() != sz2);
            assertFalse(sl.isEmpty());
            sl.clear();
            assertTrue(sl.isEmpty());
        }
    }

    @Test
    public void split() {
        // subs[0] | subs[1]
        //     0  sz
        //     |---
        //     -|--
        //     ---|
        {
            {
                Line s = new Line();
                s.put(Span.obtain(0, 0));
                s.put(Span.obtain(1, 0));
                s.put(Span.obtain(2, 0));
                Line[] subs = (Line[]) s.split(0);
                assertTrue(subs[0].size() == 0);
                assertTrue("subs[1].size()=" + subs[1].size(), subs[1].size() == 3);
            }
            {
                Line s = new Line();
                s.put(Span.obtain(0, 0));
                s.put(Span.obtain(1, 0));
                s.put(Span.obtain(2, 0));
                Line[] subs = (Line[]) s.split(1);
                assertTrue(subs[0].size() == 1);
                assertTrue(subs[1].size() == 2);
            }
            {
                Line s = new Line();
                s.put(Span.obtain(0, 0));
                s.put(Span.obtain(1, 0));
                s.put(Span.obtain(2, 0));
                Line[] subs = (Line[]) s.split(3);
                assertTrue("subs[0].size()="+subs[0].size()+",subs[1].size()="+subs[1].size(),subs[0].size() == 3);
                assertTrue(subs[1].size() == 0);
            }
        }
        {
            Line s = new Line();
            int sz = r.nextUint(500);
            for (int a = 0; a < sz; a = a + 1) {
                s.put(Span.obtain(a, 0xFFFF0000));
            }
            int split = r.nextUint(sz + 1);
            Line[] subs = (Line[]) s.split(split);
            assertTrue("sz=" + sz + ",s.size()=" + s.size() + ",subs[0].size()=" + subs[0].size() + ",split=" + split, subs[0].size() == split);
            assertTrue("sz=" + sz + ",s.size()=" + s.size() + ",split=" + split + ",subs[1].size()=" + subs[1].size() + ",(s.size()-split)=" + (s.size() - split), subs[1].size() == (s.size() - split));
        }
        {
            //
            // ---*|**
            //
            // ---
            //
            @Jailbreak Line s = new Line();
            s.behaviourOnCellSplit = Line.SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 3, 0));
            s.put(Span.obtain(3, 3, 0));
            Line[] lines = (Line[]) s.split(4);
            assertTrue(lines[0].size() == 2);
            assertTrue(lines[0].get(0).column == 0);
            assertTrue(lines[0].get(0).size == 3);
            assertTrue(lines[1].size() == 1);
        }
        {
            //
            // ---*|**
            //
            // ---*
            // **
            //
            @Jailbreak  Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            s.put(Span.obtain(0, 3, 0));
            s.put(Span.obtain(3, 3, 0));
            Line[] lines = (Line[]) s.split(4);
            assertTrue(lines[0].size() == 2);
            assertTrue(lines[0].get(0).column == 0);
            assertTrue(lines[0].get(0).size == 3);
            assertTrue(lines[0].get(3).column == 3);
            assertTrue(lines[0].get(3).size == 1);
            assertTrue(lines[1].size() == 1);
            assertTrue(lines[1].get(0).column == 0);
            assertTrue(lines[1].get(0).size == 2);
        }
        {
            // *|*
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s.put(Span.obtain(0, 2, 0));
            assertTrue(s.size() == 1);
            assertTrue(s.get(0).size == 2);
            Line[] parts = (Line[]) s.split(1);
            assertTrue(parts[0].size() == 1);
            assertTrue(parts[0].get(0).size == 1);
            assertTrue(parts[1].size() == 1);
            assertTrue(parts[1].get(0).size == 1);
        }
        for(int behave : new int[]{ SPAN_SPLIT_EXTENDS, SPAN_SPLIT_SPLITTING }) {
            {
                // *|*
                Line s = new Line();
                s.behaviourOnCellSplit = behave;
                s.put(Span.obtain(0, 2, 0));
                assertTrue(s.size() == 1);
                assertTrue(s.get(0).size == 2);
                Line[] parts = (Line[]) s.split(1);
                assertTrue("behave=" + behave,parts[0].size() == 1);
                assertTrue(parts[0].get(0).size == 1);
                assertTrue(parts[1].size() == 1);
                assertTrue(parts[1].get(0).size == 1);
            }
            {
                // |**
                Line s = new Line();
                s.behaviourOnCellSplit = behave;
                s.put(Span.obtain(0, 2, 0));
                assertTrue(s.size() == 1);
                Line[] parts = (Line[]) s.split(0);
                assertTrue(parts[0].size() == 0);
                assertTrue(parts[1].size() == 1);
                assertTrue(parts[1].get(0).size == 2);
            }
            {
                // **|
                Line s = new Line();
                s.behaviourOnCellSplit = behave;
                s.put(Span.obtain(0, 2, 0));
                Line[] parts = (Line[]) s.split(2);
                assertTrue(parts[0].size() == 1);
                assertTrue(parts[0].get(0).size == 2);
                assertTrue(parts[1].size() == 0);
            }
        }
    }

    @Test
    public void testRemoveContentExtrems() {
        {
            // |       |
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.removeContent(0, 9);
        }
        {
            // ||
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.removeContent(0,0);
        }
        {
            // *|                                               |
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,1));
            l.removeContent(1, 100);
            assertTrue(l.size() == 1);
            assertTrue(l.get(0).size == 1);
        }
        {
            // |            |*
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,14));
            l.put(new BaseCell(14, 1));
            l.removeContent(0, 14);
            assertTrue(l.size()==1);
            assertTrue(l.get(0).size == 1);
        }
        {
            // |****|*
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,5));
            l.removeContent(0,4);
            assertTrue(l.size()==1);
            assertTrue(l.get(0).size == 1);
        }
    }
    @Test
    public void testRemoveContentCommons() {
        {
            // -|-+++-|-
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeContent(1,5);
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size == 1);
            assertTrue(l.get(1).size == 1);

        }
        {
            // --+|++-|-
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeContent(3,3);
            assertTrue(l.size()==3);
            assertTrue(l.get(0).size == 2);
            assertTrue(l.get(2).size == 1);
            assertTrue(l.get(3).size == 1);
        }
        {
            // --|+++|--
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeContent(2,3);
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size == 2);
            assertTrue(l.get(2).size == 2);
        }
        {
            // --+|+|+--
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeContent(3,1);
            assertTrue(l.get(0).size==2);
            assertTrue(l.get(2).size == 1);
            assertTrue(l.get(3).size == 1);
            assertTrue(l.get(4).size == 2);
        }
        {
            // -|-+|++--
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeContent(1,2);
            assertTrue(l.get(0).size==1);
            assertTrue(l.get(1).size==2);
            assertTrue(l.get(3).size==2);
        }
    }

    @Test
    public void concat() {
        {
            {
                // -
                //  +
                Line s = new Line();
                s.put(Span.obtain(0, 1, 0));
                Line s1 = new Line();
                s1.put(Span.obtain(1, 1, 0));
                Line aux = (Line) Line.concat(s,s1);
                assertTrue(aux.size() == 2);
                assertTrue(aux.get(0).size==1);
                assertTrue(aux.get(2).size == 1);
            }
            {
                //  -
                // +++
                Line l = new Line();
                l.put(Span.obtain(1, 1, 0));
                Line l1 = new Line();
                l1.put(Span.obtain(0, 3, 0));
                Line aux = (Line) Line.concat(l,l1);
                assertTrue(aux.size() == 2);
                assertTrue(aux.get(1).size == 1);
                assertTrue(aux.get(2).size == 3);
            }
            {
                //  -=t
                // +-*
                Line l = new Line();
                l.put(Span.obtain(1, 1, 0));
                l.put(Span.obtain(2, 1, 0));
                l.put(Span.obtain(3, 1, 0));
                Line l1 = new Line();
                l1.put(Span.obtain(0, 1, 0));
                l1.put(Span.obtain(1, 1, 0));
                l1.put(Span.obtain(2, 1, 0));
                Line aux = (Line) Line.concat(l,l1);
                assertTrue(aux.size() == 6);
                assertTrue(aux.get(1).size == 1);
                assertTrue(aux.get(2).size == 1);
                assertTrue(aux.get(3).size == 1);
                assertTrue(aux.get(4).size == 1);
                assertTrue(aux.get(5).size == 1);
                assertTrue(aux.get(6).size == 1);
            }
        }
        {
            {
                Line s1 = new Line();
                Line s2 = new Line();
                s2.put(Span.obtain(0, 0));
                s2.put(Span.obtain(1, 0));
                Line merged = (Line) Line.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                Line s1 = new Line();
                s1.put(Span.obtain(0, 0));
                Line s2 = new Line();
                s2.put(Span.obtain(0, 0));
                Line merged = (Line) Line.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                Line s1 = new Line();
                Line s2 = new Line();
                s1.put(Span.obtain(0, 0));
                s1.put(Span.obtain(1, 0));
                Line merged = (Line) Line.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
        }
        {
            int sz = r.nextUint(20);
            for(int a = 0; a < sz; a = a + 1) {
                Line [] lines = new Line[2];
                int finalSz = 0;
                for(int b = 0; b < lines.length; b=b+1) {
                    lines[b]=new Line();
                    int sz2 = r.nextUint(50);
                    finalSz += sz2;
                    for(int c = 0; c < sz2; c = c + 1) {
                        lines[b].put(Span.obtain(c,0));
                    }
                }
                Line sl = (Line) Line.concat(lines[0],lines[1]);
                assertTrue("finalSz=" + finalSz + ",sl.size()=" + sl.size(),sl.size() == finalSz);
            }
        }
    }

    @Test
    public void insertContent() {
        {
            //
            // |--++*****
            //
            //
            Line s1 = new Line();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            s1.insertContent(Span.obtain(0, 2, 0));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 4);
            assertTrue(s1.get(0).column == 0);
            assertTrue(s1.get(2).column == 2);
            assertTrue(s1.get(4).column == 4);
            assertTrue(s1.get(6).column == 6);
        }
        {
            //
            // |---**-----
            //
            //
            Line s1 = new Line();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            s1.insertContent(Span.obtain(0, 0, 0));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
        }
        {
            //
            // --++*|****
            //
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            System.out.println("BEFORE");
            s1.dump();
            s1.insertContent(Span.obtain(5, 2, 0));
            s1.dump();
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
        }
        {
            //
            // --++*|****
            //
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            s1.insertContent(Span.obtain(5, 2, 0));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 5);
        }
        {
            //
            // --++*|****
            //
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            s1.insertContent(5,2);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testInsertContent() {
        //
        // --++*|****
        //
        //
        Line s1 = new Line();
        s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
        s1.put(Span.obtain(0, 2, 0));
        s1.put(Span.obtain(2, 2, 0));
        s1.put(Span.obtain(4, 5, 0));
        s1.insertContent(Span.obtain(5, 2, 0));
    }

    @Test
    public void removeContent() {
        {
            Logger.debug("Testing");
            //
            // |+
            // +
            //
            Line s1 = new Line();
            s1.put(Span.obtain(0, 1, 0));
            s1.removeContent(0,0);
            assertTrue(s1.size()==1);
            assertTrue(s1.get(0).column == 0);
            assertTrue(s1.get(0).size == 1);
        }
        {
            //
            // |
            //
            Line s1 = new Line();
            s1.removeContent(0,0);
            assertTrue(s1.size()==0);
        }
        {
            //
            // +|++-|
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s1.put(Span.obtain(0, 3, 0));
            s1.put(Span.obtain(3, 1, 0));
            s1.removeContent(1,3);
            assertTrue(s1.size()==1);
            assertTrue(s1.get(0).size == 1 && s1.get(0).column == 0);
        }
        {
            //
            // +++|---n|n
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s1.put(Span.obtain(0, 3, 0));
            s1.put(Span.obtain(3, 3, 0));
            s1.put(Span.obtain(6, 2, 0));
            s1.removeContent(3,4);
            assertTrue(s1.size()==2);
            assertTrue(s1.get(0).size==3 && s1.get(0).column == 0);
            assertTrue(s1.get(3).size==1 && s1.get(3).column == 3);
        }
        {
            //
            // --|-+|++-
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s1.put(Span.obtain(0, 3, 0));
            s1.put(Span.obtain(3, 3, 0));
            s1.put(Span.obtain(6, 1, 0));
            s1.removeContent(2,2);
            assertTrue(s1.size()==3);
            assertTrue(s1.get(0).size==2 && s1.get(0).column == 0);
            assertTrue(s1.get(2).size==2 && s1.get(2).column == 2);
            assertTrue(s1.get(4).size==1 && s1.get(4).column == 4);
        }
        {
            //
            // +|++|++
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s1.put(Span.obtain(0, 5, 0));
            s1.removeContent(1,2);
            assertTrue(s1.size()==1);
            assertTrue(s1.get(0).size == 3);
            assertTrue(s1.get(0).column == 0);
        }
        {
            //
            // +++|+-*-|----
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s1.put(Span.obtain(0, 4, 0));
            s1.put(Span.obtain(4, 1, 0));
            s1.put(Span.obtain(5, 1, 0));
            s1.put(Span.obtain(6, 5, 0));
            s1.removeContent(3,4);
            assertTrue(s1.size() == 2);
            assertTrue(s1.get(0).size == 3);
            assertTrue(s1.get(3).size == 4);
        }
        {
            //
            // --**ttttt
            // -*ttttt
            //
            Line s1 = new Line();
            s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            s1.removeContent(1, 2);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
            assertTrue(s1.get(0).size == 1);
            assertTrue(s1.get(1).size == 1);
            assertTrue(s1.get(2).size == 5);
        }
        {
            //
            // ------**+
            // ----**+
            //
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s.put(Span.obtain(0, 6, 0));
            s.put(Span.obtain(6, 2, 0));
            s.put(Span.obtain(8, 1, 0));
            s.removeContent(2, 2);
            assertTrue(s.size() == 3);
            assertTrue(s.get(0).size == 4);
            assertTrue(s.get(4).size == 2);
            assertTrue(s.get(6).size == 1);
        }
        {
            //
            // ------**+
            // ------*
            //
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s.put(Span.obtain(0, 6, 0));
            s.put(Span.obtain(6, 2, 0));
            s.put(Span.obtain(8, 1, 0));
            s.removeContent(6, 2);
            assertTrue(s.size() == 2);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 1);
        }
        {
            // ----|**|
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
            s.put(Span.obtain(0, 4, 0));
            s.put(Span.obtain(4, 2, 0));
            s.removeContent(4, 2);
            assertTrue(s.size() == 1);
            assertTrue(s.get(0).size == 4);
        }
        {
            //
            // ------**+
            // ------**+
            //
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 6, 0));
            s.put(Span.obtain(6, 2, 0));
            s.put(Span.obtain(8, 1, 0));
            s.removeContent(0, 0);
            s.dump();
            assertTrue(s.size() == 3);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 2);
            assertTrue(s.get(8).size == 1);
        }
        {
            // *|--|*
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 1, 0));
            s.put(Span.obtain(1, 2, 0));
            s.put(Span.obtain(3, 1, 0));
            s.removeContent(1,2);
            assertTrue(s.size() == 2);
            assertTrue(s.get(0).size == 1);
            assertTrue(s.get(1).size == 1);
        }
        {
            // *|++|*
            Line s = new Line();
            s.put(Span.obtain(0, 1, 0));
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 1, 0));
            s.put(Span.obtain(1, 2, 0));
            s.put(Span.obtain(3, 1, 0));
            s.removeContent(1,2);
            assertTrue(s.size() == 2);
            assertTrue(s.get(0).size == 1);
            assertTrue(s.get(1).size == 1);
        }
        {
            //
            // ------**+
            // xx--**+
            //
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            s.put(Span.obtain(0, 6, 0));
            s.put(Span.obtain(6, 2, 0));
            s.put(Span.obtain(8, 1, 0));
            s.removeContent(2, 2);
            assertTrue(s.size() == 4);
            assertTrue(s.get(0).size == 2);
        }
        {
            //
            // -|+|+
            // -+
            //
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(Span.obtain(0, 1, 0));
            l.put(Span.obtain(1, 2, 0));
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size==1);
            assertTrue(l.get(0).column==0);
            assertTrue(l.get(1).column==1);
            assertTrue(l.get(1).size==2);
            l.dump();
            l.removeContent(1,1);
            l.dump();
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size==1);
            assertTrue(l.get(0).column==0);
            assertTrue(l.get(1).column==1);
            assertTrue(l.get(1).size==1);
        }
        {
            //
            // ----|----
            //
            Line l = new Line();
            l.behaviourOnCellSplit = SPAN_SPLIT_SPLITTING;
            l.put(Span.obtain(0, 8, 0));
            l.removeContent(4,0);
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size == 4);
            assertTrue(l.get(4).size == 4);
        }
        {
            int sz = r.nextUint(100);
            int index = 0;
            for(int a = 0; a < sz; a = a + 1) {
                Line l = new Line();
                int t = r.nextUint(100);
                for(int b = 0; b < t; b = b + 1) {
                    int off = 1+r.nextUint(5);
                    index += off;
                    int size = 1+r.nextUint(50);
                    l.put(Span.obtain(index, size, 0));
                    index += size;
                }
                assertTrue("l.size()="+l.size()+",t="+t,l.size() == t);
                l.removeContent(0,index+49);
                assertTrue(l.size()==0);
            }
        }
        {
            // ------**+
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 6, 0));
            s.put(Span.obtain(6, 2, 0));
            s.put(Span.obtain(8, 1, 0));
            assertTrue(s.size() == 3);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 2);
            assertTrue(s.get(8).size == 1);
        }
        {
            // ------**+
            //     **+
            Line s = new Line();
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 6, 0));
            s.put(Span.obtain(6, 2, 0));
            s.put(Span.obtain(8, 1, 0));
            s.dump();
            s.removeContent(2, 2);
            s.dump();
            assertTrue(s.size() == 2);
            assertTrue("s.get(4).size=" + s.get(4).size, s.get(4).size == 2);
            assertTrue(s.get(6).size == 1);
        }
    }
}