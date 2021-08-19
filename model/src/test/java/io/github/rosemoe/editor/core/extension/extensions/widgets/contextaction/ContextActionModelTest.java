package io.github.rosemoe.editor.core.extension.extensions.widgets.contextaction;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

public class ContextActionModelTest {
    Random r = new Random();

    @Test
    public void instanciate() {
        for(int a = 0; a < 20; a = a + 1) {
            ContextActionModel b = new ContextActionModel();
        }
    }
}