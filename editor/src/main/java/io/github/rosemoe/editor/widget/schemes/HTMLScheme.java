/*
 *    CodeEditor - the awesome code editor for Android
 *    Copyright (C) 2020-2021  Rosemoe
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *     Please contact Rosemoe by email roses2020@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.editor.widget.schemes;

import io.github.rosemoe.editor.widget.EditorColorScheme;

/**
 * ColorScheme for HTML Language for editor
 */
public class HTMLScheme extends EditorColorScheme {

    @Override
    public void applyDefault() {
        super.applyDefault();
        setColor(OPERATOR, 0xff4fc3f7);
        setColor(BLOCK_LINE, 0xff717171);
        setColor(BLOCK_LINE_CURRENT, 0xffffffff);
        setColor(NON_PRINTABLE_CHAR, 0xffdddddd);
        setColor(CURRENT_LINE, 0xff464646);
        setColor(SELECTION_INSERT, 0xffffffff);
        setColor(SELECTION_HANDLE, 0xffffffff);
        setColor(LINE_NUMBER, 0xff2b9eaf);
        setColor(LINE_DIVIDER, 0xff2b9eaf);
        setColor(LINE_NUMBER_BACKGROUND, 0xff1e1e1e);
        setColor(WHOLE_BACKGROUND, 0xff212121);
        setColor(ATTRIBUTE_VALUE, 0xff8bc34a);
        setColor(ATTRIBUTE_NAME, 0xff333333);
        setColor(HTML_TAG, 0xffff6060);
        setColor(TEXT_NORMAL, 0xffffffff);
        setColor(IDENTIFIER_NAME, 0xfff0be4b);
        setColor(COMMENT, 0xffbdbdbd);
    }

}