package io.github.rosemoe.editor.core.extension.events;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class EventTest {

    Random r = new Random();

    @Test
    public void compareTo() {
        {
            Event e1 = new io.github.rosemoe.editor.core.extension.events.Event();
            Event e2 = new io.github.rosemoe.editor.core.extension.events.Event();
            assertTrue(e1.compareTo(e2) == 0);
            e1.priorityRing = Event.PRIORITY_HIGH;
            assertTrue(e1 > e2);
            assertFalse(e1 <= e2);
            e1.priorityRing = Event.PRIORITY_LOW;
            assertTrue(e1 < e2);
            assertFalse(e1 >= e2);
        }
    }

    @Test
    public void getType() {
    }

    @Test
    public void getSubType() {
    }

    @Test
    public void stopHorizontalPropagation() {
    }

    @Test
    public void stopVerticalPropagation() {
    }

    @Test
    public void getArg() {
    }

    @Test
    public void putArg() {
        @Jailbreak Event e1 = new io.github.rosemoe.editor.core.extension.events.Event();
        int sz = r.nextUint(50);
        for(int a = 0; a < sz; a=a+1) {
            e1.putArg(new Integer(a));
        }
        assertTrue(e1.args.size() == sz);
    }

    @Test
    public void putArgs() {
    }
}