package io.github.rosemoe.editor.core.grid;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.util.CEObject;
import io.github.rosemoe.editor.core.util.Logger;

import static io.github.rosemoe.editor.core.grid.Cell.*;


public class Line<T extends Cell> extends ConcurrentSkipListMap<Integer, T> implements Iterable<T> {
    public int behaviourOnCellSplit = SPLIT_INVALIDATE;

    public Line() {

    }
    public Line(ConcurrentNavigableMap<Integer, T> init) {
        super(init);
    }
    public int getBehaviourOnCellSplit() {
        return behaviourOnCellSplit;
    }
    @Override
    public T get(Object key) {
        return super.get(key);
    }

    @Override
    public Iterator<T> iterator() {
        return super.values().iterator();
    }

    /**
     * Clone this line.
     * @return cloned line.
     */
    public Line<T> clone() {
        Line<T> cloned = new Line<T>();
        for(T c : this) {
            cloned.append((T) c.clone());
        }
        return cloned;
    }
    /**
     * Remove a span from the span line.
     * @return
     */
    public T remove(T cell) {
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
        String[] patterns = new String[] {"*","-","+",",","x",":","!","p","^","$","/"};
        int idx = 0;
        for(T c:this) {
            for(int a=0;a<c.size;a+=1) {
                res += patterns[idx];
            }
            if ( c.size > 0 ) {
                idx = (idx + 1)%patterns.length;
            }
        }
        Logger.debug(res);
    }

    public T put(T cell){
        super.put(cell.column, (T) cell);
        return super.get(cell.column);
    }

    public void put(Line<T> line) {
        for(T cell : line){
            put(cell);
        }
    }

    /**
     * Append a cell at the end of this line.
     * @param cell
     */
    public T append(T cell) {
        if ( lastEntry() != null ) {
            T c = lastEntry().value;
            cell.column = c.column+c.size;
        }
        return put(cell);
    }
    public void append(Line<T> line) {
        for(T c : line) {
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
    public Line<T>[] split(int col) {
        Class lineClass = getClass();
        Logger.debug("lineClass="+lineClass);
        Line<T>[] parts = new Line[2];
        parts[0]=new Line<T>();
        parts[1]=new Line<T>();
        int startOfNewLine = -1;
        for(T cell : this) {
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
                    case SPLIT_INVALIDATE:
                    case SPLIT_EXTENDS:
                    case SPLIT_SPLITTING:
                        if ( startOfNewLine == -1 ) {
                            startOfNewLine = col;
                        }
                        final int oldSz = cell.size;
                        cell.size = col - cell.column;
                        if ( cell.size > 0 ) {
                            cell.enabled = !(getBehaviourOnCellSplit() == SPLIT_INVALIDATE);
                            parts[0].put(cell);
                        }
                        T otherPart = (T) cell.clone();
                        otherPart.size = oldSz - cell.size;
                        otherPart.column = 0;
                        otherPart.enabled = !(getBehaviourOnCellSplit() == SPLIT_INVALIDATE);
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
     * Insert a line into an other, warning this destruct the original line.
     * @param offset
     * @param l
     */
    public void insertLine(int offset, Line<T> l) {
        insertCell(offset,l.getWidth());
        remove(offset);
        for(T c : l) {
            c.column += offset;
            put(c);
        }
    }
    /**
     * Insert a cell in the line.
     *  behaviours:
     *  - it extend a cell
     *  - it split existing cell and insert between
     *  - it invalidate existing cell and insert between
     * @param cell the cell to insert
     */
    public void insertCell(T cell) {
        // here we got undefined :
        //   - or do not insert the span (we just use fields)
        //   - or do insert the span but do not respect the behaviourOnSpanSplit policy
        if ( getBehaviourOnCellSplit() == SPLIT_EXTENDS) {
            throw new RuntimeException("Error : give insert a SpanCell in SPAN_SPLIT_EXTENDS policy will produce undermined behaviour, aborting");
        }
        insertCell(cell.column, cell.size);
        if ( cell.size > 0 ) {
            put(cell.column, cell);
        }
    }
    public void insertCell(final int col, final int size) {
        Integer[] keys = keySet().toArray(new Integer[size()]);
        for(int a = size()-1; a >= 0; a --) {
            T s = get(keys[a]);
            if ( s.column >= col ) {
                remove(s.column);
                s.column = s.column+size;
                put(s.column, s);
            } else if (s.column < col && (s.column + s.size) > col) {
                switch (getBehaviourOnCellSplit()) {
                    case SPLIT_EXTENDS:
                        remove(s.column);
                        s.size += size;
                        put(s.column, s);
                        break;
                    case SPLIT_INVALIDATE:
                    case SPLIT_SPLITTING:
                        int oldSz = s.size;
                        int index = col + size;
                        s.size = (col - s.column);
                        T c = (T) s.clone();
                        c.column = index;
                        c.size = oldSz - s.size;
                        c.enabled = !(behaviourOnCellSplit == SPLIT_INVALIDATE);
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
    private void removeCellsExtend(int col, int sz, T cell) {
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
    private void removeCellsInvalidate(int col, int sz, T cell) {
        remove(cell);
    }
    private void removeCellsSplitting(int col, int sz, T cell) {
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
            T c = (T) cell.clone();
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
        for(T cell : this) {
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
                    case SPLIT_EXTENDS:
                        removeCellsExtend(col, sz, cell);
                        break;
                    case SPLIT_SPLITTING:
                        removeCellsSplitting(col, sz, cell);
                        break;
                    case SPLIT_INVALIDATE:
                        removeCellsInvalidate(col,sz,cell);
                        break;
                }
            }
        }
    }

    public Line<T> subLine(int col) {
        return subLine(col,-1);
    }
    /**
     * Get a subpart of the Line
     * @param col 0..n-1 col in the line
     * @param sz  0..n   size of the subline to retrieve
     * @return the subline starting at index 0 for a size of sz
     */
    public Line<T> subLine(int col, int sz) {
        if ( sz == -1 ) {
            Entry e = lastEntry();
            if ( e != null ) {
                T c = (T) e.value;
                sz = (c.column + c.size) - col;
            } else {
                sz = 0;
            }
        }
        Integer firstKey = floorKey(col);
        Integer lastKey = floorKey(col+sz);
        if ( firstKey == null && lastKey == null ) {
            return null;
        }
        if ( firstKey == null ) {
            firstKey = col;
        }
        if ( lastKey == firstKey || lastKey == null ) {
            lastKey = col+sz;
        }
        Line<T>res = new Line();
        ConcurrentNavigableMap<Integer, T> submap = this.clone().subMap(firstKey, true, lastKey, (lastKey <= col+sz));
        NavigableSet<Integer> ks = submap.keySet();
        for(Integer key : ks) {
            T cell = submap.remove(key);
            if ( key < col ) {
                cell.size -= (col-key);
                cell.column = 0;
                cell.enabled = (behaviourOnCellSplit != SPLIT_INVALIDATE);
            } else if ((key+cell.size) > col+sz) {
                cell.size -= ((key+cell.size)-(col+sz));
                if ( cell.size == 0 ) {
                    continue;
                }
                cell.enabled = (behaviourOnCellSplit != SPLIT_INVALIDATE);
            } else {
                cell.column -= col;
            }
            res.put(cell.column, cell);
        }
        return res;
    }

    /**
     * Return size of the content in this line.
     * @return size of the content.
     */
    public int getWidth() {
        if ( lastEntry() == null ) { return 0; }
        T c = lastEntry().value;
        return c.column+c.size;
    }

    @Override
    public String toString() {
        String res = "";
        for(T c : this) {
            res += c;
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if ( o instanceof Line ) {
            Line l = (Line) o;
            if ( size() != l.size() ) { return false; }
            return behaviourOnCellSplit == l.behaviourOnCellSplit &&
                    width == l.width &&
                    isEmpty == l.isEmpty;

        } else {
            throw new RuntimeException("Cannot compare those objects");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), behaviourOnCellSplit, width, isEmpty);
    }
}
