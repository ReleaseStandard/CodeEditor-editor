package io.github.rosemoe.editor.core;

public class Cell<T> extends CEObject {
    public int size = 0;
    public int column = 0;
    T delegate = null;
    public Cell(int column) {
        this.column = column;
        this.size = 1;
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
    }
    public Cell obtain(Object ...args) {
        return null;
    }
    public Cell clone(Object ...args) {
        return null;
    }
}
