package io.github.rosemoe.editor.core.extension.extensions.widgets.completion;

import org.junit.Test;

import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class IdentifierAutoCompleteModelTest {

    @Test
    public void testSetKeywords() {
        @Jailbreak IdentifierAutoCompleteModel iacm = new IdentifierAutoCompleteModel();
        iacm.setKeywords(new String[]{"public","class","IdentifierAutoCompleteModelTest"});
        assertTrue(iacm.mKeywords[0].equals("public"));
    }

    @Test
    public void testGetKeywords() {
        @Jailbreak IdentifierAutoCompleteModel iacm = new IdentifierAutoCompleteModel();
        iacm.setKeywords(new String[]{"public","class","IdentifierAutoCompleteModelTest"});
        assertTrue(iacm.getKeywords().length == iacm.mKeywords.length);
    }
}