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
package io.github.rosemoe.editor.core.extension.plugins;

import android.graphics.Canvas;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.extension.plugins.widgets.SystemExtensionModel;
import io.github.rosemoe.editor.core.extension.plugins.widgets.SystemExtensionView;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * This class provide a widget system for CodeEditor.
 * Each widget can provide custom xml attributes and colors for the color widget.
 * We take a widget as :
 *  an application, or a component of an interface, that enables a user to perform a function or access a service.
 *
 * @author Release Standard
 */
public abstract class SystemExtensionController extends Extension {

    protected SystemExtensionModel model = null;
    protected SystemExtensionView view = null;

    public SystemExtensionController(CodeEditor editor) {
        super(editor);
    }

    @Override
    protected void handleEventEmit(Event e) {
        editor.plugins.dispatch(e);
    }

    @Override
    public void dispatch(Event e) {
        super.dispatch(e);
        Logger.v("Dispatch on widget requested");
    }
}
