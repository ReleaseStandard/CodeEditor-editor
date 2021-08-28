package io.github.rosemoe.editor.core.content;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.analyze.analyzer.CodeAnalyzer;
import io.github.rosemoe.editor.core.content.processors.indexer.CachedContentIndexer;
import io.github.rosemoe.editor.core.content.processors.indexer.ContentIndexer;
import io.github.rosemoe.editor.core.content.processors.indexer.NoCacheContentIndexer;
import io.github.rosemoe.editor.core.grid.BaseCell;
import io.github.rosemoe.editor.core.grid.Cell;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class CodeAnalyzerResultContentTest {
    Random r = new Random();

    @Test
    public void testInsert() {
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.insert(0, 0, "test");
            assertTrue(content.get(0).getWidth() == 4);
        }
        {
            for(int a=0; a< 10; a=a+1) {
                int bound = 10 + r.nextUint(100);
                String s = r.nextString(bound);
                CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
                content.insert(0, 0, s);
                assertTrue(content.get(0).getWidth() == s.length());
            }
        }
    }

    @Test
    public void testReflexivity() {
        CodeAnalyzerResultContent c1 = new CodeAnalyzerResultContent();
        CodeAnalyzerResultContent c2 = new CodeAnalyzerResultContent();
        for(int a = 0; a < r.nextUint(10); a=a+1) {
            String text = r.nextString(r.nextUint(100));
            c1.append(text);
            c2.append(text);
        }
        assertTrue(c1.equals(c2) && c2.equals(c1));
    }
    @Test
    public void testEquals() {
        {
            // aze
            // az
            // a
            //
            // aze
            // az
            // a
            CodeAnalyzerResultContent c1 = new CodeAnalyzerResultContent();
            c1.append("aze");
            c1.append("az");
            c1.append("a");
            CodeAnalyzerResultContent c2 = new CodeAnalyzerResultContent();
            c2.append("aze");
            c2.append("az");
            c2.append("a");
            c1.dump();
            c2.dump();
            assertTrue(c1.equals(c2));
        }
        {
            // aze
            // a
            //
            // a
            // aze
            CodeAnalyzerResultContent c1 = new CodeAnalyzerResultContent();
            c1.append("aze");
            c1.append("a");
            CodeAnalyzerResultContent c2 = new CodeAnalyzerResultContent();
            c2.append("a");
            c2.append("aze");
            assertFalse(c1.equals(c2));
        }
    }

    @Test
    public void testCheckLine() {
        {
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.checkLine(0);
            content.checkLineAndColumn(0,0);
        }
        {
            // ok
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("ok");
            content.checkLine(0);
            content.checkLine(1);
            content.checkLineAndColumn(0,0);
            content.checkLineAndColumn(0,1);
            content.checkLineAndColumn(0,2);
        }
        {
            // abc
            // ab
            // a
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("abc");
            content.append("ab");
            content.append("a");
            content.checkLine(0);
            content.checkLine(1);
            content.checkLine(2);
            content.checkLine(3);
            content.checkLineAndColumn(0,0);
            content.checkLineAndColumn(0,1);
            content.checkLineAndColumn(0,2);
            content.checkLineAndColumn(0,3);
            content.checkLineAndColumn(1,0);
            content.checkLineAndColumn(1,1);
            content.checkLineAndColumn(1,2);
            content.checkLineAndColumn(2,0);
            content.checkLineAndColumn(2,1);
            content.checkLineAndColumn(3,0);
        }
        {
            //
            // a
            // abc
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append();
            content.append("a");
            content.append("abc");
            content.checkLine(0);
            content.checkLine(1);
            content.checkLine(2);
            content.checkLine(3);
            content.checkLineAndColumn(0,0);
            content.checkLineAndColumn(1,0);
            content.checkLineAndColumn(1,1);
            content.checkLineAndColumn(2,0);
            content.checkLineAndColumn(2,1);
            content.checkLineAndColumn(2,2);
            content.checkLineAndColumn(2,3);
            content.checkLineAndColumn(3,0);
        }
    }

    @Test
    public void testLength() {
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            int total = 0;
            for(int a = 0; a < r.nextUint(100); a=a+1) {
                int sz = r.nextUint(100);
                content.append(r.nextString(sz));
                total += sz;
            }
            assertTrue(content.length() == total);
        }
    }
    @Test
    public void testCharAt() {
        {
            // -a*z$e$
            // azez123
            // aze
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("-a*z$e$");
            content.append("azez123");
            content.append("aze");
            assertTrue( content.charAt(3) == 'z');
        }
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.beginStreamCharGetting(0);
            int total = 0;
            for(int a = 0; a < r.nextUint(20); a=a+1) {
                Line<ContentCell> l = new Line<>();
                int bound = r.nextUint(20);
                total += bound;
                for(int b = 0; b < bound; b=b+1) {
                    l.append(new ContentCell('a'));
                }
                content.append(l);
            }
            for(int a = 0; a < 10 + r.nextUint(20); a=a+1) {
                int i = r.nextUint(total);
                assertTrue("i=" + i + ",a=" + a + ",total="+total, content.charAtNaive(i) == content.charAtWithIndexer(i));
            }
        }
    }
    @Test
    public void testCharAtWithIndexer() {
        {
            //
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.beginStreamCharGetting(0);
            try {
                content.charAtWithIndexer(0);
                assertTrue(false);
            } catch (RuntimeException e) {
                assertTrue(true);
            }
        }
        {
            //
            // aze
            // aa
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append();
            content.append("aze");
            content.append("aa");
            assertTrue(content.charAtWithIndexer(0) == 'a');
            assertTrue(content.charAtWithIndexer(1) == 'z');
            assertTrue(content.charAtWithIndexer(2) == 'e');
            assertTrue(content.charAtWithIndexer(3) == 'a');
            assertTrue(content.charAtWithIndexer(4) == 'a');
            try {
                content.charAtWithIndexer(5);
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }
        }
    }
    @Test
    public void testCharAtNaive() {
        {
            //
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            try {
                content.charAtNaive(0);
                assertTrue(false);
            } catch (RuntimeException e) {
                assertTrue(true);
            }
        }
        {
            //
            // aze
            // aa
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append();
            content.append("aze");
            content.append("aa");
            assertTrue(content.charAtNaive(0) == 'a');
            assertTrue(content.charAtNaive(1) == 'z');
            assertTrue(content.charAtNaive(2) == 'e');
            assertTrue(content.charAtNaive(3) == 'a');
            assertTrue(content.charAtNaive(4) == 'a');
            try {
                content.charAtNaive(5);
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }
        }
    }
    @Test
    public void testAppend() {
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            int totalSize = 0;
            int nLines = r.nextUint(20);
            for (int a = 0; a < nLines; a = a + 1) {
                String txt = r.nextString(r.nextUint(20));
                content.append(txt);
                totalSize += txt.length();
            }

            int verif = 0;
            for (Line<ContentCell> l : content) {
                verif += l.getWidth();
            }
            assertTrue(content.size() == nLines);
            assertTrue(verif == totalSize);
        }
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            int i = content.append("test");
            assertTrue("i=" + i ,i==0);
            assertTrue(content.get(0).getWidth() == 4);
        }
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("ok\nok\na");
            assertTrue(content.size()==3);
        }
    }

    @Test
    public void testSubSequence() {
        {
            // aazez
            // aa
            // er
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("aazez");
            content.append("aa");
            content.append("er");
            String allSeq = "aazezaaer";
            ContentIndexer[] idxs = new ContentIndexer[] {
                new CachedContentIndexer(content),
                new NoCacheContentIndexer(content)
            };
            for(ContentIndexer ci : idxs) {
                content.contentIndexer = ci;
                Set<Set<Integer>> combinations = Sets.combinations(ImmutableSet.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 2);
                for(Set<Integer> combination : combinations) {
                    Integer [] arr = combination.toArray(new Integer[2]);
                    System.out.println("ci="+ci.getClass()+"arr[0]="+arr[0]+"arr[1]="+arr[1]);
                    CharSequence res = content.subSequence(arr[0], arr[1]);
                    CharSequence res2 = allSeq.subSequence(arr[0],arr[1]);
                    System.out.println("res="+res+",res2="+res2);
                    assertTrue(res.equals(res2));
                }
            }
        }
    }
}