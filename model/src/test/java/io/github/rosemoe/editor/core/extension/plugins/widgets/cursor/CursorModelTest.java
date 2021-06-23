package io.github.rosemoe.editor.core.extension.plugins.widgets.cursor;

import org.junit.Test;

import static org.junit.Assert.*;

public class CursorModelTest {

    @Test
    public void instanciate() {
        for( int a = 0; a < 100; a = a + 1){
            CursorModel b = new CursorModel();
        }
    }
}