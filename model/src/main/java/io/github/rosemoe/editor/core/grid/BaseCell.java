package io.github.rosemoe.editor.core.grid;

public class BaseCell extends Cell {

    public BaseCell(int column) {
        super(column);
    }

    public BaseCell(int column, int size) {
        super(column, size);
    }
    @Override
    public void dataClear() {
        
    }
}
