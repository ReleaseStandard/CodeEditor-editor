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
package io.github.rosemoe.editor.core.grid.instances;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.grid.Cell;

/**
 * The span model - it could end to variety of implementation in the view (e.g. Canvas, SpanCell)
 * it is just a region on the editor that should get a given color.
 *
 * @author Rose
 */
public class SpanCell extends Cell {

    public int color;
    public int underlineColor = 0;

    @Override
    public void dataClear() {
        color = underlineColor = 0;
    }

    @Override
    public Cell dataClone(Cell cloning) {
        SpanCell c = (SpanCell) cloning;
        c.color = color;
        c.underlineColor = underlineColor;
        return c;
    }

    private static final BlockingQueue<SpanCell> cacheQueue = new ArrayBlockingQueue<>(8192 * 2);

    /**
     * @return an empty span with default settings.
     */
    public static SpanCell EMPTY() {
        return obtain(0, ColorManager.DEFAULT_BACKGROUND_COLOR);
    }

    public SpanCell() {}
    /**
     * Create a new span
     *
     * @param size size of the span
     * @param color Type of span
     * @see SpanCell#obtain(int, int)
     */
    private SpanCell(int size, int color) {
        super(size);
        this.color = color;
    }
    private SpanCell(int column, int size, int color) {
        super(column, size);
        this.color = color;
    }

    /**
     * Make a copy of this span
     */
    public SpanCell copy() {
        SpanCell copy = obtain(column, color);
        copy.underlineColor = underlineColor;
        return copy;
    }

    public boolean recycle() {
        clear();
        return cacheQueue.offer(this);
    }

    public static void recycleAll(SpanCell[] spanCells) {
        for (SpanCell spanCell : spanCells) {
            if (!spanCell.recycle()) {
                return;
            }
        }
    }

    public static SpanCell obtain(int column, int color) {
        return obtain(column, 1, color);
    }
    public static SpanCell obtain(int column, int size, int color) {
        SpanCell spanCell = cacheQueue.poll();
        if (spanCell == null) {
            spanCell = new SpanCell(column, size, color);
        } else {
            spanCell.column = column;
            spanCell.color = color;
            spanCell.size = size;
        }
        return spanCell;
    }
}
