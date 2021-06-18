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

public class ColorPluginHTML extends ColorPlugin {

    public ColorPluginHTML(CodeEditor editor) {
        super(editor);
        name = "HTML theme";
        description = "ColorScheme for HTML Language for editor";
    }

    @Override
    public HashMap<String, Integer> getColors() {
        return new HashMap<String, Integer>() {{
            put("base00", 0xffffffff);
            put("base1", 0xffbdbdbd);
            put("base2", 0xff464646);
            put("base3", 0xff212121);
            put("accent1", 0xff4fc3f7);
            put("accent5", 0xfff0be4b);
            put("accent6",0xff333333);
            put("accent7",0xff008080);
            put("comment", 0xffbdbdbd);
            put("wholeBackground", 0xff212121);
            put("textNormal", 0xffffffff);
            put("lineNumberBackground", 0xff1e1e1e);
            put("linedivider", 0xff2b9eaf);
            put("lineNumberPanelText", 0xff2b9eaf);
            put("currentLine", 0xff464646);
            put("nonPrintableChar", 0xffdddddd);
            put("selectionInsert", 0xffffffff);
            put("selectionHandle", 0xffffffff);
            put("lineNumberPanel", 0xff2b9eaf);
        }};
    }
}
