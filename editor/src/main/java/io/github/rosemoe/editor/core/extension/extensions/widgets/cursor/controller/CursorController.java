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
package io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.controller;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

import io.github.rosemoe.editor.core.extension.extensions.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.contentAnalyzer.controller.ContentMap;
import io.github.rosemoe.editor.core.extension.extensions.widgets.contentAnalyzer.processors.indexer.CachedIndexer;
import io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.view.CursorView;
import io.github.rosemoe.editor.core.extension.extensions.widgets.userinput.UserInputModel;
import io.github.rosemoe.editor.core.extension.extensions.langs.LanguagePlugin;
import io.github.rosemoe.editor.core.CharPosition;
import io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.CursorModel;
import io.github.rosemoe.editor.core.IntPair;
import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.util.shortcuts.A;

import static io.github.rosemoe.editor.core.extension.extensions.langs.helpers.TextUtils.isEmoji;

/**
 * @author Rose
 * Warning:The cursor position will update automatically when the content has been changed by other way
 * Cursor controller : the cursor widget aka blinking part, context action (context action popup).
 *
 */
public final class CursorController extends WidgetExtensionController {

    private final ContentMap mContent;
    private final CachedIndexer mIndexer;
    private LanguagePlugin mLanguage;
    public float mInsertSelWidth;

    public CursorModel model = new CursorModel();
	public final CursorView view;
    public CursorBlinkController blink;        // Manage cursor blink effect
    private CharPosition mLeft, mRight;
    public ArrayList<CursorPartController> parts = new ArrayList<>();

    /**
     * Create a new CursorController for ContentMap
     *
     * @param content Target content
     */
    public CursorController(ContentMap content, CodeEditor editor) {
        super(editor);
        mContent = content;
        mIndexer = new CachedIndexer(content);
        view     = new CursorView(editor);
        blink = new CursorBlinkController(editor, CursorBlinkController.DEFAULT_CURSOR_BLINK_PERIOD);
        mInsertSelWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, Resources.getSystem().getDisplayMetrics()) / 2;
    }

    /**
     * Whether the given character is a white space character
     *
     * @param c Character to check
     * @return Result whether a space char
     */
    protected static boolean isWhitespace(char c) {
        return (c == '\t' || c == ' ');
    }

    /**
     * Make left and right cursor on the given position
     *
     * @param line   The line position
     * @param column The column position
     */
    public void set(int line, int column) {
        setLeft(line, column);
        setRight(line, column);
    }

    /**
     * Make left cursor on the given position
     *
     * @param line   The line position
     * @param column The column position
     */
    public void setLeft(int line, int column) {
        model.mLeft = mIndexer.getCharPosition(line, column).clone();
    }

    /**
     * Make right cursor on the given position
     *
     * @param line   The line position
     * @param column The column position
     */
    public void setRight(int line, int column) {
        model.mRight = mIndexer.getCharPosition(line, column).clone();
    }

    /**
     * Get the left cursor line
     *
     * @return line of left cursor
     */
    public int getLeftLine() {
        return model.mLeft.getLine();
    }

    /**
     * Get the left cursor column
     *
     * @return column of left cursor
     */
    public int getLeftColumn() {
        return model.mLeft.getColumn();
    }

    /**
     * Get the right cursor line
     *
     * @return line of right cursor
     */
    public int getRightLine() {
        return model.mRight.getLine();
    }

    /**
     * Get the right cursor column
     *
     * @return column of right cursor
     */
    public int getRightColumn() {
        return model.mRight.getColumn();
    }

    /**
     * Whether the given position is in selected region
     *
     * @param line   The line to query
     * @param column The column to query
     * @return Whether is in selected region
     */
    public boolean isInSelectedRegion(int line, int column) {
        if (line >= getLeftLine() && line <= getRightLine()) {
            boolean yes = true;
            if (line == getLeftLine()) {
                yes = column >= getLeftColumn();
            }
            if (line == getRightLine()) {
                yes = yes && column < getRightColumn();
            }
            return yes;
        }
        return false;
    }

    /**
     * Get the left cursor index
     *
     * @return index of left cursor
     */
    public int getLeft() {
        return model.mLeft.index;
    }

    /**
     * Get the right cursor index
     *
     * @return index of right cursor
     */
    public int getRight() {
        return model.mRight.index;
    }

    /**
     * Notify the Indexer to update its cache for current display position
     * <p>
     * This will make querying actions quicker
     * <p>
     * Especially when the editor user want to set a new cursor position after scrolling long time
     *
     * @param line First visible line
     */
    public void updateCache(int line) {
        mIndexer.getCharIndex(line, 0);
    }

    /**
     * Get the using Indexer object
     *
     * @return Using Indexer
     */
    public CachedIndexer getIndexer() {
        return mIndexer;
    }

    /**
     * Get whether text is selected
     *
     * @return Whether selected
     */
    public boolean isSelected() {
        return model.mLeft.index != model.mRight.index;
    }

    /**
     * Returns whether auto indent is enabled
     *
     * @return Enabled or disabled
     */
    public boolean isAutoIndent() {
        return model.mAutoIndentEnabled;
    }

    /**
     * Enable or disable auto indent when insert text through CursorController
     *
     * @param enabled Auto Indent state
     */
    public void setAutoIndent(boolean enabled) {
        model.mAutoIndentEnabled = enabled;
    }

    /**
     * Set language for auto indent
     *
     * @param lang The target language
     */
    public void setLanguage(LanguagePlugin lang) {
        mLanguage = lang;
    }

    /**
     * Set tab width for auto indent
     *
     * @param width tab width
     */
    public void setTabWidth(int width) {
        model.mTabWidth = width;
    }
    public int getTabWidth() { return model.mTabWidth; }
    public void onCommitText(CharSequence text) {
        onCommitText(text, true);
    }

    /**
     * Commit text at current state
     *
     * @param text Text commit by InputConnection
     */
    public void onCommitText(CharSequence text, boolean applyAutoIndent) {
        if (isSelected()) {
            mContent.replace(getLeftLine(), getLeftColumn(), getRightLine(), getRightColumn(), text);
        } else {
            if (model.mAutoIndentEnabled && text.length() != 0 && applyAutoIndent) {
                char first = text.charAt(0);
                if (first == '\n') {
                    String line = mContent.getLineString(getLeftLine());
                    int p = 0, count = 0;
                    while (p < getLeftColumn()) {
                        if (isWhitespace(line.charAt(p))) {
                            if (line.charAt(p) == '\t') {
                                count += model.mTabWidth;
                            } else {
                                count++;
                            }
                            p++;
                        } else {
                            break;
                        }
                    }
                    String sub = line.substring(0, getLeftColumn());
                    try {
                        count += mLanguage.getIndentAdvance(sub);
                    } catch (Exception e) {
                        Log.w("EditorCursor", "Language object error", e);
                    }
                    StringBuilder sb = new StringBuilder(text);
                    sb.insert(1, createIndent(count));
                    text = sb;
                }
            }
            mContent.insert(getLeftLine(), getLeftColumn(), text);
        }
    }

    /**
     * Create indent space
     *
     * @param p Target width effect
     * @return Generated space string
     */
    private String createIndent(int p) {
        int tab = 0;
        int space;
        if (mLanguage.useTab()) {
            tab = p / model.mTabWidth;
            space = p % model.mTabWidth;
        } else {
            space = p;
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < tab; i++) {
            s.append('\t');
        }
        for (int i = 0; i < space; i++) {
            s.append(' ');
        }
        return s.toString();
    }

    /**
     * Handle delete submit by InputConnection
     */
    public void onDeleteKeyPressed() {
        if (isSelected()) {
            mContent.delete(getLeftLine(), getLeftColumn(), getRightLine(), getRightColumn());
        } else {
            int col = getLeftColumn(), len = 1;
            //Do not put cursor inside a emotion character
            if (col > 1) {
                char before = mContent.charAt(getLeftLine(), col - 2);
                if (isEmoji(before)) {
                    len = 2;
                }
            }
            mContent.delete(getLeftLine(), getLeftColumn() - len, getLeftLine(), getLeftColumn());
        }
    }

    /**
     * Get position after moving left once
     * @param position A packed pair (line, column) describing the original position
     * @return A packed pair (line, column) describing the result position
     */
    public long getLeftOf(long position) {
        int line = IntPair.getFirst(position);
        int column = IntPair.getSecond(position);
        if (column - 1 >= 0) {
            if (column - 2 >= 0) {
                char ch = mContent.charAt(line, column - 2);
                if (isEmoji(ch)) {
                    column--;
                }
            }
            return IntPair.pack(line, column - 1);
        } else {
            if (line == 0) {
                return 0;
            } else {
                int c_column = mContent.getColumnCount(line - 1);
                return IntPair.pack(line - 1, c_column);
            }
        }
    }

    /**
     * Get position after moving right once
     * @param position A packed pair (line, column) describing the original position
     * @return A packed pair (line, column) describing the result position
     */
    public long getRightOf(long position) {
        int line = IntPair.getFirst(position);
        int column = IntPair.getSecond(position);
        int c_column = mContent.getColumnCount(line);
        if (column + 1 <= c_column) {
            char ch = mContent.charAt(line, column);
            if (isEmoji(ch)) {
                column++;
                if (column + 1 > c_column) {
                    column--;
                }
            }
           return IntPair.pack(line, column + 1);
        } else {
            if (line + 1 == mContent.getLineCount()) {
                return IntPair.pack(line, c_column);
            } else {
                return IntPair.pack(line + 1, 0);
            }
        }
    }

    /**
     * Get position after moving up once
     * @param position A packed pair (line, column) describing the original position
     * @return A packed pair (line, column) describing the result position
     */
    public long getUpOf(long position) {
        int line = IntPair.getFirst(position);
        int column = IntPair.getSecond(position);
        if (line - 1 < 0) {
            line = 1;
        }
        int c_column = mContent.getColumnCount(line - 1);
        if (column > c_column) {
            column = c_column;
        }
        return IntPair.pack(line - 1, column);
    }

    /**
     * Get position after moving down once
     * @param position A packed pair (line, column) describing the original position
     * @return A packed pair (line, column) describing the result position
     */
    public long getDownOf(long position) {
        int line = IntPair.getFirst(position);
        int column = IntPair.getSecond(position);
        int c_line = mContent.getLineCount();
        if (line + 1 >= c_line) {
            return IntPair.pack(line, mContent.getColumnCount(line));
        } else {
            int c_column = mContent.getColumnCount(line + 1);
            if (column > c_column) {
                column = c_column;
            }
            return IntPair.pack(line + 1, column);
        }
    }

    /**
     * Get copy of left cursor
     */
    public CharPosition left() {
        return mLeft.clone();
    }

    /**
     * Get copy of right cursor
     */
    public CharPosition right() {
        return mRight.clone();
    }

    /**
     * Internal call back before insertion
     *
     * @param startLine   Start line
     * @param startColumn Start column
     */
    public void beforeInsert(int startLine, int startColumn) {
        model.cache0 = mIndexer.getCharPosition(startLine, startColumn).clone();
    }

    /**
     * Internal call back before deletion
     *
     * @param startLine   Start line
     * @param startColumn Start column
     * @param endLine     End line
     * @param endColumn   End column
     */
    public void beforeDelete(int startLine, int startColumn, int endLine, int endColumn) {
        model.cache1 = mIndexer.getCharPosition(startLine, startColumn).clone();
        model.cache2 = mIndexer.getCharPosition(endLine, endColumn).clone();
    }

    /**
     * Internal call back before replace
     */
    public void beforeReplace() {
        mIndexer.beforeReplace(mContent);
    }

    /**
     * Internal call back after insertion
     *
     * @param startLine       Start line
     * @param startColumn     Start column
     * @param endLine         End line
     * @param endColumn       End column
     * @param insertedContent Inserted content
     */
    public void afterInsert(int startLine, int startColumn, int endLine, int endColumn,
                     CharSequence insertedContent) {
        mIndexer.afterInsert(mContent, startLine, startColumn, endLine, endColumn, insertedContent);
        int beginIdx = model.cache0.getIndex();
        if (getLeft() >= beginIdx) {
            model.mLeft = mIndexer.getCharPosition(getLeft() + insertedContent.length()).clone();
        }
        if (getRight() >= beginIdx) {
            model.mRight = mIndexer.getCharPosition(getRight() + insertedContent.length()).clone();
        }
    }

    /**
     * Internal call back
     *
     * @param startLine      Start line
     * @param startColumn    Start column
     * @param endLine        End line
     * @param endColumn      End column
     * @param deletedContent Deleted content
     */
    public void afterDelete(int startLine, int startColumn, int endLine, int endColumn,
                     CharSequence deletedContent) {
        mIndexer.afterDelete(mContent, startLine, startColumn, endLine, endColumn, deletedContent);
        int beginIdx = model.cache1.getIndex();
        int endIdx = model.cache2.getIndex();
        int left = getLeft();
        int right = getRight();
        if (beginIdx > right) {
            return;
        }
        if (endIdx <= left) {
            model.mLeft = mIndexer.getCharPosition(left - (endIdx - beginIdx)).clone();
            model.mRight = mIndexer.getCharPosition(right - (endIdx - beginIdx)).clone();
        } else if (/* endIdx > left && */ endIdx < right) {
            if (beginIdx <= left) {
                model.mLeft = mIndexer.getCharPosition(beginIdx).clone();
                model.mRight = mIndexer.getCharPosition(right - (endIdx - Math.max(beginIdx, left))).clone();
            } else {
                model.mRight = mIndexer.getCharPosition(right - (endIdx - beginIdx)).clone();
            }
        } else {
            if (beginIdx <= left) {
                model.mLeft = mIndexer.getCharPosition(beginIdx).clone();
                model.mRight = model.mLeft.clone();
            } else {
                model.mRight = mIndexer.getCharPosition(left + (right - beginIdx)).clone();
            }
        }
    }

    /**
     * Set cursor blinking period
     * If zero or negative period is passed, the cursor will always be shown.
     *
     * @param period The period time of cursor blinking
     */
    public void setBlinkPeriod(int period) {
        if (blink == null) {
            blink = new CursorBlinkController(editorController, period);
        } else {
            int before = blink.model.period;
            blink.model.period = period;
            if (before <= 0 && blink.model.valid && blink.view.editor.view.isAttachedToWindow()) {
                blink.view.editor.view.post(blink);
            }
        }
    }
    /**
     * Set whether blinking is enabled on the cursor.
     * @param state
     */
    public void setBlinkEnabled(boolean state) {
        if ( blink != null ) {
            blink.setEnabled(state);
        }
    }
    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if ( blink != null ) {
            blink.setEnabled(state);
        }
    }

    @Override
    public void attachView(View v) {

    }

    @Override
    protected void initFromJson(JsonNode extension) {
        JsonNode blink = extension.get("blink");
        if ( blink != null ) {
            JsonNode period = blink.get("period");
            if ( period != null ) {
                setBlinkPeriod(period.asInt());
            }
        }
    }

    @Override
    public void clear() {
        parts.clear();
        if (!isSelected()) {
            model.mInsertHandle.clear();
        }
        if (!editorController.mTextActionPresenter.shouldShowCursor()) {
            model.mLeftHandle.clear();
            model.mRightHandle.clear();
        }
    }

    @Override
    public void handleRefresh(Object canvas, Object ...args) {

        Integer firstVisibleChar = (Integer) args[0];
        Integer lastVisibleChar = (Integer) args[1];
        Integer line = (Integer) args[2];
        Float paintingOffset = (Float) args[3];
        Integer row = (Integer) args[4];

        if (isSelected()) {
            if (editorController.mTextActionPresenter.shouldShowCursor()) {
                if (getLeftLine() == line && isInside(getLeftColumn(), firstVisibleChar, lastVisibleChar, line)) {
                    float centerX = paintingOffset + editorController.measureText(editorController.mBuffer, firstVisibleChar, getLeftColumn() - firstVisibleChar);
                    parts.add(new CursorPartController(editorController, row, centerX, A.getRectF(model.mLeftHandle), false, UserInputModel.LEFT));
                }
                if (getRightLine() == line && isInside(getRightColumn(), firstVisibleChar, lastVisibleChar, line)) {
                    float centerX = paintingOffset + editorController.measureText(editorController.mBuffer, firstVisibleChar, getRightColumn() - firstVisibleChar);
                    parts.add(new CursorPartController(editorController, row, centerX, A.getRectF(model.mRightHandle), false, UserInputModel.RIGHT));

                }
            }
        } else if (getLeftLine() == line && isInside(getLeftColumn(), firstVisibleChar, lastVisibleChar, line)) {
            float centerX = paintingOffset + editorController.measureText(editorController.mBuffer, firstVisibleChar, getLeftColumn() - firstVisibleChar);
            parts.add(new CursorPartController(editorController, row, centerX, editorController.userInput.shouldDrawInsertHandle() ? A.getRectF(model.mInsertHandle) : null, true));
        }

        // call refresh on underlying objects
        for(CursorPartController part : parts) {
            part.refresh((Canvas)canvas);
        }
    }
    /**
     * Is inside the region
     *
     * @param index Index to test
     * @param start Start of region
     * @param end   End of region
     * @param line  Checking line
     * @return Whether cursor should be drawn in this row
     */
    public boolean isInside(int index, int start, int end, int line) {
        // Due not to draw duplicate cursors for a single one
        if (index == end && editorController.mText.getLine(line).length() != end) {
            return false;
        }
        return index >= start && index <= end;
    }
}

