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
package io.github.rosemoe.editor.core.content.controller;

import io.github.rosemoe.editor.core.content.ContentLineModel;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;

/**
 * One line of content in the CodeAnalyzerResultContent.
 */
public class ContentLineController extends Line<ContentCell> implements CharSequence {

    public ContentLineModel model = new ContentLineModel();

    public ContentLineController() {
        this(true);
    }

    public ContentLineController(CharSequence text) {
        this(true);
        // TODO : init with the text
    }

    private ContentLineController(boolean extendedInit) {
    }

    public int getId() {
        return model.id;
    }

    public void setId(int id) {
        model.id = id;
    }

    @Override
    public int length() {
        return getWidth();
    }

    @Override
    public char charAt(int index) {
        return get(index).toString().toCharArray()[0];
    }

    @Override
    public ContentLineController subSequence(int start, int end) {
        return (ContentLineController) subLine(start,end-start);
    }
}
