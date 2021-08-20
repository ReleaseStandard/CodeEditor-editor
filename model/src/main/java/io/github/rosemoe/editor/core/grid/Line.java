package io.github.rosemoe.editor.core.grid;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.CEObject;
import io.github.rosemoe.editor.core.util.Logger;


public class Line extends ConcurrentSkipListMap<Integer, Cell> implements Iterable<Cell> {
    public int behaviourOnCellSplit = Cell.SPLIT_INVALIDATE;

    public int getBehaviourOnCellSplit() {
        return behaviourOnCellSplit;
    }
    @Override
    public Cell get(Object key) {
        return super.get(key);
    }

    @Override
    public Iterator<Cell> iterator() {
        return super.values().iterator();
    }

    /**
     * Clone this line.
     * @return cloned line.
     */
    public Line clone() {
        Line cloned = new Line();
        for(Cell c : this) {
            cloned.append(c.clone());
        }
        return cloned;
    }
    /**
     * Remove a span from the span line.
     * @return
     */
    public Cell remove(Cell cell) {
        return remove(cell.column);
    }

    /**
     * Dump the current state of the Line.
     */
    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        CEObject.dump(this,offset);
        String res = "// ";
        String[] patterns = new String[] {"*","-","+",","};
        int idx = 0;
        for(Cell c:this) {
            for(int a=0;a<c.size;a+=1) {
                res += patterns[idx];
            }
            if ( c.size > 0 ) {
                idx = (idx + 1)%patterns.length;
            }
        }
        Logger.debug(res);
    }

    public Cell put(Cell cell) {
        return super.put(cell.column, cell);
    }

    public void put(Line line) {
        for(Cell cell : line){
            put(cell);
        }
    }

    /**
     * Append a cell at the end of this line.
     * @param cell
     */
    public Cell append(Cell cell) {
        if ( lastEntry() != null ) {
            Cell c = lastEntry().value;
            cell.column += c.column+c.size;
        }
        return put(cell);
    }
    public void append(Line line) {
        for(Cell c : line) {
            append(c);
        }
    }

    /**
     * Split the line at given column index.
     * WARNING: this operation is destructive on the original Line (we do not create new Cells)
     * if we split on a span rather than between spans, we remove the span
     * @param col index 0..n-1
     * @return newly created Line with the column index updated.
     */
    public Line[] split(int col) {
        Class lineClass = getClass();
        Logger.debug("lineClass="+lineClass);
        Line[] parts = new Line[2];
        try {
            parts[0] = (Line) lineClass.newInstance();
            parts[1] = (Line) lineClass.newInstance();
        } catch (Exception e) {}

        int startOfNewLine = -1;
        for(Cell cell : values()) {
            if ( (cell.column + cell.size) <= col  ) {
                Logger.debug("Case 1");
                parts[0].put(cell);
            } else if ( cell.column >= col ) {
                Logger.debug("Case 2");
                if ( startOfNewLine == -1 ) {
                    startOfNewLine = cell.column;
                }
                cell.column = cell.column - startOfNewLine;
                parts[1].put(cell);
            } else if (cell.column < col && (cell.column+cell.size) > col ) {
                Logger.debug("Case 3");
                switch ( getBehaviourOnCellSplit() ) {
                    case Cell.SPLIT_INVALIDATE:
                    case Cell.SPLIT_EXTENDS:
                    case Cell.SPLIT_SPLITTING:
                        if ( startOfNewLine == -1 ) {
                            startOfNewLine = col;
                        }
                        final int oldSz = cell.size;
                        cell.size = col - cell.column;
                        if ( cell.size > 0 ) {
                            cell.enabled = !(getBehaviourOnCellSplit() == Cell.SPLIT_INVALIDATE);
                            parts[0].put(cell);
                        }
                        Cell otherPart = cell.clone();
                        otherPart.size = oldSz - cell.size;
                        otherPart.column = 0;
                        otherPart.enabled = !(getBehaviourOnCellSplit() == Cell.SPLIT_INVALIDATE);
                        if ( otherPart.size > 0 ) {
                            parts[1].put(otherPart);
                        }
                        break;
                }

            }
        }
        return parts;
    }

    /**
     * This function merge two SpanLines together and returns the concatened span line.
     * Warning : this operation in destructive on given entry lines
     * @param one
     * @param two
     * @return
     */
    public static Line concat(Line one, Line two) {
        Line result = null;
        try {
            result = one.getClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for(Object o : one) {
            Cell span = (Cell) o;
            result.put(span);
        }
        int index = 0;
        if ( one.size() > 0 ) {
            Cell lastCell = (Cell) one.lastEntry().getValue();
            index = lastCell.column + lastCell.size;
        }
        for(Object o : two) {
            Cell span = (Cell) o;
            span.column = index + span.column;
            result.put(span);
        }
        one.clear();
        two.clear();
        return result;
    }




    /**
     * Test if the span line is empty (contains no SpanCell).
     * @return
     */
    public boolean isEmpty() {
        return size()==0;
    }



    /**
     * Insert a cell in the line.
     *  behaviours:
     *  - it extend a cell
     *  - it split existing cell and insert between
     *  - it invalidate existing cell and insert between
     * @param cell the cell to insert
     */
    public void insertCell(Cell cell) {
        // here we got undefined :
        //   - or do not insert the span (we just use fields)
        //   - or do insert the span but do not respect the behaviourOnSpanSplit policy
        if ( getBehaviourOnCellSplit() == Cell.SPLIT_EXTENDS) {
            throw new RuntimeException("Error : give insert a SpanCell in SPAN_SPLIT_EXTENDS policy will produce undermined behaviour, aborting");
        }
        insertCell(cell.column, cell.size);
        if ( cell.size > 0 ) {
            put(cell.column, cell);
        }
    }
    public void insertCell(final int col, final int size) {
        Cell[] arr = (Cell[]) values().toArray(new Cell[size()]);
        for(int a = arr.length-1; a >= 0; a --) {
            Cell s = arr[a];
            if ( s.column >= col ) {
                remove(s.column);
                s.column = s.column+size;
                put(s.column, s);
            } else if (s.column < col && (s.column + s.size) > col) {
                switch (getBehaviourOnCellSplit()) {
                    case Cell.SPLIT_EXTENDS:
                        remove(s.column);
                        s.size += size;
                        put(s.column, s);
                        break;
                    case Cell.SPLIT_INVALIDATE:
                        remove(s.column);
                        break;
                    case Cell.SPLIT_SPLITTING:
                        int oldSz = s.size;
                        int index = col + size;
                        s.size = (col - s.column);
                        Cell c = s.clone();
                        c.column = index;
                        c.size = oldSz - s.size;
                        put(index, c);
                        break;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Remove spans on affected region by the deletion.
     * @param col index 0..n-1
     * @param sz size 0..n
     */
    private void removeCellsExtend(int col, int sz, Cell cell) {
        int startIndex = cell.column;
        int startLength = cell.size;
        int startShift = col - cell.column;
        if ( startShift < 0 ) {
            startIndex = col;
            startLength = 0;
        } else {
            startLength = cell.column + startShift;
        }

        int endShift = (cell.column+cell.size) - (col+sz);
        if ( endShift <= 0 ) {
            // NOTHING TO DO HERE
        } else {
            startLength += endShift;
        }

        Logger.debug("startIndex="+startIndex+",startShift="+startShift+",startLength="+startLength+",endShift="+endShift+",startLength="+startLength);

        if ( startShift <= 0 && endShift <= 0 ) {
            remove(cell);
        } else {
            remove(cell.column);
            cell.column = startIndex;
            cell.size = startLength;
            put(cell.column, cell);
        }
    }
    private void removeCellsInvalidate(int col, int sz, Cell cell) {
        remove(cell);
    }
    private void removeCellsSplitting(int col, int sz, Cell cell) {
        int startIndex = cell.column;
        int startLength = cell.size;
        int startShift = col - cell.column;
        if ( startShift < 0 ) {
            startIndex = col;
            startLength = 0;
        } else {
            startLength = startShift;
        }
        if ( startShift <= 0 || startLength <= 0 ) {
            remove(cell);
        }
        int endShift = (cell.column+cell.size) - (col+sz);
        if ( endShift <= 0 ) {
            // NOTHING TO DO HERE
        } else {
            Cell c = cell.clone();
            c.column = col;
            c.size = endShift;
            put(col, c);
        }

        if ( startShift <= 0 || startLength <= 0 ) {
        } else {
            remove(cell.column);
            cell.column = startIndex;
            cell.size = startLength;
            put(cell.column, cell);
        }
    }
    /**
     * This function permit to remove elements from the SpanLine (e.g. span shift).
     * @param col index 0..n-1 where to start removing.
     * @param sz size 1..n size of the remove.
     */
    public void removeCells(int col, int sz) {
        for(Cell cell : this) {
            if ( (cell.column+cell.size) <= col ) {
                // 1. NOTHING happen here we just keep the spans as it is
            } else if ( (cell.column) >= col+sz ) {
                // 3. we must shift this span
                remove(cell.column);
                cell.column -= sz;
                put(cell.column, cell);
            } else {
                // 2. we must move column or size or both (prerequisite: col=0, sz=0)
                switch (getBehaviourOnCellSplit()) {
                    case Cell.SPLIT_EXTENDS:
                        removeCellsExtend(col, sz, cell);
                        break;
                    case Cell.SPLIT_SPLITTING:
                        removeCellsSplitting(col, sz, cell);
                        break;
                    case Cell.SPLIT_INVALIDATE:
                        removeCellsInvalidate(col,sz,cell);
                        break;
                }
            }
        }
    }
    public Line subCells(int col, int sz) {
        Line cells = new Line();
        Integer firstKey = floorKey(col);
        Integer lastKey = floorKey(col+sz);
        if ( firstKey == null && lastKey == null ) {
            return null;
        }
        if ( firstKey == null ) {
            firstKey = col;
        }
        int latent = -1;
        int latentSz = 0;
        for(int a = firstKey; a < col+sz; ) {
            Cell c = get(a);
            if ( c == null ) {
                if ( latent == -1 ) { latent = Math.max(a,col); }
                latentSz += c.size - Math.min(0, col - c.size) ;
                a += 1;
            } else {
                if ( latent != -1 ) {
                    subCellsDecision(cells, latent, latentSz);
                    latent = -1;
                    latentSz = 0;
                }
                cells.append(c);
                a += c.size;
            }
        }
        if ( latent != -1 ) {
            subCellsDecision(cells, latent, latentSz);
        }
        cells.removeCells(0,col);
        return cells;
    }
    private Line subCellsDecision(Line cells, int col, int size) {
        switch (getBehaviourOnCellSplit()) {
            case Cell.SPLIT_INVALIDATE: {
                BaseCell bc = new BaseCell(col, size);
                bc.enabled = false;
                cells.append(bc);
            } break;
            case Cell.SPLIT_EXTENDS:
            case Cell.SPLIT_SPLITTING: {
                Cell c = null;
                Integer firstKey = floorKey(col);
                if (firstKey == null) {
                    c = new BaseCell(col, size);
                    c.enabled = false;
                } else {
                    c = get(firstKey).clone();
                    c.size = size;
                    c.column = col;
                }
                cells.append(c);
            } break;
        }
        return cells;
    }

    /**
     * Return size of the content in this line.
     * @return size of the content.
     */
    public int getWidth() {
        if ( lastEntry() == null ) { return 0; }
        Cell c = lastEntry().value;
        return c.column+c.size;
    }
}
