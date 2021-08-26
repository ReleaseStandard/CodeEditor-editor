package io.github.rosemoe.editor.core.content.processors.indexer;

import org.junit.*;

import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;
import static org.junit.Assert.*;

public class CachedIndexerTest {

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
    public void getCharPosition() {
        {
            // abc
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("abc");
            @Jailbreak CachedIndexer indexer = new CachedIndexer(content);
            CharPosition cp = indexer.getCharPosition(0);
            assertTrue(content.size()==1);
            assertTrue(content.get(0).size() == 3);
            assertTrue(content.get(0).get(0).c == 'a');
            indexer.dump();
            assertTrue(cp != null);
            assertTrue(cp.column == 0 && cp.line == 0);
        }
        {
            //
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            @Jailbreak CachedIndexer indexer = new CachedIndexer(content);
            CharPosition cp = indexer.getCharPosition(0);
            assertTrue(content.size()==0 && cp == null);
        }
        {
            CodeAnalyzerResultContent content = createSample();
            content.dump();
            @Jailbreak CachedIndexer indexer = new CachedIndexer(content);
            CharPosition cp = indexer.getCharPosition(0);
            assertTrue(cp.column == 0 && cp.line == 0);
            cp = indexer.getCharPosition(4);
            assertTrue(cp.column == 4 && cp.line == 0);
            cp = indexer.getCharPosition(7);
            assertTrue("cp.column="+cp.column+",cp.line="+cp.line, cp.column == 0 && cp.line == 1);
            indexer.findNearestByIndex(0);
        }
    }
}