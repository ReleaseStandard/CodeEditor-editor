package io.github.rosemoe.editor.core.content;

import org.junit.Test;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.analyze.analyzer.CodeAnalyzer;
import io.github.rosemoe.editor.core.grid.BaseCell;
import io.github.rosemoe.editor.core.grid.Cell;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.util.Random;

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
    public void testAppendText() {
        CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
        int i = content.append("test");
        assertTrue(i==0);
        assertTrue(content.get(0).getWidth() == 4);
    }

    @Test
    public void testCharAt() {
        {
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
            assertTrue( content.charAt(3) == 'z');
        }
        /*
        {
            CodeAnalyzerResultContent content = new CodeAnalyzerResultContent();
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
                assertTrue("i=" + i + ",a=" + a + ",total="+total, content.charAtNaive(i) == content.charAtPseudoDico(i));
            }
        }
        */
    }
}