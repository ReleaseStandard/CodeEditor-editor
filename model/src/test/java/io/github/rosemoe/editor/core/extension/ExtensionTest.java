package io.github.rosemoe.editor.core.extension;

import org.junit.Test;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.util.Logger;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class ExtensionTest {

    @Test
    public void testSetEnabled() {
        @Jailbreak Extension e1 = new Extension();
        e1.setEnabled(true);
        assertTrue(e1.isEnabled());
        e1.setEnabled(false);
        assertTrue(e1.isDisabled());
    }

    @Test
    public void testIsEnabled() {
        @Jailbreak  Extension e1 = new Extension();
        e1.setEnabled(true);
        assertTrue(e1.isEnabled() == e1.enabled);
        e1.setEnabled(false);
        System.out.println("e1");
        System.out.println("e1.enabled="+e1.enabled+",e1.isEnabled()="+e1.isEnabled());
        assertTrue(e1.enabled == false);
    }

    @Test
    public void testToggleIsEnabled() {
        @Jailbreak Extension e1 = new Extension();
        for(int a = 0; a < 10; a=a+1) {
            boolean b = e1.isEnabled();
            e1.toggleIsEnabled();
            assertTrue(e1.isEnabled() != b);
        }
    }

    @Test
    public void testSubscription() {
        @Jailbreak Extension e1 = new Extension();
        class CustomEvent extends Event { }
        class CustomEvent2 extends Event { }
        e1.subscribe(CustomEvent.class);
        assertTrue(e1.issubscribed(CustomEvent.class));
        assertFalse(e1.issubscribed(CustomEvent2.class));
        e1.unsubscribe(CustomEvent.class);
        assertTrue(e1.issubscribed(CustomEvent.class));
    }

    @Test
    public void testPrioritySystem() {
        @Jailbreak Extension e1 = new Extension();
        @Jailbreak Extension e2 = new Extension();
        e1.priorityRing = PrioritySystem.PRIORITY_HIGH;
        e2.priorityRing = PrioritySystem.PRIORITY_LOW;
        assertTrue(e1.compareTo(e2) < 0);
        e1.priorityRing = PrioritySystem.PRIORITY_STD;
        e2.priorityRing = PrioritySystem.PRIORITY_LOW;
        assertTrue(e1.compareTo(e2) < 0);
        e1.priorityRing = PrioritySystem.PRIORITY_STD;
        e2.priorityRing = PrioritySystem.PRIORITY_STD;
        assertTrue(e1.compareTo(e2) == 0);
    }

    @Test
    public void testColorRegistration() {
        CodeEditorModel editor = new CodeEditorModel();
        Extension e1 = new Extension(editor);
        int color = 0xFF00FF00;
        e1.registerPrefixedColorIfNotIn("test",color);
        assertTrue(e1.getPrefixedColor("test") == color);
    }
}