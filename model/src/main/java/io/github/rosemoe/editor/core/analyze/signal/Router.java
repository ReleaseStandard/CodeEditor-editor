package io.github.rosemoe.editor.core.analyze.signal;

/**
 * An object that implements Router process an entry signal.
 * e.g. Keyboard, Mouse and or produce the result directly or forward the signal to a subclass.
 */
public interface Router {
    /**
     * Route the signal through this node.
     * signal could be accepted, rejected, forwarded
     * @param action requested action
     * @param args arguments of the action
     * @return true if a route has been found else false
     */
    boolean route(Routes action, Object ...args);
}
