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
package io.github.rosemoe.editor.core.extension.plugins.widgets.completion;

public class CompletionItemModel {
    /**
     * Text to commit when selected
     */
    protected String commit;

    /**
     * Text to display as title in adapter
     */
    protected String label;

    /**
     * Text to display as description in adapter
     */
    protected String desc;

    /**
     * CursorController offset in {@link CompletionItemModel#commit}
     */
    protected int cursorOffset;

    /**
     * @param offset
     */
    protected void cursorOffset(int offset) {
        if (offset < 0 || offset > commit.length()) {
            throw new IllegalArgumentException();
        }
        cursorOffset = offset;
    }
}
