package io.github.rosemoe.editor.core.grid;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

import io.github.rosemoe.editor.core.util.Logger;

public class Grid extends ConcurrentSkipListMap<Integer, Line> implements Iterable<Line> {

    public int behaviourOnCellSplit = Line.SPAN_SPLIT_SPLITTING;

    @Override
    public Iterator<Line> iterator() {
        return super.values().iterator();
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
            get(lineStart).removeContent(colStart, colStop - colStart);
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
            get(lineStart).insertContent(colStart, colStop - colStart);
        } else {
            Line[] startParts = get(lineStart).split(colStart);
            put(lineStart, startParts[0]);
            startParts[1].insertContent(0, colStop);
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
}
