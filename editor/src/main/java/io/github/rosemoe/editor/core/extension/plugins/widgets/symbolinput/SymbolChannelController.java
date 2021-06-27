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
package io.github.rosemoe.editor.core.extension.plugins.widgets.symbolinput;

import android.view.View;

import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.extension.plugins.widgets.cursor.controller.CursorController;
import io.github.rosemoe.editor.core.CodeEditor;

/**
 * A channel to insert symbols in {@link CodeEditor}
 * @author Rosemoe
 */
public class SymbolChannelController {

    /**
     * Inserts the given text in the editor.
     * <p>
     * This method allows you to insert texts externally to the content of editor.
     * The content of {@param symbolText} is not checked to be exactly characters of symbols.
     *
     * @throws IllegalArgumentException If the {@param selectionRegion} is invalid
     * @param symbolText Text to insert, usually a text of symbols
     * @param selectionOffset New selection position relative to the start of text to insert.
     *                        Ranging from 0 to symbolText.length()
     */
    protected void insertSymbol(CodeEditor editor, String symbolText, int selectionOffset) {
        if (selectionOffset < 0 || selectionOffset > symbolText.length()) {
            throw new IllegalArgumentException("selectionOffset is invalid");
        }
        CursorController cur = editor.getText().getCursor();
        if (cur.isSelected()) {
            cur.onDeleteKeyPressed();
            editor.notifyExternalCursorChange();
        }
        editor.getText().insert(cur.getRightLine(), cur.getRightColumn(), symbolText);
        editor.notifyExternalCursorChange();
        if (selectionOffset != symbolText.length()) {
            editor.setSelection(cur.getRightLine(), cur.getRightColumn() - (symbolText.length() - selectionOffset));
        }
    }

}
