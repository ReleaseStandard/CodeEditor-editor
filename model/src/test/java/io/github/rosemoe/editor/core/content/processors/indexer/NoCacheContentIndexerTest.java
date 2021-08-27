package io.github.rosemoe.editor.core.content.processors.indexer;

import org.junit.Ignore;
import org.junit.Test;

import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class NoCacheContentIndexerTest {

    @Test
    public void getCharPosition() {
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            @Jailbreak NoCacheContentIndexer indexer = new NoCacheContentIndexer(content);
            assertTrue(indexer.getCharPosition(new CharPosition(0)) == null);
        }
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            @Jailbreak NoCacheContentIndexer indexer = new NoCacheContentIndexer(content);
            assertTrue(indexer.getCharPosition(new CharPosition(-1)) == null);
        }
        {
            // aze
            // z
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append("aze");
            content.append("z");
            content.append();
            @Jailbreak NoCacheContentIndexer indexer = new NoCacheContentIndexer(content);
            assertTrue(indexer.getCharPosition(new CharPosition(0)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(1)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(2)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(3)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(4)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(5)) == null);
            assertTrue(indexer.getCharPosition(new CharPosition(0,0)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(0,1)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(0,2)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(0,3)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(1,0)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(1,1)) != null);
            assertTrue(indexer.getCharPosition(new CharPosition(2,0)) != null);

        }
    }
    @Test
    @Ignore("This is know")
    public void testCharPosition() {
        // a
        CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
        content.append("a");
        @Jailbreak NoCacheContentIndexer indexer = new NoCacheContentIndexer(content);
        assertTrue(indexer.getCharPosition(new CharPosition(0,0)) != null);
        assertTrue(indexer.getCharPosition(new CharPosition(0,1)) != null);
        assertTrue(indexer.getCharPosition(new CharPosition(0,2)) == null);
    }
}