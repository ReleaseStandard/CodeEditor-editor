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
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.util.CEObject;

/**
 * This a data class of a character position in the Content Grid
 * RuntimeExceptions could be avoided by the caller.
 *
 * @author Rose
 */
public final class CharPosition extends CEObject implements Comparable {

    // index, line, column are use [-1;INTEGER_MAX] values
    public final static int INVALID = -1;
    public int index;
    public int line;
    public int column;

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
        if ( line < INVALID || column < INVALID || index < INVALID ) {
            throw new RuntimeException("Invalid initialization");
        }
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
        this(INVALID,INVALID,INVALID);
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof CharPosition) {
            CharPosition a = (CharPosition) another;

            if ( index == INVALID || a.index == INVALID ) {
                if ( line == INVALID || column == INVALID || a.column == INVALID || a.line == INVALID ) {
                    throw new RuntimeException("Cannot compare thoses object, one seem to be not initialised");
                } else {
                    return line == a.line && column == a.column;
                }
            } else {
                return index == a.index;
            }
        } else if ( another instanceof Integer ) {
            if ( index == INVALID ) {
                throw new RuntimeException("Cannot compare thoses object, one seem to be not initialised");
            } else {
                return index == (Integer)another;
            }
        } else if ( another == null ) {
            return false;
        }
        else {
            throw new RuntimeException("Cannot compare object with : " + another.getClass());
        }
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
        return new CharPosition(line, column, index);
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
            if ( index == INVALID ) {
                throw new RuntimeException("Cannot compare integer with INVALID");
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
        if (charPosition.index == INVALID || index == INVALID) {
            if (!(charPosition.line == INVALID || line == INVALID || column == INVALID || charPosition.column == INVALID)) {
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
                System.out.println("this=");
                dump();
                System.out.println("vs=");
                charPosition.dump();
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
                if ( cp1.index == INVALID || cp2.index == INVALID || cp3.index == INVALID ) {
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

    /**
     * Check the current object contains enought information to get a position on the grid.
     * @return
     */
    public boolean isValid() {
        return ( index != INVALID || ( line != INVALID && column != INVALID ) );
    }
}
