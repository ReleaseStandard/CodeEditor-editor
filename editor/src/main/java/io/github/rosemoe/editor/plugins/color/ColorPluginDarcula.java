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

import io.github.rosemoe.editor.R;
import io.github.rosemoe.editor.core.CodeEditor;

public class ColorPluginDarcula extends ColorPlugin {

    public ColorPluginDarcula(CodeEditor editor) {
        super(editor);
        name        = "Darcula theme";
        description = "picked from Android Studio, Thanks to liyujiang-gzu (GitHub @liyujiang-gzu)";
    }

    @Override
    public HashMap<String, Integer> getColors() {
        return new HashMap<String, Integer>() {{
            put("base00", 0xffffffff);
            put("base1", 0xff606366);
            put("base2", 0xff323232);
            put("base3", 0xff2b2b2b);
            put("accent1", 0xffcc7832);
            put("accent4", 0xFF9876aa);
            put("accent6", 0xffffffff);
            put("accent7", 0xff6a8759);
            put("comment", 0xff808080);
            put("wholeBackground", 0xff2b2b2b);
            put("textNormal", 0xffffffff);
            put("lineNumberBackground", 0xff313335);
            put("lineNumberPanel", 0xff606366);
            put("linedivider", 0xff606366);
            put("scrollbarThumb", 0xffa6a6a6);
            put("scrollbarThumbPressed", 0xff565656);
            put("selectedTextBackground", 0xff3676b8);
            put("matchedTextBackground", 0xff32593d);
            put("currentLine", 0xff323232);
            put("selectionInsert", 0xffffffff);
            put("selectionHandle", 0xffffffff);
            put("blockLine", 0xff575757);
            put("blockLineCurrent", 0xdd575757);
            put("nonPrintableChar", 0xff6a8759);
        }};
    }

}
