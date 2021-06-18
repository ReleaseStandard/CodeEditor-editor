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
package io.github.rosemoe.editor.core.extension.plugins.widgets.colorAnalyzer.controller;

import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.StyleableRes;

import java.util.HashMap;
import java.util.Map;

import io.github.rosemoe.editor.R;
import io.github.rosemoe.editor.core.color.ColorManager;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.extension.plugins.widgets.WidgetController;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.widgets.colorAnalyzer.extension.ColorSchemeEvent;

import static io.github.rosemoe.editor.core.extension.plugins.widgets.colorAnalyzer.extension.ColorSchemeEvent.*;

/**
 * This class manages the colors of editor.
 * Colors scheme must be very simple, eg: we define colors types.
 * Then it's up to the language analysis to apply and on which part of the text.
 * Themes cannot have language specific colors except by overriding getters below.
 * https://github.com/altercation/solarized
 * @author Rose
 */
public class ColorSchemeController extends WidgetController {

    /**
     * For sub classes
     */
    public ColorSchemeController(CodeEditor editor) {
        super(editor);
        initialize(false);
    }
    public ColorSchemeController(CodeEditor editor, boolean invert) {
        super(editor);
        initialize(invert);
    }
    private void initialize(boolean invert) {
        name        = "color";
        description = "widget responsible from displaying color to the screen";
        subscribe(ColorSchemeEvent.class);
        if ( invert ) {
            editor.colorManager.invertColorScheme();
        }
    }
    public void initFromAttributeSets(AttributeSet attrs, TypedArray a) {
        /*
        int test = 235363207;

        for(@StyleableRes int colorId : COLORS.keySet()) {
            int colorValue = a.getColor(colorId,test);
            if ( colorValue == test) { continue; }
            //updateColor(colorId, colorValue);
        }
        a.recycle();

         */
    }

    @Override
    public void handleEventEmit(Event e) {
        super.handleEventEmit(e);
        editor.plugins.dispatch(e);
    }


    @Override
    public void handleEventDispatch(Event e, String subtype) {
        ColorSchemeEvent cse = (ColorSchemeEvent) e;
        ColorManager manager = editor.colorManager;
        switch(subtype) {
            case UPDATE_COLOR: {
                String color = (String) e.getArg(0);
                Object colorValue = e.getArg(1);
                manager.register(color,colorValue);
                reloadColorScheme();
                break;
            }
            case UPDATE_THEME: {
                Logger.v("Theme update received");
                //noinspection unchecked
                HashMap<String, Object> colors = (HashMap<String, Object>) e.getArg(0);
                manager.reset();
                for (String color : colors.keySet()) {
                    manager.register(color, colors.get(color));
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
        editor.invalidate();
    }
}
