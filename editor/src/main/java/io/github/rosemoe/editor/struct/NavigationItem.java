/*
 *    CodeEditor - the awesome code editor for Android
 *    Copyright (C) 2020-2021  Rosemoe
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *     Please contact Rosemoe by email roses2020@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.editor.struct;

/**
 * Model for code navigation
 *
 * @author Rose
 */
@SuppressWarnings("CanBeFinal")
public class NavigationItem {

    /**
     * The line position
     */
    public int line;

    /**
     * The description
     */
    public String label;

    /**
     * Create a new navigation
     *
     * @param line  The line position
     * @param label The description
     */
    public NavigationItem(int line, String label) {
        this.line = line;
        this.label = label;
    }

}