package io.github.rosemoe.editor.core.analyzer;

import io.github.rosemoe.editor.core.content.controller.ContentGrid;

/**
 * A Result store is used to manage stack of results.
 * Theses results are then passed to the analyzer.
 * The ResultStore must must thread safe, many analyzer on different thread could request it.
 */
public class ResultStore {
    public ContentGrid mText;
}
