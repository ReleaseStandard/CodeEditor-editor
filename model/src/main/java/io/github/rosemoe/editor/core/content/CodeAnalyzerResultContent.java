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
package io.github.rosemoe.editor.core.content;

import java.util.ArrayList;
import java.util.List;

import io.github.rosemoe.editor.core.content.processors.ContentLineRemoveListener;
import io.github.rosemoe.editor.core.content.processors.indexer.CachedContentIndexer;
import io.github.rosemoe.editor.core.content.processors.indexer.ContentIndexer;
import io.github.rosemoe.editor.core.content.processors.indexer.NoCacheContentIndexer;

import io.github.rosemoe.editor.core.analyze.result.AnalyzerResult;
import io.github.rosemoe.editor.core.grid.Grid;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;

import static io.github.rosemoe.editor.core.grid.Cell.*;

/**
 * This class saves the text content for editor and maintains line widths
 * We must provide a CharSequence interface for this result.
 * @author Rose
 */
public class CodeAnalyzerResultContent extends Grid<ContentCell> implements CharSequence, AnalyzerResult {

    public int textLength;
    public int nestedBatchEdit;

    private ContentIndexer contentIndexer;
    private List<ContentListener> mListeners;
    private ContentLineRemoveListener mLineListener;


    /**
     * This constructor will create a CodeAnalyzerResultContent object with no text
     */
    public CodeAnalyzerResultContent() {
        behaviourOnCellSplit = SPLIT_SPLITTING;
        textLength = 0;
        nestedBatchEdit = 0;
        mListeners = new ArrayList<>();
        contentIndexer = new NoCacheContentIndexer(this);
    }

    /**
     * This constructor will create a CodeAnalyzerResultContent object with the given source
     * If you give us null,it will just create a empty CodeAnalyzerResultContent object
     *
     * @param src The source of CodeAnalyzerResultContent
     */
    public CodeAnalyzerResultContent(CharSequence src) {
        this();
        append(src);
    }

    /**
     * Set a line listener
     *
     * @param lis the listener,maybe null
     * @see ContentLineRemoveListener
     */
    public void setLineListener(ContentLineRemoveListener lis) {
        this.mLineListener = lis;
    }

    /**
     * Get the character at the given position
     * If (column == getColumnCount(line)),it returns '\n'
     * IndexOutOfBoundsException is thrown
     *
     * @param line   The line position of character
     * @param column The column position of character
     * @return The character at the given position
     */
    public char charAt(int line, int column) {
        checkLineAndColumn(line, column, true);
        if (column == getColumnCount(line)) {
            return '\n';
        }
        ContentCell cc = (ContentCell) get(line).get(column);
        return cc.c;
    }

    /**
     * Get how many lines there are
     *
     * @return Line count
     */
    public int getLineCount() {
        return size();
    }

    /**
     * Get how many characters is on the given line
     * If (line < 0 or line >= getLineCount()),it will throw a IndexOutOfBoundsException
     *
     * @param line The line to get
     * @return Character count on line
     */
    public int getColumnCount(int line) {
        checkLine(line);
        return get(line).getWidth();
    }

    /**
     * Get the given line text without '\n' character
     *
     * @param line The line to get
     * @return New String object of this line
     */
    public String getLineString(int line) {
        checkLine(line);
        return get(line).toString();
    }

    /**
     * Get characters of line
     */
    public void getLineChars(int line, char[] dest) {
        dest = get(line).toString().toCharArray();
    }

    /**
     * Transform the (line,column) position to index
     * This task will usually completed by {@link ContentIndexer}
     *
     * @param line   Line of index
     * @param column Column on line of index
     * @return Transformed index for the given arguments
     */
    public int getCharIndex(int line, int column) {
        //return getIndexer().getCharIndex(line, column);
        return -1;
    }

    /**
     * Insert content to this object
     *  @param line   The insertion's line position
     * @param column The insertion's column position
     * @param text   The text you want to insert at the position
     * @return
     */
    public CharSequence insert(int line, int column, CharSequence text) {
        checkLineAndColumn(line, column, true);
        if (text == null) {
            throw new IllegalArgumentException("text can not be null");
        }

        //-----Notify------
        //if (cursor != null)
        //    cursor.beforeInsert(line, column);
        // TODO : break

        int workLine = line;
        int workIndex = column;
        if (workIndex == -1) {
            workIndex = 0;
        }
        Line<ContentCell> currLine = get(workLine);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                Line<ContentCell> newLine = new Line<ContentCell>();
                newLine.append(currLine.subLine(workIndex));
                currLine.removeCells(workIndex, currLine.getWidth());
                put(workLine + 1, newLine);
                currLine = newLine;
                workIndex = 0;
                workLine++;
            } else {
                ContentCell cc = new ContentCell(c);
                cc.column = workIndex;
                currLine.insertCell(cc);
                workIndex++;
            }
        }
        textLength += text.length();
        return text;
    }

    /**
     * Delete character in [start,end)
     *  @param start Start position in content
     * @param end   End position in content
     * @return the deleted content.
     */
    public String delete(int start, int end) {
        /*
        CharPosition startPos = getIndexer().getCharPosition(start);
        CharPosition endPos = getIndexer().getCharPosition(end);
        if (start != end) {
            return delete(startPos.line, startPos.column, endPos.line, endPos.column);
        }*/
        return null;
    }

    /**
     * Delete text in the given region
     *  @param startLine         The start line position
     * @param columnOnStartLine The start column position
     * @param endLine           The end line position
     * @param columnOnEndLine   The end column position
     * @return
     */
    public String delete(int startLine, int columnOnStartLine, int endLine, int columnOnEndLine) {
        removeCells(startLine, columnOnStartLine, endLine, columnOnEndLine);
        // TODO break
        // this.dispatchAfterDelete(startLine, columnOnStartLine, endLine, columnOnEndLine, changedContent);
        return "";
    }

    /**
     * Replace the text in the given region
     * This action will completed by calling {@link CodeAnalyzerResultContent#delete(int, int, int, int)} and {@link CodeAnalyzerResultContent#insert(int, int, CharSequence)}
     *
     * @param startLine         The start line position
     * @param columnOnStartLine The start column position
     * @param endLine           The end line position
     * @param columnOnEndLine   The end column position
     * @param text              The text to replace old text
     */
    public void replace(int startLine, int columnOnStartLine, int endLine, int columnOnEndLine, CharSequence text) {
        if (text == null) {
            throw new IllegalArgumentException("text can not be null");
        }
        delete(startLine, columnOnStartLine, endLine, columnOnEndLine);
        insert(startLine, columnOnStartLine, text);
    }

    /**
     * When you are going to use {@link CharSequence#charAt(int)} frequently,you are required to call
     * this method.Because the way CodeAnalyzerResultContent save text,it is usually slow to transform index to
     * (line,column) from the start of text when the text is big.
     * By calling this method,you will be able to get faster because calling this will
     * cause the ITextContent object use a ContentIndexer with cache.
     * The performance is highly improved while linearly getting characters.
     *
     * @param initialIndex The ContentIndexer with cache will take it into this index to its cache
     */
    public void beginStreamCharGetting(int initialIndex) {
        contentIndexer = new CachedContentIndexer(this);
        contentIndexer.getCharPosition(initialIndex);
    }

    /**
     * When you finished calling {@link CharSequence#charAt(int)} frequently,you can call this method
     * to free the ContentIndexer with cache.
     * This is not forced.
     */
    public void endStreamCharGetting() {
        contentIndexer = new NoCacheContentIndexer(this);
    }

    /**
     * A delegate method.
     * Notify the ContentActionStackAnalyzer to begin batch edit(enter a new layer).
     * NOTE: batch edit in Android can be nested.
     *
     * @return Whether in batch edit
     */
    public boolean beginBatchEdit() {
        nestedBatchEdit++;
        return isInBatchEdit();
    }

    /**
     * A delegate method.
     * Notify the ContentActionStackAnalyzer to end batch edit(exit current layer).
     *
     * @return Whether in batch edit
     */
    public boolean endBatchEdit() {
        nestedBatchEdit--;
        if (nestedBatchEdit < 0) {
            nestedBatchEdit = 0;
        }
        return isInBatchEdit();
    }

    /**
     * Returns whether we are in batch edit
     *
     * @return Whether in batch edit
     */
    public boolean isInBatchEdit() {
        return nestedBatchEdit > 0;
    }

    /**
     * Add a new {@link ContentListener} to the CodeAnalyzerResultContent
     *
     * @param listener The listener to add
     */
    public void addContentListener(ContentListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can not be null");
        }
        if (listener instanceof ContentIndexer) {
            throw new IllegalArgumentException("Permission denied");
        }
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Remove the given listener of this CodeAnalyzerResultContent
     *
     * @param listener The listener to remove
     */
    public void removeContentListener(ContentListener listener) {
        if (listener instanceof ContentIndexer) {
            throw new IllegalArgumentException("Permission denied");
        }
        mListeners.remove(listener);
    }

    /**
     * Get the using {@link ContentIndexer} object
     *
     * @return ContentIndexer for this object
     */
    /*
    public ContentIndexer getIndexer() {
        if (contentIndexer.getClass() != CachedContentIndexer.class && cursor != null) {
            return cursor.getIndexer();
        }
        return contentIndexer;
    }
*/
    @Override
    public boolean equals(Object anotherObject) {
        if (anotherObject instanceof CodeAnalyzerResultContent) {
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) anotherObject;
            if (content.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < this.size(); i++) {
                if (!get(i).equals(content.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the text in StringBuilder form
     * Used by TextColorProvider
     * This can improve the speed in char getting for tokenizing
     *
     * @return StringBuilder form of CodeAnalyzerResultContent
     */
    public StringBuilder toStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.ensureCapacity(textLength + 10);
        boolean first = true;
        final int lines = size();
        for (int i = 0; i < lines; i++) {
            Line<ContentCell> line = this.get(i);
            if (!first) {
                sb.append('\n');
            } else {
                first = false;
            }
            // TODO break
            //line.appendTo(sb);
        }
        return sb;
    }

    /**
     * Check whether the line is valid
     *
     * @param line Line to check
     */
    protected void checkLine(int line) {
        if (line >= size()) {
            throw new StringIndexOutOfBoundsException("Line " + line + " out of bounds. line count:" + size());
        }
    }

    /**
     * Check whether the line and column is valid
     *
     * @param line       The line to check
     * @param column     The column to check
     * @param allowEqual Whether allow (column == getColumnCount(line))
     */
    public void checkLineAndColumn(int line, int column, boolean allowEqual) {
        checkLine(line);
        int len = get(line).getWidth();
        if (column > len || (!allowEqual && column == len)) {
            throw new StringIndexOutOfBoundsException(
                    "Column " + column + " out of bounds.line: " + line + " ,column count:" + len);
        }
    }

    @Override
    public int length() {
        int length = 0;
        for(Line l : this) {
            length += l.getWidth();
        }
        return length;
    }

    @Override
    public char charAt(int index) {
        return charAtNaive(index);
    }

    public char charAtNaive(final int index) {
        int idx = 0;
        // TODO : or use the contentIndexer system or find another system.
        for(Line<ContentCell> l : this) {
            if ( ( idx + l.getWidth() ) < index ) {
                idx += l.getWidth();
                continue;
            }
            int col = idx - index;
            Integer key = floorKey(col);
            return l.get(key).c;
        }
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }

    public int append(CharSequence text) {
        Line<ContentCell> line = new Line<>();
        for(int a = 0; a < text.length(); a=a+1) {
            line.append(new ContentCell(text.charAt(a)));
        }
        return append(line);
    }
}
