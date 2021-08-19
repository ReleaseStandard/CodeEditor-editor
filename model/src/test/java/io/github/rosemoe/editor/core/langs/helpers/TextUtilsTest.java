package io.github.rosemoe.editor.core.langs.helpers;

import org.junit.Test;

import io.github.rosemoe.editor.core.extension.extensions.langs.helpers.TextUtils;

import static org.junit.Assert.*;

public class TextUtilsTest {

    @Test
    public void countLeadingSpaceCount() {
    }

    @Test
    public void createIndent() {
    }

    @Test
    public void isEmoji() {
        assertFalse(TextUtils.isEmoji('o'));
        assertFalse(TextUtils.isEmoji('x'));
        assertFalse(TextUtils.isEmoji('o'));
    }
}