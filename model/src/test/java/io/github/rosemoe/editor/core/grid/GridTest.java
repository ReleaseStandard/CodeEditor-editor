package io.github.rosemoe.editor.core.grid;

import org.junit.Ignore;
import org.junit.Test;

import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.grid.instances.SpanCell;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class GridTest {
    Random r = new Random();

    @Test
    @Ignore("This bug is known")
    public void testInsertContentBug() {
        {
            // --++*|****
            // --++*o****
            Grid<BaseCell> map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_SPLITTING;
            Line s1 = new Line();
            s1.append(new BaseCell( 2));
            s1.append(new BaseCell( 2));
            s1.append(new BaseCell( 5));
            map.append(s1);
            map.insertContent(0,5,6);
            System.out.println("<=== map.dump() ===>");
            map.dump();
            assertTrue("s1.size()=" + s1.size(), s1.size() == 5);
        }
    }
    @Test
    public void insertContent() {
        {
            // ****|****
            // xx
            //
            // oo
            //
            // ****oo****
            // xx
            Grid<BaseCell> g = new Grid<>(), g1 = new Grid<>();
            g.behaviourOnCellSplit = Cell.SPLIT_SPLITTING;
            Line<BaseCell> l1 = new Line<>(), l2 = new Line<>(), l3 = new Line<>();
            l1.append(new BaseCell(8));
            l2.append(new BaseCell(2));
            l3.append(new BaseCell(2));
            g1.append(l3);
            g.append(l1,l2);
            g.insertContent(0,4, g1);
        }
        {
            //
            // |--++*****
            //
            //
            Grid map = new Grid();
            Line s1 = new Line();
            s1.put(new BaseCell(0, 2));
            s1.put(new BaseCell(2, 2));
            s1.put(new BaseCell(4, 5));
            map.put(0,s1);
            map.insertContent(0,0,2);
            assertTrue(map.size() == 1);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
            assertTrue(s1.get(2).column == 2);
            assertTrue(s1.get(4).column == 4);
            assertTrue(s1.get(6).column == 6);
        }
        {
            // |**--+++++
            //
            //
            // **--+++++
            Grid map = new Grid();
            map.behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;
            Line s1 = new Line();
            s1.put(new BaseCell(0, 2));
            s1.put(new BaseCell(2, 2));
            s1.put(new BaseCell(4, 5));
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
            l.put(new BaseCell(0, 2));
            l.put(new BaseCell(2, 3));
            l1.put(new BaseCell(0, 2));
            l1.put(new BaseCell(2, 5));
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
            l.put(new BaseCell(0, 1));
            l.put(new BaseCell(1, 2));
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
            l.put(new BaseCell(0, 3));
            l.put(new BaseCell(3, 2));
            map.put(0, l);
            Line l1 = new Line();
            l1.put(new BaseCell(0, 5));
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
            l.put(new BaseCell(0, 3));
            l.put(new BaseCell(3, 2));
            Line l1 = new Line();
            l1.put(new BaseCell(0, 5));
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
            l.put(new BaseCell(0, 2));
            map.put(0,l);
            Line l2 = new Line();
            l2.put(new BaseCell(0, 2));
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
            l.put(new BaseCell(0, 1));
            l.put(new BaseCell(1, 8));
            l.put(new BaseCell(9, 1));
            map.put(0, l);
            Line l1 = new Line();
            l1.put(new BaseCell(0, 5));
            l1.put(new BaseCell(5, 2));
            l1.put(new BaseCell(7, 5));
            map.put(1, l1);
            Line l2 = new Line();
            l2.put(new BaseCell(0, 4));
            l2.put(new BaseCell(4, 4));
            l2.put(new BaseCell(8, 4));
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
            l.put(new BaseCell(0, 1));
            l.put(new BaseCell(1, 8));
            l.put(new BaseCell(9, 1));
            map.put(0, l);
            Line l1 = new Line();
            l1.put(new BaseCell(0, 5));
            l1.put(new BaseCell(5, 2));
            l1.put(new BaseCell(7, 5));
            map.put(1, l1);
            Line l2 = new Line();
            l2.put(new BaseCell(0, 4));
            l2.put(new BaseCell(4, 4));
            l2.put(new BaseCell(8, 4));
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
            l.put(new BaseCell(0, 3));
            l.put(new BaseCell(3, 4));
            map.put(0,l);
            Line l1 = new Line();
            l1.put(new BaseCell(0, 7));
            map.put(1,l1);
            Line l2 = new Line();
            l2.put(new BaseCell(0, 4));
            l2.put(new BaseCell(4, 2));
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
            l.put(new BaseCell(0, 3));
            l.put(new BaseCell(3, 4));
            map.put(0,l);
            Line l1 = new Line();
            l1.put(new BaseCell(0, 7));
            map.put(1,l1);
            Line l2 = new Line();
            l2.put(new BaseCell(0, 4));
            l2.put(new BaseCell(4, 2));
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
        s.append(sz);
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

    @Test
    public void testAppend() {
        {
            Grid g = new Grid();
            g.append(new Line());
            assertTrue(g.size() == 1);
        }
        {
            Grid g = new Grid();
            Line l = new Line();
            l.append(new BaseCell(2));
            l.append(new BaseCell(10));
            g.append(l);
            assertTrue(g.size() == 1);
        }
    }
    @Test
    public void testSubGrid() {
        {
            // +++|+---**,,|...
            //     +---**,,
            Grid g = new Grid();
            Line l = new Line();
            l.append(new BaseCell(4));
            l.append(new BaseCell(3));
            l.append(new BaseCell(2));
            l.append(new BaseCell(2));
            l.append(new BaseCell(3));
            g.dump();
            g.append(l);
            g.dump();
            Grid g1 = g.subGrid(0,3,0,11);
            assertTrue(g1.size() == 1);
            assertTrue(g1.get(0).size()==4);
            assertTrue(g1.get(0).get(3).size == 1);
            assertTrue(g1.get(0).get(4).size == 3);
            assertTrue(g1.get(0).get(7).size == 2);
            assertTrue(g1.get(0).get(9).size == 2);
        }
        {
            // --+++|*
            // $$:::-
            // ====+|+
            Grid g = new Grid();
            Line l1 = new Line(),l2 = new Line(),l3 = new Line();
            l1.append(new BaseCell(2));
            l1.append(new BaseCell(3));
            l1.append(new BaseCell(1));
            l2.append(new BaseCell(2));
            l2.append(new BaseCell(3));
            l2.append(new BaseCell(1));
            l3.append(new BaseCell(4));
            l3.append(new BaseCell(2));
            g.append(l1);
            g.append(l2);
            g.append(l3);
            Grid g1 = g.subGrid(0,5,2,5);
            assertTrue(g1.size()==3);
            assertTrue(g1.get(0).get(5).size==1);
            assertTrue(g1.get(1).get(0).size==2);
            assertTrue(g1.get(1).get(2).size==3);
            assertTrue(g1.get(1).get(5).size==1);
            assertTrue(g1.get(2).get(0).size==4);
            assertTrue(g1.get(2).get(4).size==1);
        }
    }
}