package io.github.rosemoe.editor.core.extension.extensions.colorChange.analysis.spans;

import org.junit.Ignore;
import org.junit.Test;

import io.github.rosemoe.editor.core.Cell;
import io.github.rosemoe.editor.core.Line;
import io.github.rosemoe.editor.core.color.spans.Span;
import io.github.rosemoe.editor.core.color.spans.SpanLine;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static io.github.rosemoe.editor.core.Line.SPAN_SPLIT_EXTENDS;
import static io.github.rosemoe.editor.core.Line.SPAN_SPLIT_INVALIDATE;
import static io.github.rosemoe.editor.core.Line.SPAN_SPLIT_SPLITTING;
import static org.junit.Assert.*;

public class SpanLineTest {

    Random r = new Random();

    @Test
    public void testSimpleMethods() {
        int sz = r.nextUint(20);
        for(int a = 0; a < sz; a=a+1) {
            SpanLine sl = new SpanLine();
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
                Line<Span> s = new Line();
                s.put(Span.obtain(0, 0));
                s.put(Span.obtain(1, 0));
                s.put(Span.obtain(2, 0));
                Line[] subs = (Line[]) s.split(0);
                assertTrue(subs[0].size() == 0);
                assertTrue("subs[1].size()=" + subs[1].size(), subs[1].size() == 3);
            }
            {
                SpanLine s = new SpanLine();
                s.put(Span.obtain(0, 0));
                s.put(Span.obtain(1, 0));
                s.put(Span.obtain(2, 0));
                Line[] subs = (Line[]) s.split(1);
                assertTrue(subs[0].size() == 1);
                assertTrue(subs[1].size() == 2);
            }
            {
                SpanLine s = new SpanLine();
                s.put(Span.obtain(0, 0));
                s.put(Span.obtain(1, 0));
                s.put(Span.obtain(2, 0));
                Line[] subs = (Line[]) s.split(3);
                assertTrue("subs[0].size()="+subs[0].size()+",subs[1].size()="+subs[1].size(),subs[0].size() == 3);
                assertTrue(subs[1].size() == 0);
            }
        }
        {
            SpanLine s = new SpanLine();
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
            @Jailbreak  SpanLine s = new SpanLine();
            s.behaviourOnCellSplit = Line.SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 3, 0));
            s.put(Span.obtain(3, 3, 0));
            Line[] lines = (Line[]) s.split(4);
            assertTrue(lines[0].size() == 1);
            assertTrue(lines[0].get(0).column == 0);
            assertTrue(lines[0].get(0).size == 3);
            assertTrue(lines[1].size() == 0);
        }
        {
            //
            // ---*|**
            //
            // ---*
            // **
            //
            @Jailbreak  SpanLine s = new SpanLine();
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
            SpanLine s = new SpanLine();
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
                SpanLine s = new SpanLine();
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
                SpanLine s = new SpanLine();
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
                SpanLine s = new SpanLine();
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
    public void concat() {
        {
            {
                // -
                //  +
                SpanLine s = new SpanLine();
                s.put(Span.obtain(0, 1, 0));
                SpanLine s1 = new SpanLine();
                s1.put(Span.obtain(1, 1, 0));
                SpanLine aux = (SpanLine) SpanLine.concat(s,s1);
                assertTrue(aux.size() == 2);
                assertTrue(aux.get(0).size==1);
                assertTrue(aux.get(2).size == 1);
            }
            {
                //  -
                // +++
                SpanLine l = new SpanLine();
                l.put(Span.obtain(1, 1, 0));
                SpanLine l1 = new SpanLine();
                l1.put(Span.obtain(0, 3, 0));
                SpanLine aux = (SpanLine) SpanLine.concat(l,l1);
                assertTrue(aux.size() == 2);
                assertTrue(aux.get(1).size == 1);
                assertTrue(aux.get(2).size == 3);
            }
            {
                //  -=t
                // +-*
                SpanLine l = new SpanLine();
                l.put(Span.obtain(1, 1, 0));
                l.put(Span.obtain(2, 1, 0));
                l.put(Span.obtain(3, 1, 0));
                SpanLine l1 = new SpanLine();
                l1.put(Span.obtain(0, 1, 0));
                l1.put(Span.obtain(1, 1, 0));
                l1.put(Span.obtain(2, 1, 0));
                SpanLine aux = (SpanLine) SpanLine.concat(l,l1);
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
                SpanLine s1 = new SpanLine();
                SpanLine s2 = new SpanLine();
                s2.put(Span.obtain(0, 0));
                s2.put(Span.obtain(1, 0));
                SpanLine merged = (SpanLine) SpanLine.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                SpanLine s1 = new SpanLine();
                s1.put(Span.obtain(0, 0));
                SpanLine s2 = new SpanLine();
                s2.put(Span.obtain(0, 0));
                SpanLine merged = (SpanLine) SpanLine.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                SpanLine s1 = new SpanLine();
                SpanLine s2 = new SpanLine();
                s1.put(Span.obtain(0, 0));
                s1.put(Span.obtain(1, 0));
                SpanLine merged = (SpanLine) SpanLine.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
        }
        {
            int sz = r.nextUint(20);
            for(int a = 0; a < sz; a = a + 1) {
                SpanLine [] lines = new SpanLine[2];
                int finalSz = 0;
                for(int b = 0; b < lines.length; b=b+1) {
                    lines[b]=new SpanLine();
                    int sz2 = r.nextUint(50);
                    finalSz += sz2;
                    for(int c = 0; c < sz2; c = c + 1) {
                        lines[b].put(Span.obtain(c,0));
                    }
                }
                SpanLine sl = (SpanLine) SpanLine.concat(lines[0],lines[1]);
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
        SpanLine s1 = new SpanLine();
        s1.behaviourOnCellSplit = SPAN_SPLIT_EXTENDS;
        s1.put(Span.obtain(0, 2, 0));
        s1.put(Span.obtain(2, 2, 0));
        s1.put(Span.obtain(4, 5, 0));
        s1.insertContent(Span.obtain(5, 2, 0));
    }

    @Test
    @Ignore("TODO")
    public void removeContentBugs() {
        {
            //
            // ------**+
            //       **+
            //
            SpanLine s = new SpanLine();
            s.behaviourOnCellSplit = SPAN_SPLIT_INVALIDATE;
            s.put(Span.obtain(0, 6, 0));
            s.put(Span.obtain(6, 2, 0));
            s.put(Span.obtain(8, 1, 0));
            s.removeContent(2, 2);
            assertTrue(s.size() == 2);
            assertTrue("s.get(6).size=" + s.get(6).size, s.get(6).size == 1);
            assertTrue(s.get(8).size == 1);
        }
    }
    @Test
    public void removeContent() {
        {
            Logger.debug("Testing");
            //
            // |+
            // +
            //
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
            s1.removeContent(0,0);
            assertTrue(s1.size()==0);
        }
        {
            //
            // +|++-|
            //
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s1 = new SpanLine();
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
            SpanLine s = new SpanLine();
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
            SpanLine s = new SpanLine();
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
            SpanLine s = new SpanLine();
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
            SpanLine s = new SpanLine();
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
            SpanLine s = new SpanLine();
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
            SpanLine s = new SpanLine();
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
            SpanLine s = new SpanLine();
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
            SpanLine l = new SpanLine();
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
            SpanLine l = new SpanLine();
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
                SpanLine l = new SpanLine();
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
    }
}