package io.github.rosemoe.editor.core.extension.plugins.widgets.contextaction;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class ContextActionModelTest {
    Random r = new Random();

    @Test
    public void instanciate() {
        for(int a = 0; a < 20; a = a + 1) {
            ContextActionModel b = new ContextActionModel();
        }
    }
}