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

import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;

/**
 * A helper class for ITextContent to transform (line,column) and index
 *
 * @author Rose
 */
public abstract class ContentIndexer {

    protected final CodeAnalyzerResultContent content;

    public ContentIndexer(CodeAnalyzerResultContent content) {
        this.content = content;
    }

    /**
     * Get the index of (line,column)
     *
     * @param line   The line position of index
     * @param column The column position of index
     * @return Calculated index, -1 if error
     */
    public abstract int getCharIndex(int line, int column);

    /**
     * Get the line position of index
     *
     * @param index The index you want to know its line
     * @return Line position of index, -1 if error
     */
    public abstract int getCharLine(int index);

    /**
     * Get the column position of index
     *
     * @param index The index you want to know its column
     * @return Column position of index, -1 if error
     */
    public abstract int getCharColumn(int index);

    /**
     * Get the CharPosition for the given index
     * You are not expected to make changes with this CharPosition
     *
     * @param index The index you want to get
     * @return The CharPosition object or null if not in.
     */
    public abstract CharPosition getCharPosition(int index);

    /**
     * Get the CharPosition for the given (line,column)
     * You are not expected to make changes with this CharPosition
     *
     * @param line   The line position you want to get
     * @param column The column position you want to get
     * @return The CharPosition object or null if not found.
     */
    public abstract CharPosition getCharPosition(int line, int column);

}
