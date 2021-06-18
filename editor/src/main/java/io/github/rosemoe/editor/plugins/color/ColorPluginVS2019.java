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

import androidx.annotation.Nullable;

import java.util.HashMap;

import io.github.rosemoe.editor.R;
import io.github.rosemoe.editor.core.CodeEditor;

public class ColorPluginVS2019 extends ColorPlugin {

    public ColorPluginVS2019(CodeEditor editor) {
        super(editor);
        name = "VS2019 theme";
        description = "picked from Visual Studio 2019, Thanks to liyujiang-gzu (GitHub @liyujiang-gzu)";
    }

    @Nullable
    @Override
    public HashMap<String, Integer> getColors() {
        return new HashMap<String, Integer>() {{
            put("base00", 0xffdcdcdc);
            put("base1", 0xff57a64a);
            put("base2", 0xff3676b8);
            put("base3", 0xff1e1e1e);
            put("accent1", 0xff569cd6);
            put("accent5", 0xff4ec9b0);
            put("accent4", 0xffdcdcaa);
            put("accent6", 0xffdcdcdc);
            put("accent7", 0xffd69d85);
            put("comment", 0xff57a64a);
            put("wholeBackground", 0xff1e1e1e);
            put("textNormal", 0xffdcdcdc);
            put("lineNumberBackground", 0xff1e1e1e);
            put("lineNumberPanelText", 0xff2b9eaf);
            put("lineNumberPanel", 0xff2b9eaf);
            put("linedivider", 0xff2b9eaf);
            put("scrollbarThumb", 0xff3e3e42);
            put("scrollbarThumbPressed", 0xff9e9e9e);
            put("selectedTextBackground", 0xff3676b8);
            put("matchedTextBackground", 0xff653306);
            put("currentLine", 0xff464646);
            put("selectionInsert", 0xffffffff);
            put("selectionHandle", 0xffffffff);
            put("blockLine", 0xff717171);
            put("blockLineCurrent", 0);
            put("nonPrintableChar", 0xffdddddd);
        }};
    }
}
