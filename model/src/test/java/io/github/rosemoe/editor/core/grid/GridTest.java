package io.github.rosemoe.editor.core.grid;

import org.junit.Test;

import io.github.rosemoe.editor.core.grid.instances.color.Span;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class GridTest {
    Random r = new Random();

    @Test
    public void insertContent() {
        {
            //
            // |--++*****
            //
            //
            Grid map = new Grid();
            Line s1 = new Line();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            map.put(0,s1);
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
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;
            Line s1 = new Line();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            map.put(0,s1);
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
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;
            Line s1 = new Line();
            s1.put(Span.obtain(0, 2, 0));
            s1.put(Span.obtain(2, 2, 0));
            s1.put(Span.obtain(4, 5, 0));
            map.put(0,s1);
            map.insertContent(0,0,0);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
        }
        {
            //
            // --*|**
            // yymmmmm
            //
            // --*
            //   **
            // yymmmmm
            //
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;
            Line l = new Line(), l1 = new Line();
            l.put(Span.obtain(0, 2, 0));
            l.put(Span.obtain(2, 3, 0));
            l1.put(Span.obtain(0, 2, 0));
            l1.put(Span.obtain(2, 5, 0));
            map.put(0,l);
            map.put(1,l1);
            map.dump();
            map.insertContent(0, 3, 1, 2);
            assertTrue(map.size() == 3);
            System.out.println("OK3");
            map.dump();
            assertTrue(map.get(0).size() == 2);
            assertTrue(map.get(0).get(0).size==2);
            assertTrue(map.get(0).get(2).size==1);
            assertTrue(map.get(1).size()==1);
            assertTrue(map.get(1).get(2).size==2);
            assertTrue(map.get(2).get(0).size==2);
            assertTrue(map.get(2).get(2).size==5);
        }
    }
    @Test
    public void testRemoveContent() {
        {
            //
            // -|+|+
            //
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_SPLITTING;
            Line l = new Line();
            l.put(Span.obtain(0, 1, 0));
            l.put(Span.obtain(1, 2, 0));
            map.put(0,l);
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
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;
            Line l = new Line();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 2, 0));
            map.put(0, l);
            Line l1 = new Line();
            l1.put(Span.obtain(0, 5, 0));
            map.put(1, l1);
            map.removeContent(0, 2,1,3);
            map.dump();
            assertTrue("map.size()=" + map.size(), map.size() == 1);
            assertTrue("map.get(0).size()=" + map.get(0).size(), map.get(0).size()==2);
            assertTrue(map.get(0).size() == 2);
            assertTrue(map.get(0).get(0).size == 2);
            assertTrue(map.get(0).get(2).size == 2);
        }
        {
            // **|*--
            // ===|==
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_SPLITTING;
            Line l = new Line();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 2, 0));
            Line l1 = new Line();
            l1.put(Span.obtain(0, 5, 0));
            map.put(0, l);
            map.put(1, l1);
            System.out.println("<=== OK ===>");
            map.dump();
            map.removeContent(0,2,1,3);
            map.dump();
            assertTrue(map.size() == 1);
            assertTrue(map.get(0).size() == 2);
            assertTrue("map.get(0).get(0)=" +map.get(0).get(0).size, map.get(0).get(0).size == 2);
            assertTrue(map.get(0).get(2).size == 2);

        }
        {
            Logger.debug("< === >");
            // -|-
            // *|*
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_EXTENDS;
            Line l = new Line();
            l.put(Span.obtain(0, 2, 0));
            map.put(0,l);
            Line l2 = new Line();
            l2.put(Span.obtain(0, 2, 0));
            map.put(1, l2);
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
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_EXTENDS;
            Line l = new Line();
            l.put(Span.obtain(0, 1, 0));
            l.put(Span.obtain(1, 8, 0));
            l.put(Span.obtain(9, 1, 0));
            map.put(0, l);
            Line l1 = new Line();
            l1.put(Span.obtain(0, 5, 0));
            l1.put(Span.obtain(5, 2, 0));
            l1.put(Span.obtain(7, 5, 0));
            map.put(1, l1);
            Line l2 = new Line();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 4, 0));
            l2.put(Span.obtain(8, 4, 0));
            map.put(2,l2);
            map.removeContent(0,4,0,6);
            assertTrue(map.size() == 3);
            assertTrue(map.get(0).size() == 3);
        }
        {
            // *==|=======*
            // $$$$$**$$$|$
            // $$$$++++$$$$
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_EXTENDS;
            Line l = new Line();
            l.put(Span.obtain(0, 1, 0));
            l.put(Span.obtain(1, 8, 0));
            l.put(Span.obtain(9, 1, 0));
            map.put(0, l);
            Line l1 = new Line();
            l1.put(Span.obtain(0, 5, 0));
            l1.put(Span.obtain(5, 2, 0));
            l1.put(Span.obtain(7, 5, 0));
            map.put(1, l1);
            Line l2 = new Line();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 4, 0));
            l2.put(Span.obtain(8, 4, 0));
            map.put(2, l2);
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
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_SPLITTING;
            Line l = new Line();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 4, 0));
            map.put(0,l);
            Line l1 = new Line();
            l1.put(Span.obtain(0, 7, 0));
            map.put(1,l1);
            Line l2 = new Line();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 2, 0));
            map.put(2,l2);
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
    public void testRemoveContentBug() {
        {
            //
            // ---+|+++
            // xxxxxxx
            // zzzz|mm
            //
            // --- mm
            //
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;
            Line l = new Line();
            l.put(Span.obtain(0, 3, 0));
            l.put(Span.obtain(3, 4, 0));
            map.put(0,l);
            Line l1 = new Line();
            l1.put(Span.obtain(0, 7, 0));
            map.put(1,l1);
            Line l2 = new Line();
            l2.put(Span.obtain(0, 4, 0));
            l2.put(Span.obtain(4, 2, 0));
            map.put(2,l2);
            map.removeContent(0,4,2,4);
            System.out.println("|================================|");
            map.dump();
            assertTrue("map.size()=" + map.size(), map.size() == 1);
            assertTrue("map.get(0).size()=" + map.get(0).size(),map.get(0).size() == 3);
            assertTrue("map.get(0).get(0).size=" + map.get(0).get(0).size,map.get(0).get(0).size==3);
            assertTrue("map.get(0).get(3).size="+map.get(0).get(3).size, map.get(0).get(3).size==1);
            assertTrue(map.get(0).get(4).size == 2);
        }
    }

    @Test
    public void appendLine() {
        Grid s = new Grid();
        int sz = r.nextUint(400);
        s.appendLines(sz);
        assertTrue(sz == s.size());
    }

    static int count;
    @Test
    public void testForEachCell() {
        count = 0;
        Grid g = new Grid(){
            @Override
            public void handleForEachCell(Cell c) {
                count+=1;
            }
        };
        Line l = new Line();
        l.put(new BaseCell(0,10));
        l.put(new BaseCell(1,2));
        g.put(0,l);
        Line l1 = new Line();
        l1.put(new BaseCell(4,4));
        g.put(1,l1);
        g.forEachCell();
        assertTrue(count == 3);
    }
}