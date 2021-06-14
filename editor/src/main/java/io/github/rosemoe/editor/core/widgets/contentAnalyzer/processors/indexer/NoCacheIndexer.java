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
package io.github.rosemoe.editor.core.widgets.contentAnalyzer.processors.indexer;

import io.github.rosemoe.editor.core.widgets.contentAnalyzer.controller.ContentMapController;

/**
 * Indexer without cache
 *
 * @author Rose
 */
public final class NoCacheIndexer extends CachedIndexer implements Indexer {

    /**
     * Create a indexer without cache
     *
     * @param content Target content
     */
    public NoCacheIndexer(ContentMapController content) {
        super(content);
        //Disable dynamic indexing
        if (super.getMaxCacheSize() != 0) {
            super.setMaxCacheSize(0);
        }
        if (super.isHandleEvent()) {
            super.setHandleEvent(false);
        }
    }

}

