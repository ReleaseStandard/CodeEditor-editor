package io.github.rosemoe.editor.core.extension.extensions.widgets.completion;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

public class CompletionWindowModelTest {
    Random r = new Random();
    @Test
    public void instanciate() {
        for(int a = 0; a < r.nextUint()%10; a = a + 1 ) {
            CompletionWindowModel b = new CompletionWindowModel();
        }
    }
}