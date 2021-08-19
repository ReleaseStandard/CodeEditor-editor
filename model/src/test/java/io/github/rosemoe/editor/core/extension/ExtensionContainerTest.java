package io.github.rosemoe.editor.core.extension;

import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.util.Random;

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
    public void testDispatchSystem() throws InterruptedException {
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
                Thread.sleep(100);
            }
            if (countLock.isHeldByCurrentThread()) {
                countLock.unlock();
            }

            assertTrue("count="+count+",sz="+sz,sz == count);
        }
        {
            CodeEditorModel editor = new CodeEditorModel();
            count2 = 0;
            int instances = r.nextUint(100);
            ExtensionContainer ec = new ExtensionContainer();
            for(int a = 0; a < instances; a = a + 1) {
                Extension e = new Extension(editor) {
                    @Override
                    protected void handleEventDispatch(Event e, String subtype) {
                        super.handleEventDispatch(e, subtype);
                        count2Lock.lock();
                        count2 += 1;
                        count2Lock.unlock();
                    }

                    @Override
                    public boolean issubscribed(Class type) {
                        return true;
                    }
                };
                ec.put(e);
            }

            int sz = 2 + r.nextUint(100);
            for(int a = 0; a < sz; a = a + 1) {
                ec.dispatch(new Event());
            }

            // Warning : you may have to increase this delay (may cause that some event are not counted).
            for(int a = 0; a < 20; a ++) {
                count2Lock.lock();
                if (sz*instances == count2) { break; }
                count2Lock.unlock();
                Thread.sleep(100);
            }
            if (count2Lock.isHeldByCurrentThread()) {
                count2Lock.unlock();
            }
            assertTrue("count2="+count2+",sz="+sz+",instances="+instances+","+sz*instances+" == "+count2,sz*instances == count2);
        }
    }
}