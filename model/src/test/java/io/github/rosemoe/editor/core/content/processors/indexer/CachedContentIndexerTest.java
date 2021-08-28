package io.github.rosemoe.editor.core.content.processors.indexer;

import org.checkerframework.checker.units.qual.C;
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
            System.out.println("Side effect exameple");
            indexer.dump();
            assertTrue(indexer.getCharIndex(0,0) == 0);
            indexer.dump();
            assertTrue(indexer.getCharLine(0) == 0);
            assertTrue(indexer.getCharColumn(0) == 0);
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
    public void testGetCharPositionBug() {
        // aazez
        // aa
        // er
        @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
        content.append("aazez");
        content.append("aa");
        content.append("er");
        @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
        {
            CharPosition cp = indexer.getCharPosition(-1);
            assertTrue(cp == null);
            cp = indexer.getCharPosition(0);
            assertTrue(cp.line == 0 && cp.column == 0);
            cp = indexer.getCharPosition(1);
            assertTrue(cp.line == 0 && cp.column == 1);
            cp = indexer.getCharPosition(2);
            assertTrue(cp.line == 0 && cp.column == 2);
            cp = indexer.getCharPosition(3);
            assertTrue(cp.line == 0 && cp.column == 3);
            cp = indexer.getCharPosition(4);
            assertTrue(cp.line == 0 && cp.column == 4);
            cp = indexer.getCharPosition(5);
            assertTrue(cp.line == 1 && cp.column == 0);
            cp = indexer.getCharPosition(6);
            assertTrue(cp.line == 1 && cp.column == 1);
            cp = indexer.getCharPosition(7);
            assertTrue(cp.line == 2 && cp.column == 0);
            cp = indexer.getCharPosition(8);
            assertTrue(cp.line == 2 && cp.column == 1);
            cp = indexer.getCharPosition(9);
            assertTrue(cp != null);
            cp = indexer.getCharPosition(10);
            assertTrue(cp == null);

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
            assertTrue(cp.column == 0 && cp.line == 0 && cp.index == 0);
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
            indexer.findNearest(new CharPosition(0));
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
    public void testCannotCompareThoseCharPositionBug() {
        @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
        @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
        //indexer.cache.contains(new CharPosition(0));
        indexer.getCharLine(0);
    }

    @Test
    public void testCompleteWithContent() {
        // aze
        @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
        content.append("aze");
        content.append("zz");
        @Jailbreak CachedContentIndexer indexer = new CachedContentIndexer(content);
        CharPosition res = indexer.completeWithContent(new CharPosition(0));
        assertTrue(res != null);
        assertTrue(res.column == 0 && res.line == 0 && res.index == 0);
        res = indexer.completeWithContent(new CharPosition(0,1));
        assertTrue(res != null);
        assertTrue(res.column == 1 && res.line == 0 && res.index == 1);
        res = indexer.completeWithContent(new CharPosition(2));
        assertTrue(res != null);
        assertTrue(res.column == 2 && res.line == 0 && res.index == 2);
        res = indexer.completeWithContent(new CharPosition(3));
        assertTrue(res != null);
        assertTrue(res.column == 0 && res.line == 1 && res.index == 3);
        res = indexer.completeWithContent(new CharPosition(4));
        assertTrue(res != null);
        assertTrue(res.column == 1 && res.line == 1 && res.index == 4);
        CharPosition cp = indexer.completeWithContent(new CharPosition(100));
        assertTrue( cp != null);
    }

    @Test
    public void testCharAtWith() {
        {
            //
            // aze
            // aa
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append();
            content.append("aze");
            content.append("aa");
            CachedContentIndexer contentIndexer = new CachedContentIndexer(content);
            CharPosition res = contentIndexer.processCharPosition(5);
            content.dump();
            res.dump();
            assertTrue(res != null && res.index == 5 && res.line == 2 && res.column == 2);
        }
        {
            //
            // aze
            // aa
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append();
            content.append("aze");
            content.append("aa");
            CachedContentIndexer contentIndexer = new CachedContentIndexer(content);
            CharPosition res = contentIndexer.completeWithContent(new CharPosition(5));
            content.dump();
            res.dump();
            assertTrue(res != null && res.index == 5 && res.line == 2 && res.column == 2);
        }
        {
            //
            // aze
            // aa
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append();
            content.append("aze");
            content.append("aa");
            CachedContentIndexer contentIndexer = new CachedContentIndexer(content);
            CharPosition cp = contentIndexer.getCharPosition(5);
            cp.dump();
            assertTrue(cp != null && cp.index == 5 && cp.line == 2 && cp.column == 2);
        }
    }
}