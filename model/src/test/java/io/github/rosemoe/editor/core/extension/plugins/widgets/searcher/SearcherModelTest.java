package io.github.rosemoe.editor.core.extension.plugins.widgets.searcher;

import org.junit.Test;

import static org.junit.Assert.*;

public class SearcherModelTest {

    @Test
    public void checkState() {
        SearcherModel sm = new SearcherModel();
        assertFalse(sm.isInitialized());

        sm.searchText = "ok";
        assertTrue(sm.isInitialized());
    }
}