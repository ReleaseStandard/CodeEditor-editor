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
package io.github.rosemoe.editor.core.content.processors.indexer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import io.github.rosemoe.editor.core.util.CEObject;
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.content.ContentListener;

/**
 * ContentIndexer Impl for CodeAnalyzerResultContent
 * With cache
 *
 * @author Rose
 */
public class CachedContentIndexer extends ContentIndexer implements ContentListener {

    private final CharPosition mZeroPoint = new CharPosition().zero();
    private final CharPosition mEndPoint = new CharPosition();
    private final int mSwitchLine = 50;
    private int mSwitchIndex = 50;
    private boolean mHandleEvent = true;
    private boolean mHasException = false;

    class Cache extends ConcurrentSkipListSet<CharPosition> {
        public int maxSize = 1000;
        @Override
        public boolean add(CharPosition charPosition) {
            if ( size() >= maxSize ) { return false; }
            return super.add(charPosition);
        }
    };
    Cache cache = new Cache();

    /**
     * Create a new CachedContentIndexer for the given content
     *
     * @param content CodeAnalyzerResultContent to manage
     */
    public CachedContentIndexer(CodeAnalyzerResultContent content) {
        super(content);
    }

    /**
     * If the querying index is larger than the switch
     * We will add its result to cache
     *
     * @param s Switch
     */
    public void setSwitchIndex(int s) {
        mSwitchIndex = s;
    }

    /**
     * Get nearest cache for the given index
     *
     * @param index Querying index
     * @return Nearest cache or null if there is no nearest.
     */
    private CharPosition findNearestByIndex(int index) {
        return findNearest(new CharPosition(index));
    }

    private CharPosition findNearest(CharPosition pos) {
        CharPosition cp1 = cache.floor(pos);
        CharPosition cp2 = cache.ceiling(pos);
        return CharPosition.nearest(cp1,pos,cp2);
    }
    /**
     * Get nearest cache for the given line
     *
     * @param line Querying line
     * @return Nearest cache or null if there is no nearest
     */
    private CharPosition findNearestByLine(int line) {
        return findNearest(new CharPosition(line,0));
    }

    /**
     * From the given position to find forward in text
     *
     * @param start Given position
     * @param index Querying index
     * @return The querying position
     */
    private CharPosition findIndexForward(CharPosition start, int index) {
        if (start.index > index) {
            throw new IllegalArgumentException("Unable to find backward from method findIndexForward()");
        }
        int workLine = start.line;
        int workColumn = start.column;
        int workIndex = start.index;
        //Move the column to the line end
        {
            int column = content.getColumnCount(workLine);
            workIndex += column - workColumn;
            workColumn = column;
        }
        while (workIndex < index) {
            workLine++;
            workColumn = content.getColumnCount(workLine);
            workIndex += workColumn + 1;
        }
        if (workIndex > index) {
            workColumn -= workIndex - index;
        }
        CharPosition pos = new CharPosition();
        pos.column = workColumn;
        pos.line = workLine;
        pos.index = index;
        return pos;
    }

    /**
     * From the given position to find backward in text
     *
     * @param start Given position
     * @param index Querying index
     * @return The querying position
     */
    private CharPosition findIndexBackward(CharPosition start, int index) {
        if (start.index < index) {
            throw new IllegalArgumentException("Unable to find forward from method findIndexBackward()");
        }
        int workLine = start.line;
        int workColumn = start.column;
        int workIndex = start.index;
        while (workIndex > index) {
            workIndex -= workColumn + 1;
            workLine--;
            if (workLine != -1) {
                workColumn = content.getColumnCount(workLine);
            } else {
                //Reached the start of text,we have to use findIndexForward() as this method can not handle it
                return findIndexForward(mZeroPoint, index);
            }
        }
        int dColumn = index - workIndex;
        if (dColumn > 0) {
            workLine++;
            workColumn = dColumn - 1;
        }
        CharPosition pos = new CharPosition();
        pos.column = workColumn;
        pos.line = workLine;
        pos.index = index;
        return pos;
    }

    /**
     * From the given position to find forward in text
     *
     * @param start  Given position
     * @param line   Querying line
     * @param column Querying column
     * @return The querying position
     */
    private CharPosition findLiCoForward(CharPosition start, int line, int column) {
        if (start.line > line) {
            throw new IllegalArgumentException("can not find backward from findLiCoForward()");
        }
        int workLine = start.line;
        int workIndex = start.index;
        {
            //Make index to to left of line
            workIndex = workIndex - start.column;
        }
        while (workLine < line) {
            workIndex += content.getColumnCount(workLine) + 1;
            workLine++;
        }
        CharPosition pos = new CharPosition();
        pos.column = 0;
        pos.line = workLine;
        pos.index = workIndex;
        return findInLine(pos, line, column);
    }

    /**
     * From the given position to find backward in text
     *
     * @param start  Given position
     * @param line   Querying line
     * @param column Querying column
     * @return The querying position
     */
    private CharPosition findLiCoBackward(CharPosition start, int line, int column) {
        if (start.line < line) {
            throw new IllegalArgumentException("can not find forward from findLiCoBackward()");
        }
        int workLine = start.line;
        int workIndex = start.index;
        {
            //Make index to the left of line
            workIndex = workIndex - start.column;
        }
        while (workLine > line) {
            workIndex -= content.getColumnCount(workLine - 1) + 1;
            workLine--;
        }
        CharPosition pos = new CharPosition();
        pos.column = 0;
        pos.line = workLine;
        pos.index = workIndex;
        return findInLine(pos, line, column);
    }

    /**
     * From the given position to find in this line
     *
     * @param pos    Given position
     * @param line   Querying line
     * @param column Querying column
     * @return The querying position
     */
    private CharPosition findInLine(CharPosition pos, int line, int column) {
        if (pos.line != line) {
            throw new IllegalArgumentException("can not find other lines with findInLine()");
        }
        int index = pos.index - pos.column + column;
        CharPosition pos2 = new CharPosition();
        pos2.column = column;
        pos2.line = line;
        pos2.index = index;
        return pos2;
    }

    /**
     * Get max cache size
     *
     * @return max cache size
     */
    protected int getMaxCacheSize() {
        return cache.maxSize;
    }

    /**
     * Set max cache size
     *
     * @param maxSize max cache size
     */
    protected void setMaxCacheSize(int maxSize) {
        cache.maxSize = maxSize;
    }

    /**
     * For NoCacheContentIndexer
     *
     * @return whether handle changes
     */
    protected boolean isHandleEvent() {
        return mHandleEvent;
    }

    /**
     * For NoCacheContentIndexer
     *
     * @param handle Whether handle changes to refresh cache
     */
    protected void setHandleEvent(boolean handle) {
        mHandleEvent = handle;
    }

    @Override
    public int getCharIndex(int line, int column) {
        CharPosition cp = getCharPosition(line, column);
        return (cp == null) ? -1 : cp.index;
    }

    @Override
    public int getCharLine(int index) {
        CharPosition cp = getCharPosition(index);
        return (cp == null) ? -1 : cp.line;
    }

    @Override
    public int getCharColumn(int index) {
        CharPosition cp = getCharPosition(index);
        return (cp == null) ? -1 : cp.column;
    }

    @Override
    public CharPosition getCharPosition(int index) {
        CharPosition pos = findNearestByIndex(index);
        if ( pos == null ) { return null; }
        CharPosition res;
        if (pos.index == index) {
            return pos;
        } else if (pos.index < index) {
            res = findIndexForward(pos, index);
        } else {
            res = findIndexBackward(pos, index);
        }
        if (Math.abs(index - pos.index) >= mSwitchIndex) {
            cache.add(res);
        }
        return res;
    }

    @Override
    public CharPosition getCharPosition(int line, int column) {
        CharPosition pos = findNearestByLine(line);
        if ( pos == null ) {
            CharPosition cp = new CharPosition(line, column);
            cache.add(cp);
            return cp;
        }
        CharPosition res;
        if (pos.line == line) {
            if (pos.column == column) {
                return pos;
            }
            return findInLine(pos, line, column);
        } else if (pos.line < line) {
            res = findLiCoForward(pos, line, column);
        } else {
            res = findLiCoBackward(pos, line, column);
        }
        if (Math.abs(pos.line - line) > mSwitchLine) {
            cache.add(res);
        }
        return res;
    }

    @Override
    public void beforeReplace(CodeAnalyzerResultContent content) {
        //Do nothing
    }

    @Override
    public void afterInsert(CodeAnalyzerResultContent content, int startLine, int startColumn, int endLine, int endColumn,
                            CharSequence insertedContent) {
        if (isHandleEvent()) {
            for (CharPosition pos : cache) {
                if (pos.line == startLine) {
                    if (pos.column >= startColumn) {
                        pos.index += insertedContent.length();
                        pos.line += endLine - startLine;
                        pos.column = endColumn + pos.column - startColumn;
                    }
                } else if (pos.line > startLine) {
                    pos.index += insertedContent.length();
                    pos.line += endLine - startLine;
                }
            }
        }
    }

    @Override
    public void afterDelete(CodeAnalyzerResultContent content, int startLine, int startColumn, int endLine, int endColumn,
                            CharSequence deletedContent) {
        if (isHandleEvent()) {
            List<CharPosition> garbage = new ArrayList<>();
            for (CharPosition pos : cache) {
                if (pos.line == startLine) {
                    if (pos.column >= startColumn)
                        garbage.add(pos);
                } else if (pos.line > startLine) {
                    if (pos.line < endLine) {
                        garbage.add(pos);
                    } else if (pos.line == endLine) {
                        garbage.add(pos);
                    } else {
                        pos.index -= deletedContent.length();
                        pos.line -= endLine - startLine;
                    }
                }
            }
            cache.removeAll(garbage);
        }
    }

    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        CEObject.dumpAll(this, offset);
    }
}

