package io.github.rosemoe.editor.core;

import java.lang.reflect.Field;

import io.github.rosemoe.editor.core.util.Logger;

/**
 * CodeEditorObject : base harness for CodeEditor objects.
 */
public class CEObject {
    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        dump(this, offset);
    }
    public static void dump(Object obj) {
        dump(obj,"");
    }
    public static void dump(Object obj, String offset){
        String res = "hashCode="+obj.hashCode();
        Class c = obj.getClass();
        for(Field f : c.getFields()) {
            f.setAccessible(true);
            Object o = null;
            try {
                o = f.get(obj);
            } catch (IllegalAccessException e) { }
            res += "," + f.getName() + "=" + o;
        }
        Logger.debug(offset + res);
    }
}
