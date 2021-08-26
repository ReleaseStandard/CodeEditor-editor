package io.github.rosemoe.editor.core.content.processors.indexer;

import org.junit.*;

import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;
import static org.junit.Assert.*;

public class CachedContentIndexerTest {

    Random r = new Random();

    public CodeAnalyzerResultContent createSample() {
        // -a*z$e$
        // azez123
        // aze
        CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
        Line<ContentCell> l1 = new Line<>(), l2 = new Line<>(), l3 = new Line<>();
        l1.append(new ContentCell('-'));
        l1.append(new ContentCell('a'));
        l1.append(new ContentCell('*'));
        l1.append(new ContentCell('z'));
        l1.append(new ContentCell('$'));
        l1.append(new ContentCell('e'));
        l1.append(new ContentCell('$'));
        l2.append(new ContentCell('a'));
        l2.append(new ContentCell('z'));
        l2.append(new ContentCell('e'));
        l2.append(new ContentCell('z'));
        l2.append(new ContentCell('1'));
        l2.append(new ContentCell('2'));
        l2.append(new ContentCell('3'));
        l3.append(new ContentCell('a'));
        l3.append(new ContentCell('z'));
        l3.append(new ContentCell('e'));
        content.append(l1,l2,l3);
        return content;
    }

    @Test
    public void testFindNearest() {
        {
            // a|ze
            // z
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("aze");
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            indexer.cache.add(new CharPosition(0, 0, 0));
            indexer.cache.add(new CharPosition(0, 1, 1));
            indexer.cache.add(new CharPosition(0,2,2));
            CharPosition cpRes = indexer.findNearest(new CharPosition(1));
            assertTrue(cpRes!=null);
            assertTrue("cpRes.column=" + cpRes.column, cpRes.column == 1);
            assertTrue(cpRes.line == 0);
            assertTrue(cpRes.index == 1);
        }
        {
            // aze|f
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("azef");
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            indexer.cache.add(new CharPosition( 0));
            indexer.cache.add(new CharPosition( 1));
            indexer.cache.add(new CharPosition(2));
            CharPosition cpRes = indexer.findNearest(new CharPosition(1));
            assertTrue(cpRes!=null);
            cpRes.dump();
            assertTrue("cpRes.column=" + cpRes.column, cpRes.column == 1);
            assertTrue(cpRes.line == 0);
            assertTrue(cpRes.index == 1);
        }
    }

    @Test
    public void getChars() {
        {
            //
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            assertTrue(-1 == indexer.getCharIndex(0,0));
            assertTrue(-1 == indexer.getCharLine(0));
            assertTrue(-1 == indexer.getCharColumn(0));
        }
        {
            // abc
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("abc");
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            int i = indexer.getCharIndex(0,0);
            assertTrue("i="+i,0 == i);
            assertTrue(0 == indexer.getCharLine(0));
            assertTrue(0 == indexer.getCharColumn(0));
            assertTrue(2 == indexer.getCharIndex(0,2));
            assertTrue(0 == indexer.getCharLine(2));
            assertTrue(2 == indexer.getCharColumn(2));
        }
    }

    @Test
    public void getCharPosition() {
        {
            // abc
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("abc");
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            assertTrue(content.size()==1);
            assertTrue(content.get(0).size() == 3);
            assertTrue(content.get(0).get(0).c == 'a');
            CharPosition cp = indexer.getCharPosition(0);
            assertTrue(cp != null);
            cp.dump();
            assertTrue(cp.column == 0 && cp.line == 0);
        }
        {
            //
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            CharPosition cp = indexer.getCharPosition(0);
            assertTrue(content.size()==0 && cp == null);
        }
        {
            CodeAnalyzerResultContent content = createSample();
            content.dump();
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            CharPosition cp = indexer.getCharPosition(0);
            assertTrue(cp.column == 0 && cp.line == 0);
            cp = indexer.getCharPosition(4);
            assertTrue(cp.column == 4 && cp.line == 0);
            cp = indexer.getCharPosition(7);
            assertTrue("cp.column="+cp.column+",cp.line="+cp.line, cp.column == 0 && cp.line == 1);
            indexer.findNearestByIndex(0);
        }
    }
    @Test
    public void testCache() {
        // aaaazzz
        // zeazeaze
        // azezzz
        @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
        content.append("aaaazzz");
        content.append("zeazeaze");
        content.append("azezzz");
        @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
        assertTrue(indexer.cache.add(new CharPosition(0)));
        CharPosition cp = indexer.cache.ceiling(new CharPosition(0));
        assertTrue(cp != null);
        cp.dump();
        assertTrue(cp.index == 0);
        assertTrue(cp.column == 0);
        assertTrue(cp.line == 0);
    }
    @Test
    public void testProcessContent() {
        {
            // aaaazzz
            // zeazeaze
            // azezzz
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("aaaazzz");
            content.append("zeazeaze");
            content.append("azezzz");
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            indexer.processContent();
        }
        {
            //
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            indexer.processContent();
        }
    }
    @Test
    public void testProcessIndex() {
        {
            // aaaazzz
            // zeazeaze
            // azezzz
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("aaaazzz");
            content.append("zeazeaze");
            content.append("azezzz");
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            assertTrue(indexer.processIndex(0,0)==0);
            assertTrue(indexer.processIndex(0,2)==2);
            int res = indexer.processIndex(1,0);
            assertTrue("res=" + res, res==7);
            res = indexer.processIndex(2,5);
            assertTrue("res="+res,res==20);
            assertTrue(indexer.processIndex(123,123) == -1);
        }
        {
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            assertTrue(indexer.processIndex(0,2)==-1);
        }
    }
    @Test
    public void testProcessCharPosition() {
        {
            // aaaazzz
            // zeazeaze
            // azezzz
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("aaaazzz");
            content.append("zeazeaze");
            content.append("azezzz");
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            {
                CharPosition cp = indexer.processCharPosition(0);
                assertTrue(cp.column == 0 && cp.line == 0);
            }
            {
                CharPosition cp = indexer.processCharPosition(7);
                assertTrue(cp.column == 0 && cp.line == 1);
            }
            {
                CharPosition cp = indexer.processCharPosition(9);
                cp.dump();
                assertTrue(cp.column == 2 && cp.line == 1);
            }
        }
        {
            // a
            // aa
            // aaa
            //
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("a");
            content.append("aa");
            content.append("aaa");
            content.append();
            @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
            {
                CharPosition cp = indexer.processCharPosition(0);
                assertTrue(cp.column == 0 && cp.line == 0);
            }
            {
                CharPosition cp = indexer.processCharPosition(1);
                assertTrue(cp.column == 0 && cp.line == 1);
            }
            {
                CharPosition cp = indexer.processCharPosition(2);
                assertTrue(cp.column == 1 && cp.line == 1);
            }
            {
                CharPosition cp = indexer.processCharPosition(3);
                assertTrue(cp.column == 0 && cp.line == 2);
            }
            {
                CharPosition cp = indexer.processCharPosition(4);
                assertTrue(cp.column == 1 && cp.line == 2);
            }
            {
                CharPosition cp = indexer.processCharPosition(5);
                assertTrue(cp.column == 2 && cp.line == 2);
            }
            {
                CharPosition cp = indexer.processCharPosition(6);
                assertTrue(cp == null);
            }
        }
    }
}