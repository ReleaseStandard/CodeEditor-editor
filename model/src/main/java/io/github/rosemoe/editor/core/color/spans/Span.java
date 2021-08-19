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
package io.github.rosemoe.editor.core.color.spans;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.extension.extensions.colorChange.ColorSchemeExtension;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * The span model - it could end to variety of implementation in the view (e.g. Canvas, Span)
 * it is just a region on the editor that should get a given color.
 *
 * @author Rose
 */
public class Span {

    public int column;
    public int size;
    public int color;
    public int underlineColor = 0;

    public void clear() {
        size = color = column = underlineColor = 0;
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
        this.column = column;
        this.color = color;
    }
    private Span(int column, int size, int color) {
        this.column = column;
        this.color = color;
        this.size = size;
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
        this.column = column;
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

    public static void recycleAll(Span[] spans) {
        for (Span span : spans) {
            if (!span.recycle()) {
                return;
            }
        }
    }

    /**
     * End column for the span (e.g. span of size 2 with column=0 will get his end at 1)
     * @return
     */
    public int getColumnEnd() {
        if ( size == 0 ) {
            return column;
        }
        return column + size - 1;
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

    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        Logger.debug(offset + "column=" + column + ",color="+ color+",underlineColor="+underlineColor+",size="+size);
    }
}
