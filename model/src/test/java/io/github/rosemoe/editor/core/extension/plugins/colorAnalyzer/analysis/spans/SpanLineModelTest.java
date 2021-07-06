package io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

public class SpanLineModelTest {
    Random r = new Random();
    @Test
    public void instanciate() {
        for(int a = 0; a < 5; a = a + 1) {
            SpanLineModel b = new SpanLineModel();
        }
    }
}