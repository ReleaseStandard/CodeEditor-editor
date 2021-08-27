package io.github.rosemoe.editor.core.content.processors.indexer;

import org.junit.Test;

import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class BaseContentIndexerTest {
    class Base extends BaseContentIndexer {

        public Base(CodeAnalyzerResultContent content) {
            super(content);
        }

        @Override
        protected CharPosition getCharPosition(CharPosition charPosition) {
            return null;
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
            @Jailbreak Base indexer = new Base(content);
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
            @Jailbreak Base indexer = new Base(content);
            assertTrue(indexer.processIndex(0,2)==-1);
        }
        {

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
            @Jailbreak Base indexer = new Base(content);
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
            @Jailbreak Base indexer = new Base(content);
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
                assertTrue(cp.column == 0 && cp.line == 3);
            }
            {
                CharPosition cp = indexer.processCharPosition(7);
                assertTrue(cp == null);
            }
        }
        {
            //
            //
            //
            //
            @Jailbreak CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
            content.append();
            content.append();
            content.append();
            content.append();
            @Jailbreak Base indexer = new Base(content);
            {
                CharPosition cp = indexer.processCharPosition(0);
                assertTrue(cp.line == 3 && cp.column == 0);
            }
            {
                CharPosition cp = indexer.processCharPosition(1);
                assertTrue(cp == null );
            }
        }
    }
}