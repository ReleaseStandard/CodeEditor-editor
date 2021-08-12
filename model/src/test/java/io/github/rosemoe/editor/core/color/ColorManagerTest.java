package io.github.rosemoe.editor.core.color;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class ColorManagerTest {

    Random r = new Random();
    @Test
    public void attach() throws InterruptedException {
        {
            @Jailbreak ColorManager cm = new ColorManager();
            assertTrue(cm.observers.size() == 0);
            cm.attach(new Observer() {
                @Override
                public void update(Observable o, Object arg) {

                }
            });
            assertTrue(cm.observers.size() == 1);
        }

        {
            @Jailbreak ColorManager cm = new ColorManager();
            Vector<Thread> tab = new Vector<>();
            int sz = 20;
            for (int a = 0; a < sz; a = a + 1) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        cm.attach(new Observer() {
                            @Override
                            public void update(Observable o, Object arg) {

                            }
                        });
                    }
                };
                tab.add(t);
                t.start();
            }
            assertTrue(tab.size() == sz);
            for (Thread t : tab) {
                t.join();
            }
        }
    }

    @Test
    public void notifyAllObservers() throws InterruptedException {
        {
            class MyObserver implements Observer {
                public int count = 0;
                @Override
                public void update(Observable o, Object arg) {
                    count += 1;
                }
            }
            @Jailbreak ColorManager cm = new ColorManager();
            MyObserver m = new MyObserver();
            cm.attach(m);
            for(int a = 0; a < 20 ; a = a + 1 ) {
                cm.attach(new Observer() {
                    @Override
                    public void update(Observable o, Object arg) {

                    }
                });
            }
            assertTrue(m.count == 0);
            int sz = r.nextUint(100);
            Vector<Thread> tab = new Vector<>();
            for(int a = 0; a < sz ; a = a + 1 ) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        cm.notifyAllObservers();
                    }
                };
                tab.add(t);
                t.start();

            }
            for(Thread t : tab) { t.join(); }
            assertTrue("count=" + m.count, m.count == sz);
        }
    }

    @Test
    public void reset() {
    }

    @Test
    public void init() {
    }

    @Test
    public void decodeHex() {
        {
            String[] chars = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
            for (int a = 0; a < 20; a = a + 1) {
                int value = 0;
                String h = "";
                for (int b = 0; b < 8; b = b + 1) {
                    int i = r.nextUint(16);
                    int sh = i << (b * 4);
                    value = value | sh;
                    h = chars[i] + h;
                }
                assertTrue("h=0x" + h + ",value=" + String.format("0x%08X", value) + "(" + value + "),ColorManager.decodeHex(h)=" + ColorManager.decodeHex(h), ColorManager.decodeHex(h) == value);
            }
        }
    }

    @Test
    public void getColor() {
        {
            @Jailbreak ColorManager cm = new ColorManager();
            int col = r.nextInt();
            cm.register("test", col);
            cm.register("test2","test");
            assertTrue(cm.getColor("test") == col);
            assertTrue(cm.getColor("test2") == col);
        }
    }

    @Test
    public void register() {
    }

    @Test
    public void registerIfNotIn() {
    }

    @Test
    public void invertColorScheme() {
    }

}