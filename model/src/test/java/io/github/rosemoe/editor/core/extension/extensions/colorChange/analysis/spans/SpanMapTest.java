package io.github.rosemoe.editor.core.extension.extensions.colorChange.analysis.spans;

import org.junit.Ignore;
import org.junit.Test;

import io.github.rosemoe.editor.core.Line;
import io.github.rosemoe.editor.core.color.spans.Span;
import io.github.rosemoe.editor.core.color.spans.SpanLine;
import io.github.rosemoe.editor.core.color.spans.SpanMap;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class SpanMapTest {

    Random r = new Random();
    @Test
    public void appendLine() {
        SpanMap s = new SpanMap();
        int sz = r.nextUint(400);
        s.appendLines(sz);
        assertTrue(sz == s.size());
    }

    @Test
    public void insertLines() {
    }

    @Test
    public void insertContent() {
        {
            //
            // |--++*****
            //
            //
            SpanMap map = new SpanMap();
            SpanLine s1 = new SpanLine();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            map.add(0,s1);
            map.insertContent(0,0,2);
            assertTrue(map.size() == 1);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
            assertTrue(s1.get(2).column == 2);
            assertTrue(s1.get(4).column == 4);
            assertTrue(s1.get(6).column == 6);
        }
        {
            //
            // --++*|****
            //
            //
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_INVALIDATE;
            SpanLine s1 = new SpanLine();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            map.add(0,s1);
            map.insertContent(0,5,6);
            System.out.println("<=== map.dump() ===>");
            map.dump();
            assertTrue("s1.size()=" + s1.size(), s1.size() == 2);
        }
        {
            // |**--+++++
            //
            //
            // **--+++++
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_INVALIDATE;
            SpanLine s1 = new SpanLine();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            map.add(0,s1);
            map.insertContent(0,0,0);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
        }
        {
            //
            // --*|**
            // yymmmmm
            //
            // --
            //
            // yymmmmm
            //
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_INVALIDATE;
            SpanLine l = new SpanLine(), l1 = new SpanLine();
            l.put(Span.obtain(0, 2, 0));
            l.put(Span.obtain(2, 3, 0));
            l1.put(Span.obtain(0, 2, 0));
            l1.put(Span.obtain(2, 5, 0));
            map.add(0,l);
            map.add(1,l1);
            map.insertContent(0, 3, 1, 2);
            assertTrue(map.size() == 3);
            SpanLine[] lines = map.getLines();
            System.out.println("OK3");
            map.dump();
            assertTrue(lines[0].get(0).column==0);
            assertTrue(lines[0].get(0).size==2);
            assertTrue(lines[1].size()==0);
            assertTrue(lines[2].get(0).column==0);
            assertTrue("lines[2].get(0)=" + lines[2].get(0), lines[2].get(0).size==2);
            assertTrue(lines[2].get(2).column==2);
            assertTrue(lines[2].get(2).size==5);
        }
    }

    @Test
    public void testRemoveContent() {
        {
            //
            // -|+|+
            //
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_SPLITTING;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 1, 0));
            l.put(Span.obtain(1, 2, 0));
            map.add(0,l);
            assertTrue(map.size() == 1);
            assertTrue(map.get(0).size() == 2);
            assertTrue(map.get(0).get(0).size==1);
            assertTrue(map.get(0).get(0).column==0);
            assertTrue(map.get(0).get(1).column==1);
            assertTrue(map.get(0).get(1).size==2);
            map.removeContent(0, 1,0,2);
            map.dump();
            assertTrue(map.size() == 1);
            assertTrue(map.get(0).size() == 2);
            assertTrue(map.get(0).get(0).size==1);
            assertTrue(map.get(0).get(0).column==0);
            assertTrue(map.get(0).get(1).column==1);
            assertTrue(map.get(0).get(1).size==1);
        }
        {
            // ++|+--
            // ***|**
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_INVALIDATE;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 2, 0));
            map.add(0, l);
            SpanLine l1 = new SpanLine();
            l1.put(Span.obtain(0, 5, 0));
            map.add(1, l1);
            map.removeContent(0, 2,1,3);
            assertTrue("map.size()=" + map.size(), map.size() == 1);
            assertTrue("map.get(0).size()=" + map.get(0).size(), map.get(0).size()==0);
        }
        {
            // **|*--
            // ===|==
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_SPLITTING;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 2, 0));
            SpanLine l1 = new SpanLine();
            l1.put(Span.obtain(0, 5, 0));
            map.add(0, l);
            map.add(1, l1);
            map.removeContent(0,2,1,3);
            assertTrue(map.size() == 1);
            assertTrue(map.get(0).size() == 2);
            assertTrue("map.get(0).get(0)=" +map.get(0).get(0).size, map.get(0).get(0).size == 2);
            assertTrue(map.get(0).get(2).size == 2);

        }
        {
            Logger.debug("< === >");
            // -|-
            // *|*
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_EXTENDS;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 2, 0));
            map.add(0,l);
            SpanLine l2 = new SpanLine();
            l2.put(Span.obtain(0, 2, 0));
            map.add(1, l2);
            assertTrue(map.size() == 2);
            assertTrue(map.get(0).size() == 1);
            assertTrue(map.get(0).get(0).size == 2);
            assertTrue(map.get(0).get(0).column == 0);
            assertTrue(map.get(1).size() == 1);
            assertTrue(map.get(1).get(0).size == 2);
            assertTrue(map.get(1).get(0).column == 0);
            map.removeContent(0,1,1,1);
            map.dump();
            assertTrue(map.size() == 1);
            assertTrue("map.get(0).size()=" + map.get(0).size(), map.get(0).size() == 2);
            assertTrue(map.get(0).get(0).size == 1);
            assertTrue(map.get(0).get(1).size == 1);
        }
        {
            // *===|==|===*
            // $$$$$--$$$$$
            // $$$$++++$$$$
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_EXTENDS;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 1, 0));
            l.put(Span.obtain(1, 8, 0));
            l.put(Span.obtain(9, 1, 0));
            map.add(0, l);
            SpanLine l1 = new SpanLine();
            l1.put(Span.obtain(0, 5, 0));
            l1.put(Span.obtain(5, 2, 0));
            l1.put(Span.obtain(7, 5, 0));
            map.add(1, l1);
            SpanLine l2 = new SpanLine();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 4, 0));
            l2.put(Span.obtain(8, 4, 0));
            map.add(2,l2);
            map.removeContent(0,4,0,6);
            assertTrue(map.size() == 3);
            assertTrue(map.get(0).size() == 3);
        }
        {
            // *==|=======*
            // $$$$$**$$$|$
            // $$$$++++$$$$
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_EXTENDS;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 1, 0));
            l.put(Span.obtain(1, 8, 0));
            l.put(Span.obtain(9, 1, 0));
            map.add(0, l);
            SpanLine l1 = new SpanLine();
            l1.put(Span.obtain(0, 5, 0));
            l1.put(Span.obtain(5, 2, 0));
            l1.put(Span.obtain(7, 5, 0));
            map.add(1, l1);
            SpanLine l2 = new SpanLine();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 4, 0));
            l2.put(Span.obtain(8, 4, 0));
            map.add(2, l2);
            map.removeContent(0, 3, 1, 8);
            assertTrue(map.size() == 2);
            map.get(0).dump();
            assertTrue(map.get(0).size() == 3);
            assertTrue(map.get(1).size() == 3);
        }
        {
            //
            // ---+|+++
            // xxxxxxx
            // zzzz|mm
            //
            // ---+mm
            //
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_SPLITTING;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 4, 0));
            map.add(0,l);
            SpanLine l1 = new SpanLine();
            l1.put(Span.obtain(0, 7, 0));
            map.add(1,l1);
            SpanLine l2 = new SpanLine();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 2, 0));
            map.add(2,l2);
            map.removeContent(0,4,2,4);
            System.out.println("|================================|");
            map.dump();
            assertTrue("map.size()=" + map.size(), map.size() == 1);
            assertTrue("map.get(0).size()=" + map.get(0).size(),map.get(0).size() == 3);
            assertTrue("map.get(0).get(0).size=" + map.get(0).get(0).size,map.get(0).get(0).size==3);
            assertTrue("map.get(0).get(3).size="+map.get(0).get(3).size, map.get(0).get(3).size==1);
            assertTrue("map.get(0).get(4).size="+map.get(0).get(4).size, map.get(0).get(4).size==2);
        }
    }
    @Test
    @Ignore("SPAN_SPLIT_INVALIDATE on a SpanMap could cause implevisible span shift, you should better use SPAN_SPLIT_SPLITTING")
    public void testRemoveContentBug() {
        {
            //
            // ---+|+++
            // xxxxxxx
            // zzzz|mm
            //
            // --- mm
            //
            SpanMap map = new SpanMap();
            map.behaviourOnCellSplit = Line.SPAN_SPLIT_INVALIDATE;
            SpanLine l = new SpanLine();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 4, 0));
            map.add(0,l);
            SpanLine l1 = new SpanLine();
            l1.put(Span.obtain(0, 7, 0));
            map.add(1,l1);
            SpanLine l2 = new SpanLine();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 2, 0));
            map.add(2,l2);
            map.removeContent(0,4,2,4);
            System.out.println("|================================|");
            map.dump();
            assertTrue("map.size()=" + map.size(), map.size() == 1);
            assertTrue("map.get(0).size()=" + map.get(0).size(),map.get(0).size() == 2);
            assertTrue("map.get(0).get(0).size=" + map.get(0).get(0).size,map.get(0).get(0).size==3);
            assertTrue("map.get(0).get(0).column="+map.get(0).get(0).column, map.get(0).get(0).column==0);
            assertTrue("map.get(0).get(4).size="+map.get(0).get(4).size, map.get(0).get(4).size==2);
        }
    }
}












