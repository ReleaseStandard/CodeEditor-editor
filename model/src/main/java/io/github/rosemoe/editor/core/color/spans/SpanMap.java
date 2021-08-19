/*
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.core.color.spans;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.rosemoe.editor.core.Grid;
import io.github.rosemoe.editor.core.Line;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * This class is a SpanLine container (line displayed to the screen).
 * All indexes are from 0 to n
 *
 * @author Release Standard
 */
public class SpanMap extends Grid<Span> {

    public int behaviourOnCellSplit = Line.SPAN_SPLIT_SPLITTING;

    public void SpanMap() {

    }
    /**
     * Append an empty line to the span 
     * @return
     */
    public SpanLine appendLine() {
        int newIndex = size();
        SpanLine l = SpanLine.EMPTY();
        l.behaviourOnCellSplit = behaviourOnCellSplit;
        put(newIndex, l);
        return get(newIndex);
    }
    /**
     * Insert a SpanLine at a specific position in the span 
     */
    public void add(int index, SpanLine line) {
        line.behaviourOnCellSplit = behaviourOnCellSplit;
        put(index,line);
    }
    /**
     * Complete the current spansuch as it while contains finalSizeInLines.
     * It will not remove extra lines
     *
     * @param finalSizeInLines 0..n
     */
    public void appendLines(int finalSizeInLines) {
        while(size() < finalSizeInLines) {
            appendLine();
        }
    }

    /**
     * lineno : 0..n-1 the span line to get
     * @return null if the line is not in the spanmap
     */
    public SpanLine get(Object key) {
        return (SpanLine) super.get(key);
    }
    /**
     * This will get the required span line or create it if it doesn't exists.
     * lineno : 0..n-1 the span line to get.
     * @param lineno
     * @return
     */
    public SpanLine getAddIfNeeded(int lineno) {
        appendLines(lineno+1);
        return get(lineno);
    }

    /**
     * Test if the spanis empty.
     * @return
     */
    public boolean isEmpty() {
        return size()==0;
    }

    /**
     * Remove the SpanLine at the specified index.
     * @param index
     */
    public SpanLine remove(int index) {
        SpanLine sl = get(index);
        if ( sl == null ) { return null; }
        int newSz = sl.size();
        Span.recycleAll((Span[]) sl.values().toArray(new Span[newSz]));
        return (SpanLine) super.remove(index);
    }

    public SpanLine[] getLines() {
        return concurrentSafeGetValues();
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
            Logger.debug("startParts: {0: ",startParts[0].size(),",startParts[0].size=",startParts[0].get(0).size,", 1: ",startParts[1].size(),"}");
            Logger.debug("stopParts: {0: ",stopParts[0].size(),", 1: ",stopParts[1].size(),"}");
            SpanLine sl = (SpanLine) SpanLine.concat(startParts[0],stopParts[1]);
            Logger.debug("concat="+sl.size());
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
                    SpanLine sl = remove(a + lineShift);
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
                SpanLine sl = remove(a);
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
    //public void insertContent()
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
        for(Map.Entry<Integer, SpanLine> sl : entrySet().toArray(new Map.Entry[keySet().size()])) {
            Logger.debug(offset+"dump for line index " + sl.getKey());
            sl.getValue().dump(Logger.OFFSET);
        }
    }

    public SpanLine addNormalIfNull() {
        appendLines(1);
        return get(0);
    }
    /**
     * This function is used to avoid concurrent exception when working with Collections.
     * @return
     */
    public SpanLine[] concurrentSafeGetValues() {
        SpanLine[] lines = null;
        while (lines == null ) {
            try {
                lines = values().toArray(new SpanLine[size()]);
            } catch (java.util.ConcurrentModificationException e) {
                Logger.debug("This error is harmless if not repeat to much");
                e.printStackTrace();
                lines=null;
            }
        }
        return lines;
    }
}
