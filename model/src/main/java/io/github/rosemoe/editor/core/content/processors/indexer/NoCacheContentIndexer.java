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

import static io.github.rosemoe.editor.core.content.processors.indexer.CharPosition.INVALID;

/**
 * ContentIndexer without cache
 *
 * @author Rose
 */
public final class NoCacheContentIndexer extends BaseContentIndexer {

    /**
     * Create a indexer without cache
     *
     * @param content Target content
     */
    public NoCacheContentIndexer(CodeAnalyzerResultContent content) {
        super(content);
    }

    @Override
    protected CharPosition getCharPosition(CharPosition charPosition) {
        if ( charPosition.index == INVALID ) {
            if ( charPosition.line == INVALID || charPosition.column == INVALID ) {
                return null;
            } else {
                int idx = processIndex(charPosition.line, charPosition.column);
                return new CharPosition(charPosition.line, charPosition.column, idx);
            }
        } else {
            return processCharPosition(charPosition.index);
        }
    }

}

