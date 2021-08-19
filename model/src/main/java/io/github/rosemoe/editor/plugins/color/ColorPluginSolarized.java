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

/**
 * https://ethanschoonover.com/solarized/
 *
 * @author Release Standard
 */
public class ColorPluginSolarized extends ColorPlugin {

    public ColorPluginSolarized(CodeEditorModel editor) {
        super(editor);
        name = "Solarized theme";
        description = "https://ethanschoonover.com/solarized/";
    }

    @Override
    public HashMap<String, Integer> getColors() {
        return new HashMap<String, Integer>() {{
            put("base03", 0xFF002b36);
            put("base02", 0xFF073642);
            put("base01", 0xFF586e75);
            put("base00", 0xFF657b83);
            put("base0", 0xFF839496);
            put("base1", 0xFF93a1a1);
            put("base2", 0xFFeee8d5);
            put("base3", 0xFFfdf6e3);
            put("accent1", 0xFFb58900);
            put("accent2", 0xFFcb4b16);
            put("accent3", 0xFFdc322f);
            put("accent4", 0xFFd33682);
            put("accent5", 0xFF6c71c4);
            put("accent6", 0xFF268bd2);
            put("accent7", 0xFF2aa198);
            put("accent8", 0xFF859900);
            put("comment", 0xff3f7f5f);
            put("wholeBackground", 0xffffffff);
            put("textNormal", 0xff000000);
            put("lineNumberBackground", 0xffffffff);
            put("lineNumberPanel", 0xff787878);
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
