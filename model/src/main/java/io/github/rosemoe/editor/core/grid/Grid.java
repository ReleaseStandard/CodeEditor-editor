package io.github.rosemoe.editor.core.grid;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;

public class Grid extends ConcurrentSkipListMap<Integer, Line> implements Iterable<Line> {

    @Override
    public Iterator<Line> iterator() {
        return super.values().iterator();
    }

    @Override
    public Line get(Object key) {
        return super.get(key);
    }
}
