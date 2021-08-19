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
import io.github.rosemoe.editor.core.extension.extensions.colorChange.ColorSchemeEvent;
import io.github.rosemoe.editor.plugins.Plugin;

public abstract class ColorPlugin extends Plugin {
    /**
     * Below defined constantes are for convenience only.
     * As a good citizen, you should not use them.
     * Because some languages may not have secondary keyword, or literals.
     * Or accent colors may be used for a totally different purpose.
     */
    public static final String BASE03 = "base03";
    public static final String BASE02 = "base02";
    public static final String BASE01 = "base01";
    public static final String BASE00 = "base00";
    public static final String BASE0 = "base0";
    public static final String BASE1 = "base1";
    public static final String BASE2 = "base2";
    public static final String BASE3 = "base3";
    public static final String ACCENT1 = "accent1";
    public static final String KEYWORD = ACCENT1;
    public static final String ACCENT2 = "accent2";
    public static final String SECONDARY_KEYWORD = ACCENT2;
    public static final String ACCENT3 = "accent3";
    public static final String UNDERLINE = ACCENT3;
    public static final String ACCENT4 = "accent4";
    public static final String ID_VARIABLE = ACCENT4;
    public static final String ACCENT5 = "accent5";
    public static final String ID_CLASS = ACCENT5;
    public static final String ACCENT6 = "accent6";
    public static final String ID_FUNCT = ACCENT6;
    public static final String ACCENT7 = "accent7";
    public static final String LITERAL = ACCENT7;
    public static final String ACCENT8 = "accent8";
    public static final String PUNCT = ACCENT8;

    boolean invert = false;

    public static ColorPlugin DEFAULT(CodeEditorModel editor) { return new ColorPluginSolarized(editor); }
    public ColorPlugin(CodeEditorModel editor) {
        super(editor);
    }
    public ColorPlugin(CodeEditorModel editor, boolean invert) {
        super(editor);
        this.invert = invert;
    }
    public void init() {
        name = "colorplugin";
        description = "plugin that will change editor's colors";
    }


    public void apply() {
        HashMap<String,Integer> colors = getColors();
        if ( colors == null ) {
            colors = new HashMap<>();
        }
        emit(new ColorSchemeEvent(ColorSchemeEvent.UPDATE_THEME,colors));
    }

    /**
     * Define you color scheme here.
     * @return
     */
    public abstract HashMap<String,Integer> getColors();

}
