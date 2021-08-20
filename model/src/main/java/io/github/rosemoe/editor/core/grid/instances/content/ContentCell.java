package io.github.rosemoe.editor.core.grid.instances.content;

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
}
