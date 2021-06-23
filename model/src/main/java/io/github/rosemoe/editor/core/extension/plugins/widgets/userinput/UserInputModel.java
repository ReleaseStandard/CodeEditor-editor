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
package io.github.rosemoe.editor.core.extension.plugins.widgets.userinput;

public class UserInputModel {

    public final static int HIDE_DELAY = 3000;
    public final static int HIDE_DELAY_HANDLE = 5000;
    public static final long INTERACTION_END_DELAY = 100;
    public final static int SELECTION_HANDLE_RESIZE_DELAY = 10;

    // handles : multiple stuff
    public static final int NONE = -1;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int BOTH = 2;

    public long mLastTouchedSelectionHandle = 0;
    public long mLastSetSelection = 0;
    public boolean isScaling = false;
    public long mLastScroll = 0;
    public long mLastInteraction = 0;
    public boolean mHoldingInsertHandle = false;
    public boolean mOverScrollEnabled = false;

    public final static int LEFT_EDGE   = 1;
    public final static int RIGHT_EDGE  = 1 << 1;
    public final static int TOP_EDGE    = 1 << 2;
    public final static int BOTTOM_EDGE = 1 << 3;

    /**
     * Type of handle to use for selection (left, right).
     */
    public int   selectionHandleType = -1;
    public float edgeFieldSize;


    /**
     * Whether this character is a part of word
     *
     * @param ch Character to check
     * @return Whether a part of word
     */
    public static boolean isIdentifierPart(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }

    /**
     * Checks whether the provided character is a whitespace
     *
     * @param c the char to check
     * @return Whether the provided character is a whitespace
     */
    public static boolean isWhitespace(char c) {
        return (c == '\t' || c == ' ' || c == '\f' || c == '\n' || c == '\r');
    }

    /**
     * Check whether operands are the same sign.
     * 0 is a neutral value (not signed, not unsigned)
     * @param a
     * @param b
     * @return
     */
    public static boolean isSameSign(int a, int b) {
        return (a < 0 && b < 0) || (a > 0 && b > 0);
    }

    /**
     * Whether we should draw scroll bars
     *
     * @return whether draw scroll bars
     */
    public boolean shouldDrawScrollBar(boolean holdingScrollbarVertical, boolean holdingScrollbarHorizontal) {
        return System.currentTimeMillis() - mLastScroll < HIDE_DELAY || holdingScrollbarVertical || holdingScrollbarHorizontal;
    }

    /**
     * For given x,y from the MotionEvent, compute the edge flag.
     * @param x
     * @param y
     * @param width width of editor view
     * @param height height of editor view
     * @return
     */
    public int computeEdgeFlags(float x, float y, int width, int height) {
        int flags = 0;
        if (x < edgeFieldSize) {
            flags |= LEFT_EDGE;
        }
        if (y < edgeFieldSize) {
            flags |= TOP_EDGE;
        }
        if (x > width - edgeFieldSize) {
            flags |= RIGHT_EDGE;
        }
        if (y > height - edgeFieldSize) {
            flags |= BOTTOM_EDGE;
        }
        return flags;
    }

}
