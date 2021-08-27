package io.github.rosemoe.editor.core.grid;

import io.github.rosemoe.editor.core.content.processors.indexer.BaseContentIndexer;

public class BaseCell extends Cell {

    public BaseCell() { }

    public BaseCell(int size) {
        super(size);
    }

    public BaseCell(int column, int size) {
        super(column, size);
    }

    @Override
    public Cell clone() {
        return dataClone(new BaseCell());
    }

    @Override
    public void dataClear() {
        
    }

    @Override
    public Cell dataClone(Cell cloning) {
        return super.dataClone(cloning);
    }
}
