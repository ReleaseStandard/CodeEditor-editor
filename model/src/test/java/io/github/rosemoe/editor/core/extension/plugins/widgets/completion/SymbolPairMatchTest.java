package io.github.rosemoe.editor.core.extension.plugins.widgets.completion;

import org.junit.Test;

import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class SymbolPairMatchTest {

    @Test
    public void putPair() {
        SymbolPairMatch spm = new SymbolPairMatch();
        spm.putPair('t',new SymbolPairMatch.Replacement("test", 0));
        assertTrue(spm.getCompletion('t')!=null);
        assertFalse(spm.getCompletion('t')==null);
    }

    @Test
    public void getCompletion() {
        SymbolPairMatch spm = new SymbolPairMatch();
        assertTrue(spm.getCompletion('t')==null);
    }

    @Test
    public void removeAllRules() {
        @Jailbreak SymbolPairMatch spm = new SymbolPairMatch();
        for(int a = 0; a < 10; a=a+1) {
            spm.putPair('t',new SymbolPairMatch.Replacement("test",0));
        }
        assertTrue(spm.pairMaps.size()!=0);
        spm.removeAllRules();
        assertTrue(spm.pairMaps.size()==0);
    }

    @Test
    public void testReplacement() {
        SymbolPairMatch.Replacement r = new SymbolPairMatch.Replacement("test",0);
    }

    @Test
    public void testDefaultSymbolPairs() {
        SymbolPairMatch.DefaultSymbolPairs d = new SymbolPairMatch.DefaultSymbolPairs();
    }
}