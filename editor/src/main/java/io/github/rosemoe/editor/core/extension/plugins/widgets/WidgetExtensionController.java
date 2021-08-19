package io.github.rosemoe.editor.core.extension.plugins.widgets;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

import io.github.rosemoe.editor.core.CodeEditor;
import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.extension.plugins.appcompattweaker.extension.AppCompatTweakerEvent;
import io.github.rosemoe.editor.core.util.Logger;

/**
 * A widget is a subtype of extension.
 * it has facilities for painting on the screen.
 */
public abstract class WidgetExtensionController extends Extension implements Observer {

    public WidgetExtensionView view;

    public CodeEditor editorController;
    public WidgetExtensionController(CodeEditor editor) {
        super(editor.model);
        editorController = editor;
        this.editor.colorManager.attach(this);
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if (view == null) {
            Logger.debug("View is not ready, cannot paint");
            return;
        }

        // part of the view
        editorController.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                view.invalidate();
            }
        });
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
     *
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
     *
     * @return
     */
    public float width() {
        return 0;
    }

    /**
     * An other method to init a widget.
     */
    public void initFromAttrs() {
    }

    /**
     * How to update on observable change.
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        handleUpdate();
        editorController.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
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

    public void addItemToMenu() {
        addItemToMenu(name + " enabled");
    }

    public void addItemToMenu(String title) {
        addItemToMenu(title, Menu.NONE);
    }

    /**
     * Add an enabled check case for this extension.
     */
    public void addItemToMenu(String title, int groupId) {
        emit(new AppCompatTweakerEvent(AppCompatTweakerEvent.ADD_OVERFLOW_ITEM,
                title, groupId, true, isEnabled(), new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                WidgetExtensionController.this.toggleIsEnabled();
                item.setChecked(isEnabled());
                return false;
            }
        }));
    }

    /**
     * Editor is painting this line at this row.
     *
     * @param line
     * @param row
     */
    public void drawRow(int line, int row) {

    }
}