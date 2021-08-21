package io.github.rosemoe.editor.core.grid.instances;

import io.github.rosemoe.editor.core.grid.Cell;

public class ContentCell extends Cell {

    public char c = '\0';

    @Override
    public void dataClear() {
        c = '\0';
    }

    @Override
    public Cell dataClone(Cell cloning) {
        ContentCell c = (ContentCell)cloning;
        c.c = this.c;
        return c;
    }
    public ContentCell(char c) {
        super(1);
        this.c = c;
    }

    @Override
    public String toString() {
        return Character.toString(c);
    }
}
