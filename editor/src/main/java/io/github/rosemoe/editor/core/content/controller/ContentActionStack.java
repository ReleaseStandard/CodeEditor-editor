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
import java.util.Stack;

/**
 * Helper class for ContentGrid to take down modification
 * As well as provide Undo/Redo actions
 *
 * @author Rose
 */
public final class ContentActionStack extends Stack<ContentActionStack.ContentAction> implements ContentListener {

    public int maxStackSize = 100;
    private final ContentGrid mContent;
    private InsertAction mInsertAction;
    private DeleteAction mDeleteAction;
    public boolean undoEnabled;
    public boolean replaceMark = false;
    public boolean ignoreModification = false;
    private int mStackPointer;

    /**
     * Create ContentActionStack with the target content
     *
     * @param content The ContentGrid going to attach
     */
    public ContentActionStack(ContentGrid content) {
        mContent = content;
        mInsertAction = null;
        mDeleteAction = null;
        mStackPointer = 0;
    }

    /**
     * Undo on the given ContentGrid
     *
     * @param content Undo Target
     */
    public void undo(ContentGrid content) {
        if (canUndo()) {
            ignoreModification = true;
            get(mStackPointer - 1).undo(content);
            mStackPointer--;
            ignoreModification = false;
        }
    }

    /**
     * Redo on the given ContentGrid
     *
     * @param content Redo Target
     */
    public void redo(ContentGrid content) {
        if (canRedo()) {
            ignoreModification = true;
            get(mStackPointer).redo(content);
            mStackPointer++;
            ignoreModification = false;
        }
    }

    /**
     * Whether can undo
     *
     * @return Whether can undo
     */
    public boolean canUndo() {
        return undoEnabled && (mStackPointer > 0);
    }

    /**
     * Whether can redo
     *
     * @return Whether can redo
     */
    public boolean canRedo() {
        return undoEnabled && (mStackPointer < size());
    }

    /**
     * Set whether enable this module
     *
     * @param enabled Enable or disable
     */
    public void setUndoEnabled(boolean enabled) {
        undoEnabled = enabled;
        if (!enabled) {
            cleanStack();
        }
    }

    /**
     * Set a max stack size for this ContentActionStack
     *
     * @param maxSize max stack size
     */
    public void setMaxUndoStackSize(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException(
                    "max size can not be zero or smaller.Did you want to disable undo module by calling set_undoEnabled(false)?");
        }
        maxStackSize = maxSize;
        cleanStack();
    }

    /**
     * Clean stack after add or state change
     * This is to limit stack size
     */
    private void cleanStack() {
        if (!undoEnabled) {
            clear();
            mStackPointer = 0;
        } else {
            while (mStackPointer > 1 && size() > maxStackSize) {
                remove(0);
                mStackPointer--;
            }
        }
    }

    /**
     * Clean the stack before pushing
     * If we are not at the end(Undo action executed),remove those actions
     */
    private void cleanBeforePush() {
        while (mStackPointer < size()) {
            pop();
        }
    }

    /**
     * Push a new {@link ContentAction} to stack
     * It will merge actions if possible
     *
     * @param action New {@link ContentAction}
     */
    private void pushAction(ContentAction action) {
        if (!undoEnabled) {
            return;
        }
        cleanBeforePush();
        if (mContent.isInBatchEdit()) {
            if (isEmpty()) {
                MultiAction a = new MultiAction();
                a.addAction(action);
                push(a);
                mStackPointer++;
            } else {
                ContentAction a = get(size() - 1);
                if (a instanceof MultiAction) {
                    MultiAction ac = (MultiAction) a;
                    ac.addAction(action);
                } else {
                    MultiAction ac = new MultiAction();
                    ac.addAction(action);
                    push(ac);
                    mStackPointer++;
                }
            }
        } else {
            if (isEmpty()) {
                push(action);
                mStackPointer++;
            } else {
                ContentAction last = get(size() - 1);
                if (last.canMerge(action)) {
                    last.merge(action);
                } else {
                    push(action);
                    mStackPointer++;
                }
            }
        }
        cleanStack();
    }

    @Override
    public void beforeReplace(ContentGrid content) {
        if (ignoreModification) {
            return;
        }
        replaceMark = true;
    }

    @Override
    public void afterInsert(ContentGrid content, int startLine, int startColumn, int endLine, int endColumn,
                            CharSequence insertedContent) {
        if (ignoreModification) {
            return;
        }
        mInsertAction = new InsertAction();
        mInsertAction.startLine = startLine;
        mInsertAction.startColumn = startColumn;
        mInsertAction.endLine = endLine;
        mInsertAction.endColumn = endColumn;
        mInsertAction.text = insertedContent;
        if (replaceMark) {
            ReplaceAction rep = new ReplaceAction();
            rep._delete = mDeleteAction;
            rep._insert = mInsertAction;
            pushAction(rep);
        } else {
            pushAction(mInsertAction);
        }
        replaceMark = false;
    }

    @Override
    public void afterDelete(ContentGrid content, int startLine, int startColumn, int endLine, int endColumn,
                            CharSequence deletedContent) {
        if (ignoreModification) {
            return;
        }
        mDeleteAction = new DeleteAction();
        mDeleteAction.endColumn = endColumn;
        mDeleteAction.startColumn = startColumn;
        mDeleteAction.endLine = endLine;
        mDeleteAction.startLine = startLine;
        mDeleteAction.text = deletedContent;
        if (!replaceMark) {
            pushAction(mDeleteAction);
        }
    }

    /**
     * For saving modification better
     *
     * @author Rose
     */
    public interface ContentAction {

        /**
         * Undo this action
         *
         * @param content On the given object
         */
        void undo(ContentGrid content);

        /**
         * Redo this action
         *
         * @param content On the given object
         */
        void redo(ContentGrid content);

        /**
         * Get whether the target action can be merged with this action
         *
         * @param action Target action to merge
         * @return Whether can merge
         */
        boolean canMerge(ContentAction action);

        /**
         * Merge with target action
         *
         * @param action Target action to merge
         */
        void merge(ContentAction action);

    }

    /**
     * Insert action model for ContentActionStack
     *
     * @author Rose
     */
    public static final class InsertAction implements ContentAction {

        public int startLine, endLine, startColumn, endColumn;

        public CharSequence text;

        @Override
        public void undo(ContentGrid content) {
            content.delete(startLine, startColumn, endLine, endColumn);
        }

        @Override
        public void redo(ContentGrid content) {
            content.insert(startLine, startColumn, text);
        }

        @Override
        public boolean canMerge(ContentAction action) {
            if (action instanceof InsertAction) {
                InsertAction ac = (InsertAction) action;
                return (ac.startColumn == endColumn && ac.startLine == endLine && ac.text.length() + text.length() < 10000);
            }
            return false;
        }

        @Override
        public void merge(ContentAction action) {
            if (!canMerge(action)) {
                throw new IllegalArgumentException();
            }
            InsertAction ac = (InsertAction) action;
            this.endColumn = ac.endColumn;
            this.endLine = ac.endLine;
            StringBuilder sb;
            if (text instanceof StringBuilder) {
                sb = (StringBuilder) text;
            } else {
                sb = new StringBuilder(text);
                text = sb;
            }
            sb.append(ac.text);
        }

    }

    /**
     * MultiAction saves several actions for ContentActionStack
     *
     * @author Rose
     */
    public static final class MultiAction implements ContentAction {

        private final List<ContentAction> _actions = new ArrayList<>();

        public void addAction(ContentAction action) {
            if (_actions.isEmpty()) {
                _actions.add(action);
            } else {
                ContentAction last = _actions.get(_actions.size() - 1);
                if (last.canMerge(action)) {
                    last.merge(action);
                } else {
                    _actions.add(action);
                }
            }
        }

        @Override
        public void undo(ContentGrid content) {
            for (int i = _actions.size() - 1; i >= 0; i--) {
                _actions.get(i).undo(content);
            }
        }

        @Override
        public void redo(ContentGrid content) {
            for (int i = 0; i < _actions.size(); i++) {
                _actions.get(i).redo(content);
            }
        }

        @Override
        public boolean canMerge(ContentAction action) {
            return false;
        }

        @Override
        public void merge(ContentAction action) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Delete action model for ContentActionStack
     *
     * @author Rose
     */
    public static final class DeleteAction implements ContentAction {

        public int startLine, endLine, startColumn, endColumn;

        public CharSequence text;

        @Override
        public void undo(ContentGrid content) {
            content.insert(startLine, startColumn, text);
        }

        @Override
        public void redo(ContentGrid content) {
            content.delete(startLine, startColumn, endLine, endColumn);
        }

        @Override
        public boolean canMerge(ContentAction action) {
            if (action instanceof DeleteAction) {
                DeleteAction ac = (DeleteAction) action;
                return (ac.endColumn == startColumn && ac.endLine == startLine && ac.text.length() + text.length() < 10000);
            }
            return false;
        }

        @Override
        public void merge(ContentAction action) {
            if (!canMerge(action)) {
                throw new IllegalArgumentException();
            }
            DeleteAction ac = (DeleteAction) action;
            this.startColumn = ac.startColumn;
            this.startLine = ac.startLine;
            StringBuilder sb;
            if (text instanceof StringBuilder) {
                sb = (StringBuilder) text;
            } else {
                sb = new StringBuilder(text);
                text = sb;
            }
            sb.insert(0, ac.text);
        }

    }

    /**
     * Replace action model for ContentActionStack
     *
     * @author Rose
     */
    public static final class ReplaceAction implements ContentAction {

        public InsertAction _insert;
        public DeleteAction _delete;

        @Override
        public void undo(ContentGrid content) {
            _insert.undo(content);
            _delete.undo(content);
        }

        @Override
        public void redo(ContentGrid content) {
            _delete.redo(content);
            _insert.redo(content);
        }

        @Override
        public boolean canMerge(ContentAction action) {
            return false;
        }

        @Override
        public void merge(ContentAction action) {
            throw new UnsupportedOperationException();
        }

    }
}
