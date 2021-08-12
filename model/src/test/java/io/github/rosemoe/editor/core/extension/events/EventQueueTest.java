package io.github.rosemoe.editor.core.extension.events;

import org.junit.Test;

import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class EventQueueTest {

    @Test
    public void pollingThread() {
    }

    @Test
    public void handlePolling() {
        @Jailbreak EventQueue eq = new EventQueue() {
            @Override
            public void handlePolling(Event e) {
                super.handlePolling(e);
            }
        };
    }
}