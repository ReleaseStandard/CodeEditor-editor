package io.github.rosemoe.editor.core.content.processors.indexer;

import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.grid.instances.ContentCell;

public abstract class BaseContentIndexer extends ContentIndexer {

    public BaseContentIndexer(CodeAnalyzerResultContent content) {
        super(content);
    }

    @Override
    public int getCharIndex(int line, int column) {
        CharPosition cp = getCharPosition(line, column);
        return (cp == null) ? -1 : cp.index;
    }

    @Override
    public int getCharLine(int index) {
        CharPosition cp = getCharPosition(index);
        return (cp == null) ? -1 : cp.line;
    }

    @Override
    public int getCharColumn(int index) {
        CharPosition cp = getCharPosition(index);
        return (cp == null) ? -1 : cp.column;
    }

    @Override
    public CharPosition getCharPosition(int index) {
        return getCharPosition(new CharPosition(index));
    }

    @Override
    public CharPosition getCharPosition(int line, int column) {
        return getCharPosition(new CharPosition(line, column));
    }

    /**
     * Get index in the content grid.
     * Assuming line column not INVALID
     * @param line 0..n-1
     * @param column 0..n-1
     * @return -1 or index
     */
    protected int processIndex(int line, int column) {
        int index = column;
        for( Integer k : content.keySet()) {
            if ( k >= line ) { return index; }
            Line<ContentCell> l = content.get(k);
            if ( l == null ) { continue; }
            index += l.getWidth();
        }
        return -1;
    }
    protected CharPosition processIndexWrappCharPosition(int line, int column) {
        int idx = processIndex(line, column);
        return new CharPosition(line, column, idx);
    }
    /**
     * Get line, column for that index in the given column.
     * @param index
     * @return
     */
    protected CharPosition processCharPosition(int index) {
        int idx = 0;
        for( Integer k : content.keySet()) {
            Line<ContentCell> l = content.get(k);
            if ( index >= idx ) {
                if ( index < ( idx + l.getWidth() ) ) {
                    return new CharPosition(k,index-idx,index);
                }
            } else {

            }
            idx += l.getWidth();
        }
        if ( idx == index ) {
            try {
                return new CharPosition(content.lastKey(), content.get(content.lastKey()).getWidth(), index);
            } catch (Exception e) { return null; }
        }
        return null;
    }

    /**
     * How do the indexer make elements retrieval.
     * @param charPosition the char position to get
     * @return the result or null if error
     */
    protected abstract CharPosition getCharPosition(CharPosition charPosition);
}
