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
package io.github.rosemoe.editor.core.grid.instances.color;

import java.util.Map;

import io.github.rosemoe.editor.core.CEObject;
import io.github.rosemoe.editor.core.grid.Grid;
import io.github.rosemoe.editor.core.grid.Line;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * This class is a SpanLine container (line displayed to the screen).
 * All indexes are from 0 to n
 *
 * @author Release Standard
 */
public class SpanMap extends Grid {

    public void SpanMap() {

    }
    /**
     * Append an empty line to the span 
     * @return
     */
    public SpanLine appendLine() {
        int newIndex = size();
        SpanLine l = SpanLine.EMPTY();
        l.behaviourOnCellSplit = behaviourOnCellSplit;
        put(newIndex, l);
        return (SpanLine) get(newIndex);
    }

    /**
     * Complete the current spansuch as it while contains finalSizeInLines.
     * It will not remove extra lines
     *
     * @param finalSizeInLines 0..n
     */
    public void appendLines(int finalSizeInLines) {
        while(size() < finalSizeInLines) {
            appendLine();
        }
    }

    /**
     * This will get the required span line or create it if it doesn't exists.
     * lineno : 0..n-1 the span line to get.
     * @param lineno
     * @return
     */
    public SpanLine getAddIfNeeded(int lineno) {
        appendLines(lineno+1);
        return (SpanLine) get(lineno);
    }

    //public void insertContent()
    /**
     * Dump debug information on this class.
     */
    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        if ( !Logger.DEBUG ) { return; }
        Logger.debug(offset+"number of lines in : "+ size());
        //noinspection unchecked
        for(Map.Entry<Integer, SpanLine> sl : entrySet().toArray(new Map.Entry[keySet().size()])) {
            Logger.debug(offset+"dump for line index " + sl.getKey());
            sl.getValue().dump(Logger.OFFSET);
        }
    }

    public SpanLine addNormalIfNull() {
        appendLines(1);
        return (SpanLine) get(0);
    }
}
