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
package io.github.rosemoe.editor.core.extension.plugins.widgets.widgetmanager.controller;

import android.content.Intent;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.extension.plugins.widgets.widgetmanager.extension.WidgetManagerEvent;
import io.github.rosemoe.editor.core.extension.plugins.widgets.widgetmanager.view.WidgetManagerView;

/**
 * This widget disable any other, based on the Event defined with each.
 */
public class WidgetControllerManagerController extends Extension {

    /**
     * Used to pass some data between activities.
     */
    public static class DataHolder {
        public static HashMap<String,Object> container = new HashMap<>();
        public static ReentrantLock lock = new ReentrantLock();
        public static void lock() {
            lock.lock();
        }
        public static void unlock() {
            lock.unlock();
        }
        public static void put(String key, Object o) {
            container.put(key, o);
        }
        public static Object get(String key) {
            return container.get(key);
        }
        public static void clear() {
            container.clear();
        }
    }

    private final CodeEditor editorController;
    public WidgetControllerManagerController(CodeEditor editor) {
        super(editor.model);
        editorController = editor;
        name = "WidgetManager";
        description = "Allow enable disable widgets from plugins and display this gui";
        subscribe(WidgetManagerEvent.class);
    }

    @Override
    protected void handleEventDispatch(Event e, String subtype) {
        WidgetManagerEvent wme = (WidgetManagerEvent) e;
        switch (subtype) {
            case WidgetManagerEvent.ISENABLED: {
                String wname = (String) wme.getArg(0);
                Boolean state = (Boolean) wme.getArg(1);
                Extension w = (Extension) editor.plugins.get(wname);
                w.setEnabled(state);
                break;
            }
            case WidgetManagerEvent.TOGGLE: {
                String wname = (String) wme.getArg(0);
                Extension w = (Extension) editor.plugins.get(wname);
                w.toggleIsEnabled();
                break;
            }
            case WidgetManagerEvent.GUI: {
                Intent intent = new Intent(editorController.view.getContext(), WidgetManagerView.class);
                intent.putExtra("widgets", editor.plugins.toArray(new Extension[editor.plugins.size()]));
                intent.putExtra("plugins", editor.plugins.toArray(new Extension[editor.plugins.size()]));
                editorController.view.getContext().startActivity(intent);
                new Thread() {
                    @Override
                    public void run() {
                        WidgetControllerManagerController.DataHolder.lock();
                        while (WidgetControllerManagerController.DataHolder.get("kind") == null ) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                        String kind = (String) DataHolder.get("kind");
                        Extension e = (Extension) DataHolder.get("extension");
                        if ( kind.equals("widgets") ) {
                            editor.plugins.get(e.name).setEnabled(e.isEnabled());
                        }
                        if ( kind.equals("plugins") ) {
                            editor.plugins.get(e.name).setEnabled(e.isEnabled());
                        }
                        DataHolder.clear();
                        DataHolder.unlock();
                    }
                }.start();
            }
        }
    }

    @Override
    protected void handleEventEmit(Event e) {
        editor.plugins.dispatch(e);
    }
}
