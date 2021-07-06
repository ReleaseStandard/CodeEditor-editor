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
package io.github.rosemoe.editor.core.extension.plugins.loopback;

import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionController;
import io.github.rosemoe.editor.core.extension.plugins.loopback.extension.LoopbackEvent;
import io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.extension.UserInputEvent;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.CodeEditor;

import static io.github.rosemoe.editor.core.extension.plugins.loopback.extension.LoopbackEvent.*;

public class LoopbackController extends SystemExtensionController {
    public LoopbackController(CodeEditor editor) {
        super(editor);
        subscribe(LoopbackEvent.class);
        Logger.debug("TYPE_LOOPBACK=",issubscribed(LoopbackEvent.class),",TYPE_USERINPUT=",issubscribed(UserInputEvent.class));
        name        = "loopback";
        description = "SystemExtensionController that allow interactions between plugins";
    }
    @Override
    public void handleEventEmit(Event e) {
        for(Extension sec : editor.systemPlugins.extensions) {
            if( sec instanceof LoopbackController) {
                continue;
            }
            sec.dispatch(e);
        }
        editor.plugins.dispatch(e);
    }

    @Override
    public void handleEventDispatch(Event e, String subtype) {
        LoopbackEvent uie = (LoopbackEvent) e;
        switch(subtype) {
            case PLUGINS_BROADCAST:
                emit(new LoopbackEvent(PLUGINS_BROADCAST));
                break;
        }
    }

}
