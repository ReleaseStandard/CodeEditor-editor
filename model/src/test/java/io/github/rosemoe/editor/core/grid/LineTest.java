package io.github.rosemoe.editor.core.grid;

import org.junit.Ignore;
import org.junit.Test;

import io.github.rosemoe.editor.core.grid.instances.SpanCell;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static io.github.rosemoe.editor.core.grid.Cell.SPLIT_EXTENDS;
import static io.github.rosemoe.editor.core.grid.Cell.SPLIT_INVALIDATE;
import static io.github.rosemoe.editor.core.grid.Cell.SPLIT_SPLITTING;
import static org.junit.Assert.*;

public class LineTest {

    Random r = new Random();

    @Test
    public void testTemplating() {
        Line<BaseCell>[] parts = new Line[2];
        assertTrue(parts != null ) ;
        assertTrue(parts.length == 2);
    }
    @Test
    public void testAppend() {
        Line<BaseCell> l = new Line<BaseCell>();
        for(int a = 0; a < 10; a ++) {
            int sz = r.nextUint(100);
            int col = r.nextUint(100);
            assertTrue("col="+col+",sz="+sz,l.append(new BaseCell(col, sz))!=null);
        }
        assertTrue(l.size()==10);
        assertTrue(l.getWidth() <= 10 * 200);
    }

    @Test
    public void testPut() {
        {
            // *--
            Line<BaseCell> l = new Line<BaseCell>();
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
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            assertTrue(s.size() == 3);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 2);
            assertTrue(s.get(8).size == 1);
        }
        {
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_SPLITTING;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.put(s);
            assertTrue(s1.size() == 3);
            assertTrue(s1.get(0).size == 6);
            assertTrue(s1.get(6).size == 2);
            assertTrue(s1.get(8).size == 1);
        }
        {
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.append(new BaseCell(6));
            l.append(new BaseCell(2));
            l.append(new BaseCell(1));
            l.append(new BaseCell(10));
            assertTrue(l.size()==4);
            assertTrue(l.get(9).size==10);
        }
        {
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,6));
            l.put(new BaseCell(6,2));
            l.put(new BaseCell(8,1));
            Line<BaseCell> l1 = new Line<BaseCell>();
            l1.append(new BaseCell(0,2));
            l1.append(new BaseCell(0,2));
            l.append(l1);
            l.dump();
            l1.dump();
            assertTrue(l.size() == 5);
            assertTrue(l.get(11).size == 2);
        }
        {
            Line<BaseCell> l = new Line<BaseCell>();
            l.append(new BaseCell(0,1));
            assertTrue(l.size()==1);
            assertTrue(l.get(0).size==1);
        }
    }

    @Test
    public void testSplitBug1() {
        {
            // +++-|--m
            //
            // +++-
            // --m
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_SPLITTING;
            s.put(new BaseCell(0,3));
            s.put(new BaseCell(3,3));
            s.put(new BaseCell(6,1));
            Line<BaseCell>[] lines = s.split(4);
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
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s.put(new BaseCell(0,3));
            s.put(new BaseCell(3,3));
            s.put(new BaseCell(6,1));
            Line<BaseCell>[] lines = s.split(4);
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
        Line<BaseCell> l = new Line<BaseCell>() {{
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
        {
            int sz = r.nextUint(20);
            for (int a = 0; a < sz; a = a + 1) {
                Line<SpanCell> sl = new Line<SpanCell>();
                int dex = 0;
                int sz2 = 1 + r.nextUint(50);
                for (int b = 0; b < sz2; b = b + 1) {
                    final int choice = r.nextUint(2);
                    int sz3 = 1 + r.nextUint(10);
                    int off = r.nextUint(3);
                    dex += off;
                    switch (choice) {
                        case 0:
                            sl.put(dex, SpanCell.obtain(dex, sz3, 0));
                            break;
                        case 1:
                            sl.put(SpanCell.obtain(dex, sz3, 0));
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
        {
            int i = r.nextUint(100);
            int length = 0;
            Line<BaseCell> l = new Line<BaseCell>();
            for(int a = 0; a < i; a=a+1) {
                int sz = r.nextUint(10);
                l.append(new BaseCell(sz));
                length += sz;
            }
            assertTrue(l.getWidth()==length);
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
                Line<BaseCell> s = new Line<BaseCell>();
                s.put(new BaseCell(0,1));
                s.put(new BaseCell(1,1));
                s.put(new BaseCell(2,1));
                Line<BaseCell>[] subs = s.split(0);
                assertTrue(subs[0].size() == 0);
                assertTrue("subs[1].size()=" + subs[1].size(), subs[1].size() == 3);
            }
            {
                Line<BaseCell> s = new Line<BaseCell>();
                s.put(new BaseCell(0,1));
                s.put(new BaseCell(1,1));
                s.put(new BaseCell(2,1));
                Line<BaseCell>[] subs = (Line<BaseCell>[]) s.split(1);
                assertTrue(subs[0].size() == 1);
                assertTrue(subs[1].size() == 2);
            }
            {
                Line<BaseCell> s = new Line<BaseCell>();
                s.put(new BaseCell(0,1));
                s.put(new BaseCell(1,1));
                s.put(new BaseCell(2,1));
                Line<BaseCell>[] subs = (Line<BaseCell>[]) s.split(3);
                assertTrue("subs[0].size()="+subs[0].size()+",subs[1].size()="+subs[1].size(),subs[0].size() == 3);
                assertTrue(subs[1].size() == 0);
            }
        }
        {
            Line<BaseCell> s = new Line<BaseCell>();
            int sz = r.nextUint(500);
            for (int a = 0; a < sz; a = a + 1) {
                s.put(new BaseCell(a,1));
            }
            int split = r.nextUint(sz + 1);
            Line<BaseCell>[] subs = (Line<BaseCell>[]) s.split(split);
            assertTrue("sz=" + sz + ",s.size()=" + s.size() + ",subs[0].size()=" + subs[0].size() + ",split=" + split, subs[0].size() == split);
            assertTrue("sz=" + sz + ",s.size()=" + s.size() + ",split=" + split + ",subs[1].size()=" + subs[1].size() + ",(s.size()-split)=" + (s.size() - split), subs[1].size() == (s.size() - split));
        }
        {
            //
            // ---*|**
            //
            // ---
            //
            @Jailbreak Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;
            s.put(new BaseCell(0,3));
            s.put(new BaseCell(3,3));
            Line<BaseCell>[] lines = (Line<BaseCell>[]) s.split(4);
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
            @Jailbreak  Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_SPLITTING;
            s.put(new BaseCell(0,3));
            s.put(new BaseCell(3,3));
            Line<BaseCell>[] lines = (Line<BaseCell>[]) s.split(4);
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
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_EXTENDS;
            s.put(new BaseCell(0,2));
            assertTrue(s.size() == 1);
            assertTrue(s.get(0).size == 2);
            Line<BaseCell>[] parts = (Line<BaseCell>[]) s.split(1);
            assertTrue(parts[0].size() == 1);
            assertTrue(parts[0].get(0).size == 1);
            assertTrue(parts[1].size() == 1);
            assertTrue(parts[1].get(0).size == 1);
        }
        for(int behave : new int[]{SPLIT_EXTENDS, SPLIT_SPLITTING}) {
            {
                // *|*
                Line<BaseCell> s = new Line<BaseCell>();
                s.behaviourOnCellSplit = behave;
                s.put(new BaseCell(0,2));
                assertTrue(s.size() == 1);
                assertTrue(s.get(0).size == 2);
                Line<BaseCell>[] parts = (Line<BaseCell>[]) s.split(1);
                assertTrue("behave=" + behave,parts[0].size() == 1);
                assertTrue(parts[0].get(0).size == 1);
                assertTrue(parts[1].size() == 1);
                assertTrue(parts[1].get(0).size == 1);
            }
            {
                // |**
                Line<BaseCell> s = new Line<BaseCell>();
                s.behaviourOnCellSplit = behave;
                s.put(new BaseCell(0,2));
                assertTrue(s.size() == 1);
                Line<BaseCell>[] parts = (Line<BaseCell>[]) s.split(0);
                assertTrue(parts[0].size() == 0);
                assertTrue(parts[1].size() == 1);
                assertTrue(parts[1].get(0).size == 2);
            }
            {
                // **|
                Line<BaseCell> s = new Line<BaseCell>();
                s.behaviourOnCellSplit = behave;
                s.put(new BaseCell(0,2));
                Line<BaseCell>[] parts = (Line<BaseCell>[]) s.split(2);
                assertTrue(parts[0].size() == 1);
                assertTrue(parts[0].get(0).size == 2);
                assertTrue(parts[1].size() == 0);
            }
        }
    }

    @Test
    public void testRemoveCellsExtrems() {
        {
            // |       |
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.removeCells(0, 9);
        }
        {
            // ||
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.removeCells(0,0);
        }
        {
            // *|                                               |
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,1));
            l.removeCells(1, 100);
            assertTrue(l.size() == 1);
            assertTrue(l.get(0).size == 1);
        }
        {
            // |            |*
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,14));
            l.put(new BaseCell(14, 1));
            l.removeCells(0, 14);
            assertTrue(l.size()==1);
            assertTrue(l.get(0).size == 1);
        }
        {
            // |****|*
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,5));
            l.removeCells(0,4);
            assertTrue(l.size()==1);
            assertTrue(l.get(0).size == 1);
        }
    }
    @Test
    public void testRemoveCellsCommons() {
        {
            // -|-+++-|-
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeCells(1,5);
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size == 1);
            assertTrue(l.get(1).size == 1);

        }
        {
            // --+|++-|-
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeCells(3,3);
            assertTrue(l.size()==3);
            assertTrue(l.get(0).size == 2);
            assertTrue(l.get(2).size == 1);
            assertTrue(l.get(3).size == 1);
        }
        {
            // --|+++|--
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeCells(2,3);
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size == 2);
            assertTrue(l.get(2).size == 2);
        }
        {
            // --+|+|+--
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeCells(3,1);
            assertTrue(l.get(0).size==2);
            assertTrue(l.get(2).size == 1);
            assertTrue(l.get(3).size == 1);
            assertTrue(l.get(4).size == 2);
        }
        {
            // -|-+|++--
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,2));
            l.put(new BaseCell(2,3));
            l.put(new BaseCell(5,2));
            l.removeCells(1,2);
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
                Line<BaseCell> l = new Line<BaseCell>();
                Line<BaseCell> l1 = new Line<BaseCell>();
                l.put(new BaseCell(0,1));
                l1.put(new BaseCell(1,1));
                Line<BaseCell> aux = (Line<BaseCell>) Line.concat(l,l1);
                assertTrue(aux.size() == 2);
                assertTrue(aux.get(0).size==1);
                assertTrue(aux.get(2).size==1);
            }
            {
                //  -
                // +++
                Line<BaseCell> l = new Line<BaseCell>();
                l.put(new BaseCell(1,1));
                Line<BaseCell> l1 = new Line<BaseCell>();
                l1.put(new BaseCell(0,3));
                Line<BaseCell> aux = (Line<BaseCell>) Line.concat(l,l1);
                assertTrue(aux.size() == 2);
                assertTrue(aux.get(1).size == 1);
                assertTrue(aux.get(2).size == 3);
            }
            {
                //  -=t
                // +-*
                //  -=t+-*
                Line<BaseCell> l = new Line<BaseCell>();
                l.put(new BaseCell(1,1));
                l.put(new BaseCell(2,1));
                l.put(new BaseCell(3,1));
                Line<BaseCell> l1 = new Line<BaseCell>();
                l1.put(new BaseCell(0,1));
                l1.put(new BaseCell(1,1));
                l1.put(new BaseCell(2,1));
                Line<BaseCell> aux = (Line<BaseCell>) Line.concat(l,l1);
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
                //
                // +-
                Line<BaseCell> s1 = new Line<BaseCell>();
                Line<BaseCell> s2 = new Line<BaseCell>();
                s2.append(new BaseCell(1));
                s2.append(new BaseCell(1));
                Line<BaseCell> merged = (Line<BaseCell>) Line.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                // +
                // *
                Line<BaseCell> s1 = new Line<BaseCell>();
                s1.put(new BaseCell(0,1));
                Line<BaseCell> s2 = new Line<BaseCell>();
                s2.put(new BaseCell(0,1));
                Line<BaseCell> merged = (Line<BaseCell>) Line.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                Line<BaseCell> s1 = new Line<BaseCell>();
                Line<BaseCell> s2 = new Line<BaseCell>();
                s1.put(new BaseCell(0,1));
                s1.put(new BaseCell(1,1));
                Line<BaseCell> merged = (Line<BaseCell>) Line.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
        }
        {
            int sz = r.nextUint(20);
            for(int a = 0; a < sz; a = a + 1) {
                Line<BaseCell> [] lines = new Line[2];
                int finalSz = 0;
                for(int b = 0; b < lines.length; b=b+1) {
                    lines[b]=new Line<BaseCell>();
                    int sz2 = r.nextUint(50);
                    finalSz += sz2;
                    for(int c = 0; c < sz2; c = c + 1) {
                        lines[b].put(new BaseCell(c,1));
                    }
                }
                Line<BaseCell> sl = (Line<BaseCell>) Line.concat(lines[0],lines[1]);
                assertTrue("finalSz=" + finalSz + ",sl.size()=" + sl.size(),sl.size() == finalSz);
            }
        }
    }

    @Test
    public void insertCells() {
        {
            //
            // |--++*****
            //
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.put(new BaseCell(0,2));
            s1.put(new BaseCell(2,2));
            s1.put(new BaseCell(4,5));
            s1.insertCell(new BaseCell(0,2));
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
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.put(new BaseCell(0,2));
            s1.put(new BaseCell(2,2));
            s1.put(new BaseCell(4,5));
            s1.insertCell(new BaseCell(0,0));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
        }
        {
            // --++*|****
            // --++*--****
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_SPLITTING;
            s1.put(new BaseCell(0,2));
            s1.put(new BaseCell(2,2));
            s1.put(new BaseCell(4,5));
            s1.insertCell(new BaseCell(5,2));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 5);
            assertTrue(s1.get(0).size == 2);
            assertTrue(s1.get(2).size == 2);
            assertTrue(s1.get(4).size == 1);
            assertTrue(s1.get(5).size == 2);
            assertTrue(s1.get(7).size == 4);
        }
        {
            // --++*|****
            // --++*******
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_EXTENDS;
            s1.put(new BaseCell(0,2));
            s1.put(new BaseCell(2,2));
            s1.put(new BaseCell(4,5));
            s1.insertCell(5,2);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
            assertTrue(s1.get(0).size == 2);
            assertTrue(s1.get(2).size == 2);
            assertTrue(s1.get(4).size == 7);
        }
    }

    // Test that all behaviour lead to the same length on the content
    @Test
    @Ignore("This test coud lead to bug")
    public void testGenericity() {
        Line<BaseCell> l = new Line<BaseCell>();
        int n = r.nextUint(20);
        for(int a = 0; a < n; a = a + 1) {
            l.append(new BaseCell(r.nextUint(100)));
        }
        int insertSize = r.nextUint(100);
        int insertCol = r.nextUint(l.getWidth());
        int [] behaves = new int[]{SPLIT_EXTENDS, SPLIT_INVALIDATE, SPLIT_SPLITTING};
        int [] results = new int[behaves.length];
        for(int a = 0; a < behaves.length; a = a + 1) {
            Line<BaseCell> l2 = l.clone();
            l2.behaviourOnCellSplit = behaves[a];
            l2.insertCell(insertCol, insertSize);
            results[a] = l2.getWidth() + l2.size();
        }
        assertTrue("{cut: {insertSize: " + insertSize+", insertCol: " + insertCol + "}, n: "+n+"}", results[0]==results[1] && results[1]==results[2]);
    }

    @Test
    @Ignore("TODO")
    public void testInsertBug() {
        {
            // --++*|****
            // --++ xx
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s1.put(new BaseCell(0,2));
            s1.put(new BaseCell(2,2));
            s1.put(new BaseCell(4,5));
            System.out.println("BEFORE");
            s1.dump();
            s1.insertCell(new BaseCell(5, 2));
            s1.dump();
            assertTrue("s1.size()=" + s1.size(), s1.size() == 5);
            assertTrue(s1.get(0).size == 2);
            assertTrue(s1.get(2).size == 2);
            assertTrue(s1.get(4).size == 1);
            assertTrue(s1.get(5).size == 2);
            assertTrue(s1.get(7).size == 4);
        }
    }
    @Test(expected = RuntimeException.class)
    public void testInsertCells() {
        //
        // --++*|****
        //
        //
        Line<BaseCell> s1 = new Line<BaseCell>();
        s1.behaviourOnCellSplit = SPLIT_EXTENDS;
        s1.put(new BaseCell(0,2));
        s1.put(new BaseCell(2,2));
        s1.put(new BaseCell(4,5));
        s1.insertCell(new BaseCell(5,2));
    }

    @Test
    public void removeCells() {
        {
            Logger.debug("Testing");
            //
            // |+
            // +
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.put(new BaseCell(0,1));
            s1.removeCells(0,0);
            assertTrue(s1.size()==1);
            assertTrue(s1.get(0).column == 0);
            assertTrue(s1.get(0).size == 1);
        }
        {
            //
            // |
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.removeCells(0,0);
            assertTrue(s1.size()==0);
        }
        {
            //
            // +|++-|
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_EXTENDS;
            s1.put(new BaseCell(0,3));
            s1.put(new BaseCell(3,1));
            s1.removeCells(1,3);
            assertTrue(s1.size()==1);
            assertTrue(s1.get(0).size == 1 && s1.get(0).column == 0);
        }
        {
            //
            // +++|---n|n
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_EXTENDS;
            s1.put(new BaseCell(0,3));
            s1.put(new BaseCell(3,3));
            s1.put(new BaseCell(6,2));
            s1.removeCells(3,4);
            assertTrue(s1.size()==2);
            assertTrue(s1.get(0).size==3 && s1.get(0).column == 0);
            assertTrue(s1.get(3).size==1 && s1.get(3).column == 3);
        }
        {
            //
            // --|-+|++-
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_EXTENDS;
            s1.put(new BaseCell(0,3));
            s1.put(new BaseCell(3,3));
            s1.put(new BaseCell(6,1));
            s1.removeCells(2,2);
            assertTrue(s1.size()==3);
            assertTrue(s1.get(0).size==2 && s1.get(0).column == 0);
            assertTrue(s1.get(2).size==2 && s1.get(2).column == 2);
            assertTrue(s1.get(4).size==1 && s1.get(4).column == 4);
        }
        {
            //
            // +|++|++
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_EXTENDS;
            s1.put(new BaseCell(0,5));
            s1.removeCells(1,2);
            assertTrue(s1.size()==1);
            assertTrue(s1.get(0).size == 3);
            assertTrue(s1.get(0).column == 0);
        }
        {
            //
            // +++|+-*-|----
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_EXTENDS;
            s1.put(new BaseCell(0,4));
            s1.put(new BaseCell(4,1));
            s1.put(new BaseCell(5,1));
            s1.put(new BaseCell(6,5));
            s1.removeCells(3,4);
            assertTrue(s1.size() == 2);
            assertTrue(s1.get(0).size == 3);
            assertTrue(s1.get(3).size == 4);
        }
        {
            //
            // --**ttttt
            // -*ttttt
            //
            Line<BaseCell> s1 = new Line<BaseCell>();
            s1.behaviourOnCellSplit = SPLIT_EXTENDS;
            s1.put(new BaseCell(0,2));
            s1.put(new BaseCell(2,2));
            s1.put(new BaseCell(4,5));
            s1.removeCells(1, 2);
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
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_EXTENDS;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            s.removeCells(2, 2);
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
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_EXTENDS;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            s.removeCells(6, 2);
            assertTrue(s.size() == 2);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 1);
        }
        {
            // ----|**|
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_EXTENDS;
            s.put(new BaseCell(0,4));
            s.put(new BaseCell(4,2));
            s.removeCells(4, 2);
            assertTrue(s.size() == 1);
            assertTrue(s.get(0).size == 4);
        }
        {
            //
            // ------**+
            // ------**+
            //
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            s.removeCells(0, 0);
            s.dump();
            assertTrue(s.size() == 3);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 2);
            assertTrue(s.get(8).size == 1);
        }
        {
            // *|--|*
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s.put(new BaseCell(0,1));
            s.put(new BaseCell(1,2));
            s.put(new BaseCell(3,1));
            s.removeCells(1,2);
            assertTrue(s.size() == 2);
            assertTrue(s.get(0).size == 1);
            assertTrue(s.get(1).size == 1);
        }
        {
            // *|++|*
            Line<BaseCell> s = new Line<BaseCell>();
            s.put(new BaseCell(1,2));
            s.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s.put(new BaseCell(0,1));
            s.put(new BaseCell(1,2));
            s.put(new BaseCell(3,1));
            s.removeCells(1,2);
            assertTrue(s.size() == 2);
            assertTrue(s.get(0).size == 1);
            assertTrue(s.get(1).size == 1);
        }
        {
            //
            // ------**+
            // xx--**+
            //
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_SPLITTING;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            s.removeCells(2, 2);
            assertTrue(s.size() == 4);
            assertTrue(s.get(0).size == 2);
        }
        {
            //
            // -|+|+
            // -+
            //
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,1));
            l.put(new BaseCell(1,2));
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size==1);
            assertTrue(l.get(0).column==0);
            assertTrue(l.get(1).column==1);
            assertTrue(l.get(1).size==2);
            l.dump();
            l.removeCells(1,1);
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
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.put(new BaseCell(0,8));
            l.removeCells(4,0);
            assertTrue(l.size() == 2);
            assertTrue(l.get(0).size == 4);
            assertTrue(l.get(4).size == 4);
        }
        {
            int sz = r.nextUint(100);
            int index = 0;
            for(int a = 0; a < sz; a = a + 1) {
                Line<BaseCell> l = new Line<BaseCell>();
                int t = r.nextUint(100);
                for(int b = 0; b < t; b = b + 1) {
                    int off = 1+r.nextUint(5);
                    index += off;
                    int size = 1+r.nextUint(50);
                    l.put(new BaseCell(index, size));
                    index += size;
                }
                assertTrue("l.size()="+l.size()+",t="+t,l.size() == t);
                l.removeCells(0,index+49);
                assertTrue(l.size()==0);
            }
        }
        {
            // ------**+
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            assertTrue(s.size() == 3);
            assertTrue(s.get(0).size == 6);
            assertTrue(s.get(6).size == 2);
            assertTrue(s.get(8).size == 1);
        }
        {
            // ------**+
            //     **+
            Line<BaseCell> s = new Line<BaseCell>();
            s.behaviourOnCellSplit = SPLIT_INVALIDATE;
            s.put(new BaseCell(0,6));
            s.put(new BaseCell(6,2));
            s.put(new BaseCell(8,1));
            s.dump();
            s.removeCells(2, 2);
            s.dump();
            assertTrue(s.size() == 2);
            assertTrue("s.get(4).size=" + s.get(4).size, s.get(4).size == 2);
            assertTrue(s.get(6).size == 1);
        }
    }

    @Test
    public void subCells() {
        {
            // *|*-|-
            //   *-
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_INVALIDATE;
            l.append(new BaseCell(2));
            l.append(new BaseCell(2));
            Line<BaseCell> sub = l.subLine(1,2);
            l.dump();
            sub.dump();
            assertTrue(sub.size() == 2);
            assertTrue("sub.get(1).enabled="+sub.get(1).enabled+",l.behaviourOnCellSplit="+l.behaviourOnCellSplit,sub.get(1).enabled == ( l.behaviourOnCellSplit != SPLIT_INVALIDATE ));
        }
        {
            // |*|
            //  *
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = SPLIT_INVALIDATE;
            l.append(new BaseCell(1));
            l.dump();
            Line<BaseCell> sub = l.subLine(0,1);
            sub.dump();
            assertTrue(sub.size()==1);
        }
        {
            // +++|--|**
            //     --
            Line<BaseCell> l = new Line<BaseCell>();
            l.behaviourOnCellSplit = new int[]{SPLIT_EXTENDS,SPLIT_INVALIDATE,SPLIT_SPLITTING}[r.nextUint(3)];
            l.append(new BaseCell(3));
            l.append(new BaseCell(2));
            l.append(new BaseCell(2));
            l.dump();
            Line<BaseCell> sub = l.subLine(3,2);
            sub.dump();
            for(Cell c : sub) {
                Logger.debug("c="+c.column);
                c.dump();
            }
            assertTrue(""+sub.size(),sub.size() == 1);
        }
        {
            // |*
            //
            Line<BaseCell> l = new Line<BaseCell>();
            l.append(new BaseCell(1));
            l.dump();
            Line<BaseCell> sub = l.subLine(0,0);
            sub.dump();
            assertTrue(sub.size()==0);
        }
        {
            // |*|
            //  *
            Line<BaseCell> l = new Line<BaseCell>();
            l.append(new BaseCell(1));
            Line<BaseCell> sub = l.subLine(0,1);
            sub.dump();
            assertTrue(sub.size()==1);
            assertTrue(sub.get(0).size == 1);
        }
        {
            // |*|*
            //  *
            Line<BaseCell> l = new Line<BaseCell>();
            l.append(new BaseCell(2));
            Line<BaseCell> sub = l.subLine(0,1);
            assertTrue(sub.size()==1);
            assertTrue("sub.get(0).enabled="+sub.get(0).enabled+",l.behaviourOnCellSplit="+l.behaviourOnCellSplit,sub.get(0).enabled == ( l.behaviourOnCellSplit != SPLIT_INVALIDATE ));
        }
        {
            // +++|+---**,,|...
            //     +---**,,
            Line<BaseCell> l = new Line<BaseCell>();
            l.append(new BaseCell(4));
            l.append(new BaseCell(3));
            l.append(new BaseCell(2));
            l.append(new BaseCell(2));
            l.append(new BaseCell(3));
            l.dump();
            Line<BaseCell> sub = l.subLine(3,8);
            sub.dump();
            assertTrue(sub.size()==4);
            sub.dump();
            assertTrue(sub.get(3).size == 1);
            assertTrue(sub.get(4).size == 3);
            assertTrue(sub.get(7).size == 2);
            assertTrue(sub.get(9).size == 2);
        }
    }

    @Test
    public void testToString() {
        {
            Line<ContentCell> l = new Line<ContentCell>();
            l.append(new ContentCell('a'));
            l.append(new ContentCell('b'));
            l.append(new ContentCell('c'));
            assertTrue(l.toString().equals("abc"));
        }
    }

    @Test
    public void testInsertLine() {
        {
            // **--|++++
            // ++---
            // **--++---++++
            Line<BaseCell> l = new Line<>(), l2 = new Line<>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.append(new BaseCell(2));
            l.append(new BaseCell(2));
            l.append(new BaseCell(4));
            l2.append(new BaseCell(2));
            l2.append(new BaseCell(3));
            l.insertLine(4,l2);
            assertTrue(l.size() == 5);
            assertTrue(l.get(0).size == 2);
            assertTrue(l.get(2).size == 2);
            assertTrue(l.get(4).size == 2);
            assertTrue(l.get(6).size == 3);
            assertTrue(l.get(9).size == 4);
        }
        {
            // **--++|++
            // **$$
            // **--++**$$++
            Line<BaseCell> l = new Line<>(), l2 = new Line<>();
            l.behaviourOnCellSplit = SPLIT_SPLITTING;
            l2.behaviourOnCellSplit = SPLIT_SPLITTING;
            l.append(new BaseCell(2));
            l.append(new BaseCell(2));
            l.append(new BaseCell(4));
            l2.append(new BaseCell(2));
            l2.append(new BaseCell(2));
            l.insertLine(6,l2);
            assertTrue(l.size() == 6);
            assertTrue(l.get(0).size == 2);
            assertTrue(l.get(2).size == 2);
            assertTrue(l.get(4).size == 2);
            assertTrue(l.get(6).size == 2);
            assertTrue(l.get(8).size == 2);
            assertTrue(l.get(10).size == 2);
        }
    }
    @Test
    @Ignore("This is a bug")
    public void testInsertLineBug() {
        {
            // **--++|++
            // **$$
            // **--++++++++
            Line<BaseCell> l = new Line<>(), l2 = new Line<>();
            l.behaviourOnCellSplit = SPLIT_EXTENDS;
            l2.behaviourOnCellSplit = SPLIT_EXTENDS;
            l.append(new BaseCell(2));
            l.append(new BaseCell(2));
            l.append(new BaseCell(4));
            l2.append(new BaseCell(2));
            l2.append(new BaseCell(2));
            l.dump();
            l2.dump();
            l.insertLine(6,l2);
            Logger.debug("END");
            l.dump();
            assertTrue(l.size() == 3);
            assertTrue(l.get(0).size == 2);
            assertTrue(l.get(2).size == 2);
            assertTrue(l.get(4).size == 6);
        }
    }
}