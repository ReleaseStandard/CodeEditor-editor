package io.github.rosemoe.editor.core.analyze.analyzer.content;

import io.github.rosemoe.editor.core.content.controller.CodeAnalyzerResultContent;

/**
 * For saving modification better
 *
 * @author Rose
 */
interface ContentAction {

    /**
     * Undo this action
     *
     */
    void undo();

    /**
     * Redo this action
     *
     */
    void redo();

    /**
     * Get whether the target action can be merged with this action
     *
     * @param action Target action to merge
     * @return Whether can merge
     */
    boolean canMerge(ContentAction action);

    /**
     * Merge with target action
     *
     * @param action Target action to merge
     */
    void merge(ContentAction action);

}