package io.github.rosemoe.editor.core;

import io.github.rosemoe.editor.core.color.ColorManager;

/**
 * Model for CodeEditor.
 */
public class CodeEditorModel {

    public ColorManager colorManager = new ColorManager();                  // retain all colors.

    /**
     * The default size when creating the editor object. Unit is sp.
     */
    public static final int DEFAULT_TEXT_SIZE = 20;

    /**
     * Background position of CodeEditor.
     */
    public Rect background = new Rect();
}
