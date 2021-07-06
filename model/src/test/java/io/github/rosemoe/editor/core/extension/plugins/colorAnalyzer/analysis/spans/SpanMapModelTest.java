package io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

public class SpanMapModelTest {
    Random r = new Random();
    @Test
    public void instanciate() {
        for(int a = 0; a < 10 ; a = a + 1) {
            SpanMapModel b = new SpanMapModel();
        }
    }
}