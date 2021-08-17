package io.github.rosemoe.editor.core.langs.helpers;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class LineNumberCalculatorTest {

    Random r = new Random();
    @Test
    public void update() {
        {
            LineNumberCalculator lnc = new LineNumberCalculator("ok\n\n\naze");
            lnc.update(10);
            assertTrue("lnc=" + lnc.getLine(), lnc.getLine() == 3 && lnc.getColumn() == 3);
        }
        {
            LineNumberCalculator lnc = new LineNumberCalculator("ok");
            lnc.update(10);
            assertTrue("lnc=" + lnc.getLine()+",col=" + lnc.getColumn(), lnc.getLine() == 0 && lnc.getColumn() == 2);
        }
        {
            LineNumberCalculator lnc = new LineNumberCalculator("");
            lnc.update(10);
            assertTrue("lnc=" + lnc.getLine()+",col=" + lnc.getColumn(), lnc.getLine() == 0 && lnc.getColumn() == 0);
        }
        {
            String s = "";
            int sz = r.nextUint(100);
            for(int a = 0; a < sz ; a=a+1) {
                for(int b = 0; b < r.nextUint(100) ; b=b+1) {
                    s = s + "a";
                }
                s = s + "\n";
            }
            LineNumberCalculator lnc = new LineNumberCalculator(s);
            lnc.update(10000);
            assertTrue("sz=" + sz + ",lnc.getLine()=" + lnc.getLine() + ",s=" + s, lnc.getLine() == sz);
        }
    }

    @Test
    public void findLine() {
        {
            String s = "";
            int sz = r.nextUint(100);
            for(int a = 0; a < sz ; a=a+1) {
                s = s + "\n";
            }
            @Jailbreak LineNumberCalculator lnc = new LineNumberCalculator(s);
            lnc.update(sz);
            lnc.mOffset = 0;
            lnc.mLength = 1000000000;
            assertTrue(lnc.findIndexLineStart() == 0);
            assertTrue(lnc.findIndexLineEnd() == 0);
            lnc.mOffset = 1;
            lnc.mLength = 1;
            assertTrue("lnc.findLineStart()=" + lnc.findIndexLineStart(), lnc.findIndexLineStart() == 1);
            assertTrue("lnc.findLineEnd()=" + lnc.findIndexLineEnd(),lnc.findIndexLineEnd() == 1);
        }
        {
            @Jailbreak LineNumberCalculator lnc = new LineNumberCalculator("ok\nok");
            lnc.mOffset = 0;
            lnc.mLength = 2;
            assertTrue("lnc.findLineStart()=" + lnc.findIndexLineStart(), lnc.findIndexLineStart() == 0);
            assertTrue("lnc.findLineEnd()=" + lnc.findIndexLineEnd(),lnc.findIndexLineEnd() == 2);
        }
    }
}