package io.github.rosemoe.editor.core.extension.plugins.widgets.contentAnalyzer;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class ContentLineModelTest {

    Random r = new Random();
    @Test
    public void initialise() {
        for( int a = 0; a < 100; a = a + 1) {
            ContentLineModel b = new ContentLineModel();
            b.initialise(r.nextBoolean());
        }
    }

    @Test
    public void checkIndex() {

        ContentLineModel b = new ContentLineModel();
        b.initialise(true);
        for( int a = 0; a < 20 + r.nextUint()%100; a = a + 1) {
            b.insert(0, r.nextChar());
        }
        try {
            b.checkIndex(0);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
        try {
            b.checkIndex(120);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void ensureCapacity() {
        ContentLineModel a = new ContentLineModel();
        a.initialise(true);
        for( int b = 0; b < r.nextUint()% 20; b = b + 1) {
            int val = r.nextUint() % 20;
            a.ensureCapacity(val);
            assertTrue(a.value.length >= val);
        }
    }

    @Test
    public void insert() {
        ContentLineModel a = new ContentLineModel();
        a.initialise(true);
        int len = r.nextUint()% 20;
        for( int b = 0; b < len ; b=b+1) {
            a.insert(0, r.nextChar());
        }
        assertTrue(a.length == len);
        a.insert(0, "ok", 0, 2);
        assertTrue(a.length == len + 2);
    }

    @Test
    public void append() {
        ContentLineModel a = new ContentLineModel();
        a.initialise(true);
        int sz = r.nextUint() % 20;
        for(int b = 0; b < sz; b=b+1) {
            String c = "a" + r.nextString();
            a.append(c, 0, 1);
        }
        System.out.println(a.value);
        assertTrue(sz == a.length);
    }

    @Test
    public void delete() {
        ContentLineModel a = new ContentLineModel();
        a.initialise(true);
        a.append(r.nextString(100), 0, 50);
        int deletions = r.nextUint() % 20;
        for(int b = 0; b < deletions; b = b + 1) {
            a.delete(0,1);
        }
        assertTrue(a.length == 50 - deletions );
    }

    @Test
    public void lastIndexOf() {
        char[] src = new char[] {'a','b','c'};
        char[] trg = new char[] {'a'};
        assertTrue(ContentLineModel.lastIndexOf(src,3,trg,1,0) == 0);
    }
}