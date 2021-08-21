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
import io.github.rosemoe.editor.core.grid.Cell;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;

/**
 * One line of content in the ContentMap.
 */
public class ContentLineController extends Line<ContentCell> implements CharSequence {

    public ContentLineModel model = new ContentLineModel();

    public ContentLineController() {
        this(true);
    }

    public ContentLineController(CharSequence text) {
        this(true);
        insert(0, text);
    }

    private ContentLineController(boolean extendedInit) {
        model.initialise(extendedInit);
    }



    public int getId() {
        return model.id;
    }

    public void setId(int id) {
        model.id = id;
    }

    /**
     * Inserts the specified {@code CharSequence} into this sequence.
     * <p>
     * The characters of the {@code CharSequence} argument are inserted,
     * in order, into this sequence at the indicated offset, moving up
     * any characters originally above that position and increasing the length
     * of this sequence by the length of the argument s.
     * <p>
     * The result of this method is exactly the same as if it were an
     * invocation of this object's
     * {@link #insert(int, CharSequence, int, int) insert}(dstOffset, s, 0, s.length())
     * method.
     *
     * <p>If {@code s} is {@code null}, then the four characters
     * {@code "null"} are inserted into this sequence.
     *
     * @param dstOffset the offset.
     * @param s         the sequence to be inserted
     * @return a reference to this object.
     * @throws IndexOutOfBoundsException if the offset is invalid.
     */
    public ContentLineController insert(int dstOffset, CharSequence s) {
        if (s == null)
            s = "null";
        if (s instanceof String)
            return insert(dstOffset, (String) s);
        return insert(dstOffset, s, 0, s.length());
    }
    public ContentLineController insert(int dstOffset, CharSequence s,
                       int start, int end) {
        model.insert(dstOffset,s,start,end);
        return this;
    }

    public ContentLineController insert(int offset, char c) {
        model.insert(offset,c);
        return this;
    }

    public ContentLineController append(CharSequence s, int start, int end) {
        model.append(s,start,end);
        return this;
    }

    public int lastIndexOf(String str, int fromIndex) {
        return ContentLineModel.lastIndexOf(model.value, model.length,
                str.toCharArray(), str.length(), fromIndex);
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
