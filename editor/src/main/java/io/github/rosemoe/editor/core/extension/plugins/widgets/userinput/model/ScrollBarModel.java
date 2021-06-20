package io.github.rosemoe.editor.core.extension.plugins.widgets.userinput.model;

import io.github.rosemoe.editor.core.model.Rect;

public class ScrollBarModel {
    public Rect bar = new Rect();
    public Rect barTrack = new Rect();
    public boolean holding = false;
    public String orientation = "vertical";
}
