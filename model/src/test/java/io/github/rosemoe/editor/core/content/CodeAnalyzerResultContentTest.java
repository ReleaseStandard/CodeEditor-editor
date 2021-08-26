package io.github.rosemoe.editor.core.content;

import org.junit.Test;

import io.github.rosemoe.editor.core.CodeEditorModel;
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
}