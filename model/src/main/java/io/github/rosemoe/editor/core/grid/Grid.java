package io.github.rosemoe.editor.core.grid;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.util.Logger;

/**
 * Basic object used by reports (fill the object).
 * and by CodeEditor (display the object).
 */
public class Grid extends ConcurrentSkipListMap<Integer, Line> implements Iterable<Line> {

    public int behaviourOnCellSplit = Cell.SPLIT_SPLITTING;

    @Override
    public Iterator<Line> iterator() {
        return super.values().iterator();
    }

    /**
     * List all cells in common order, from the top of editor to bottom.
     */
    public void forEachCell() {
        for(Line l : this) {
            for(Cell c : l) {
                handleForEachCell(c);
            }
        }
    }
    public void handleForEachCell(Cell c) {

    }

    @Override
    public Line get(Object key) {
        return super.get(key);
    }
    /**
     * Insert a Line at a specific position in the span
     */
    public void put(int index, Line line) {
        line.behaviourOnCellSplit = behaviourOnCellSplit;
        super.put(index,line);
    }
    /**
     * Test if the spanis empty.
     * @return
     */
    public boolean isEmpty() {
        return size()==0;
    }
    public void removeContent(int lineStart, int colStart, int lineStop, int colStop) {
        // ---+|+++      ---++++
        // ***        => ***
        // yy|yyy        ---+yyy
        if ( lineStart > lineStop ) {
            throw new RuntimeException("INVALID : lineStart=" + lineStart + ",lineStop=" + lineStop);
        }
        else if ( lineStart == lineStop ) {
            get(lineStart).removeCells(colStart, colStop - colStart);
        } else {
            Line[] startParts = get(lineStart).split(colStart);
            Line[] stopParts = get(lineStop).split(colStop);
            Line sl = Line.concat(startParts[0],stopParts[1]);
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
                    Line sl = (Line) super.remove(a + lineShift);
                    put(a, sl);
                } else {
                    remove(a);
                }
            }
        }
    }
    /**
     * Insert some content in the span 
     */
    public void insertContent(int lineStart, int colStart, int lineStop, int colStop) {
        // ---+|+++      ---+|+++
        // ***        =>
        //               ***
        // where x is insertion, | insertion point
        int lineShift = lineStop - lineStart;
        if ( lineShift > 0 ) {
            for (int a = size() - 1; a >= lineStart + 1; a = a - 1) {
                Line sl = remove(a);
                put(a + lineShift, sl);
            }
        }
        // ---+|+++      ---+
        // ***        => xx+++
        //               ***
        Logger.debug("lineStart="+lineStart+",colStart="+colStart+",lineStop="+lineStop+",colStop="+colStop);
        if ( lineStart > lineStop ) {
            throw new RuntimeException("INVALID : lineStart=" + lineStart + ",lineStop=" + lineStop);
        }
        else if ( lineStart == lineStop ) {
            get(lineStart).insertCell(colStart, colStop - colStart);
        } else {
            Line[] startParts = get(lineStart).split(colStart);
            put(lineStart, startParts[0]);
            startParts[1].insertCell(0, colStop);
            put(lineStart+lineShift, startParts[1]);
        }
    }
    public void insertContent(int line, int colStart, int colStop) {
        insertContent(line, colStart, line, colStop);
    }
    /**
     * Remove the Line at the specified index.
     * @param index
     */
    public Line remove(int index) {
        Line sl = (Line) get(index);
        if ( sl == null ) { return null; }
        return (Line) super.remove(index);
    }
    /**
     * Append an empty line to the span 
     * @return
     */
    public Line append() {
        int newIndex = size();
        Line l = new Line();
        l.behaviourOnCellSplit = behaviourOnCellSplit;
        put(newIndex, l);
        return get(newIndex);
    }
    /**
     * Complete the current spansuch as it while contains finalSizeInLines.
     * It will not remove extra lines
     *
     * @param finalSizeInLines 0..n
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
    public int append(Line l) {
        int idx = 0;
        Entry e = lastEntry();
        if ( e != null ) {
            idx = (int) e.key;
        }
        put(idx+1,l);
        return idx;
    }
    public Line addNormalIfNull() {
        append(1);
        return (Line) get(0);
    }
    /**
     * This will get the required span line or create it if it doesn't exists.
     * lineno : 0..n-1 the span line to get.
     * @param lineno
     * @return
     */
    public Line getAddIfNeeded(int lineno) {
        append(lineno+1);
        return (Line) get(lineno);
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
        //noinspection unchecked
        for(Map.Entry<Integer, Line> sl : entrySet().toArray(new Map.Entry[keySet().size()])) {
            Logger.debug(offset+"dump for line index " + sl.getKey());
            sl.getValue().dump(Logger.OFFSET);
        }
    }
}
