package io.github.rosemoe.editor.core.grid;

public interface CellData {
    /**
     * Clear content of the cell.
     */
    void dataClear();

    /**
     * clone this cell.
     * @return
     */
    Cell dataClone(Cell cloning);
}
