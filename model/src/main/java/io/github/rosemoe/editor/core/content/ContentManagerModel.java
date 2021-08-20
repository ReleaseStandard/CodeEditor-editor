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

/**
 * Model for the content manager (text).
 */
public class ContentManagerModel {
    public boolean undo;
    public int maxStackSize;
    public boolean replaceMark;
    public boolean ignoreModification;

    /**
     * Whether this ContentManagerController is enabled
     *
     * @return Whether enabled
     */
    public boolean isUndoEnabled() {
        return undo;
    }

    /**
     * Get current max stack size
     *
     * @return max stack size
     */
    public int getMaxUndoStackSize() {
        return maxStackSize;
    }

    public ContentManagerModel() {
        replaceMark = false;
        ignoreModification = false;
    }
}
