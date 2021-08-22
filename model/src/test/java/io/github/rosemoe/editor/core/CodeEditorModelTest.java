package io.github.rosemoe.editor.core;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.extension.extensions.color.ColorSchemeEvent;
import io.github.rosemoe.editor.core.extension.extensions.color.ColorSchemeExtension;
import io.github.rosemoe.editor.core.extension.extensions.loopback.LoopbackController;
import io.github.rosemoe.editor.core.extension.extensions.loopback.LoopbackEvent;
import io.github.rosemoe.editor.core.util.Random;

import static io.github.rosemoe.editor.core.extension.extensions.color.ColorSchemeEvent.UPDATE_COLOR;
import static io.github.rosemoe.editor.core.extension.extensions.color.ColorSchemeEvent.UPDATE_THEME;
import static io.github.rosemoe.editor.core.extension.extensions.loopback.LoopbackEvent.PLUGINS_BROADCAST;
import static org.junit.Assert.*;

public class CodeEditorModelTest {
    Random r = new Random();

    @Test
    public void instanciate() {
        CodeEditorModel a = new CodeEditorModel();
    }

    @Test
    public void testColorManager() {
        CodeEditorModel editor = new CodeEditorModel();
        editor.colorManager.register("red", 0xFFFF0000);
        assertTrue(editor.colorManager.getColor("red")==0xFFFF0000);
        Extension e = new Extension(){
            @Override
            public boolean issubscribed(Class type) {
                return true;
            }
        };
        editor.plugins.put(e);
        assertTrue(editor.plugins.size() == 1);
        editor.plugins.put(new ColorSchemeExtension(editor));
        editor.plugins.dispatch(new ColorSchemeEvent(UPDATE_THEME,new HashMap<String, Object>(){{
            put("red", 0);
        }}));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        assertTrue(editor.colorManager.getColor("red")==0);
    }

    static int count = 0;
    @Test
    @Ignore("Know bug")
    public void testLoopback() {
        count = 0;
        CodeEditorModel editor = new CodeEditorModel();
        editor.plugins.put(new Extension() {
            @Override
            public boolean issubscribed(Class type) {
                return true;
            }

            @Override
            public void dispatch(Event e) {
                count+=1;
            }
        }, new LoopbackController(editor));
        editor.plugins.dispatch(new LoopbackEvent(PLUGINS_BROADCAST));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        assertTrue("count=" + count, count == 2);
    }
}
