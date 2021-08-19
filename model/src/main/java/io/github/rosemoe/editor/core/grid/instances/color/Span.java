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
package io.github.rosemoe.editor.core.grid.instances.color;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.grid.Cell;

/**
 * The span model - it could end to variety of implementation in the view (e.g. Canvas, Span)
 * it is just a region on the editor that should get a given color.
 *
 * @author Rose
 */
public class Span extends Cell {

    public int color;
    public int underlineColor = 0;

    @Override
    public void dataClear() {
        color = underlineColor = 0;
    }

    @Override
    public Cell dataClone(Cell cloning) {
        Span c = (Span) cloning;
        c.color = color;
        c.underlineColor = underlineColor;
        return c;
    }

    private static final BlockingQueue<Span> cacheQueue = new ArrayBlockingQueue<>(8192 * 2);

    /**
     * @return an empty span with default settings.
     */
    public static Span EMPTY() {
        return obtain(0, ColorManager.DEFAULT_BACKGROUND_COLOR);
    }

    public Span() {}
    /**
     * Create a new span
     *
     * @param column  Start column of span
     * @param color Type of span
     * @see Span#obtain(int, int)
     */
    private Span(int column, int color) {
        super(column);
        this.color = color;
    }
    private Span(int column, int size, int color) {
        super(column, size);
        this.color = color;
    }

    /**
     * Make a copy of this span
     */
    public Span copy() {
        Span copy = obtain(column, color);
        copy.underlineColor = underlineColor;
        return copy;
    }

    public boolean recycle() {
        clear();
        return cacheQueue.offer(this);
    }

    public static void recycleAll(Span[] spans) {
        for (Span span : spans) {
            if (!span.recycle()) {
                return;
            }
        }
    }

    public static Span obtain(int column, int color) {
        return obtain(column, 1, color);
    }
    public static Span obtain(int column, int size, int color) {
        Span span = cacheQueue.poll();
        if (span == null) {
            span = new Span(column, size, color);
        } else {
            span.column = column;
            span.color = color;
            span.size = size;
        }
        return span;
    }

    @Override
    public Span clone(Object... args) {
        return Span.obtain((Integer)args[0],(Integer)args[1],color);
    }
}
