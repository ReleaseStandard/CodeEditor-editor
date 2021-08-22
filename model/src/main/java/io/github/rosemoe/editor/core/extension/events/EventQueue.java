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

import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

import io.github.rosemoe.editor.core.util.Logger;

public class EventQueue extends PriorityBlockingQueue<Event> {

    public final static int POLLING_MS = 100;
    public EventQueue() {
        pollingThread();
    }
    Thread t ;
    public void pollingThread() {
        t = new Thread() {
            @Override
            public void run() {
                while (true)  {
                    try {
                        Event e = EventQueue.this.poll();
                        while ( e != null ) {
                            handlePolling(e);
                            e.disengage();
                            e = EventQueue.this.poll();
                        }
                        Thread.sleep(POLLING_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
    public void handlePolling(Event e) { }
}
