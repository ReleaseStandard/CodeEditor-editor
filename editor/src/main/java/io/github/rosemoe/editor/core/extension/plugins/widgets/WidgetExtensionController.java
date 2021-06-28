package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.graphics.Canvas;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.plugins.SystemExtensionController;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * A widget is a subtype of extension.
 * it has facilities for painting on the screen.
 */
public abstract class WidgetExtensionController extends SystemExtensionController implements Observer {

    public WidgetExtensionView view;
    public WidgetExtensionController(CodeEditor editor) {
        super(editor);
        editor.colorManager.attach(this);
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if( view == null ) {
            Logger.debug("View is not ready, cannot paint");
            return;
        }

        // part of the view
        if ( state ) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        view.invalidate();
    }

    /**
     * Feed the widget with new data, it will modify model and view.
     */
    public final void refresh(Object canvas, Object... args) {
        if (isDisabled()) {
            return;
        }
        handleRefresh(canvas, args);
    }

    protected void handleRefresh(Object canvas, Object... args) {
    }

    /**
     * Method that attach View to the widget.
     * @param v View to attach.
     */
    public abstract void attachView(View v);

    /**
     * Clear data in the model and invalidate the view.
     */
    public void clear() {
    }

    /**
     * Get the width of the widget.
     * @return
     */
    public float width() {
        return 0;
    }

    /**
     * An other method to init a widget.
     */
    public void initFromAttrs(){}

    /**
     * How to update on observable change.
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        handleUpdate();
        editor.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ( view != null ) {
                    handleUpdateUIthread();
                }
            }
        });
    }

    /**
     * Defined for convenience, use this if you plan to modify a View.
     */
    public void handleUpdateUIthread() {

    }

    /**
     * Defined for convenience, use this to make change in the background.
     */
    public void handleUpdate() {

    }
    static int menuId = 1000;
    public void addItemToMenu(Menu menu) {
        addItemToMenu(menu, name + " enabled");
    }
    public void addItemToMenu(Menu menu, String title) {
        addItemToMenu(menu,title,Menu.NONE);
    }
    /**
     * Add an enabled check case for this extension.
     * @param menu
     */
    public void addItemToMenu(Menu menu, String title, int groupId) {
        MenuItem mi = menu.add(groupId, menuId++, Menu.NONE, title);
        mi.setCheckable(true);
        mi.setChecked(isEnabled());
        mi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                WidgetExtensionController.this.toggleIsEnabled();
                mi.setChecked(isEnabled());
                return false;
            }
        });
    }

    /**
     * Editor is painting this line at this row.
     * @param line
     * @param row
     */
    public void drawRow(int line, int row) {

    }
}
