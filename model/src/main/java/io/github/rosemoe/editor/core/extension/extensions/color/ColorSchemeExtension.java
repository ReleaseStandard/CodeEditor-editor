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
package io.github.rosemoe.editor.core.extension.extensions.color;

import java.util.HashMap;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * This class manages the colors of editor.
 * Colors scheme must be very simple, eg: we define colors types.
 * Then it's up to the language analysis to apply and on which part of the text.
 * Themes cannot have language specific colors except by overriding getters below.
 * https://github.com/altercation/solarized
 * @author Rose
 */
public class ColorSchemeExtension extends Extension {

    final ColorManager colorManager;

    /**
     * For sub classes
     */
    public ColorSchemeExtension(CodeEditorModel editor) {
        super(editor);
        colorManager = editor.colorManager;
        initialize(false);
    }
    public ColorSchemeExtension(CodeEditorModel editor, boolean invert) {
        super(editor);
        colorManager = editor.colorManager;
        initialize(invert);
    }
    private void initialize(boolean invert) {
        name        = "color";
        description = "widget responsible from displaying color to the screen";
        subscribe(ColorSchemeEvent.class);
        if ( invert ) {
            colorManager.invertColorScheme();
        }
    }

    @Override
    public void handleEventEmit(Event e) {
        super.handleEventEmit(e);
        editor.plugins.dispatch(e);
    }


    @Override
    public void handleEventDispatch(Event e, String subtype) {
        ColorSchemeEvent cse = (ColorSchemeEvent) e;
        switch(subtype) {
            case ColorSchemeEvent.UPDATE_COLOR: {
                String color = (String) e.getArg(0);
                Object colorValue = e.getArg(1);
                colorManager.register(color,colorValue);
                reloadColorScheme();
                break;
            }
            case ColorSchemeEvent.UPDATE_THEME: {
                Logger.v("Theme update received");
                //noinspection unchecked
                HashMap<String, Object> colors = (HashMap<String, Object>) e.getArg(0);
                colorManager.reset();
                for (String color : colors.keySet()) {
                    colorManager.register(color, colors.get(color));
                }
                reloadColorScheme();
                break;
            }
        }
    }

    /**
     * Reload colors into the editor.
     */
    public void reloadColorScheme() {
        Logger.debug();
        // Update spanner
        //editor.analyzer.mCodeAnalyzer.lockView();
        //editor.analyzer.mCodeAnalyzer.lockBuild();
        //editor.analyzer.mCodeAnalyzer.clearInBuild();
        /*
        if (editor.analyzer != null) {
            editor.analyzer.shutdown();
            editor.analyzer.setCallback(null);
        }
        editor.setEditorLanguage(editor.mLanguage);
        editor.invalidate();
        */
        //editorController.view.invalidate();
    }
}
