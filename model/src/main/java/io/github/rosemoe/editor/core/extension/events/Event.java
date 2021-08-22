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
package io.github.rosemoe.editor.core.extension.events;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.CEObject;
import io.github.rosemoe.editor.core.extension.PrioritySystem;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * Basically any type of events.
 *
 * @author Release Standard
 */
public class Event extends PrioritySystem {

    private CountDownLatch inUse = new CountDownLatch(1);
    public boolean stopHorizontalPropagation = false;
    public boolean stopVerticalPropagation   = false;
    /**
     * Type of event, eg event type from EventInput, event type from the layout
     * @return
     */
    public Class getType() { return this.getClass(); }
    /**
     * We define a subtype, eg if event type if UserInput, we can user the scrollby subtype.
     */
    public String subtype = "none";
    public String getSubType() { return subtype; }
    /**
     * Stop propagation of this event at the current propagation state.
     */
    public void stopHorizontalPropagation() {
        stopHorizontalPropagation = true;
    }
    /**
     * Stop propagation of this event before entry in lower priority events.
     */
    public void stopVerticalPropagation() {
        stopVerticalPropagation = true;
    }

    public ArrayList<Object> args = new ArrayList<>();

    public Event(Class type, String subtype) {
        this.subtype = subtype;
    }
    public Event() {
    }
    public Event(String subtype, Object ...args) {
        this.subtype = subtype;
        putArgs(args);
    }
    /**
     * Get argument associated with a given index.
     * @param index index of argument to get.
     * @return
     */
    public Object getArg(int index) {
        try {
            return args.get(index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Put a given arg in the event.
     * @param arg
     */
    public Event putArg(Object arg) { args.add(arg); return this; }
    public Event putArgs(Object ...args) {
        for(Object arg : args) {
            putArg(arg);
        }
        return this;
    }
    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        CEObject.dump(this, offset);
    }
    /**
     * Lock event consumption.
     */
    public void engage() {
        inUse = new CountDownLatch(1);
    }
    /**
     * Unlock event consumption
     */
    public void disengage() {
        inUse.countDown();
    }
    /**
     * Wait until event can be consummed again.
     */
    public void waitReady() {
        try {
            inUse.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
