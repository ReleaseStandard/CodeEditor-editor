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
package io.github.rosemoe.editor.core.analyze.analyzer.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.github.rosemoe.editor.core.util.CEObject;
import io.github.rosemoe.editor.core.analyze.ResultStore;
import io.github.rosemoe.editor.core.analyze.analyzer.Analyzer;
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.analyze.signal.Routes;

import static io.github.rosemoe.editor.core.analyze.ResultStore.RES_CONTENT;
import static io.github.rosemoe.editor.core.analyze.signal.Routes.*;

/**
 * Helper class for CodeAnalyzerResultContent to take down modification
 * As well as provide Undo/Redo actions
 *
 * @author Rose
 */
public final class ContentActionStackAnalyzer extends Analyzer {

    public int maxStackSize = 100;
    private InsertAction mInsertAction;
    private DeleteAction mDeleteAction;
    public boolean undoEnabled = true;
    public boolean replaceMark = false;
    public boolean ignoreModification = false;
    private int mStackPointer;
    private Stack<ContentAction> stack = new Stack<>();

    /**
     * Create ContentActionStackAnalyzer with the target content
     */
    public ContentActionStackAnalyzer(ResultStore resultStore) {
        super(resultStore);
        mInsertAction = null;
        mDeleteAction = null;
        mStackPointer = 0;
    }

    @Override
    public boolean route(Routes action, Object... args) {
        switch (action) {
            case ACTION_UNDO:
                undo();
                return true;
            case ACTION_REDO:
                redo();
                return true;
            case ACTION_EDIT_BATCH:
                switch ((Routes)args[0]) {
                    case BEGIN: {
                        beginBatchEdit();
                    } return true;
                    case END: {
                        endBatchEdit();
                    } return true;
                }
        }
        return false;
    }

    /**
     * Undo on the given CodeAnalyzerResultContent
     *
     */
    private void undo() {
        if (canUndo()) {
            ignoreModification = true;
            ContentAction action = stack.get(mStackPointer - 1);
            action.undo();
            mStackPointer--;
            ignoreModification = false;
        }
    }

    /**
     * Redo on the given CodeAnalyzerResultContent
     *
     */
    private void redo() {
        if (canRedo()) {
            ignoreModification = true;
            ContentAction action = stack.get(mStackPointer);
            action.redo();
            mStackPointer++;
            ignoreModification = false;
        }
    }

    /**
     * Whether can undo
     *
     * @return Whether can undo
     */
    private boolean canUndo() {
        return undoEnabled && (mStackPointer > 0);
    }

    /**
     * Whether can redo
     *
     * @return Whether can redo
     */
    private boolean canRedo() {
        return undoEnabled && (mStackPointer < stack.size());
    }

    /**
     * Set whether enable this module
     *
     * @param enabled Enable or disable
     */
    private void setUndoEnabled(boolean enabled) {
        undoEnabled = enabled;
        if (!enabled) {
            cleanStack();
        }
    }

    /**
     * Set a max stack size for this ContentActionStackAnalyzer
     *
     * @param maxSize max stack size
     */
    private void setMaxUndoStackSize(int maxSize) {
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
            stack.clear();
            mStackPointer = 0;
        } else {
            while (mStackPointer > 1 && stack.size() > maxStackSize) {
                stack.remove(0);
                mStackPointer--;
            }
        }
    }

    /**
     * Clean the stack before pushing
     * If we are not at the end(Undo action executed),remove those actions
     */
    private void cleanBeforePush() {
        while (mStackPointer < stack.size()) {
            stack.pop();
        }
    }

    public int nestedBatchEdit = 0;

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
     * Push a new {@link ContentAction} to stack
     * It will merge actions if possible
     *
     * @param action New {@link ContentAction}
     */
    private void pushAction(ContentAction action) {
        CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) resultStore.getResultInBuild(RES_CONTENT);
        if (!undoEnabled) {
            return;
        }
        cleanBeforePush();
        if (isInBatchEdit()) {
            if (stack.isEmpty()) {
                MultiAction a = new MultiAction();
                a.addAction(action);
                stack.push(a);
                mStackPointer++;
            } else {
                ContentAction a = stack.lastElement();
                if (a instanceof MultiAction) {
                    MultiAction ac = (MultiAction) a;
                    ac.addAction(action);
                } else {
                    MultiAction ac = new MultiAction();
                    ac.addAction(action);
                    stack.push(ac);
                    mStackPointer++;
                }
            }
        } else {
            if (stack.isEmpty()) {
                stack.push(action);
                mStackPointer++;
            } else {
                ContentAction last = stack.get(stack.size() - 1);
                if (last.canMerge(action)) {
                    last.merge(action);
                } else {
                    stack.push(action);
                    mStackPointer++;
                }
            }
        }
        cleanStack();
    }

    public void beforeReplace(CodeAnalyzerResultContent content) {
        if (ignoreModification) {
            return;
        }
        replaceMark = true;
    }

    public void afterInsert(int startLine, int startColumn, int endLine, int endColumn,
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

    public void afterDelete(int startLine, int startColumn, int endLine, int endColumn,
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

    @Override
    public void analyze() {

    }

    /**
     * Insert action model for ContentActionStackAnalyzer
     *
     * @author Rose
     */
    private class InsertAction implements ContentAction {

        public int startLine, endLine, startColumn, endColumn;

        public CharSequence text;

        @Override
        public void undo() {
            System.out.println("Content : ");
            CEObject.dump(this);
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) ContentActionStackAnalyzer.this.resultStore.getResultInBuild(RES_CONTENT);
            String deleted = content.delete(startLine, startColumn, endLine, endColumn);
            ContentActionStackAnalyzer.this.afterDelete(startLine, startColumn, endLine, endColumn, deleted);
        }

        @Override
        public void redo() {
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) ContentActionStackAnalyzer.this.resultStore.getResultInBuild(RES_CONTENT);
            String inserted = String.valueOf(content.insert(startLine, startColumn, text));
            ContentActionStackAnalyzer.this.afterInsert(startLine, startColumn, endLine, endColumn, inserted);
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
     * MultiAction saves several actions for ContentActionStackAnalyzer
     *
     * @author Rose
     */
    private class MultiAction implements ContentAction {

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
        public void undo() {
            for (int i = _actions.size() - 1; i >= 0; i--) {
                _actions.get(i).undo();
            }
        }

        @Override
        public void redo() {
            for (int i = 0; i < _actions.size(); i++) {
                _actions.get(i).redo();
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
     * Delete action model for ContentActionStackAnalyzer
     *
     * @author Rose
     */
    private class DeleteAction implements ContentAction {

        public int startLine, endLine, startColumn, endColumn;

        public CharSequence text;

        @Override
        public void undo() {
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) ContentActionStackAnalyzer.this.resultStore.getResultInBuild(RES_CONTENT);
            String inserted = (String) content.insert(startLine, startColumn, text);
            ContentActionStackAnalyzer.this.afterInsert(startLine, startColumn, endLine, endColumn, inserted);
        }

        @Override
        public void redo() {
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) ContentActionStackAnalyzer.this.resultStore.getResultInBuild(RES_CONTENT);
            String deleted = content.delete(startLine, startColumn, endLine, endColumn);
            ContentActionStackAnalyzer.this.afterDelete(startLine, startColumn, endLine, endColumn, deleted);
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
     * Replace action model for ContentActionStackAnalyzer
     *
     * @author Rose
     */
    private class ReplaceAction implements ContentAction {

        public InsertAction _insert;
        public DeleteAction _delete;

        @Override
        public void undo() {
            _insert.undo();
            _delete.undo();
        }

        @Override
        public void redo() {
            _delete.redo();
            _insert.redo();
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
