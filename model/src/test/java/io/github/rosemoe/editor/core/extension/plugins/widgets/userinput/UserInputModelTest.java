package io.github.rosemoe.editor.core.extension.plugins.widgets.userinput;

import org.junit.Test;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class UserInputModelTest {

    Random r = new Random();

    @Test
    public void isWhitespace() {
        assertFalse(UserInputModel.isWhitespace('i'));
        assertTrue(UserInputModel.isWhitespace(' '));
    }

    @Test
    public void isSameSign() {
        assertFalse(UserInputModel.isSameSign(0,0));
        assertTrue(UserInputModel.isSameSign(-1,-1));
        assertTrue(UserInputModel.isSameSign(1,1));
        assertFalse(UserInputModel.isSameSign(0,-1));
        assertFalse(UserInputModel.isSameSign(-1,0));
    }

    @Test
    public void shouldDrawScrollBar() {
        UserInputModel model = new UserInputModel();
        int c = 0;
        int max = 20;
        for(int a = 0; a < max; a = a + 1) {
            c += model.shouldDrawScrollBar(r.nextUint()%2==1,r.nextUint()%2==1) ? 1:0;
        }
        assertTrue(c > 0 && c < max);
    }

    @Test
    public void computeEdgeFlags() {
        System.out.println("TODO : computeEdgeFlags");
    }
}
