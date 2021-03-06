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
package io.github.rosemoe.editor.core.extension;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * This is class is a container for extension with a priority queue.
 * You can view it as a bus.
 * You can connect the bus an other.
 * Extensions in the communicate with others with some rules defined in the handle{emit,dispatch} function.
 */
public class ExtensionContainer extends Extension implements Iterable<Extension> {

    public PriorityBlockingQueue<Extension> extensions = new PriorityBlockingQueue<Extension>();

    public ExtensionContainer() {
        super(null);
    }

    @Override
    public boolean issubscribed(Class type) {
        return true;
    }

    /**
     * Inside an extension container, a dispatch() call cause an event dispatching on all plugins of the container.
     * With priority based processing (priority of extensions) as the event priority is alrdy handled by Extension.
     * - first we check : priority of event
     * - then we check  : priority of extension
     * @param e
     */
    @Override
    protected void handleEventDispatch(Event e, String subtype) {
        handleEventDispatchSyncronousMultiThread(e, subtype);
    }
    protected void handleEventDispatchSyncronousMonoThread(Event e, String subtype) {
        Extension olde = null;
        for(Extension extension : ExtensionContainer.this) {
            if( e.stopHorizontalPropagation ) {
                Logger.debug("Vertical propagation of event stopped");
                break;
            }
            if ( e.stopVerticalPropagation ) {
                Logger.debug("TEST ==> " + extension.compareTo(olde));
                if ( extension.compareTo(olde) > 0 ) {
                    Logger.debug("Vertical propagation stopped");
                    break;
                }
            }
            if ( extension.issubscribed(e) ) {
	            e.engage();
	            extension.dispatch(e);
	            e.waitReady();
	    }
            olde = extension;
        }
    }
    protected void handleEventDispatchAsyncronousMonoThread(Event e, String subtype) {
        Extension olde = null;
        for(Extension extension : ExtensionContainer.this) {
            if( e.stopHorizontalPropagation ) {
                Logger.debug("Vertical propagation of event stopped");
                break;
            }
            if ( e.stopVerticalPropagation ) {
                Logger.debug("TEST ==> " + extension.compareTo(olde));
                if ( extension.compareTo(olde) > 0 ) {
                    Logger.debug("Vertical propagation stopped");
                    break;
                }
            }
            if ( extension.issubscribed(e) ) {
	            extension.dispatch(e);
	    }
            olde = extension;
        }
    }
    protected void handleEventDispatchSyncronousMultiThread(Event e, String subtype) {
        new Thread() {
            @Override
            public void run() {
                handleEventDispatchSyncronousMonoThread(e,subtype);
            }
        }.start();
    }
    protected void handleEventDispatchAsyncronousMultiThread(Event e, String subtype) {
        new Thread() {
            @Override
            public void run() {
                handleEventDispatchAsyncronousMonoThread(e,subtype);
            }
        }.start();
    }

    @Override
    protected void handleEventEmit(Event e) {
        // Depends on the type of the event, the plugin
        throw new RuntimeException("You cannot call emit() on an Extension contains");
    }

    /**
     * Add an extension in the container.
     * @param extensions extension(s) to add
     */
    public void put(Extension ...extensions) {
        for(Extension extension : extensions) {
            this.extensions.add(extension);
        }
    }


    /**
     * Retrieve an extension by it's name.
     * Extensions number is so low that we don't have to cache theses results.
     * @param name extension name
     * @return extension for the given name or null
     */
    public Extension get(String name) {
        for (Iterator<Extension> it = extensions.iterator(); it.hasNext(); ) {
            Extension extension = it.next();
            if(extension.name.equals(name)) {
                return extension;
            }
        }
        return null;
    }

    @SuppressWarnings("NewApi")
    @Override
    public Iterator<Extension> iterator() {
        Extension[] exts = toArray(new Extension[extensions.size()]);
        Arrays.sort(exts);
        return Arrays.stream(exts).iterator();
    }
    public Object[] toArray() {
        return extensions.toArray();
    }
    public Extension[] toArray(Extension [] tab) {
        return extensions.toArray(tab);
    }
    public int size() {
        return extensions.size();
    }
}
