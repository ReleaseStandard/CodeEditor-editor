package io.github.rosemoe.editor.core.grid;

import io.github.rosemoe.editor.core.CEObject;

public abstract class Cell extends CEObject {

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
    public Cell clone() {
        Cell c = null;
        try {
            c = this.getClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        c.column = column;
        c.size = size;
        dataClone(c);
        return c;
    }
    public Cell obtain(Object ...args) {
        return null;
    }
    protected abstract void dataClear();
    protected abstract Cell dataClone(Cell cloning);
}
