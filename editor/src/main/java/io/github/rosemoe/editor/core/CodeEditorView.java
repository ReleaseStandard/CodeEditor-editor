package io.github.rosemoe.editor.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import io.github.rosemoe.editor.core.extension.plugins.widgets.completion.SymbolPairMatch;
import io.github.rosemoe.editor.core.extension.plugins.widgets.contentAnalyzer.controller.ContentLineController;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.shortcuts.A;

/**
 * android view of CodeEditor.
 */
public class CodeEditorView extends View {
    CodeEditor editor = null;


    public CodeEditorView(Context context) {
        this(context, null);
    }

    public CodeEditorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CodeEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CodeEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public void attachEditor(CodeEditor editor) {
        this.editor = editor;
    }
    public void drawBackground(Canvas canvas) {
        editor.drawColor(canvas, editor.colorManager.getColor("wholeBackground"), A.getRectF(editor.model.background));
    }

    @Override
    public boolean isHorizontalScrollBarEnabled() {
        if ( ! haveEditor() ) { return false; }
        return editor.userInput.scrollBarH.isEnabled();
    }

    @Override
    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        if ( ! haveEditor() ) { return; }
        editor.userInput.scrollBarH.setEnabled(horizontalScrollBarEnabled);

    }

    @Override
    public boolean isVerticalScrollBarEnabled() {
        if ( ! haveEditor() ) { return false; }
        return editor.userInput.scrollBarV.isEnabled();
    }

    @Override
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if ( ! haveEditor() ) { return; }
        editor.userInput.scrollBarV.setEnabled(verticalScrollBarEnabled);
    }
    
    //-------------------------------------------------------------------------------
    //-------------------------Override methods--------------------------------------
    //-------------------------------------------------------------------------------

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ( ! haveEditor() ) { return; }
        editor.drawView(canvas);
    }

    @Override
    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        AccessibilityNodeInfo node = super.createAccessibilityNodeInfo();
        node.setEditable(editor.isEditable());
        node.setTextSelection(editor.cursor.getLeft(), editor.cursor.getRight());
        node.setScrollable(true);
        node.setInputType(InputType.TYPE_CLASS_TEXT);
        node.setMultiLine(true);
        node.setText(editor.getText().toStringBuilder());
        node.setLongClickable(true);
        node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COPY);
        node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CUT);
        node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_PASTE);
        node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT);
        node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
        node.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        return node;
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        switch (action) {
            case AccessibilityNodeInfo.ACTION_COPY:
                editor.copyText();
                return true;
            case AccessibilityNodeInfo.ACTION_CUT:
                editor.cutText();
                return true;
            case AccessibilityNodeInfo.ACTION_PASTE:
                editor.pasteText();
                return true;
            case AccessibilityNodeInfo.ACTION_SET_TEXT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    editor.setText(arguments.getCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE));
                    return true;
                }
                return false;
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD:
                editor.movePageDown();
                return true;
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD:
                editor.movePageUp();
                return true;
        }
        return super.performAccessibilityAction(action, arguments);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return isEnabled() && editor.isEditable();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (!editor.isEditable() || !isEnabled()) {
            return null;
        }
        outAttrs.inputType = editor.mInputType != 0 ? editor.mInputType : EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE;
        outAttrs.initialSelStart = editor.getCursor() != null ? editor.getCursor().getLeft() : 0;
        outAttrs.initialSelEnd = editor.getCursor() != null ? editor.getCursor().getRight() : 0;
        outAttrs.initialCapsMode = editor.mConnection.view.getCursorCapsMode(0);
        // Prevent fullscreen when the screen height is too small
        // Especially in landscape mode
        if(!editor.isFullscreenAllowed()) {
            outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_FLAG_NO_FULLSCREEN;
        }
        editor.mConnection.reset();
        editor.setExtracting(null);
        return editor.mConnection.view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Logger.debug("On touch event");
        if (!isEnabled()) {
            Logger.debug("Touches are not enabled");
            return false;
        }
        boolean handlingBefore = editor.userInput.handlingMotions();
        boolean res = editor.userInput.onTouchEvent(event);
        boolean handling = editor.userInput.handlingMotions();
        boolean res2 = false;
        boolean res3 = false;
        if (!handling && !handlingBefore) {
            res2 = editor.userInput.view.gestureDetector.onTouchEvent(event);
            res3 = editor.userInput.view.scaleDetector.onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            editor.mVerticalEdgeGlow.onRelease();
            editor.mHorizontalGlow.onRelease();
        }
        return (res3 || res2 || res);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        editor.mKeyMetaStates.onKeyDown(event);
        boolean isShiftPressed = editor.mKeyMetaStates.isShiftPressed();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MOVE_HOME:
            case KeyEvent.KEYCODE_MOVE_END:
                if (isShiftPressed && (!editor.cursor.isSelected())) {
                    editor.mLockedSelection = editor.cursor.left();
                } else if(!isShiftPressed && editor.mLockedSelection != null) {
                    editor.mLockedSelection = null;
                }
                editor.mKeyMetaStates.adjust();
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:{
                return editor.mTextActionPresenter != null && editor.mTextActionPresenter.onExit();
            }
            case KeyEvent.KEYCODE_DEL:
                if (editor.isEditable()) {
                    editor.cursor.onDeleteKeyPressed();
                    editor.notifyExternalCursorChange();
                }
                return true;
            case KeyEvent.KEYCODE_FORWARD_DEL: {
                if (editor.isEditable()) {
                    editor.mConnection.view.deleteSurroundingText(0, 1);
                    editor.notifyExternalCursorChange();
                }
                return true;
            }
            case KeyEvent.KEYCODE_ENTER: {
                if (editor.isEditable()) {
                    if (editor.completionWindow.view.isShowing()) {
                        editor.completionWindow.select();
                        return true;
                    }
                    NewlineHandler[] handlers = editor.mLanguage.getNewlineHandlers();
                    if (handlers == null || editor.getCursor().isSelected()) {
                        editor.cursor.onCommitText("\n", true);
                    } else {
                        ContentLineController line = editor.mText.getLine(editor.cursor.getLeftLine());
                        int index = editor.cursor.getLeftColumn();
                        String beforeText = line.subSequence(0, index).toString();
                        String afterText = line.subSequence(index, line.length()).toString();
                        boolean consumed = false;
                        for (NewlineHandler handler : handlers) {
                            if (handler != null) {
                                if (handler.matchesRequirement(beforeText, afterText)) {
                                    try {
                                        NewlineHandler.HandleResult result = handler.handleNewline(beforeText, afterText, editor.getTabWidth());
                                        if (result != null) {
                                            editor.cursor.onCommitText(result.text, false);
                                            int delta = result.shiftLeft;
                                            if (delta != 0) {
                                                int newSel = Math.max(editor.getCursor().getLeft() - delta, 0);
                                                CharPosition charPosition = editor.getCursor().getIndexer().getCharPosition(newSel);
                                                editor.setSelection(charPosition.line, charPosition.column);
                                            }
                                            consumed = true;
                                        } else {
                                            continue;
                                        }
                                    } catch (Exception e) {
                                        Log.w(Logger.LOG_TAG, "Error occurred while calling Language's NewlineHandler", e);
                                    }
                                    break;
                                }
                            }
                        }
                        if (!consumed) {
                            editor.cursor.onCommitText("\n", true);
                        }
                    }
                    editor.notifyExternalCursorChange();
                }
                return true;
            }
            case KeyEvent.KEYCODE_DPAD_DOWN:
                editor.moveSelectionDown();
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                editor.moveSelectionUp();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                editor.moveSelectionLeft();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                editor.moveSelectionRight();
                return true;
            case KeyEvent.KEYCODE_MOVE_END:
                editor.moveSelectionEnd();
                return true;
            case KeyEvent.KEYCODE_MOVE_HOME:
                editor.moveSelectionHome();
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                editor.movePageDown();
                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                editor.movePageUp();
                return true;
            case KeyEvent.KEYCODE_TAB:
                if (editor.isEditable()) {
                    editor.commitTab();
                }
                return true;
            case KeyEvent.KEYCODE_PASTE:
                if (editor.isEditable()) {
                    editor.pasteText();
                }
                return true;
            case KeyEvent.KEYCODE_COPY:
                editor.copyText();
                return true;
            case KeyEvent.KEYCODE_SPACE:
                if (editor.isEditable()) {
                    editor.getCursor().onCommitText(" ");
                    editor.notifyExternalCursorChange();
                }
                return true;
            default:
                if (event.isCtrlPressed() && !event.isAltPressed()) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_V:
                            if (editor.isEditable()) {
                                editor.pasteText();
                            }
                            return true;
                        case KeyEvent.KEYCODE_C:
                            editor.copyText();
                            return true;
                        case KeyEvent.KEYCODE_X:
                            if (editor.isEditable()) {
                                editor.cutText();
                            } else {
                                editor.copyText();
                            }
                            return true;
                        case KeyEvent.KEYCODE_A:
                            editor.selectAll();
                            return true;
                        case KeyEvent.KEYCODE_Z:
                            if (editor.isEditable()) {
                                editor.undo();
                            }
                            return true;
                        case KeyEvent.KEYCODE_Y:
                            if (editor.isEditable()) {
                                editor.redo();
                            }
                            return true;
                    }
                } else if (!event.isCtrlPressed() && !event.isAltPressed()) {
                    if (event.isPrintingKey() && editor.isEditable()) {
                        String text = new String(Character.toChars(event.getUnicodeChar(event.getMetaState())));
                        SymbolPairMatch.Replacement replacement = null;
                        if (text.length() == 1 && editor.isSymbolCompletionEnabled()) {
                            replacement = editor.mLanguageSymbolPairs.getCompletion(text.charAt(0));
                        }
                        if (replacement == null || replacement == SymbolPairMatch.Replacement.NO_REPLACEMENT) {
                            editor.getCursor().onCommitText(text);
                            editor.notifyExternalCursorChange();
                        } else {
                            editor.getCursor().onCommitText(replacement.text);
                            int delta = (replacement.text.length() - replacement.selection);
                            if (delta != 0) {
                                int newSel = Math.max(editor.getCursor().getLeft() - delta, 0);
                                CharPosition charPosition = editor.getCursor().getIndexer().getCharPosition(newSel);
                                editor.setSelection(charPosition.line, charPosition.column);
                                editor.notifyExternalCursorChange();
                            }
                        }
                    } else {
                        return super.onKeyDown(keyCode, event);
                    }
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        editor.mKeyMetaStates.onKeyUp(event);
        if (!editor.mKeyMetaStates.isShiftPressed() && editor.mLockedSelection != null && !editor.cursor.isSelected()) {
            editor.mLockedSelection = null;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean warn = false;
        //Fill the horizontal layout if WRAP_CONTENT mode
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
            warn = true;
        }
        //Fill the vertical layout if WRAP_CONTENT mode
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST || MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
            warn = true;
        }
        if (warn) {
            Log.i(Logger.LOG_TAG, "onMeasure():Code editor does not support wrap_content mode when measuring.It will just fill the whole space.");
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_SCROLL) {
            float v_scroll = -event.getAxisValue(MotionEvent.AXIS_VSCROLL);
            if (v_scroll != 0) {
                editor.userInput.view.onScroll(event, event, 0, v_scroll * 20);
            }
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    public boolean haveEditor() {
        return editor != null;
    }
    @Override
    public void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        if ( ! haveEditor() ) { return; }
        editor.model.background.right = w;
        editor.model.background.bottom = h;
        editor.getVerticalEdgeEffect().setSize(w, h);
        editor.getHorizontalEdgeEffect().setSize(h, w);
        editor.getVerticalEdgeEffect().finish();
        editor.getHorizontalEdgeEffect().finish();
        if (editor.isWordwrap() && w != oldWidth) {
            editor.mLayout.createLayout(editor);
        } else {
            editor.userInput.view.scrollBy(editor.getOffsetX() > editor.getScrollMaxX() ? editor.getScrollMaxX() - editor.getOffsetX() : 0, editor.getOffsetY() > editor.getScrollMaxY() ? editor.getScrollMaxY() - editor.getOffsetY() : 0);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if ( ! haveEditor() ) { return; }
        editor.cursor.blink.model.valid = editor.cursor.blink.model.period > 0;
        if (editor.cursor.blink.model.valid) {
            post(editor.cursor.blink);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if ( ! haveEditor() ) { return; }
        editor.cursor.blink.model.valid = false;
        removeCallbacks(editor.cursor.blink);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if ( ! haveEditor() ) { return; }
        if (editor.userInput.view.getScroller().computeScrollOffset()) {
            invalidate();
        }
    }
}
