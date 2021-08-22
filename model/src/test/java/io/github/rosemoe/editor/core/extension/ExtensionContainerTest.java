package io.github.rosemoe.editor.core.extension;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class ExtensionContainerTest {

    Random r = new Random();
    @Test
    public void testIterator() {
        {
            ExtensionContainer ec = new ExtensionContainer();
            Extension e1 = new Extension(), e2 = new Extension();
            ec.put(e1, e2);
            int count = 0;
            for (Extension e : ec) {
                count += 1;
            }
            assertTrue(count == 2);
        }
        {
            ExtensionContainer ec = new ExtensionContainer();
            int sz = r.nextUint(50);
            for(int a = 0; a < sz; a = a + 1) {
                ec.put(new Extension());
            }
            assertTrue(ec.extensions.size() == sz);
            int t = 0;
            for(Extension e : ec.extensions) {
                t+=1;
            }
            assertTrue(sz == t);
        }
    }

    static int count = 0;
    static ReentrantLock countLock = new ReentrantLock();
    static int count2 = 0;
    static ReentrantLock count2Lock = new ReentrantLock();

    @Test
    public void testDispatchSystem1() throws InterruptedException {
        {
            CodeEditorModel editor = new CodeEditorModel();
            count = 0;
            Extension e = new Extension(editor) {
                @Override
                protected void handleEventDispatch(Event e, String subtype) {
                    super.handleEventDispatch(e, subtype);
                    countLock.lock();
                    count += 1;
                    countLock.unlock();
                }

                @Override
                public boolean issubscribed(Class type) {
                    return true;
                }
            };
            ExtensionContainer ec = new ExtensionContainer();
            ec.put(e);

            int sz = 2 + r.nextUint(50);
            for(int a = 0; a < sz; a = a + 1) {
                ec.dispatch(new Event());
            }
            // Warning : you may have to increase this delay (may cause that some event are not counted).
            for(int a = 0; a < 20; a ++) {
                countLock.lock();
                if (sz == count) { break; }
                countLock.unlock();
                Thread.sleep(1000);
            }
            if (countLock.isHeldByCurrentThread()) {
                countLock.unlock();
            }

            assertTrue("count="+count+",sz="+sz,sz == count);
        }
    }
    public void evalExtensionContainer(String msg, ExtensionContainer ec) throws InterruptedException {
        Logger.DEBUG = false;
        CodeEditorModel editor = new CodeEditorModel();
        count2 = 0;
        int instances = r.nextUint(50);
        for(int a = 0; a < instances; a = a + 1) {
            Extension e = new Extension(editor) {
                @Override
                protected void handleEventDispatch(Event e, String subtype) {
                    count2 += 1;
                }

                @Override
                public boolean issubscribed(Class type) {
                    return true;
                }

            };
            ec.put(e);
        }

        int sz = 2 + r.nextUint(50);
        for(int a = 0; a < sz; a = a + 1) {
            Event e = new Event("e" + a);
            ec.dispatch(e);
        }

        // Warning : you may have to increase this delay (may cause that some event are not counted).
        int s = 5000;
        for(int a = 0; a < s/100; a ++) {
            if (sz*instances == count2) { break; }
            Thread.sleep(s/50);
        }
        Logger.DEBUG = true;
        System.out.println(msg + " " + instances + " extensions for " + sz + " events give " + count2 + " in " + s + " ms (" + ((float)count2/((float)s*0.001)) + "/s)" );
        //assertTrue("count2="+count2+",sz="+sz+",instances="+instances+","+sz*instances+" == "+count2,sz*instances == count2);
    }

    @Test
    public void benchlarkDispatchSystems() throws InterruptedException {
        evalExtensionContainer("dispatch multi threads", new ExtensionContainer());
        evalExtensionContainer("dispatch mono thread", new ExtensionContainer(){
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                handleEventDispatchSyncronousMonoThread(e, subtype);
            }
        });
        evalExtensionContainer("dispatch mono thread asynchronous", new ExtensionContainer(){
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                handleEventDispatchAsyncronousMonoThread(e, subtype);
            }
        });
        evalExtensionContainer("dispatch multithreads thread asynchronous", new ExtensionContainer(){
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                handleEventDispatchAsyncronousMultiThread(e, subtype);
            }
        });
    }

    static int count3 = 0;
    ReentrantLock countLock3 = new ReentrantLock();
    @Test(timeout=20000)
    public void testStopHorizontalPropagation() {
        CodeEditorModel editor = new CodeEditorModel();
        count3 = 0;
        Extension e = new Extension(editor) {
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                super.handleEventDispatch(e, subtype);
                countLock3.lock();
                count3 += 1;
                countLock3.unlock();
                e.stopHorizontalPropagation();
            }
            @Override
            public boolean issubscribed(Class type) {
                return true;
            }
        };
        Extension e2 = new Extension(editor) {
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                super.handleEventDispatch(e, subtype);
                countLock3.lock();
                count3 += 1;
                countLock3.unlock();
            }
            @Override
            public boolean issubscribed(Class type) {
                return true;
            }
        };
        ExtensionContainer ec = new ExtensionContainer();
        ec.put(e,e2);
        Logger.debug("Before dispatch");
        ec.dispatch(new Event());
        Logger.debug("After dispatch");
        for(int a = 0; a < 20; a ++) {
            try {
                if(count3 == 1) { break; }
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        if (countLock3.isHeldByCurrentThread()) {
            countLock3.unlock();
        }
        assertTrue(count3 == 1);
    }

    static int count4 = 0;
    ReentrantLock countLock4 = new ReentrantLock();
    @Test(timeout=20000)
    public void testStopVerticalPropagation() {
        CodeEditorModel editor = new CodeEditorModel();
        count4 = 0;
        Extension e = new Extension(editor) {
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                Logger.debug("Event dispatched in extension 1");
                countLock4.lock();
                count4 += 1;
                countLock4.unlock();
                Logger.debug(" AFTER EVENT DISPATCH");
                e.stopVerticalPropagation();
                e.dump();
            }
            @Override
            public boolean issubscribed(Class type) {
                return true;
            }
        };
        Extension e2 = new Extension(editor) {
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                Logger.debug("Event dispatched in extension 2");
                countLock4.lock();
                count4 += 1;
                countLock4.unlock();
            }
            @Override
            public boolean issubscribed(Class type) {
                return true;
            }
        };
        Extension e3 = new Extension(editor) {
            @Override
            protected void handleEventDispatch(Event e, String subtype) {
                Logger.debug("Event dispatched in extension 3");
                countLock4.lock();
                count4 += 1;
                countLock4.unlock();
            }
            @Override
            public boolean issubscribed(Class type) {
                return true;
            }
        };
        e.priorityRing = PrioritySystem.PRIORITY_HIGH;
        e2.priorityRing = PrioritySystem.PRIORITY_HIGH;
        e3.priorityRing = PrioritySystem.PRIORITY_STD;
        ExtensionContainer ec = new ExtensionContainer();
        ec.put(e2,e,e3);
        Event evt = new Event();
        ec.dispatch(evt);
        for(int a = 0; a < 20; a ++) {
            try {
                if(count4 == 2) { break; }
                Thread.sleep(1000);
                if(count4 == 2) { break; }
                Logger.debug();
                Logger.debug();
                Logger.debug("count4=" + count4);
                evt.dump();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        if (countLock4.isHeldByCurrentThread()) {
            countLock4.unlock();
        }
        assertTrue(count4 == 2);
    }
    @Ignore("This a know bug")
    @Test
    public void testManifoldBug() {
        @Jailbreak ExtensionContainer ec = new ExtensionContainer();
        Extension e = new Extension();
        ec.put(e);
    }
    @Test
    public void testPriorityBreak() {
        Extension e1 = new Extension(), e2 = new Extension(), e3 = new Extension();
        e1.priorityRing = PrioritySystem.PRIORITY_LOW;
        e2.priorityRing = PrioritySystem.PRIORITY_HIGH;
        e3.priorityRing = PrioritySystem.PRIORITY_STD;
        ExtensionContainer ec = new ExtensionContainer();
        ec.put(e1,e2,e3);
        int a = 0;
        for(Extension extension : ec) {
            if ( a == 0 ) {
                assertTrue(e2 == extension);
            } else if ( a == 1 ) {
                assertTrue(e3 == extension);
            } else if ( a == 2 ) {
                assertTrue(e1 == extension);
            }
            a += 1;
        }
    }
}