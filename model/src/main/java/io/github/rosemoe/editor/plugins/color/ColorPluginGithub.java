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

public class ColorPluginGithub extends ColorPlugin {

    public ColorPluginGithub(CodeEditorModel editor) {
        super(editor);
        name = "GitHub theme";
        description = "picked from Notepad++ v7.8.1, Thanks to liyujiang-gzu (GitHub @liyujiang-gzu)";
    }

    @Override
    public HashMap<String, Integer> getColors() {
        return new HashMap<String, Integer>() {{
            put("base00", 0xff24292e);
            put("base1", 0xff6a737d);
            put("base2", 0xffbec0c1);
            put("base3", 0xffffffff);
            put("accent1", 0xffde3a49);
            put("accent6", 0xff24292e);
            put("accent7", 0xff032f62);
            put("accent8", 0xff005cc5);
            put("comment", 0xff6a737d);
            put("wholeBackground", 0xffffffff);
            put("textNormal", 0xff24292e);
            put("lineNumberBackground", 0xffffffff);
            put("lineNumberPanel", 0xffbec0c1);
            put("selectionInsert", 0xffc7edcc);
            put("selectionHandle", 0xffc7edcc);
        }};
    }
}
