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
package io.github.rosemoe.editor.core.content;

public class ContentLineModel {
    public char[] value;

    public int length;

    /**
     * Id in BinaryHeap
     */
    public int id;

    public void initialise(boolean extended) {
        id = -1;

        if ( extended ) {
            length = 0;
            value = new char[32];
        }
    }

    /**
     * Check if requested index is valid.
     * @param index in the content line 0..n-1
     */
    public void checkIndex(int index) {
        if (index < 0 || index >= length) {
            throw new StringIndexOutOfBoundsException("index = " + index + ", length = " + length);
        }
    }

    /**
     * Ensure that the content line has enough space to process.
     * @param capacity
     */
    public void ensureCapacity(int capacity) {
        if (value.length < capacity) {
            int newLength = value.length * 2 < capacity ? capacity + 2 : value.length * 2;
            char[] newValue = new char[newLength];
            System.arraycopy(value, 0, newValue, 0, length);
            value = newValue;
        }
    }
}
