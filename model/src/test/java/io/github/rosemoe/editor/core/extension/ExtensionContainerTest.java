package io.github.rosemoe.editor.core.extension;

import org.junit.Ignore;
import org.junit.Test;

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
    @Test
    @Ignore("TODO")
    public void testDispatchSystem() {
        {
            count = 0;
            Extension e = new Extension() {
                @Override
                protected void handleEventDispatch(Event e, String subtype) {
                    super.handleEventDispatch(e, subtype);
                    count += 1;
                }

                @Override
                public boolean issubscribed(Class type) {
                    return true;
                }
            };
            ExtensionContainer ec = new ExtensionContainer();
            ec.put(e);
        }
    }
}