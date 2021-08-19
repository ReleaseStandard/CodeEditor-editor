package io.github.rosemoe.editor.core.extension.extensions.appcompattweaker;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.extension.events.Event;
import io.github.rosemoe.editor.core.extension.extensions.appcompattweaker.extension.AppCompatTweakerEvent;
import io.github.rosemoe.editor.core.util.Logger;

public class AppCompatTweakerController extends Extension {

    public ConcurrentHashMap<Integer, ArrayList<Object>> items = new ConcurrentHashMap<>();

    Menu menu = null;

    public AppCompatTweakerController(CodeEditor editor) {
        super(editor.model);
        subscribe(AppCompatTweakerEvent.class);
        name = "appcompattweaker";
        description = "AppCompatActivity tweaker";
    }
    public void put(ArrayList<Object> value) {
        items.put(items.size(), value);
    }

    static int menuId = 1000;
    public MenuItem addItem(ArrayList<Object> args) {
        MenuItem mi = null;
        if ( menu == null ) {
            put((ArrayList<Object>) args.clone());
        } else {
            String title = (String) args.get(0);
            Integer groupId = (Integer) args.get(1);
            Boolean checkable = false;
            if ( args.size() > 2 ) {
                checkable = (Boolean) args.get(2);
            }
            Boolean checked = false;
            if ( args.size() > 3 ) {
                checked = (Boolean) args.get(3);
            }
            mi = menu.add(groupId, menuId++, Menu.NONE, title);
            mi.setCheckable(checkable);
            mi.setChecked(checked);
            if ( args.size() > 4 ) {
                mi.setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) args.get(4));
            }
        }
        return mi;
    }
    @Override
    public void handleEventDispatch(Event e, String subtype) {
        AppCompatTweakerEvent uie = (AppCompatTweakerEvent) e;
        switch(subtype) {
            case AppCompatTweakerEvent.ADD_OVERFLOW_ITEM:
                String title = (String) e.getArg(0);
                Integer groupId = (Integer) e.getArg(1);
                Logger.debug("Adding title=",title,",groupId=",groupId);
                addItem(e.args);
                break;
        }
    }

    /**
     * Attach Menu to this controller.
     * @param menu
     */
    public void attachMenu(Menu menu) {
        if ( menu == null ) { return; }
        this.menu = menu;
        for(int a = 0 ; a < items.size(); a=a+1) {
            addItem(items.get(a));
        }
        items.clear();
    }
}
