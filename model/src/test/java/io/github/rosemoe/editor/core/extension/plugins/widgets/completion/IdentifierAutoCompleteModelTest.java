package io.github.rosemoe.editor.core.extension.plugins.widgets.completion;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;
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