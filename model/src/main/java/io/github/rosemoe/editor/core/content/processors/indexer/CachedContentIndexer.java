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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;
import io.github.rosemoe.editor.core.util.CEObject;
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.content.ContentListener;
import io.github.rosemoe.editor.core.util.Logger;

import static io.github.rosemoe.editor.core.content.processors.indexer.CharPosition.INVALID;

/**
 * ContentIndexer Impl for CodeAnalyzerResultContent
 * With cache
 *
 * @author Rose
 */
public class CachedContentIndexer extends BaseContentIndexer implements ContentListener {

    /**
     * To be sure that we don't get RuntimeException from the CharPosition object,
     * we have to ensure that inserted object into cache have no INVALID field.
     */
    class Cache extends ConcurrentSkipListSet<CharPosition> {
        public int maxSize = 1000;
        @Override
        public boolean add(CharPosition charPosition) {
            if ( size() >= maxSize ) { return false; }
            CharPosition charPosition1 = completeWithContent(charPosition);
            if( charPosition1 == null ) { return false; }
            return super.add(charPosition1);
        }
        public void dump() {
            dump("");
        }
        public void dump(String offset) {
            CEObject.dumpAll(this, offset);
        }
        /**
         * Get max cache size
         *
         * @return max cache size
         */
        public int getMaxCacheSize() {
            return cache.maxSize;
        }

        /**
         * Set max cache size
         *
         * @param maxSize max cache size
         */
        public void setMaxCacheSize(int maxSize) {
            cache.maxSize = maxSize;
        }
    };
    public Cache cache = new Cache();

    /**
     * complete informations in the given charPosition from the content.
     * @param charPosition information to complete theses values could be outside the map.
     * @return information completed or null when error
     */
    public CharPosition completeWithContent(CharPosition charPosition) {
        if ( charPosition.index == INVALID ) {
            if ( charPosition.line == INVALID || charPosition.column == INVALID ) {
                return null;
            }
            else {
                // index is missing in this case but can be deduced from CodeAnalyzerResultContent object
                charPosition.index = processIndex(charPosition.line, charPosition.column);
            }
        } else {
            if ( charPosition.line == INVALID || charPosition.column == INVALID ) {
                // line or column is missing but we can deduce it from the index
                charPosition = processCharPosition(charPosition.index);
            }
        }
        return charPosition;
    }
    /**
     * Try to index all the content in the map (process both {line, column} and {idx}
     */
    private void processContent() {
        int idx = 0;
        for(Integer k : content.keySet()) {
            Line<ContentCell> line = content.get(k);
            for(Integer k1 : line.keySet()) {
                ContentCell cc = line.get(k1);
                CharPosition cp = new CharPosition(k,k1,idx);
                cache.add(cp);
                idx += cc.size;
            }
        }
    }

    /**
     * Create a new CachedContentIndexer for the given content
     *
     * @param content CodeAnalyzerResultContent to manage
     */
    public CachedContentIndexer(CodeAnalyzerResultContent content) {
        super(content);
    }

    /**
     * Find the nearest position in the cache.
     * @param pos 0..n find the nearest of this position
     * @return
     */
    private CharPosition findNearest(CharPosition pos) {
        CharPosition cp1 = cache.floor(pos);
        CharPosition cp2 = cache.ceiling(pos);
        return CharPosition.nearest(cp1, pos, cp2);
    }



    /**
     * Get the CharPosition for given entry, add it to the cache if not present.
     * @param charPosition
     * @return
     */
    @Override
    public CharPosition getCharPosition(CharPosition charPosition) {
        CharPosition res = charPosition;
        if ( charPosition.index == INVALID || charPosition.column == INVALID || charPosition.line == INVALID ) {
            res = completeWithContent(charPosition);
        }
        if ( res == null ) { return null; }
        if ( ! cache.contains(res) ) {
            if ( res.index > content.length() ) { return null; }
            cache.add(res);
        }
        return cache.floor(res);
    }

    @Override
    public void beforeReplace(CodeAnalyzerResultContent content) {
        //Do nothing
    }

    @Override
    public void afterInsert(CodeAnalyzerResultContent content, int startLine, int startColumn, int endLine, int endColumn,
                            CharSequence insertedContent) {
        for (CharPosition pos : cache) {
            if (pos.line == startLine) {
                if (pos.column >= startColumn) {
                    pos.index += insertedContent.length();
                    pos.line += endLine - startLine;
                    pos.column = endColumn + pos.column - startColumn;
                }
            } else if (pos.line > startLine) {
                pos.index += insertedContent.length();
                pos.line += endLine - startLine;
            }
        }
    }

    @Override
    public void afterDelete(CodeAnalyzerResultContent content, int startLine, int startColumn, int endLine, int endColumn,
                            CharSequence deletedContent) {
        List<CharPosition> garbage = new ArrayList<>();
        for (CharPosition pos : cache) {
            if (pos.line == startLine) {
                if (pos.column >= startColumn)
                    garbage.add(pos);
            } else if (pos.line > startLine) {
                if (pos.line < endLine) {
                    garbage.add(pos);
                } else if (pos.line == endLine) {
                    garbage.add(pos);
                } else {
                    pos.index -= deletedContent.length();
                    pos.line -= endLine - startLine;
                }
            }
        }
        cache.removeAll(garbage);
    }
}

