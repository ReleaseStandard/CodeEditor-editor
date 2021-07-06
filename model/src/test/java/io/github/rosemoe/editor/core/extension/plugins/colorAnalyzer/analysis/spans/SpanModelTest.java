package io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

public class SpanModelTest {
    Random r = new Random();
    @Test
    public void clear() {
        for(int a = 0; a < 20 ; a ++ ) {
            SpanModel b = new SpanModel();
        }
    }
}