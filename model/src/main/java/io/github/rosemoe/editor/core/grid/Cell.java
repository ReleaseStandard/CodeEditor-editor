package io.github.rosemoe.editor.core.grid;

import io.github.rosemoe.editor.core.util.CEObject;

public abstract class Cell extends CEObject {

    public static final int SPLIT_EXTENDS = 0;
    public static final int SPLIT_INVALIDATE = 1;
    public static final int SPLIT_SPLITTING = 2;

    public boolean enabled = true;
    public int size = 0;
    public int column = 0;

    public Cell() { }
    public Cell(int size) {
        this.column = 0;
        this.size = size;
    }
    public Cell(int column, int size) {
        this.column = column;
        this.size = size;
    }
    /**
     * Get the start column of the cell.
     */
    public int getStart() {
        return column;
    }
    /**
     * Get the stop column of the cell (excluded).
     * @return
     */
    public int getStop() {
        return column + size;
    }
    public void clear() {
        size = column = 0;
        dataClear();
    }
    public abstract Cell clone();

    public Cell obtain(Object ...args) {
        return null;
    }
    protected abstract void dataClear();
    protected Cell dataClone(Cell cloning) {
        cloning.column = column;
        cloning.size = size;
        cloning.enabled = enabled;
        return cloning;
    }
}
