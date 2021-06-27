package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class SymbolInputModelTest {
    Random r = new Random();

    @Test
    public void instanciate() {
        for(int a = 0; a < 10; a = a + 1 ) {
            SymbolInputModel b = new SymbolInputModel();
        }
    }
}