package io.github.rosemoe.editor.core.extension.plugins.appcompattweaker.extension;

import io.github.rosemoe.editor.core.extension.events.Event;

public class AppCompatTweakerEvent extends Event {

    public final static String ADD_OVERFLOW_ITEM = "add_overflow_item";

    public AppCompatTweakerEvent(String subtype, Object ...args) {
        super(subtype,args);
    }
}
