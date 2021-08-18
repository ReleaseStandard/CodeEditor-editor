package io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class SpanLineTest {

    Random r = new Random();
    @Test
    public void split() {
        // subs[0] | subs[1]
        // 0                 sz
        // |------------------
        // ------------|------
        // ------------------|
        //
        //
        {
            {
                SpanLine s = new SpanLine();
                s.add(Span.obtain(0, 0));
                s.add(Span.obtain(1, 0));
                s.add(Span.obtain(2, 0));
                SpanLine[] subs = s.split(0);
                assertTrue(subs[0].size() == 0);
                assertTrue("subs[1].size()=" + subs[1].size(), subs[1].size() == 3);
            }
            {
                SpanLine s = new SpanLine();
                s.add(Span.obtain(0, 0));
                s.add(Span.obtain(1, 0));
                s.add(Span.obtain(2, 0));
                SpanLine[] subs = s.split(1);
                assertTrue(subs[0].size() == 1);
                assertTrue(subs[1].size() == 2);
            }
            {
                SpanLine s = new SpanLine();
                s.add(Span.obtain(0, 0));
                s.add(Span.obtain(1, 0));
                s.add(Span.obtain(2, 0));
                SpanLine[] subs = s.split(3);
                assertTrue("subs[0].size()="+subs[0].size()+",subs[1].size()="+subs[1].size(),subs[0].size() == 3);
                assertTrue(subs[1].size() == 0);
            }
        }
        {
            SpanLine s = new SpanLine();
            int sz = r.nextUint(500);
            for (int a = 0; a < sz; a = a + 1) {
                s.add(Span.obtain(a, 0xFFFF0000));
            }
            int split = r.nextUint(sz + 1);
            SpanLine[] subs = s.split(split);
            assertTrue("sz=" + sz + ",s.size()=" + s.size() + ",subs[0].size()=" + subs[0].size() + ",split=" + split, subs[0].size() == split);
            assertTrue("sz=" + sz + ",s.size()=" + s.size() + ",split=" + split + ",subs[1].size()=" + subs[1].size() + ",(s.size()-split)=" + (s.size() - split), subs[1].size() == (s.size() - split));
        }
    }

    @Test
    public void concat() {
        {
            {
                SpanLine s1 = new SpanLine();
                SpanLine s2 = new SpanLine();
                s2.add(Span.obtain(0, 0));
                s2.add(Span.obtain(1, 0));
                SpanLine merged = SpanLine.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                SpanLine s1 = new SpanLine();
                s1.add(Span.obtain(0, 0));
                SpanLine s2 = new SpanLine();
                s2.add(Span.obtain(0, 0));
                SpanLine merged = SpanLine.concat(s1,s2);
                assertTrue(merged.size()==2);
            }
            {
                SpanLine s1 = new SpanLine();
                SpanLine s2 = new SpanLine();
                s1.add(Span.obtain(0, 0));
                s1.add(Span.obtain(1, 0));
                SpanLine merged = SpanLine.concat(s1,s2);
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
                        lines[b].add(Span.obtain(c,0));
                    }
                }
                SpanLine sl = SpanLine.concat(lines[0],lines[1]);
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
            s1.add(Span.obtain(0, 0, 2));
            s1.add(Span.obtain(2, 0, 2));
            s1.add(Span.obtain(4, 0, 5));
            s1.insertContent(Span.obtain(0,0, 2));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 4);
            assertTrue(s1.get(0).column == 0);
            assertTrue(s1.get(2).column == 2);
            assertTrue(s1.get(4).column == 4);
            assertTrue(s1.get(6).column == 6);
        }
        {
            //
            // --++**|***
            //
            //
            SpanLine s1 = new SpanLine();
            s1.add(Span.obtain(0, 0, 2));
            s1.add(Span.obtain(2, 0, 2));
            s1.add(Span.obtain(4, 0, 5));
            s1.insertContent(Span.obtain(5,0, 2));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 4);
        }
        {
            SpanLine s1 = new SpanLine();
            s1.add(Span.obtain(0, 0, 2));
            s1.add(Span.obtain(2, 0, 2));
            s1.add(Span.obtain(4, 0, 5));
            s1.insertContent(Span.obtain(0,0, 0));
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
        }
    }

    @Test
    public void removeContent() {
        {
            //
            // --**ttttt
            // -*ttttt
            //
            SpanLine s1 = new SpanLine();
            s1.add(Span.obtain(0, 0, 2));
            s1.add(Span.obtain(2, 0, 2));
            s1.add(Span.obtain(4, 0, 5));
            s1.removeContent(1, 2);
            assertTrue("s1.size()=" + s1.size(), s1.size() == 3);
            Span[] arr = s1.concurrentSafeGetValues();
            assertTrue(arr[0].column == 0);
            assertTrue(arr[0].size == 1);
            assertTrue(arr[1].column == 1);
            assertTrue(arr[1].size == 1);
            assertTrue(arr[2].column == 2);
            assertTrue(arr[2].size == 5);
        }
        {
            //
            // ------**+
            // ----**+
            //
            SpanLine s = new SpanLine();
            s.add(Span.obtain(0, 0, 6));
            s.add(Span.obtain(6, 0, 2));
            s.add(Span.obtain(8, 0, 1));
            s.removeContent(2, 2);
            assertTrue(s.size() == 3);
            Span[] arr = s.concurrentSafeGetValues();
            assertTrue(arr[0].column == 0);
            assertTrue(arr[0].size == 4);
        }
        {
            //
            // ------**+
            // ------+
            //
            SpanLine s = new SpanLine();
            s.add(Span.obtain(0, 0, 6));
            s.add(Span.obtain(6, 0, 2));
            s.add(Span.obtain(8, 0, 1));
            s.removeContent(6, 2);
            assertTrue(s.size() == 2);
            Span[] arr = s.concurrentSafeGetValues();
            assertTrue(arr[arr.length-1].column == 6);
            assertTrue(arr[arr.length-1].size == 1);
        }
        {
            //
            // ------**+
            // ------**+
            //
            SpanLine s = new SpanLine();
            s.add(Span.obtain(0, 0, 6));
            s.add(Span.obtain(6, 0, 2));
            s.add(Span.obtain(8, 0, 1));
            s.removeContent(0, 0);
            assertTrue(s.size() == 3);
        }
    }
}