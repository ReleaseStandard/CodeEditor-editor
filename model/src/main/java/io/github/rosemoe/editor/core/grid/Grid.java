package io.github.rosemoe.editor.core.grid;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.util.Logger;

/**
 * Basic object used by reports (fill the object).
 * and by CodeEditor (display the object).
 */
public class Grid<T extends Cell> extends ConcurrentSkipListMap<Integer, Line<T>> implements Iterable<Line<T>> {

    public int behaviourOnCellSplit = Cell.SPLIT_SPLITTING;

    @Override public Iterator<Line<T>> iterator() {
        return super.values().iterator();
    }
    @Override public boolean isEmpty() {
        return size()==0;
    }
    @Override public Line<T> get(Object key) {
        return super.get(key);
    }
    @Override public String toString() {
        String res = "";
        for(Line<T> l : this) {
            res += l;
        }
        return res;
    }

    /**
     * Get sub grid of initial grid.
     * The returned grid is copy not pointers.
     * @param lineStart 0..n-1 line of sub grid start
     * @param colStart 0..n-1 column of sub grid start
     * @param lineStop 0..n-1 line of subgrid stop
     * @param colStop 0..n-1 columne of sub grid stop
     * @return a subgrid with {@link Cell} copied from the original grid.
     */
    public Grid<T> subGrid(int lineStart, int colStart, int lineStop, int colStop) {
        Grid<T> g = new Grid<T>();
        if ( lineStart == lineStop ) {
            g.put(lineStart,(get(lineStart).subLine(colStart,colStop-colStart)));
        } else {
            for (int a = lineStart; a <= lineStop; a++) {
                if ( a == lineStart ) {
                    g.put(a,(get(a).subLine(colStart,-1)));
                } else if ( a == lineStop ) {
                    g.put(a,(get(a).subLine(0,colStop)));
                } else {
                    g.put(a,get(a).clone());
                }
            }
        }
        return g;
    }
    /**
     * Insert a {@link Line} into the grid at the specified line.
     * @param index 0..n-1 where to insert the line (override the Line if it exists alrdy).
     * @param line the {@link Line} to insert.
     */
    public void put(int index, Line<T> line) {
        line.behaviourOnCellSplit = behaviourOnCellSplit;
        super.put(index,line);
    }
    public void removeCells(int lineStart, int colStart, int lineStop, int colStop) {
        // ---+|+++      ---++++
        // ***        => ***
        // yy|yyy        ---+yyy
        if ( lineStart > lineStop ) {
            throw new RuntimeException("INVALID : lineStart=" + lineStart + ",lineStop=" + lineStop);
        }
        else if ( lineStart == lineStop ) {
            get(lineStart).removeCells(colStart, colStop - colStart);
        } else {
            Line<T>[] startParts = get(lineStart).split(colStart);
            Line<T>[] stopParts = get(lineStop).split(colStop);
            Line<T> sl = Line.concat(startParts[0],stopParts[1]);
            put(lineStop, sl);
        }
        // ---+      ---+yyy
        // ***    =>
        // ---+yyy
        int lineShift = lineStop - lineStart;
        if ( lineShift > 0 ) {
            final int sz = size();
            for (int a = lineStart; a < sz; a=a+1) {
                if ( a + lineShift < sz ) {
                    Line<T> sl = super.remove(a + lineShift);
                    put(a, sl);
                } else {
                    remove(a);
                }
            }
        }
    }
    /**
     * Insert some empty cells in the grid.
     * @param lineStart 0..n-1 line of insertStart
     * @param colStart 0..n-1 column of insertStart
     * @param lineStop 0..n-1 line of insertStop
     * @param colStop 0..n-1 column of insertStop
     */
    public void insertCells(int lineStart, int colStart, int lineStop, int colStop) {
        // ---+|+++      ---+|+++
        // ***        =>
        //               ***
        // where x is insertion, | insertion point
        int lineShift = lineStop - lineStart;
        if (lineShift > 0) {
            for (int a = size() - 1; a >= lineStart + 1; a = a - 1) {
                Line<T> sl = remove(a);
                put(a + lineShift, sl);
            }
        }
        // ---+|+++      ---+
        //            =>     +++
        // ***           ***
        Logger.debug("lineStart=" + lineStart + ",colStart=" + colStart + ",lineStop=" + lineStop + ",colStop=" + colStop);
        if (lineStart > lineStop) {
            throw new RuntimeException("INVALID : lineStart=" + lineStart + ",lineStop=" + lineStop);
        } else if (lineStart == lineStop) {
            get(lineStart).insertCell(colStart, colStop - colStart);
        } else {
            Line<T>[] startParts = get(lineStart).split(colStart);
            put(lineStart, startParts[0]);
            startParts[1].insertCell(0, colStop);
            put(lineStart + lineShift, startParts[1]);
        }
    }
    /**
     * Insert cells from an other grid into this one, insertStop is deduced from {@link Grid} g1.
     * @param lineStart 0..n-1 line of insertStart
     * @param colStart 0..n-1 column of insertStop
     * @param g1 Grid to pickup elements from.
     */
    public void insertCells(int lineStart, int colStart, Grid<T> g1) {
        int lineStop = 0;
        int colStop = 0;
        Entry e = g1.lastEntry();
        if ( e != null ) {
            Line lstop = (Line) e.value;
            lineStop = (int) e.key;
            Entry e1 = lstop.lastEntry();
            if ( e1 != null ) {
                Cell c = (Cell) e1.value;
                colStop = c.column + c.size;
            }
        }
        insertCells(lineStart,colStart,lineStop,colStop);
        for(int a = lineStart; a < lineStop; a = a + 1 ) {
            Line insertLine = get(a);
            Line toInsertLine = g1.get(a-lineStart);
            insertLine.insertLine((a==lineStart)?colStart:0,toInsertLine);
        }
    }
    public void insertCells(int line, int colStart, int colStop) {
        insertCells(line, colStart, line, colStop);
    }
    /**
     * Remove the Line at the specified index.
     * @param index
     */
    public Line<T> remove(int index) {
        Line<T> sl = get(index);
        if ( sl == null ) { return null; }
        return super.remove(index);
    }
    public void replace(int lineStart, int colStart, int lineStop, int colStop, Grid newContent) {
        removeCells(lineStart,colStart,lineStop,colStop);
        //insertContent(lineStart, colStart, lineStop, colStop, newContent);
    }
    /**
     * Append an empty {@link Line} to this {@link Grid}.
     * @return
     */
    public Line<T> append() {
        int newIndex = size();
        Line<T> l = new Line<T>();
        l.behaviourOnCellSplit = behaviourOnCellSplit;
        put(newIndex, l);
        return get(newIndex);
    }
    /**
     * Complete the current spansuch as it while contains finalSizeInLines.
     * It will not remove extra lines
     * @param finalSizeInLines 0..n the size to complete up to.
     */
    public void append(int finalSizeInLines) {
        while(size() < finalSizeInLines) {
            append();
        }
    }
    /**
     * Append line to the grid
     * @return
     */
    public int append(Line<T> l) {
        int idx = 0;
        Entry e = lastEntry();
        if ( e != null ) {
            idx = (int) e.key + 1;
        }
        put(idx,l);
        return idx;
    }
    public void append(Line<T> ...ls) {
        for(Line<T> l : ls) {
            append(l);
        }
    }
    public Line<T> addNormalIfNull() {
        append(1);
        return get(0);
    }
    /**
     * This will get the required span line or create it if it doesn't exists.
     * lineno : 0..n-1 the span line to get.
     * @param lineno
     * @return
     */
    public Line<T> getAddIfNeeded(int lineno) {
        append(lineno+1);
        return get(lineno);
    }
    /**
     * Dump debug information on this class.
     */
    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        if ( !Logger.DEBUG ) { return; }
        Logger.debug(offset+"number of lines in : "+ size());
        for(Integer i : keySet()) {
            Line<T> l = get(i);
            Logger.debug(offset+"line idx="+i);
            l.dump(offset + Logger.OFFSET);
        }
    }
}
