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
package io.github.rosemoe.editor.core.content.processors.indexer;


import io.github.rosemoe.editor.core.IntPair;
import io.github.rosemoe.editor.core.util.CEObject;

/**
 * This a data class of a character position in ContentMapController
 *
 * @author Rose
 */
public final class CharPosition extends CEObject implements Comparable<Object> {

    //Packaged due to make changes

    public final static int INVALID = -1;
    public int index = INVALID;
    public int line = INVALID;
    public int column = INVALID;

    /**
     * Get the index
     *
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get column
     *
     * @return column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get line
     *
     * @return line
     */
    public int getLine() {
        return line;
    }

    /**
     * Make this CharPosition zero and return self
     *
     * @return self
     */
    public CharPosition zero() {
        index = line = column = 0;
        return this;
    }

    public CharPosition(int line, int column, int index) {
        this.line = line;
        this.column = column;
        this.index = index;
    }
    public CharPosition(int line, int column) {
        this(line,column,INVALID);
    }

    public CharPosition(int index) {
        this(INVALID,INVALID,index);
    }

    public CharPosition() {

    }
    @Override
    public boolean equals(Object another) {
        if (another instanceof CharPosition) {
            CharPosition a = (CharPosition) another;
            return a.column == column &&
                    a.line == line &&
                    a.index == index;
        }
        return false;
    }

    /**
     * Convert {@link CharPosition#line} and {@link CharPosition#column} to a Long number
     *
     * First integer is line and second integer is column
     * @return A Long integer describing the position
     */
    public long toIntPair() {
        return IntPair.pack(line, column);
    }

    /**
     * Make a copy of this CharPosition and return the copy
     *
     * @return New CharPosition including info of this CharPosition
     */
    @Override
    public CharPosition clone() {
        CharPosition pos = new CharPosition();
        pos.index = index;
        pos.line = line;
        pos.column = column;
        return pos;
    }

    @Override
    public String toString() {
        return "CharPosition(line = " + line + ",column = " + column + ",index = " + index + ")";
    }

    @Override
    public int compareTo(final Object obj) {
        if ( obj instanceof CharPosition ) {
           return compareToCharPosition((CharPosition) obj);
        } else if ( obj instanceof Integer ) {
            if ( index <= -1 ) {
                throw new RuntimeException("Cannot compare integer with -1");
            } else {
                return Integer.compare(index, (Integer) obj);
            }
        } else {
            throw new RuntimeException("Cannot compare CharPosition with " + obj.getClass());
        }
    }

    /**
     * Compare to a CharPosition object.
     * @param charPosition position to compare with.
     * @return 0, -1, 1, Integer.compare, RuntimeException
     */
    private int compareToCharPosition(CharPosition charPosition) {
        if (charPosition.index == -1 || index == -1) {
            if (!(charPosition.line == -1 || line == -1 || column == -1 || charPosition.column == -1)) {
                int cpmCol = Integer.compare(column, charPosition.column);
                int cmpLine = Integer.compare(line, charPosition.line);
                if (cmpLine < 0) {
                    return -1;
                } else if (cmpLine > 0) {
                    return 1;
                } else {
                    if (cpmCol < 0) {
                        return -1;
                    } else if (cpmCol > 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } else {
                throw new RuntimeException("Cannot compare those CharPosition");
            }
        } else {
            return Integer.compare(index, charPosition.index);
        }
    }

    /**
     * Find the nearest for a given cp2 between cp3 and cp1.
     * @param cp1
     * @param cp2
     * @param cp3
     * @return or cp1 or cp3 or null
     */
    public static CharPosition nearest(CharPosition cp1, final CharPosition cp2, CharPosition cp3) {
        if ( cp1 == null ) {
            if ( cp3 == null ) {
                return null;
            } else {
                return cp3;
            }
        } else {
            if ( cp3 == null ) {
                return cp1;
            } else {
                if ( cp1 > cp3 ) {
                    CharPosition aux = cp1;
                    cp1 = cp3;
                    cp3 = aux;
                }
                if ( cp1.index == -1 || cp2.index == -1 || cp3.index == -1 ) {
                    throw new RuntimeException("We don't have size of lines at this step, can give a nearest !");
                } else {
                    int diff = cp2.index - cp1.index;
                    int diff2 = cp3.index - cp2.index;
                    if ( diff2 < diff ) {
                        return cp3;
                    } else {
                        return cp1;
                    }
                }
            }
        }
    }
}
