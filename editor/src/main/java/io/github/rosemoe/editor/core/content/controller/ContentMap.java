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
package io.github.rosemoe.editor.core.content.controller;

import java.util.ArrayList;
import java.util.List;

import io.github.rosemoe.editor.core.content.processors.ContentLineRemoveListener;
import io.github.rosemoe.editor.core.content.processors.indexer.CachedIndexer;
import io.github.rosemoe.editor.core.content.processors.indexer.Indexer;
import io.github.rosemoe.editor.core.content.processors.indexer.NoCacheIndexer;
import io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.controller.CursorController;
import io.github.rosemoe.editor.core.CharPosition;
import io.github.rosemoe.editor.core.grid.Grid;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.util.annotations.Experimental;
import io.github.rosemoe.struct.BlockLinkedList;

import static io.github.rosemoe.editor.core.grid.Cell.SPLIT_SPLITTING;

/**
 * This class saves the text content for editor and maintains line widths
 *
 * @author Rose
 */
public class ContentMap extends Grid implements CharSequence {

    /**
     * Use a BlockLinkedList instead of ArrayList.
     * <p>
     * This can be faster while inserting in large text.
     * But in other conditions, it is quite slow.
     * <p>
     * Disabled by default.
     */
    @Experimental
    public static boolean useBlock = false;
    public static int sInitialListCapacity = 1000;

    public int textLength;
    public int nestedBatchEdit;

    private List<ContentLineController> lines;
    private Indexer indexer;
    private ContentActionStack contentManager;
    private CursorController cursor;
    private List<ContentListener> mListeners;
    private ContentLineRemoveListener mLineListener;

    private final CodeEditor editor;

    /**
     * This constructor will create a ContentMap object with no text
     */
    public ContentMap() {
        this(null,null);
        behaviourOnCellSplit = SPLIT_SPLITTING;
    }

    /**
     * This constructor will create a ContentMap object with the given source
     * If you give us null,it will just create a empty ContentMap object
     *
     * @param src The source of ContentMap
     */
    public ContentMap(CharSequence src, CodeEditor editor) {
        behaviourOnCellSplit = SPLIT_SPLITTING;
        this.editor = editor;
        if (src == null) {
            src = "";
        }
        textLength = 0;
        nestedBatchEdit = 0;
        if (!useBlock)
            lines = new ArrayList<>(getInitialLineCapacity());
        else
            lines = new BlockLinkedList<>(5000);
        append();
        mListeners = new ArrayList<>();
        contentManager = new ContentActionStack(this);
        indexer = new NoCacheIndexer(this);
        if (src.length() == 0) {
            setUndoEnabled(true);
            return;
        }
        setUndoEnabled(false);
        insert(0, 0, src);
        setUndoEnabled(true);
    }

    /**
     * Test whether the two ContentLineController have the same content
     *
     * @param a ContentLineController
     * @param b another ContentLineController
     * @return Whether equals in content
     */
    private static boolean equals(ContentLineController a, ContentLineController b) {
        if (a.length() != b.length()) {
            return false;
        }
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public char charAt(int index) {
        checkIndex(index);
        CharPosition p = getIndexer().getCharPosition(index);
        return charAt(p.line, p.column);
    }

    @Override
    public int length() {
        return textLength;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start > end) {
            throw new StringIndexOutOfBoundsException("start > end");
        }
        CharPosition s = getIndexer().getCharPosition(start);
        CharPosition e = getIndexer().getCharPosition(end);
        return subGrid(s.getLine(), s.getColumn(), e.getLine(), e.getColumn()).toString();
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
        return get(line).size();
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
     * This task will usually completed by {@link Indexer}
     *
     * @param line   Line of index
     * @param column Column on line of index
     * @return Transformed index for the given arguments
     */
    public int getCharIndex(int line, int column) {
        return getIndexer().getCharIndex(line, column);
    }

    /**
     * Insert content to this object
     *
     * @param line   The insertion's line position
     * @param column The insertion's column position
     * @param text   The text you want to insert at the position
     */
    public void insert(int line, int column, CharSequence text) {
        checkLineAndColumn(line, column, true);
        if (text == null) {
            throw new IllegalArgumentException("text can not be null");
        }

        //-----Notify------
        if (cursor != null)
            cursor.beforeInsert(line, column);

        int workLine = line;
        int workIndex = column;
        if (workIndex == -1) {
            workIndex = 0;
        }
        ContentLineController currLine = (ContentLineController) get(workLine);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                ContentLineController newLine = new ContentLineController();
                newLine.append(currLine, workIndex, currLine.length());
                currLine.removeCells(workIndex, currLine.length());
                put(workLine + 1, newLine);
                currLine = newLine;
                workIndex = 0;
                workLine++;
            } else {
                currLine.insert(workIndex, c);
                workIndex++;
            }
        }
        textLength += text.length();
        this.dispatchAfterInsert(line, column, workLine, workIndex, text);
    }

    /**
     * Delete character in [start,end)
     *
     * @param start Start position in content
     * @param end   End position in content
     */
    public void delete(int start, int end) {
        checkIndex(start);
        checkIndex(end);
        CharPosition startPos = getIndexer().getCharPosition(start);
        CharPosition endPos = getIndexer().getCharPosition(end);
        if (start != end) {
            delete(startPos.line, startPos.column, endPos.line, endPos.column);
        }
    }

    /**
     * Delete text in the given region
     *
     * @param startLine         The start line position
     * @param columnOnStartLine The start column position
     * @param endLine           The end line position
     * @param columnOnEndLine   The end column position
     */
    public void delete(int startLine, int columnOnStartLine, int endLine, int columnOnEndLine) {
        StringBuilder changedContent = new StringBuilder();
        if (startLine == endLine) {
            checkLineAndColumn(endLine, columnOnEndLine, true);
            checkLineAndColumn(startLine, columnOnStartLine == -1 ? 0 : columnOnStartLine, true);
            int beginIdx = columnOnStartLine;
            if (columnOnStartLine == -1) {
                beginIdx = 0;
            }
            if (beginIdx > columnOnEndLine) {
                throw new IllegalArgumentException("start > end");
            }
            ContentLineController curr = (ContentLineController) get(startLine);
            int len = curr.length();
            if (beginIdx < 0 || beginIdx > len || columnOnEndLine > len) {
                throw new StringIndexOutOfBoundsException("column start or column end is out of bounds");
            }

            //-----Notify------
            if (cursor != null)
                if (columnOnStartLine != -1)
                    cursor.beforeDelete(startLine, columnOnStartLine, endLine, columnOnEndLine);
                else
                    cursor.beforeDelete(startLine == 0 ? 0 : startLine - 1, startLine == 0 ? 0 : getColumnCount(startLine - 1), endLine, columnOnEndLine);

            changedContent.append(curr, beginIdx, columnOnEndLine);
            curr.removeCells(beginIdx, columnOnEndLine-beginIdx);
            textLength -= columnOnEndLine - columnOnStartLine;
            if (columnOnStartLine == -1) {
                if (startLine == 0) {
                    textLength++;
                } else {
                    Line previous = get(startLine - 1);
                    previous.append(curr);
                    ContentLineController rm = (ContentLineController) remove(startLine);
                    if (mLineListener != null) {
                        mLineListener.onRemove(this, rm);
                    }
                    changedContent.insert(0, '\n');
                    startLine--;
                    columnOnStartLine = getColumnCount(startLine);
                }
            }
        } else if (startLine < endLine) {
            checkLineAndColumn(startLine, columnOnStartLine, true);
            checkLineAndColumn(endLine, columnOnEndLine, true);

            //-----Notify------
            if (cursor != null)
                cursor.beforeDelete(startLine, columnOnStartLine, endLine, columnOnEndLine);

            for (int i = 0; i < endLine - startLine - 1; i++) {
                ContentLineController line = (ContentLineController) remove(startLine + 1);
                if (mLineListener != null) {
                    mLineListener.onRemove(this, line);
                }
                textLength -= line.length() + 1;
                changedContent.append('\n').append(line);
            }
            int currEnd = startLine + 1;
            ContentLineController start = (ContentLineController) get(startLine);
            ContentLineController end = (ContentLineController) get(currEnd);
            textLength -= start.length() - columnOnStartLine;
            changedContent.insert(0, start, columnOnStartLine, start.length());
            start.removeCells(columnOnStartLine, start.length());
            textLength -= columnOnEndLine;
            changedContent.append('\n').append(end, 0, columnOnEndLine);
            end.removeCells(0, columnOnEndLine);
            textLength--;
            ContentLineController r = (ContentLineController) remove(currEnd);
            if (mLineListener != null) {
                mLineListener.onRemove(this, r);
            }
            start.append(end);
        } else {
            throw new IllegalArgumentException("start line > end line");
        }
        this.dispatchAfterDelete(startLine, columnOnStartLine, endLine, columnOnEndLine, changedContent);
    }

    /**
     * Replace the text in the given region
     * This action will completed by calling {@link ContentMap#delete(int, int, int, int)} and {@link ContentMap#insert(int, int, CharSequence)}
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
        this.dispatchBeforeReplace();
        delete(startLine, columnOnStartLine, endLine, columnOnEndLine);
        insert(startLine, columnOnStartLine, text);
    }

    /**
     * When you are going to use {@link CharSequence#charAt(int)} frequently,you are required to call
     * this method.Because the way ContentMap save text,it is usually slow to transform index to
     * (line,column) from the start of text when the text is big.
     * By calling this method,you will be able to get faster because calling this will
     * cause the ITextContent object use a Indexer with cache.
     * The performance is highly improved while linearly getting characters.
     *
     * @param initialIndex The Indexer with cache will take it into this index to its cache
     */
    public void beginStreamCharGetting(int initialIndex) {
        indexer = new CachedIndexer(this);
        indexer.getCharPosition(initialIndex);
    }

    /**
     * When you finished calling {@link CharSequence#charAt(int)} frequently,you can call this method
     * to free the Indexer with cache.
     * This is not forced.
     */
    public void endStreamCharGetting() {
        indexer = new NoCacheIndexer(this);
    }

    /**
     * Undo the last modification
     * NOTE:When there are too much modification,old modification will be deleted from ContentActionStack
     */
    public void undo() {
        contentManager.undo(this);
    }

    /**
     * Redo the last modification
     */
    public void redo() {
        contentManager.redo(this);
    }

    /**
     * Whether we can undo
     *
     * @return Whether we can undo
     */
    public boolean canUndo() {
        return contentManager.canUndo();
    }

    /**
     * Whether we can redo
     *
     * @return Whether we can redo
     */
    public boolean canRedo() {
        return contentManager.canRedo();
    }

    /**
     * Set whether enable the ContentActionStack.
     * If false,any modification will not be taken down and previous modification that
     * is already in ContentActionStack will be removed.Does not make changes to content.
     *
     * @param enabled New state for ContentActionStack
     */
    public void setUndoEnabled(boolean enabled) {
        contentManager.setUndoEnabled(enabled);
    }

    /**
     * A delegate method.
     * Notify the ContentActionStack to begin batch edit(enter a new layer).
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
     * Notify the ContentActionStack to end batch edit(exit current layer).
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
     * Add a new {@link ContentListener} to the ContentMap
     *
     * @param listener The listener to add
     */
    public void addContentListener(ContentListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can not be null");
        }
        if (listener instanceof Indexer) {
            throw new IllegalArgumentException("Permission denied");
        }
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Remove the given listener of this ContentMap
     *
     * @param listener The listener to remove
     */
    public void removeContentListener(ContentListener listener) {
        if (listener instanceof Indexer) {
            throw new IllegalArgumentException("Permission denied");
        }
        mListeners.remove(listener);
    }

    /**
     * Get the using {@link Indexer} object
     *
     * @return Indexer for this object
     */
    public Indexer getIndexer() {
        if (indexer.getClass() != CachedIndexer.class && cursor != null) {
            return cursor.getIndexer();
        }
        return indexer;
    }

    @Override
    public boolean equals(Object anotherObject) {
        if (anotherObject instanceof ContentMap) {
            ContentMap content = (ContentMap) anotherObject;
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
     * @return StringBuilder form of ContentMap
     */
    public StringBuilder toStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.ensureCapacity(textLength + 10);
        boolean first = true;
        final int lines = size();
        for (int i = 0; i < lines; i++) {
            ContentLineController line = (ContentLineController) this.get(i);
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

    public void instanciateCursor() {

    }
    /**
     * Get CursorController for editor (Create if there is not)
     *
     * @return CursorController
     */
    public CursorController getCursor() {
        if (cursor == null) {
            cursor = new CursorController(this,editor);
        }
        return cursor;
    }

    /**
     * Dispatch events to listener before replacement
     */
    private void dispatchBeforeReplace() {
        contentManager.beforeReplace(this);
        if (cursor != null)
            cursor.beforeReplace();
        if (indexer instanceof ContentListener) {
            ((ContentListener) indexer).beforeReplace(this);
        }
        for (ContentListener lis : mListeners) {
            lis.beforeReplace(this);
        }
    }

    /**
     * Dispatch events to listener after deletion
     *
     * @param startLine Start line
     * @param startCol Start Column
     * @param endLine End line
     * @param endCol End column
     * @param text Text deleted
     */
    private void dispatchAfterDelete(int startLine, int startCol, int endLine, int endCol, CharSequence text) {
        contentManager.afterDelete(this, startLine, startCol, endLine, endCol, text);
        if (cursor != null)
            cursor.afterDelete(startLine, startCol, endLine, endCol, text);
        if (indexer instanceof ContentListener) {
            ((ContentListener) indexer).afterDelete(this, startLine, startCol, endLine, endCol, text);
        }
        for (ContentListener lis : mListeners) {
            lis.afterDelete(this, startLine, startCol, endLine, endCol, text);
        }
    }

    /**
     * Dispatch events to listener after insertion
     *
     * @param startLine Start line
     * @param startCol Start Column
     * @param endLine End line
     * @param endCol End column
     * @param text Text deleted
     */
    private void dispatchAfterInsert(int startLine, int startCol, int endLine, int endCol, CharSequence text) {
        contentManager.afterInsert(this, startLine, startCol, endLine, endCol, text);
        if (cursor != null)
            cursor.afterInsert(startLine, startCol, endLine, endCol, text);
        if (indexer instanceof ContentListener) {
            ((ContentListener) indexer).afterInsert(this, startLine, startCol, endLine, endCol, text);
        }
        for (ContentListener lis : mListeners) {
            lis.afterInsert(this, startLine, startCol, endLine, endCol, text);
        }
    }

    /**
     * Check whether the index is valid
     *
     * @param index Index to check
     */
    public void checkIndex(int index) {
        if (index > length()) {
            throw new StringIndexOutOfBoundsException("Index " + index + " out of bounds. length:" + length());
        }
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

    /**
     * Returns the default capacity of text line list
     *
     * @return Default capacity
     */
    public static int getInitialLineCapacity() {
        return ContentMap.sInitialListCapacity;
    }

}
