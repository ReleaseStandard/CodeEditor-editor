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
package io.github.rosemoe.editor.core.content.processors;

import io.github.rosemoe.editor.core.content.controller.ContentMap;
import io.github.rosemoe.editor.core.content.controller.ContentLineController;

/**
 * A listener to know when a ContentLineController object is removed from ContentMap object
 *
 * @author Rose
 */
public interface ContentLineRemoveListener {

    /**
     * When a ContentLineController is removed from ContentMap, this method is called
     *
     * @param content Caller ContentMap
     * @param line    ContentLineController object removed
     */
    void onRemove(ContentMap content, ContentLineController line);

}
