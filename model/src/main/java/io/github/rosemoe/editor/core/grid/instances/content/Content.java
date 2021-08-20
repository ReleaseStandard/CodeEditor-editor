package io.github.rosemoe.editor.core.grid.instances.content;

import io.github.rosemoe.editor.core.grid.Cell;

public class Content extends Cell {

    String text = "";

    @Override
    public void dataClear() {
        text = "";
    }

    @Override
    public Cell dataClone(Cell cloning) {
        Content c = (Content)cloning;
        c.text = text;
        return c;
    }
}
