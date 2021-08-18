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
package io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans;

import java.util.Map;
import java.util.TreeMap;

import io.github.rosemoe.editor.core.util.Logger;

import static io.github.rosemoe.editor.core.extension.plugins.colorAnalyzer.analysis.spans.SpanLine.SPAN_SPLIT_INVALIDATE;

/**
 * This class is a SpanLine container (line displayed to the screen).
 * All indexes are from 0 to n
 *
 * @author Release Standard
 */
public class SpanMap {

    public int behaviourOnSpanSplit = SPAN_SPLIT_INVALIDATE;

    /**
     * lineindex, SpanLine
     * This associate a TreeMap with each line.
     * This allow row shifting durign analysis.
     * line 0..n-1
     */
    private TreeMap<Integer, SpanLine> map = new TreeMap<Integer, SpanLine>();

    public void SpanMap() {

    }
    /**
     * Append an empty line to the span map.
     * @return
     */
    public SpanLine appendLine() {
        int newIndex = map.size();
        SpanLine l = SpanLine.EMPTY();
        l.behaviourOnSpanSplit = behaviourOnSpanSplit;
        map.put(newIndex, l);
        return map.get(newIndex);
    }
    /**
     * Insert a SpanLine at a specific position in the span map.
     */
    public void add(int index, SpanLine line) {
        line.behaviourOnSpanSplit = behaviourOnSpanSplit;
        map.put(index,line);
    }
    /**
     * Complete the current spanmap such as it while contains finalSizeInLines.
     * It will not remove extra lines
     *
     * @param finalSizeInLines 0..n
     */
    public void appendLines(int finalSizeInLines) {
        while(map.size() < finalSizeInLines) {
            appendLine();
        }
    }

    /**
     * lineno : 0..n-1 the span line to get
     * @param lineno
     * @return null if the line is not in the spanmap
     */
    public SpanLine get(int lineno) {
        return map.get(lineno);
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
     * returns the size of the map.
     * @return
     */
    public int size() {
        return map.size();
    }

    /**
     * clear the spanmap, it will remove everything from the spanmap
     */
    public void clear() {
        map.clear();
    }

    /**
     * Test if the spanmap is empty.
     * @return
     */
    public boolean isEmpty() {
        return map.size()==0;
    }

    /**
     * Remove the SpanLine at the specified index.
     * @param index
     */
    public void remove(int index) {
        SpanLine sl = map.get(index);
        Span.recycleAll(sl.concurrentSafeGetValues());
        map.remove(index);
    }

    public SpanLine[] getLines() {
        return concurrentSafeGetValues();
    }

    /**
     * Remove spans and lines of all lines found between bounds.
     * @param startLine index 0..n-1 of the start line
     * @param startColumn index 0..n-1 of the start column
     * @param endLine index 0..n-1 of the end line
     * @param endColumn index 0..n-1 of the end column
     */
    public void cutLines(int startLine, int startColumn, int endLine, int endColumn) {
        SpanLine startSpanLine = map.get(startLine);
        SpanLine stopSpanLine = map.get(endLine);
        SpanLine newLine = null;
        if ( startLine != endLine ) {
            startSpanLine.removeContent(startColumn, Integer.MAX_VALUE);
            stopSpanLine.removeContent(0, endColumn);
            newLine = SpanLine.concat(startSpanLine,stopSpanLine);
        } else {
            startSpanLine.removeContent(startColumn, endColumn - startColumn);
            newLine = startSpanLine;
        }
        newLine.behaviourOnSpanSplit = behaviourOnSpanSplit;
        map.put(startLine,newLine);

        for(int i = startLine + 1; i <= endLine; i=i+1) {
            remove(i);
        }
        for(int i = endLine ; i < map.size();i=i+1) {
            SpanLine line = map.remove(i);
            map.put(i - (endLine-startLine),line);
        }
    }

    public void removeContent(int lineStart, int colStart, int lineStop, int colStop) {
        // ---+|+++      ---++++
        // ***        => ***
        // yy|yyy        ---+yyy
        if ( lineStart > lineStop ) {
            throw new RuntimeException("INVALID : lineStart=" + lineStart + ",lineStop=" + lineStop);
        }
        else if ( lineStart == lineStop ) {
            map.get(lineStart).removeContent(colStart, colStop - colStart);
        } else {
            SpanLine[] startParts = map.get(lineStart).split(colStart);
            SpanLine[] stopParts = map.get(lineStop).split(colStop);
            Logger.debug("startParts: {0: ",startParts[0].size(),", 1: ",startParts[1].size(),"}");
            Logger.debug("stopParts: {0: ",stopParts[0].size(),", 1: ",stopParts[1].size(),"}");
            SpanLine sl = SpanLine.concat(startParts[0],stopParts[1]);
            Logger.debug("concat="+sl.size());
            map.put(lineStop, sl);
        }
        // ---+      ---+yyy
        // ***    =>
        // ---+yyy
        int lineShift = lineStop - lineStart;
        if ( lineShift > 0 ) {
            final int sz = map.size();
            for (int a = lineStart; a < sz; a=a+1) {
                if ( a + lineShift < sz ) {
                    SpanLine sl = map.remove(a + lineShift);
                    map.put(a, sl);
                } else {
                    map.remove(a);
                }
            }
        }
    }
    /**
     * Insert some content in the span map.
     */
    public void insertContent(int lineStart, int colStart, int lineStop, int colStop) {
        // ---+|+++      ---+|+++
        // ***        =>
        //               ***
        // where x is insertion, | insertion point
        int lineShift = lineStop - lineStart;
        if ( lineShift > 0 ) {
            for (int a = map.size() - 1; a >= lineStart + 1; a = a - 1) {
                SpanLine sl = map.remove(a);
                map.put(a + lineShift, sl);
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
            map.get(lineStart).insertContent(colStart, colStop - colStart);
        } else {
            SpanLine[] startParts = map.get(lineStart).split(colStart);
            map.put(lineStart, startParts[0]);
            startParts[1].insertContent(0, colStop);
            map.put(lineStart+lineShift, startParts[1]);
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
        Logger.debug(offset+"number of lines in : "+ map.size());
        //noinspection unchecked
        for(Map.Entry<Integer, SpanLine> sl : map.entrySet().toArray(new Map.Entry[map.keySet().size()])) {
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
                lines = map.values().toArray(new SpanLine[size()]);
            } catch (java.util.ConcurrentModificationException e) {
                Logger.debug("This error is harmless if not repeat to much");
                e.printStackTrace();
                lines=null;
            }
        }
        return lines;
    }
}
