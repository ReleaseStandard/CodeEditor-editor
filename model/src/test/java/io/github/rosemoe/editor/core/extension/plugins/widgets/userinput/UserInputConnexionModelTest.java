package io.github.rosemoe.editor.core.extension.plugins.widgets.userinput;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserInputConnexionModelTest {

    @Test
    public void invalid() {
        UserInputConnexionModel a = new UserInputConnexionModel();
        a.invalid();
    }

    @Test
    public void reset() {
        UserInputConnexionModel a = new UserInputConnexionModel();
        a.reset();
    }

    @Test
    public void init() {
        UserInputConnexionModel a = new UserInputConnexionModel();
        a.init(0,0,0);
    }
}