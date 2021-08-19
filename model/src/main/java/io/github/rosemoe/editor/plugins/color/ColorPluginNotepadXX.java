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
package io.github.rosemoe.editor.plugins.color;


import java.util.HashMap;

import io.github.rosemoe.editor.core.CodeEditorModel;

public class ColorPluginNotepadXX extends ColorPlugin {

    public ColorPluginNotepadXX(CodeEditorModel editor) {
        super(editor);
        name        = "NotepadXX theme";
        description = "picked from Notepad++ v7.8.1, Thanks to liyujiang-gzu (GitHub @liyujiang-gzu)";
    }

    @Override
    public HashMap<String, Integer> getColors() {
        return new HashMap<String, Integer>() {{
            put("base00", 0xff000000);
            put("base1", 0xff008000);
            put("base2", 0xffe4e4e4);
            put("base3", 0xffffffff);
            put("accent1", 0xff8000ff);
            put("accent6", 0xff000000);
            put("accent7", 0xff808080);
            put("accent8", 0xff0000ff);
            put("comment", 0xff008000);
            put("wholeBackground", 0xffffffff);
            put("textNormal", 0xff000000);
            put("lineNumberBackground", 0xffe4e4e4);
            put("lineNumberPanel", 0xff808080);
            put("lineNumberPanelText", 0xffc0c0c0);
            put("linedivider", 0xffc0c0c0);
            put("selectedTextBackground", 0xff75d975);
            put("matchedTextBackground", 0xffc0c0c0);
            put("currentLine", 0xffe8e8ff);
            put("selectionInsert", 0xff8000ff);
            put("selectionHandle", 0xff8000ff);
            put("blockLine", 0xffc0c0c0);
            put("blockLineCurrent", 0);
        }};
    }
}
