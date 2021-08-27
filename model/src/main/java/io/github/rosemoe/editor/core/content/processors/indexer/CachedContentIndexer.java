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
            if ( charPosition.index == CharPosition.INVALID ) {
                if ( charPosition.line == CharPosition.INVALID || charPosition.column == CharPosition.INVALID ) {
                    return false;
                }
                else {
                    // index is missing in this case but can be deduced from CodeAnalyzerResultContent object
                    charPosition.index = processIndex(charPosition.line, charPosition.column);
                }
            } else {
                if ( charPosition.line == CharPosition.INVALID || charPosition.column == CharPosition.INVALID ) {
                    // line or column is missing but we can deduce it from the index
                    charPosition = processCharPosition(charPosition.index);
                }
            }
            if ( charPosition == null ) {
                return false;
            }
            return super.add(charPosition);
        }
        public void dump() {
            dump("");
        }
        public void dump(String offset) {
            CEObject.dumpAll(this, offset);
        }
    };
    Cache cache = new Cache();

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

    private CharPosition findNearest(CharPosition pos) {
        CharPosition cp1 = cache.floor(pos);
        CharPosition cp2 = cache.ceiling(pos);
        return CharPosition.nearest(cp1,pos,cp2);
    }

    /**
     * Get max cache size
     *
     * @return max cache size
     */
    protected int getMaxCacheSize() {
        return cache.maxSize;
    }

    /**
     * Set max cache size
     *
     * @param maxSize max cache size
     */
    protected void setMaxCacheSize(int maxSize) {
        cache.maxSize = maxSize;
    }




    /**
     * Get the CharPosition for given entry, add it to the cache if not present.
     * @param charPosition
     * @return
     */
    @Override
    public CharPosition getCharPosition(CharPosition charPosition) {
        CharPosition res = findNearest(charPosition);
        if ( ! charPosition.equals(res) ) {
            cache.add(charPosition);
            return cache.floor(charPosition);
        }
        return res;
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

