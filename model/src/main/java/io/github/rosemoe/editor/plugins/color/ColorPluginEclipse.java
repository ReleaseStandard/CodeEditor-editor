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

public class ColorPluginEclipse extends ColorPlugin {

    public ColorPluginEclipse(CodeEditorModel editor) {
        super(editor);
        name = "Eclipse theme";
        description = "picked from Eclipse IDE for Java Developers Version 2019-12 (4.14.0)";
    }

    @Override
    public HashMap<String, Integer> getColors() {
        return new HashMap<String, Integer>() {{
            put("base00", 0xff000000);
            put("base1", 0xff3f7f5f);
            put("base2", 0xffe8f2fe);
            put("base3", 0xffffffff);
            put("accent1", 0xff7f0074);
            put("accent4", 0xffb8633e);
            put("accent6", 0xff000000);
            put("accent7", 0xff2a00ff);
            put("comment", 0xff3f7f5f);
            put("wholeBackground", 0xffffffff);
            put("textNormal", 0xff000000);
            put("lineNumberBackground", 0xffffffff);
            put("lineNumberPanel", 0xff787878);
            put("linedivider",0xffd8d8d8);
            put("lineNumberPanelText",0xffd8d8d8);
            put("selectedTextBackground", 0xff3399ff);
            put("matchedTextBackground", 0xffd4d4d4);
            put("currentLine", 0xffe8f2fe);
            put("selectionInsert", 0xff03ebeb);
            put("selectionHandle", 0xff03ebeb);
            put("blockLine", 0xffd8d8d8);
            put("blockLineCurrent", 0);
        }};
    }
}
