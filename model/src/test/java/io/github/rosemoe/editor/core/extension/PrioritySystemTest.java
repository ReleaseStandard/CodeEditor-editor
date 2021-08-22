package io.github.rosemoe.editor.core.extension;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrioritySystemTest {

    @Test
    public void compareTo() {
        {
            PrioritySystem ps1 = new PrioritySystem() {
            };
            PrioritySystem ps2 = new PrioritySystem() {
            };
            ps1.priorityRing = PrioritySystem.PRIORITY_HIGH;
            ps2.priorityRing = PrioritySystem.PRIORITY_LOW;
            assertTrue(ps1.compareTo(ps2) < 0);
        }
        {
            Extension e1 = new Extension();
            e1.priorityRing = Extension.PRIORITY_LOW;
            Extension e2 = new Extension();
            e2.priorityRing = Extension.PRIORITY_HIGH;
            assertTrue(e1.compareTo(e2) > 0);
        }
    }
}