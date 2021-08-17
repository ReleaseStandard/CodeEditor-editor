package io.github.rosemoe.editor.core.extension.plugins.widgets.completion;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class CompletionItemModelTest {
    Random r = new Random();
    @Test
    public void cursorOffset() {
        CompletionItemModel a = new CompletionItemModel();
        a.commit = "okokok";
        a.cursorOffset(0);
        assertTrue(a.cursorOffset == 0);
    }
}