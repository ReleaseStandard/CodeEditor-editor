package io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans.SpanLine.SPAN_SPLIT_INVALIDATE;
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
    public void splitLine() {
        {
            //
            // ----------
            //
            // --
            // --------
            //
            SpanMap s = new SpanMap();
            SpanLine sl = new SpanLine();
            sl.add(Span.obtain(0, 0, 10));
            s.add(0, sl);
            System.out.println("DUMPING");
            s.dump();
            System.out.println("DUMPING END");
            s.splitLine(0, 2);
            System.out.println("After");
            s.dump();
            assertTrue(s.size() == 2);
            SpanLine l1 = s.get(0);
            SpanLine l2 = s.get(1);
            assertTrue(l1.size() == 2);
            assertTrue(l2.size() == 8);
        }
    }

    @Test
    public void insertLines() {
    }

    @Test
    public void cutLines() {
        {
            //
            // --
            //
            // -
            // -
            //
            SpanMap s = new SpanMap();
            SpanLine sl = new SpanLine();
            sl.add(Span.obtain(0, 0, 2));
            s.add(0, sl);
            s.cutLines(0,1,0, 1);
            s.dump();
            assertTrue(s.size() == 2);
        }
        {
            //
            // --+++
            //
            // -
            // -+++
            //
            SpanMap s = new SpanMap();
            SpanLine sl = new SpanLine();
            sl.add(Span.obtain(0, 0, 2));
            sl.add(Span.obtain(2, 0, 3));
            s.add(0, sl);
            s.cutLines(0,1,0, 1);
            assertTrue(s.size() == 2);
        }
        {
            //
            // --+++
            // *****
            //
            // -
            // ***
            //
            SpanMap s = new SpanMap();
            SpanLine sl = new SpanLine();
            sl.add(Span.obtain(0, 0, 2));
            sl.add(Span.obtain(2, 0, 3));
            s.add(0, sl);
            sl = new SpanLine();
            sl.add(Span.obtain(0, 0, 5));
            s.cutLines(0,1,1, 2);
            assertTrue(s.size() == 2);
            assertTrue(s.lines[0].size() == 1);
            assertTrue(s.lines[1].size() == 1);
            assertTrue(s.lines[1].line.get(0).size == 3);
        }
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
            s1.add(Span.obtain(0, 0, 2));
            s1.add(Span.obtain(2, 0, 2));
            s1.add(Span.obtain(4, 0, 5));
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
            SpanLine s1 = new SpanLine();
            s1.behaviourOnSpanSplit = SPAN_SPLIT_INVALIDATE;
            s1.add(Span.obtain(0, 0, 2));
            s1.add(Span.obtain(2, 0, 2));
            s1.add(Span.obtain(4, 0, 5));
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
            SpanLine s1 = new SpanLine();
            s1.add(Span.obtain(0, 0, 2));
            s1.add(Span.obtain(2, 0, 2));
            s1.add(Span.obtain(4, 0, 5));
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
            // xx
            // yymmmmm
            //
            SpanMap map = new SpanMap();
            SpanLine l = new SpanLine(), l1 = new SpanLine();
            l.add(Span.obtain(0, 0, 2));
            l.add(Span.obtain(2, 0, 3));
            l1.add(Span.obtain(0,0,2));
            l1.add(Span.obtain(2, 0, 5));
            map.add(0,l);
            map.add(1,l1);
            map.insertContent(0, 3, 1, 2);
            assertTrue(map.size() == 3);
            SpanLine[] lines = map.getLines();
            System.out.println("OK3");
            lines[0].dump();
            assertTrue(lines[0].get(0).column==0);
            assertTrue(lines[0].get(0).size==2);
            assertTrue(lines[0].get(2).column==2);
            assertTrue(lines[0].get(2).size==1);
            assertTrue(lines[1].get(2).column==2);
            assertTrue(lines[1].get(2).size==2);
            assertTrue(lines[2].get(0).column==0);
            assertTrue(lines[2].get(0).size==2);
            assertTrue(lines[2].get(2).column==2);
            assertTrue(lines[2].get(2).size==5);
        }
    }
}