package io.github.rosemoe.editor.core;

import java.util.Iterator;
import java.util.TreeMap;

public class Grid<T> extends TreeMap<Integer, Line> implements Iterable<Line> {

    @Override
    public Iterator<Line> iterator() {
        return super.values().iterator();
    }

    @Override
    public Line get(Object key) {
        return super.get(key);
    }
}
