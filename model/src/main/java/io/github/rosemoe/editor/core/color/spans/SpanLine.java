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
package io.github.rosemoe.editor.core.color.spans;

import io.github.rosemoe.editor.core.Line;
import io.github.rosemoe.editor.core.color.ColorManager;

/**
 * The class handle one line in the text editor.
 * We assume that Span do not merge (ex: you cannot put red and green brackground, that's an error).
 * Column index 0..n-1, Span
 *
 * @author Release Standard
 */
public class SpanLine extends Line<Span> {

    public SpanLine() {
    }

    /**
     * Empty spanline.
     * @return returns an empty spanline
     */
    public static SpanLine EMPTY() {
        SpanLine line = new SpanLine();
        line.put(Span.obtain(0, ColorManager.DEFAULT_TEXT_COLOR));
        return line;
    }
}
