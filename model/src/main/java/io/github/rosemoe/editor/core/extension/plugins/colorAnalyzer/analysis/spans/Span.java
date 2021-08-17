/*
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.ColorSchemeExtension;

/**
 * The span model
 *
 * @author Rose
 */
public class Span {

    public int column;
    public int color;
    public int underlineColor = 0;

    public void clear() {
        color = column = underlineColor = 0;
    }

    private static final BlockingQueue<Span> cacheQueue = new ArrayBlockingQueue<>(8192 * 2);

    /**
     * @return an empty span with default settings.
     */
    public static Span EMPTY() {
        return obtain(0, ColorManager.DEFAULT_BACKGROUND_COLOR);
    }
    /**
     * Create a new span
     *
     * @param column  Start column of span
     * @param color Type of span
     * @see Span#obtain(int, int)
     */
    private Span(int column, int color) {
        column = column;
        color = color;
    }

    public static Span obtain(int column, int color) {
        Span span = cacheQueue.poll();
        if (span == null) {
            return new Span(column, color);
        } else {
            span.column = column;
            span.color = color;
            return span;
        }
    }

    public static void recycleAll(Span[] spans) {
        for (Span span : spans) {
            if (!span.recycle()) {
                return;
            }
        }
    }

    /**
     * Set a underline for this region
     * Zero for no underline
     *
     * @param color Color for this underline (not color id of {@link ColorSchemeExtension})
     * @return Self
     */
    public Span setUnderlineColor(int color) {
        underlineColor = color;
        return this;
    }

    /**
     * Get span start column
     *
     * @return Start column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Set column of this span
     */
    public Span setColumn(int column) {
        column = column;
        return this;
    }

    /**
     * Make a copy of this span
     */
    public Span copy() {
        Span copy = obtain(column, color);
        copy.setUnderlineColor(underlineColor);
        return copy;
    }

    public boolean recycle() {
        clear();
        return cacheQueue.offer(this);
    }

}
