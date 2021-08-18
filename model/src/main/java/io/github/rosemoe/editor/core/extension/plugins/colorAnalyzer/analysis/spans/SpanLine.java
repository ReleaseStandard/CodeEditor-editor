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

import java.util.TreeMap;

import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * The class handle one line in the text editor.
 * We assume that Span do not merge (ex: you cannot put red and green brackground, that's an error).
 *
 * @author Release Standard
 */
public class SpanLine {

    /**
     * Column index 0..n-1, Span
     */
    public TreeMap<Integer, Span> line;
    public static final int SPAN_SPLIT_EXTENDS = 0;
    public static final int SPAN_SPLIT_INVALIDATE = 1;
    public static final int SPAN_SPLIT_SPLITTING = 2;
    public int behaviourOnSpanSplit = SPAN_SPLIT_INVALIDATE;
    public SpanLine() {
        line = new TreeMap<>();
    }

    /**
     * Add a new span to the spanline
     * @param col column index 0..n-1
     * @param span
     */
    public Span add(int col, Span span) {
        return line.put(col,span);
    }
    public Span add(Span span) {
        return add(span.column,span);
    }
    public void add(SpanLine line) {
        for(Span span : line.line.values()){
            add(span);
        }
    }

    /**
     * Get the number of elements in the SpanLine
     * @return
     */
    public int size() {
        return line.size();
    }

    /**
     * Get the Span to the index i in the SpanLine.
     * @param i
     * @return
     */
    public Span get(int i) {
        return line.get(i);
    }

    /**
     * Test if the span line is empty (contains no Span).
     * @return
     */
    public boolean isEmpty() {
        return line.size()==0;
    }

    /**
     * Remove a span from the span line.
     * @param i line index 0..n-1
     * @return
     */
    public Span remove(int i) {
        return line.remove(i);
    }
    /**
     * Remove a span from the span line.
     * @param span the span to remove, span.column 0..n-1
     * @return
     */
    public Span remove(Span span) {
        return remove(span.column);
    }

    /**
     * Clear the span line.
     */
    public void clear() {
        line.clear();
    }
    /**
     * Dump the current state of the SpanLine.
     */
    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        Logger.debug(offset + "span in the line="+size());
        for(Span s : concurrentSafeGetValues()) {
            s.dump(offset);
        }
    }

    /**
     * Split the line at given column index.
     * WARNING: this operation is destructive on the original SpanLine (we do not create new Spans)
     * if we split on a span rather than between spans, we remove the span
     * @param col index 0..n-1
     * @return newly created SpanLine with the column index updated.
     */
    public SpanLine[] split(int col) {
        SpanLine[] parts = new SpanLine[2];
        parts[0]=new SpanLine();
        parts[1]=new SpanLine();
        int startOfNewLine = -1;
        for(Span span : line.values()) {
            if ( (span.column + span.size) <= col  ) {
                parts[0].add(span);
            } else if ( span.column >= col ) {
                if ( startOfNewLine == -1 ) {
                    startOfNewLine = span.column;
                }
                span.setColumn(span.column - startOfNewLine);
                parts[1].add(span);
            } else if (span.column < col && (span.column+span.size) > col ) {
                switch ( behaviourOnSpanSplit ) {
                    case SPAN_SPLIT_INVALIDATE:
                        break;
                    case SPAN_SPLIT_SPLITTING:
                        if ( startOfNewLine == -1 ) {
                            startOfNewLine = col;
                        }
                        final int oldSz = span.size;
                        span.size = col - span.column;
                        parts[0].add(span);
                        Span otherPart = Span.obtain(0,span.color,oldSz-span.size);
                        parts[1].add(otherPart);
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
    public static SpanLine concat(SpanLine one, SpanLine two) {
        SpanLine result = new SpanLine();
        for(Span span : one.line.values()) {
            result.add(span);
        }
        int index = 0;
        if ( one.size() > 0 ) {
            Span lastSpan = one.line.lastEntry().getValue();
            index = lastSpan.column + lastSpan.size;
        }
        for(Span span : two.line.values()) {
            span.setColumn(index);
            result.add(index,span);
            index = span.column + span.size;
        }
        one.clear();
        two.clear();
        return result;
    }

    /**
     * Insert content into the SpanLine at specified position.
     * @param span the span to insert
     */
    public void insertContent(Span span) {
        // here we got undefined :
        //   - or do not insert the span (we just use fields)
        //   - or do insert the span but do not respect the behaviourOnSpanSplit policy
        if ( behaviourOnSpanSplit == SPAN_SPLIT_EXTENDS) {
            throw new RuntimeException("Error : give insert a Span in SPAN_SPLIT_EXTENDS policy will produce undermined behaviour, aborting");
        }
        insertContent(span.column, span.size);
        if ( span.size > 0 ) {
            line.put(span.column, span);
        }
    }
    public void insertContent(final int col, final int size) {
        Span[] arr = concurrentSafeGetValues();
        for(int a = arr.length-1; a >= 0; a --) {
            Span s = arr[a];
            if ( s.column >= col ) {
                line.remove(s.column);
                s.setColumn(s.column+size);
                line.put(s.column,s);
            } else if (s.column < col && (s.column + s.size) > col) {
                switch (behaviourOnSpanSplit) {
                    case SPAN_SPLIT_EXTENDS:
                        line.remove(s.column);
                        s.size += size;
                        line.put(s.column, s);
                        break;
                    case SPAN_SPLIT_INVALIDATE:
                        line.remove(s.column);
                        break;
                    case SPAN_SPLIT_SPLITTING:
                        int oldSz = s.size;
                        int index = col + size;
                        s.size = (col - s.column);
                        line.put(index, Span.obtain(index, s.color, oldSz - s.size));
                        break;
                }
            } else {
                break;
            }
        }
    }
    public void insertContent(int size) {
        insertContent(0,size);
    }

    /**
     * Remove spans on affected region by the deletion.
     * @param col index 0..n-1
     * @param sz size 0..n
     */
    public void removeContent(int col,int sz) {
        for(Span span : concurrentSafeGetValues()) {
            if ( span.column < col) {
                if ( (span.size) >= col ) {
                    int sizeToRemove = Math.min((span.column + span.size) - col, sz);
                    switch (behaviourOnSpanSplit) {
                        case SPAN_SPLIT_EXTENDS:
                            span.size -= sizeToRemove;
                            break;
                        case SPAN_SPLIT_SPLITTING:
                            int oldSize = span.size;
                            span.size = col - span.column;
                            int remainSize = oldSize - span.size - sizeToRemove;
                            if ( remainSize > 0 ) {
                                add(col,Span.obtain(col,span.color,remainSize));
                            }
                            break;
                        case SPAN_SPLIT_INVALIDATE:
                            remove(span);
                            break;
                    }
                }
            } else {
                line.remove(span.column);
                if ( span.column < col+sz) {
                    if (span.column + span.size <= col + sz) {
                        remove(span);
                    }
                    else {
                        int sizeToRemove = (col+sz) - span.column;
                        span.size -= sizeToRemove;
                        span.setColumn(span.column - sizeToRemove);
                        line.put(span.column, span);
                    }
                } else {
                    span.setColumn(span.column - sz);
                    line.put(span.column,span);
                }
            }
        }
    }

    /**
     * Get the length of the content in columns.
     * Prerequisite : each Span of the span map is separated.
     * @return size of the line in columns
     */
    public int lengthContent() {
        Span[] arr = concurrentSafeGetValues();
        if( arr.length == 0 ) {
            return 0;
        }
        return arr[arr.length-1].column + arr[arr.length-1].size;
    }

    /**
     * Empty spanline.
     * @return returns an empty spanline
     */
    public static SpanLine EMPTY() {
        SpanLine line = new SpanLine();
        line.add(Span.obtain(0, ColorManager.DEFAULT_TEXT_COLOR));
        return line;
    }

    /**
     * This function is used to avoid concurrent exception when working with Collections.
     * @return
     */
    public Span[] concurrentSafeGetValues() {
        Span[] spans = null;
        while (spans == null ) {
            try {
                spans = line.values().toArray(new Span[size()]);
            } catch (java.util.ConcurrentModificationException e) {
                Logger.debug("This error is harmless if not repeat to much");
                e.printStackTrace();
                spans=null;
            }
        }
        return spans;
    }
}
