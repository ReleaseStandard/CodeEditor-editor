package io.github.rosemoe.editor.core.extension.extensions.widgets.contentAnalyzer;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class ContentManagerModelTest {
    Random r = new Random();
    @Test
    public void isUndoEnabled() {
        ContentManagerModel a = new ContentManagerModel();
        boolean undo = r.nextBoolean();
        a.undo = undo;
        assertTrue(a.isUndoEnabled() == undo);
    }

    @Test
    public void getMaxUndoStackSize() {
        ContentManagerModel a = new ContentManagerModel();
        int sz = r.nextInt();
        a.maxStackSize = sz;
        assertTrue(a.getMaxUndoStackSize() == sz );
    }
}