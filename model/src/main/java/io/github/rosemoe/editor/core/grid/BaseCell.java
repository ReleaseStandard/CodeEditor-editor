package io.github.rosemoe.editor.core.grid;

public class BaseCell extends Cell {

    public BaseCell() { }

    public BaseCell(int size) {
        super(size);
    }

    public BaseCell(int column, int size) {
        super(column, size);
    }

    @Override
    public void dataClear() {
        
    }

    @Override
    public Cell dataClone(Cell cloning) {
        return cloning;
    }
}
