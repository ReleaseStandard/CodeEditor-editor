/*
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.core;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.OverScroller;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.rosemoe.editor.R;
import io.github.rosemoe.editor.core.extension.Extension;
import io.github.rosemoe.editor.core.analyzer.analyzer.CodeAnalyzer;
import io.github.rosemoe.editor.core.analyzer.results.AnalysisDoneCallback;
import io.github.rosemoe.editor.core.extension.extensions.appcompattweaker.AppCompatTweakerController;
import io.github.rosemoe.editor.core.color.spans.Span;
import io.github.rosemoe.editor.core.color.spans.SpanLine;
import io.github.rosemoe.editor.core.color.spans.SpanMap;
import io.github.rosemoe.editor.core.extension.extensions.widgets.WidgetExtensionController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.WidgetExtensionView;
import io.github.rosemoe.editor.core.analyzer.result.CodeAnalyzerResultColor;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.IdentifierAutoCompleteController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.IdentifierAutoCompleteModel;
import io.github.rosemoe.editor.core.extension.extensions.widgets.contentAnalyzer.controller.ContentMap;
import io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.CursorModel;
import io.github.rosemoe.editor.core.extension.extensions.widgets.layout.controller.AbstractLayout;
import io.github.rosemoe.editor.core.extension.extensions.loopback.LoopbackController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.widgetmanager.controller.WidgetControllerManagerController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.userinput.view.UserInputConnexionView;
import io.github.rosemoe.editor.core.extension.extensions.langs.LanguagePlugin;
import io.github.rosemoe.editor.core.util.Logger;
import io.github.rosemoe.editor.core.util.shortcuts.A;
import io.github.rosemoe.editor.plugins.debug.TestPlugin;
import io.github.rosemoe.editor.plugins.debug.ExtensionsAnalyzerPlugin;
import io.github.rosemoe.editor.plugins.debug.ExamplePlugin;
import io.github.rosemoe.editor.core.extension.extensions.widgets.userinput.controller.UserInputConnexionController;
import io.github.rosemoe.editor.core.extension.extensions.colorChange.ColorSchemeExtension;
import io.github.rosemoe.editor.core.extension.extensions.widgets.layout.controller.RowController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.userinput.controller.UserInputController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.CompletionWindowController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.SymbolPairMatch;
import io.github.rosemoe.editor.core.extension.extensions.widgets.layout.controller.RowIterator;
import io.github.rosemoe.editor.core.extension.extensions.widgets.searcher.controller.SearcherController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.contextaction.controller.ContextActionController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.cursor.controller.CursorController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.CompletionAdapter;
import io.github.rosemoe.editor.core.langs.empty.EmptyLanguage;
import io.github.rosemoe.editor.core.extension.extensions.widgets.contentAnalyzer.controller.ContentLineController;
import io.github.rosemoe.editor.core.extension.extensions.widgets.contentAnalyzer.controller.ContentListener;
import io.github.rosemoe.editor.core.extension.extensions.widgets.userinput.view.UserInputView;
import io.github.rosemoe.editor.core.util.FontCache;
import io.github.rosemoe.editor.core.extension.extensions.widgets.contentAnalyzer.processors.ContentLineRemoveListener;

import static io.github.rosemoe.editor.core.extension.extensions.langs.helpers.TextUtils.isEmoji;

/**
 * CodeEditor is a editor that can highlight text regions by doing basic syntax analyzing
 * This project in GitHub: https://github.com/Rosemoe/CodeEditor
 * <p>
 * Note:
 * RowModel and line are different in this editor
 * When we say 'row', it means a line displayed on screen. It can be a part of a line in the text object.
 * When we say 'line', it means a real line in the original text.
 *
 * @author Rosemoe
 */
public class CodeEditor implements ContentListener, TextFormatter.FormatResultReceiver, ContentLineRemoveListener, AnalysisDoneCallback {

    /**
     * Draw whitespace characters before line content start
     * <strong>Whitespace here only means space and tab</strong>
     *
     * @see #setNonPrintablePaintingFlags(int)
     */
    public static final int FLAG_DRAW_WHITESPACE_LEADING = 1;

    /**
     * Draw whitespace characters inside line content
     * <strong>Whitespace here only means space and tab</strong>
     *
     * @see #setNonPrintablePaintingFlags(int)
     */
    public static final int FLAG_DRAW_WHITESPACE_INNER = 1 << 1;

    /**
     * Draw whitespace characters after line content end
     * <strong>Whitespace here only means space and tab</strong>
     *
     * @see #setNonPrintablePaintingFlags(int)
     */
    public static final int FLAG_DRAW_WHITESPACE_TRAILING = 1 << 2;

    /**
     * Draw whitespace characters even if it is a line full of whitespaces
     * To apply this, you must enable {@link #FLAG_DRAW_WHITESPACE_LEADING}
     * <strong>Whitespace here only means space and tab</strong>
     *
     * @see #setNonPrintablePaintingFlags(int)
     */
    public static final int FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE = 1 << 3;

    /**
     * Draw newline signals in text
     *
     * @see #setNonPrintablePaintingFlags(int)
     */
    public static final int FLAG_DRAW_LINE_SEPARATOR = 1 << 4;

    /**
     * Text size scale of small graph
     */
    private static final float SCALE_MINI_GRAPH = 0.9f;

    /**
     * Internal state identifiers of action mode
     */
    public static final int ACTION_MODE_NONE = 0;
    static final int ACTION_MODE_SEARCH_TEXT = 1;
    public static final int ACTION_MODE_SELECT_TEXT = 2;
    public SymbolPairMatch mLanguageSymbolPairs;
    public AbstractLayout mLayout;
    public int mStartedActionMode;
    private int cursorPosition;
    protected int mInputType;
    private int mNonPrintableOptions;
    public float mDpUnit;
    private float mBlockLineWidth;
    private float mLineInfoTextSize;
    private boolean mWait;
    private boolean mDrag;
    private boolean mScalable;
    private boolean mEditable;
    private boolean mCharPaint;
    public boolean mWordwrap;
    private boolean mUndoEnabled;
    private boolean mBlockLineEnabled;
    private boolean mAutoCompletionEnabled;
    private boolean mCompletionOnComposing;
    private boolean mHighlightSelectedText;
    private boolean mHighlightCurrentBlock;
    private boolean mHighlightCurrentLine;
    private boolean mSymbolCompletionEnabled;
    private boolean mAllowFullscreen;
    private ClipboardManager mClipboardManager;
    private InputMethodManager mInputMethodManager;
    public ContentMap mText;
    public CodeAnalyzer analyzer;

    public CodeEditorView view;
    public CodeEditorModel model = new CodeEditorModel();

    UserInputConnexionController mConnection;             // Manage other part of the user input, eg copy, paste
    // core
    public LanguagePlugin mLanguage;

    // widgets
    public CursorController cursor;                                        // Manage the cursor
    private SearcherController searcher;                                    // Manage search in the displayed text
    private ContextActionController contextAction;                          // Manage context action showing, eg copy paste
    public CompletionWindowController completionWindow;                     // Manage completion item showing
    public  UserInputController userInput;                                  // Manage all user input, eg scale scrolling

    public Paint mPaint;
    private Paint miniGraphPaint;
    public char[] mBuffer;
    private Matrix mMatrix;
    private String mLnTip = "Line:";
    private long mLastMakeVisible = 0;

    public EditorTextActionPresenter mTextActionPresenter;
    private CursorAnchorInfo.Builder mAnchorInfoBuilder;
    protected MaterialEdgeEffect mVerticalEdgeGlow;
    protected MaterialEdgeEffect mHorizontalGlow;
    private ExtractedTextRequest mExtracting;
    private TextFormatter mFormatThread;
    private EditorEventListener mListener;
    private FontCache mFontCache;
    private Paint.FontMetricsInt mTextMetrics;
    private Paint.FontMetricsInt mGraphMetrics;

    private SymbolPairMatch mOverrideSymbolPairs;
    protected CharPosition mLockedSelection;
    public KeyMetaStates mKeyMetaStates = new KeyMetaStates(this);


    public AppCompatActivity activity = null;

    public void attachMenu(Menu menu) {
        for(Extension e : model.plugins) {
            if ( e instanceof AppCompatTweakerController) {
                ((AppCompatTweakerController) e).attachMenu(menu);
            }
        }
    }
    /**
     * Create a new instance of CodeEditor that will be find into the given root.
     * It will dynamically instanciate widgets as they are found in the layout file.
     * @param activity main activity.
     * @param root id of the root.
     * @return
     */
    public static CodeEditor newInstance(AppCompatActivity activity, @IdRes int root ) {
        View rootView = activity.findViewById(root);
        CodeEditor editor = new CodeEditor();
        editor.activity = activity;
        recurse(editor, rootView, false);
        editor.initialize();
        recurse(editor, rootView, true);
        return editor;
    }

    public static void recurse(CodeEditor editor, View rootView) { recurse(editor, rootView, true); }

    /**
     * Get views from the given root.
     * @param rootView
     * @return
     */
    public static void recurse(CodeEditor editor, View rootView, Boolean attachWidgets) {
        if( rootView instanceof ViewGroup ) {
            for(int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                recurse(editor, ((ViewGroup) rootView).getChildAt(i),attachWidgets);
            }
        }
        if ( attachWidgets ) {
            if ( rootView instanceof WidgetExtensionView) {
                WidgetExtensionView wec = (WidgetExtensionView) rootView;
                try {
                    Class c = Class.forName(wec.controllerName);
                    try {
                        Constructor<?> constructor = c.getConstructor(CodeEditor.class);
                        try {
                            WidgetExtensionController wwec = (WidgetExtensionController) constructor.newInstance(editor);
                            wwec.attachView(rootView);
                            editor.model.plugins.put(wwec);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (rootView instanceof CodeEditorView) {
                editor.view = (CodeEditorView) rootView;
            }
        }
    }
    public CodeEditor() {

    }
    public CodeEditor(CodeEditorView view) {
        this.view = view;
        initialize();
        //TypedArray ta = view.getContext().obtainStyledAttributes(attrs,R.styleable.CodeEditor);
        //getColorScheme().initFromAttributeSets(attrs,ta);
        //initFromAttributeSet(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * This modify the editor behaviour based on which attributes the user has defined in the xml file.
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    private void initFromAttributeSet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        /*TypedArray ta = view.getContext().obtainStyledAttributes(attrs,R.styleable.CodeEditor);
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
        } catch (ClassNotFoundException e) {
            Logger.v("WARNING : jackson is not avaliable : you must add it from the main application, styled attributes will not work");
            Logger.v("ex:     implementation group: 'com.fasterxml.jackson.core', name: 'jackson-core', version: '2.12.3'\n" +
                                "    implementation group: 'com.fasterxml.jackson.core', name: 'jackson-databind', version: '2.12.3'");
        }
        //String config = ta.getString(R.styleable.CodeEditor_config);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(view.getContext().getAssets().open("config.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append(line).append('\n');
        }
        String config = stringBuilder.toString();
        if ( config != null ) {
            Logger.debug("config=",config);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = null;
            try {
                rootNode = mapper.readTree(config);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }

            // configure extensions (widgets, plugins)
                JsonNode extensions = rootNode.get("extensions");
                if ( extensions != null ) {
                    for(ExtensionContainer ec : new ExtensionContainer[]{model.plugins, model.systemPlugins}) {
                        for (Extension e : ec.extensions) {
                            Logger.debug("Trying extension : ",e.name);
                            JsonNode extension = extensions.get(e.name);
                            if (extension == null) {
                                continue;
                            }
                            Logger.debug(" \\\\=> configuring extension");
                            // configure an extension
                            e.configure(extension);
                        }
                    }
                }

                // configure the core
                JsonNode core = rootNode.get("core");
                if ( core != null ) {
                    JsonNode tabWidth = core.get("tabWidth");
                    if ( tabWidth != null ) {
                        cursor.model.mTabWidth = tabWidth.asInt();
                    }
                    JsonNode textSz = core.get("textSize");
                    if ( textSz != null ) {
                        setTextSize((float) textSz.asDouble());
                    }
                }
        }


        for ( int id : new int[] {
                R.styleable.CodeEditor_widget_cursor_blink_enabled,
                R.styleable.CodeEditor_widget_completion_enabled, R.styleable.CodeEditor_lineNumberVisible, R.styleable.CodeEditor_scrollbarsEnabled, R.styleable.CodeEditor_undoEnabled, R.styleable.CodeEditor_scalable, R.styleable.CodeEditor_focusable, R.styleable.CodeEditor_focusableInTouchMode, R.styleable.CodeEditor_hightlightCurrentLine, R.styleable.CodeEditor_autoindentEnabled,
                R.styleable.CodeEditor_verticalScrollbarEnabled, R.styleable.CodeEditor_hightLightCurrentBlock, R.styleable.CodeEditor_highlightSelectedText, R.styleable.CodeEditor_displayLnPanel, R.styleable.CodeEditor_overScrollEnabled, R.styleable.CodeEditor_horizontalScrollBarEnabled, R.styleable.CodeEditor_symbolCompletionEnabled, R.styleable.CodeEditor_editable, R.styleable.CodeEditor_lineNumberEnabled, R.styleable.CodeEditor_autoCompletionOnComposing, R.styleable.CodeEditor_horizontalScrollbarEnabled,
        }) {
            boolean trash = true;
            boolean val = ta.getBoolean(id,trash);
            if ( id == R.styleable.CodeEditor_widget_completion_enabled) {
                setAutoCompletionEnabled(val);
            } else if ( id == R.styleable.CodeEditor_scrollbarsEnabled ) {
                setScrollBarEnabled(val);
            } else if ( id == R.styleable.CodeEditor_undoEnabled ) {
                setUndoEnabled(val);
            } else if ( id == R.styleable.CodeEditor_scalable ) {
                setScalable(val);
            } else if ( id == R.styleable.CodeEditor_focusable ) {
                setFocusable(val);
            } else if ( id == R.styleable.CodeEditor_focusableInTouchMode ) {
                setFocusableInTouchMode(val);
            } else if ( id == R.styleable.CodeEditor_hightlightCurrentLine ) {
                setHighlightCurrentLine(val);
            } else if ( id == R.styleable.CodeEditor_autoindentEnabled ) {
                cursor.setAutoIndent(val);
            } else if ( id == R.styleable.CodeEditor_verticalScrollbarEnabled ) {
                setVerticalScrollBarEnabled(val);
            } else if ( id == R.styleable.CodeEditor_hightLightCurrentBlock ) {
                setHighlightCurrentBlock(val);
            } else if ( id == R.styleable.CodeEditor_highlightSelectedText ) {
                setHighlightSelectedText(val);
            } else if ( id == R.styleable.CodeEditor_displayLnPanel ) {
                //lineNumber.setEnabled(val);
            } else if ( id == R.styleable.CodeEditor_overScrollEnabled ) {
                setOverScrollEnabled(val);
            } else if ( id == R.styleable.CodeEditor_horizontalScrollBarEnabled ) {
                setHorizontalScrollBarEnabled(val);
            } else if ( id == R.styleable.CodeEditor_symbolCompletionEnabled ) {
                setSymbolCompletionEnabled(val);
            } else if ( id == R.styleable.CodeEditor_editable ) {
                setEditable(val);
            } else if ( id == R.styleable.CodeEditor_lineNumberEnabled ) {
                //setLineNumberEnabled(val);
            } else if ( id == R.styleable.CodeEditor_autoCompletionOnComposing ) {
                setAutoCompletionOnComposing(val);
            } else if ( id == R.styleable.CodeEditor_lineNumberVisible ) {
                //setLineNumberEnabled(val);
            } else if ( id == R.styleable.CodeEditor_widget_cursor_blink_enabled ) {
                cursor.setBlinkEnabled(val);
            }
            TODO
        }*/
    }

    /**
     * Whether this region has visible region on screen
     *
     * @param begin The begin line of code block
     * @param end   The end line of code block
     * @param first The first visible line on screen
     * @param last  The last visible line on screen
     * @return Whether this block can be seen
     */
    private static boolean hasVisibleRegion(int begin, int end, int first, int last) {
        return (end > first && begin < last);
    }

    /**
     * Set event listener
     *
     * @see EditorEventListener
     */
    public void setEventListener(@Nullable EditorEventListener eventListener) {
        this.mListener = eventListener;
    }

    /**
     * Cancel the next animation for {@link CodeEditor#ensurePositionVisible(int, int)}
     */
    public void cancelAnimation() {
        mLastMakeVisible = System.currentTimeMillis();
    }

    /**
     * Hide completion window later
     */
    public void postHideCompletionWindow() {
        // Avoid meaningless calls
        if (!completionWindow.view.isShowing()) {
            return;
        }
        // We do this because if you hide it at once, the editor seems to flash with unknown reason
        // TODO postDelayed(() -> completionWindow.view.hide(), 50);
    }

    /**
     * Get using CompletionWindowController
     */
    @NonNull
    public CompletionWindowController getAutoCompleteWindow() {
        return completionWindow;
    }

    /**
     * Get EditorTextActionPresenter instance of this editor
     *
     * @return EditorTextActionPresenter
     */
    @NonNull
    public EditorTextActionPresenter getTextActionPresenter() {
        return mTextActionPresenter;
    }

    /**
     * Send current selection position to input method
     */
    public void updateSelection() {
        int candidatesStart = -1, candidatesEnd = -1;
        if (mConnection.model.composingLine != -1) {
            try {
                candidatesStart = mText.getCharIndex(mConnection.model.composingLine, mConnection.model.composingStart);
                candidatesEnd = mText.getCharIndex(mConnection.model.composingLine, mConnection.model.composingEnd);
            } catch (IndexOutOfBoundsException e) {
                //Ignored
            }
        }
        //Logs.log("Send selection changes: candidate(start = " + candidatesStart + ", end =" + candidatesEnd + " ) cursor(left = " + cursor.getLeft() + ", right =" + cursor.getRight() + ")");
        mInputMethodManager.updateSelection(view, cursor.getLeft(), cursor.getRight(), candidatesStart, candidatesEnd);
    }

    /**
     * Update request result for monitoring request
     */
    public void updateExtractedText() {
        if (mExtracting != null) {
            //Logs.log("Send extracted text updates");
            mInputMethodManager.updateExtractedText(view, mExtracting.token, extractText(mExtracting));
        }
    }

    /**
     * Notify input method that text has been changed for external reason
     */
    public void notifyExternalCursorChange() {
        //Logs.log("Call cursorChangeExternal()");//
        updateExtractedText();
        updateSelection();
        updateCursorAnchor();
        // Restart if composing
        if (mConnection.model.composingLine != -1) {
            restartInput();
        }
    }

    public void restartInput() {
        mConnection.invalid();
        mInputMethodManager.restartInput(view);
    }



    /**
     * Get the rect of left selection handle painted on view
     *
     * @return Rect of left handle
     */
    public RectF getLeftHandleRect() {
        return A.getRectF(cursor.model.mLeftHandle);
    }

    /**
     * Get the rect of right selection handle painted on view
     *
     * @return Rect of right handle
     */
    public RectF getRightHandleRect() {
        return A.getRectF(cursor.model.mRightHandle);
    }

    /**
     * Get the character's x offset on view
     *
     * @param line   The line position of character
     * @param column The column position of character
     * @return The x offset on screen
     */
    public float getOffset(int line, int column) {
        prepareLine(line);
        float res = measureText(mBuffer, 0, column) - getOffsetX();
        return res;
    }

    /**
     * Prepare editor
     * Initialize variants
     */
    private void initialize() {
        view.editor = this;
        mFontCache = new FontCache();
        mPaint = new Paint();
        miniGraphPaint = new Paint();
        mMatrix = new Matrix();
        searcher = new SearcherController(this);
        mAnchorInfoBuilder = new CursorAnchorInfo.Builder();
        mPaint.setAntiAlias(true);
        miniGraphPaint.setAntiAlias(true);
        mBuffer = new char[256];
        mStartedActionMode = ACTION_MODE_NONE;
        model.plugins.put(
                new ExamplePlugin(this),
                new ExtensionsAnalyzerPlugin(model),
                new TestPlugin(this)
        );
        model.background = new io.github.rosemoe.editor.core.Rect(0,0,0,0);
        mInputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mClipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cursorPosition = -1;
        mVerticalEdgeGlow = new MaterialEdgeEffect();
        mHorizontalGlow   = new MaterialEdgeEffect();
        mOverrideSymbolPairs = new SymbolPairMatch();
        setUndoEnabled(true);
        setScalable(true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        setEditorLanguage(new EmptyLanguage(model));
        setHighlightCurrentLine(true);
        setAutoCompletionEnabled(true);
        setHighlightCurrentBlock(true);
        setHighlightSelectedText(true);
        setSymbolCompletionEnabled(true);
        setEditable(true);
        setAutoCompletionOnComposing(true);
        setAllowFullscreen(false);
        setTypefaceText(Typeface.DEFAULT);
        userInput         = new UserInputController(this,view.getContext());
        mConnection       = new UserInputConnexionController(this);
        model.plugins.put(new ColorSchemeExtension(model));
        completionWindow  = new CompletionWindowController(this);
        model.plugins.put(
                userInput,
                new LoopbackController(model),
                new WidgetControllerManagerController(this),
                new AppCompatTweakerController(this)
        );
        mDpUnit = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, Resources.getSystem().getDisplayMetrics()) / 2;
        mDrag = false;
        mWait = false;
        mBlockLineEnabled = true;
        mBlockLineWidth = mDpUnit / 1.5f;
        setText(null);
        setTextSize(CodeEditorModel.DEFAULT_TEXT_SIZE);
        setLineInfoTextSize(mPaint.getTextSize());
        setTextActionMode(TextActionMode.POPUP_WINDOW_2);
        // Issue #41 View being highlighted when focused on Android 11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.setDefaultFocusHighlightEnabled(false);
        }
        userInput.scrollBarV.updateWidth();
        userInput.scrollBarH.updateWidth();
    }

    /**
     * Define symbol pairs for any language,
     * Override language settings.
     * <p>
     * Use {@link SymbolPairMatch.Replacement#NO_REPLACEMENT} to force no completion for a character
     */
    @NonNull
    public SymbolPairMatch getOverrideSymbolPairs() {
        return mOverrideSymbolPairs;
    }

    /**
     * @see CodeEditor#setSymbolCompletionEnabled(boolean)
     */
    public boolean isSymbolCompletionEnabled() {
        return mSymbolCompletionEnabled;
    }

    /**
     * Control whether auto-completes for symbol pairs
     */
    public void setSymbolCompletionEnabled(boolean symbolCompletionEnabled) {
        mSymbolCompletionEnabled = symbolCompletionEnabled;
    }
    /**
     * Whether over scroll is permitted.
     * When over scroll is enabled, the user will be able to scroll out of displaying
     * bounds if the user scroll fast enough.
     * This is implemented by {@link OverScroller#fling(int, int, int, int, int, int, int, int, int, int)}
     */
    public void setOverScrollEnabled(boolean overScrollEnabled) {
        userInput.setOverScrollEnabled(overScrollEnabled);
    }

    /**
     * Set adapter for auto-completion window
     * Will take effect next time the window updates
     *
     * @param adapter New adapter, maybe null
     */
    public void setAutoCompletionItemAdapter(@Nullable CompletionAdapter adapter) {
        completionWindow.view.setAdapter(adapter);
    }
    
    /**
     * Set to {@code false} if you don't want the editor to go fullscreen on devices with smaller screen size.
     * Otherwise, set to {@code true}
     *
     * Default value is {@code false}
     *
     * @param fullscreen Enable or disable fullscreen
     */
    public void setAllowFullscreen(boolean fullscreen) {
        this.mAllowFullscreen = fullscreen;
    }
    
    /**
     * Is the editor allowed to go fullscreen?
     *
     * @return {@code true} if fullscreen is allowed or {@code false} if it isn't
     */
    public boolean isFullscreenAllowed() {
        return mAllowFullscreen;
    }

    /**
     * Returns whether highlight current code block
     *
     * @return This module enabled / disabled
     * @see CodeEditor#setHighlightCurrentBlock(boolean)
     */
    public boolean isHighlightCurrentBlock() {
        return mHighlightCurrentBlock;
    }

    /**
     * Whether the editor should use a different color to draw
     * the current code block line and this code block's start line and end line's
     * background.
     *
     * @param highlightCurrentBlock Enabled / Disabled this module
     */
    public void setHighlightCurrentBlock(boolean highlightCurrentBlock) {
        this.mHighlightCurrentBlock = highlightCurrentBlock;
        if (!mHighlightCurrentBlock) {
            cursorPosition = -1;
        } else {
            cursorPosition = findCursorBlock();
        }
        view.invalidate();
    }

    /**
     * Whether the editor should use a different color to draw
     * the background of current line
     */
    public void setHighlightCurrentLine(boolean highlightCurrentLine) {
        mHighlightCurrentLine = highlightCurrentLine;
        view.invalidate();
    }

    /**
     * @see CodeEditor#setHighlightCurrentLine(boolean)
     */
    public boolean isHighlightCurrentLine() {
        return mHighlightCurrentLine;
    }

    /**
     * Set the editor's language.
     * A language is a for auto completion,highlight and auto indent analysis.
     *
     * @param lang New LanguagePlugin for editor
     */
    public void setEditorLanguage(@NonNull LanguagePlugin lang) {
        this.mLanguage = lang;

        // Update spanner
        if (analyzer != null) {
            analyzer.shutdown();
            analyzer.setCallback(null);
        }
        analyzer = (CodeAnalyzer) lang.analyzer;
        CodeAnalyzerResultColor result = ((CodeAnalyzerResultColor)analyzer.getResult("color"));
        if ( result != null ) {
            result.theme = getColorScheme();
        }
        this.analyzer.setCallback(this);
        if (mText != null) {
            analyzer.analyze(mText);
        }
        if (completionWindow != null) {
            completionWindow.view.hide();
            IdentifierAutoCompleteController autoComplete = new IdentifierAutoCompleteController(analyzer);
            autoComplete.model = (IdentifierAutoCompleteModel) lang.getAutoCompleteProvider();
            completionWindow.setProvider(autoComplete);
        }

        // Symbol pairs
        if (mLanguageSymbolPairs != null) {
            mLanguageSymbolPairs.setParent(null);
        }
        mLanguageSymbolPairs = mLanguage.getSymbolPairs();
        if (mLanguageSymbolPairs == null) {
            Log.w(Logger.LOG_TAG, "Language(" + mLanguage.toString() + ") returned null for symbol pairs. It is a mistake.");
            mLanguageSymbolPairs = new SymbolPairMatch();
        }
        mLanguageSymbolPairs.setParent(mOverrideSymbolPairs);

        // CursorController
        if (cursor != null) {
            cursor.setLanguage(mLanguage);
        }
        view.invalidate();
    }

    /**
     * Getter
     *
     * @return The width in dp unit
     * @see CodeEditor#setBlockLineWidth(float)
     */
    public float getBlockLineWidth() {
        return mBlockLineWidth;
    }

    /**
     * Set the width of code block line
     *
     * @param dp Width in dp unit
     */
    public void setBlockLineWidth(float dp) {
        mBlockLineWidth = dp;
        view.invalidate();
    }

    /**
     * @see #setWordwrap(boolean)
     */
    public boolean isWordwrap() {
        return mWordwrap;
    }

    /**
     * Set whether text in editor should be wrapped to fit its size
     *
     * @param wordwrap Whether to wrap words
     */
    public void setWordwrap(boolean wordwrap) {
        if (mWordwrap != wordwrap) {
            mWordwrap = wordwrap;
            mLayout.createLayout(this);
            view.invalidate();
        }
    }

    /**
     * Whether display vertical scroll bar when scrolling
     *
     * @param enabled Enabled / disabled
     */
    public void setScrollBarEnabled(boolean enabled) {
        userInput.setScrollBarEnabled(enabled);
        view.invalidate();
    }

    /**
     * Set how will the editor present text actions
     */
    public void setTextActionMode(TextActionMode mode) {
        if (cursor.isSelected()) {
            setSelection(cursor.getLeftLine(), cursor.getLeftColumn());
        }
        if (mode == TextActionMode.ACTION_MODE) {
            mTextActionPresenter = new EditorTextActionModeStarter(this);
        } else if (mode == TextActionMode.POPUP_WINDOW_2) {
            mTextActionPresenter = new TextActionPopupWindow(this);
        } else {
            contextAction = new ContextActionController(this);
            mTextActionPresenter = contextAction.view;
        }
    }

    /**
     * @return The prefix
     * @see CodeEditor#setLnTip(String)
     */
    public String getLnTip() {
        return mLnTip;
    }

    /**
     * Set the tip text before line number for the line number panel
     *
     * @param prefix The prefix for text
     */
    public void setLnTip(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        mLnTip = prefix;
        view.invalidate();
    }


    /**
     * Get the rect of insert cursor handle on view
     *
     * @return Rect of insert handle
     */
    public RectF getInsertHandleRect() {
        return A.getRectF(cursor.model.mInsertHandle);
    }

    /**
     * Get text size in pixel unit
     *
     * @return Text size in pixel unit
     * @see CodeEditor#setTextSize(float)
     * @see CodeEditor#setTextSizePx(float)
     */
    @Px
    public float getTextSizePx() {
        return mPaint.getTextSize();
    }

    /**
     * Set text size in pixel unit
     *
     * @param size Text size in pixel unit
     */
    public void setTextSizePx(@Px float size) {
        setTextSizePxDirect(size);
        mLayout.createLayout(this);
        view.invalidate();
    }

    /**
     * Set text size directly without creating layout or invalidating view
     *
     * @param size Text size in pixel unit
     */
    public void setTextSizePxDirect(float size) {
        mPaint.setTextSize(size);
        miniGraphPaint.setTextSize(size * SCALE_MINI_GRAPH);
        mTextMetrics = mPaint.getFontMetricsInt();
        mGraphMetrics = miniGraphPaint.getFontMetricsInt();
        mFontCache.clearCache();
    }

    /**
     * Paint the view on given Canvas
     *
     * @param canvas Canvas you want to draw
     */
    protected void drawView(Canvas canvas) {
        //record();
        //counter = 0;
        analyzer.recycle();

        // TODO : missing part
        if (mFormatThread != null) {
            String text = "Formatting your code...";
            float centerY = view.getHeight() / 2f;
            // TODO : repair text formatting
            WidgetExtensionView.drawColor(canvas, model.colorManager.getColor("lineNumberPanel"), io.github.rosemoe.editor.core.Rect.DEFAULT());
            float baseline = centerY - getRowHeight() / 2f + getRowBaseline(0);
            float centerX = view.getWidth() / 2f;
            mPaint.setColor(model.colorManager.getColor("lineNumberPanelText"));
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, centerX, baseline, mPaint);
            mPaint.setTextAlign(Paint.Align.LEFT);
            return;
        }

        getCursor().updateCache(getFirstVisibleLine());

        // draw the background
        view.drawBackground(canvas);

        float offsetX = -getOffsetX();
        float textOffset = offsetX;

        // update from the model
        for(Extension e : model.plugins) {
            if ( e instanceof WidgetExtensionController ) {
                WidgetExtensionController sec = (WidgetExtensionController) e;
                sec.clear();
            }
        }

        // WARNING: data processing here: instanciation controllers
        drawRows(canvas, textOffset);

        offsetX = -getOffsetX();


        if (!isWordwrap() && isBlockLineEnabled()) {
            drawBlockLines(canvas, textOffset);
        }

        // painting of widgets
        //lineNumber.refresh(canvas, offsetX, getLineCount());
        userInput.refresh(canvas);

        drawEdgeEffect(canvas);
    }


    /**
     * Clear flag in flags
     * The flag must be power of two
     *
     * @param flags Flags to filter
     * @param flag  The flag to clear
     * @return Cleared flags
     */
    private int clearFlag(int flags, int flag) {
        return (flags & flag) != 0 ? flags ^ flag : flags;
    }

    /**
     * Whether non-printable is to be drawn
     */
    private boolean shouldInitializeNonPrintable() {
        return clearFlag(mNonPrintableOptions, FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE) != 0;
    }

    /**
     * Draw rows with a {@link RowIterator}
     *
     * @param canvas              Canvas to draw
     * @param offset              Offset of text region start
     */
    private void drawRows(Canvas canvas, float offset) {

        RowIterator rowIterator = mLayout.obtainRowIterator(getFirstVisibleRow());
        CodeAnalyzerResultColor colRes = (CodeAnalyzerResultColor) analyzer.getResult("color");
        if ( colRes == null ) {
            Logger.debug("spanmap is not ready");
            analyzer.dump();

            return;
        }
        SpanMap spanMap = analyzer.getSpanMap();
        List<Integer> matchedPositions = new ArrayList<>();
        int currentLine = cursor.isSelected() ? -1 : cursor.getLeftLine();
        int currentLineBgColor = model.colorManager.getColor("currentLine");
        int lastPreparedLine = -1;
        int leadingWhitespaceEnd = 0;
        int trailingWhitespaceStart = 0;
        float circleRadius = 0f;

        if (shouldInitializeNonPrintable()) {
            float spaceWidth = mFontCache.measureChar(' ', mPaint);
            float maxD = Math.min(getRowHeight(), spaceWidth);
            maxD *= 0.18f;
            circleRadius = maxD / 2;
        }
        for (int row = getFirstVisibleRow(); row <= getLastVisibleRow() && rowIterator.hasNext(); row++) {
            RowController rowInf = rowIterator.next();
            int line = rowInf.model.lineIndex;
            ContentLineController contentLine = mText.getLine(line);
            int columnCount = contentLine.length();
            if (rowInf.model.isLeadingRow) {
                for(Extension e : model.plugins) {
                    if ( e instanceof WidgetExtensionController) {
                        ((WidgetExtensionController) e).drawRow(line, row);
                    }
                }
            }

            // Prepare data
            if (lastPreparedLine != line) {
                lastPreparedLine = line;
                prepareLine(line);
                computeMatchedPositions(line, matchedPositions);
                if (shouldInitializeNonPrintable()) {
                    long positions = findLeadingAndTrailingWhitespacePos(line);
                    leadingWhitespaceEnd = IntPair.getFirst(positions);
                    trailingWhitespaceStart = IntPair.getSecond(positions);
                }
            }

            // Get visible region on line
            float[] charPos = findFirstVisibleChar(offset, rowInf.model.startColumn, rowInf.model.endColumn, mBuffer);
            int firstVisibleChar = (int) charPos[0];
            int lastVisibleChar = firstVisibleChar;
            float paintingOffset = charPos[1];
            float temporaryOffset = paintingOffset;
            while (temporaryOffset < view.getWidth() && lastVisibleChar < columnCount) {
                char ch = mBuffer[lastVisibleChar];
                if (isEmoji(ch) && lastVisibleChar + 1 < columnCount) {
                    temporaryOffset += mFontCache.measureText(mBuffer, lastVisibleChar, lastVisibleChar + 2, mPaint);
                    lastVisibleChar++;
                } else {
                    temporaryOffset += mFontCache.measureChar(mBuffer[lastVisibleChar], mPaint);
                }
                lastVisibleChar++;
            }
            lastVisibleChar = Math.min(lastVisibleChar, rowInf.model.endColumn);

            // Draw matched text background
            if (!matchedPositions.isEmpty()) {
                for (int position : matchedPositions) {
                    drawRowRegionBackground(canvas, paintingOffset, row, firstVisibleChar, lastVisibleChar, position, position + searcher.model.searchText.length(), model.colorManager.getColor("textSelectedBackground"));
                }
            }

            float backupOffset = paintingOffset;

            // Draw selected text background
            if (cursor.isSelected() && line >= cursor.getLeftLine() && line <= cursor.getRightLine()) {
                int selectionStart = 0;
                int selectionEnd = columnCount;
                if (line == cursor.getLeftLine()) {
                    selectionStart = cursor.getLeftColumn();
                }
                if (line == cursor.getRightLine()) {
                    selectionEnd = cursor.getRightColumn();
                }
                drawRowRegionBackground(canvas, paintingOffset, row, firstVisibleChar, lastVisibleChar, selectionStart, selectionEnd, model.colorManager.getColor("textSelectedBackground"));
            }

            // Draw current line background
            if (line == currentLine) {
                drawRowBackground(canvas, currentLineBgColor, row);
            }

            // Draw text here
            // the result of the color analyzer
            {
                // Get spans
                SpanLine spans = spanMap.get(line);
                if (spans == null || spans.size() == 0) {
                    spans = SpanLine.EMPTY();
                }
                Map.Entry<Integer, Span> [] keys = spans.entrySet().toArray(new Map.Entry[spans.size()]);
                for (int a = 0; a < keys.length; a=a+1) {
                    Span span = keys[a].getValue();
                    // Draw by spans
                    Span nextSpan = null;
                    if ( a+1 < spans.size() ) {
                        nextSpan = keys[a+1].getValue();
                    }
                    int spanStart = span.column;
                    int spanEnd = nextSpan == null ? columnCount : nextSpan.column;
                    int paintStart = Math.max(firstVisibleChar, spanStart);
                    int paintEnd = Math.min(lastVisibleChar, spanEnd);
                    if( spanStart > lastVisibleChar || spanEnd < firstVisibleChar ) { continue ; }
                    float width = measureText(mBuffer, paintStart, paintEnd - paintStart);

                    //Logger.debug("line=",line,",firstVisibleChar=",firstVisibleChar,",lastVisibleChar=",lastVisibleChar,",color=",span.model.color,",paintStart=",paintStart,",paintEnd=",paintEnd,",paintingOffset=",paintingOffset);

                    // Draw text
                    drawRegionText(canvas, paintingOffset, getRowBaseline(row) - getOffsetY(), line, paintStart, paintEnd, columnCount, span.color);
                    //drawRegionText(canvas,paintingOffset, getRowBaseline(row) - getOffsetY(),line,paintStart,paintEnd,columnCount,0xFF00FF00);

                    // Draw underline
                    if (span.underlineColor != 0) {
                        RectF mRect = new RectF();
                        mRect.bottom = getRowBottom(line) - getOffsetY() - mDpUnit * 1;
                        mRect.top = mRect.bottom - getRowHeight() * 0.08f;
                        mRect.left = paintingOffset;
                        mRect.right = paintingOffset + width;
                        drawColor(canvas, span.underlineColor, mRect);
                    }

                    paintingOffset += width;

                    if (paintEnd == lastVisibleChar) {
                        break;
                    }
                }
            }

            // Draw hard wrap
            if (lastVisibleChar == columnCount && (mNonPrintableOptions & FLAG_DRAW_LINE_SEPARATOR) != 0) {
                drawSmallCharacter(canvas, paintingOffset, row, "↵");
            }

            // Recover the offset
            paintingOffset = backupOffset;

            // Draw non-printable characters
            if (circleRadius != 0f && (leadingWhitespaceEnd != columnCount || (mNonPrintableOptions & FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE) != 0)) {
                if ((mNonPrintableOptions & FLAG_DRAW_WHITESPACE_LEADING) != 0) {
                    drawWhitespaces(canvas, paintingOffset, row, firstVisibleChar, lastVisibleChar, 0, leadingWhitespaceEnd, circleRadius);
                }
                if ((mNonPrintableOptions & FLAG_DRAW_WHITESPACE_INNER) != 0) {
                    drawWhitespaces(canvas, paintingOffset, row, firstVisibleChar, lastVisibleChar, leadingWhitespaceEnd, trailingWhitespaceStart, circleRadius);
                }
                if ((mNonPrintableOptions & FLAG_DRAW_WHITESPACE_TRAILING) != 0) {
                    drawWhitespaces(canvas, paintingOffset, row, firstVisibleChar, lastVisibleChar, trailingWhitespaceStart, columnCount, circleRadius);
                }
            }

            // Draw composing text underline
            if (line == mConnection.model.composingLine) {
                int composingStart = mConnection.model.composingStart;
                int composingEnd = mConnection.model.composingEnd;
                int paintStart = Math.min(Math.max(composingStart, firstVisibleChar), lastVisibleChar);
                int paintEnd = Math.min(Math.max(composingEnd, firstVisibleChar), lastVisibleChar);
                if (paintStart != paintEnd) {
                    RectF mRect = new RectF();
                    mRect.top = getRowBottom(row) - getOffsetY();
                    mRect.bottom = mRect.top + getRowHeight() * 0.06f;
                    mRect.left = paintingOffset + measureText(mBuffer, firstVisibleChar, paintStart - firstVisibleChar);
                    mRect.right = mRect.left + measureText(mBuffer, paintStart, paintEnd - paintStart);
                    drawColor(canvas, model.colorManager.getColor("underline"), mRect);
                }
            }

            // Draw cursors
            cursor.refresh(canvas,firstVisibleChar,lastVisibleChar,line,paintingOffset,row);

        }

    }

    public void showTextActionPopup() {
        if (mTextActionPresenter instanceof TextActionPopupWindow) {
            TextActionPopupWindow window = (TextActionPopupWindow) mTextActionPresenter;
            if (window.isShowing()) {
                //window.hide(TextActionPopupWindow.DISMISS);
            } else {
                window.onBeginTextSelect();
                window.onTextSelectionEnd();
            }
        }
    }

    /**
     * Draw small characters as graph
     */
    private void drawSmallCharacter(Canvas canvas, float offset, int row, String graph) {
        // Draw
        //miniGraphPaint.setColor(model.colorManager.getColor("nonPrintableChar"));
        float baseline = getRowBottom(row) - getOffsetY() - mGraphMetrics.descent;
        canvas.drawText(graph, 0, graph.length(), offset, baseline, miniGraphPaint);
    }

    Paint paintWhitespaces = new Paint();

    /**
     * Draw non-printable characters
     */
    private void drawWhitespaces(Canvas canvas, float offset, int row, int rowStart, int rowEnd, int min, int max, float circleRadius) {
        int paintStart = Math.max(rowStart, Math.min(rowEnd, min));
        int paintEnd = Math.max(rowStart, Math.min(rowEnd, max));

        paintWhitespaces.setColor(model.colorManager.getColor("nonPrintableChar"));
        paintWhitespaces.setTypeface(Typeface.MONOSPACE);
        paintWhitespaces.setAntiAlias(true);

        if (paintStart < paintEnd) {
            float spaceWidth = mFontCache.measureChar(' ', mPaint);
            float rowCenter = (getRowTop(row) + getRowBottom(row)) / 2f - getOffsetY();
            offset += measureText(mBuffer, rowStart, paintStart - rowStart);
            while (paintStart < paintEnd) {
                char ch = mBuffer[paintStart];
                float charWidth = measureText(mBuffer, paintStart, 1);
                int paintCount = 0;
                if (ch == ' ') {
                    paintCount = 1;
                } else if (ch == '\t') {
                    paintCount = getTabWidth();
                }
                for (int i = 0; i < paintCount; i++) {
                    float charStartOffset = offset + spaceWidth * i;
                    float charEndOffset = charStartOffset + spaceWidth;
                    float centerOffset = (charStartOffset + charEndOffset) / 2f;
                    canvas.drawCircle(centerOffset, rowCenter, circleRadius, paintWhitespaces);
                }
                offset += charWidth;
                paintStart++;
            }
        }
    }

    /**
     * As the name is, we find where leading spaces end and trailing spaces start
     * Before calling this, the line should be prepared
     *
     * @param line The line to search
     */
    private long findLeadingAndTrailingWhitespacePos(int line) {
        int column = mText.getColumnCount(line);
        int leading = 0;
        int trailing = column;
        while (leading < column && isWhitespace(mBuffer[leading])) {
            leading++;
        }
        // Only them this action is needed
        if (leading != column && (mNonPrintableOptions & (FLAG_DRAW_WHITESPACE_INNER | FLAG_DRAW_WHITESPACE_TRAILING)) != 0) {
            while (trailing > 0 && isWhitespace(mBuffer[trailing - 1])) {
                trailing--;
            }
        }
        return IntPair.pack(leading, trailing);
    }

    /**
     * A quick method to predicate whitespace character
     */
    private boolean isWhitespace(char ch) {
        return ch == '\t' || ch == ' ';
    }

    /**
     * Get matched text regions on line
     *
     * @param line      Target line
     * @param positions Outputs start positions
     */
    private void computeMatchedPositions(int line, List<Integer> positions) {
        positions.clear();
        CharSequence pattern = searcher.model.searchText;
        if (pattern == null || pattern.length() == 0) {
            return;
        }
        ContentLineController seq = mText.getLine(line);
        int index = 0;
        while (index != -1) {
            index = seq.indexOf(pattern, index);
            if (index != -1) {
                positions.add(index);
                index += pattern.length();
            }
        }
    }

    /**
     * Draw background of a text region
     *
     * @param canvas         Canvas to draw
     * @param paintingOffset Paint offset x on canvas
     * @param row            The row index
     * @param firstVis       First visible character
     * @param lastVis        Last visible character
     * @param highlightStart Region start
     * @param highlightEnd   Region end
     * @param color          Color of background
     */
    private void drawRowRegionBackground(Canvas canvas, float paintingOffset, int row, int firstVis, int lastVis, int highlightStart, int highlightEnd, int color) {
        int paintStart = Math.min(Math.max(firstVis, highlightStart), lastVis);
        int paintEnd = Math.min(Math.max(firstVis, highlightEnd), lastVis);
        if (paintStart != paintEnd) {
            RectF mRect = new RectF();
            mRect.top = getRowTop(row) - getOffsetY();
            mRect.bottom = getRowBottom(row) - getOffsetY();
            mRect.left = paintingOffset + measureText(mBuffer, firstVis, paintStart - firstVis);
            mRect.right = mRect.left + measureText(mBuffer, paintStart, paintEnd - paintStart);
            drawColor(canvas, color, mRect);
        }
    }

    /**
     * Draw text region with highlighting selected text
     *
     * @param canvas      Canvas to draw
     * @param offsetX     Start paint offset x on canvas
     * @param baseline    Baseline on canvas
     * @param line        Drawing line index
     * @param startIndex  Start index to paint
     * @param endIndex    End index to paint
     * @param columnCount Column count of line
     * @param color       Color of normal text in this region
     */
    private void drawRegionText(Canvas canvas, float offsetX, float baseline, int line, int startIndex, int endIndex, int columnCount, int color) {
        boolean hasSelectionOnLine = cursor.isSelected() && line >= cursor.getLeftLine() && line <= cursor.getRightLine();
        int selectionStart = 0;
        int selectionEnd = columnCount;
        if (line == cursor.getLeftLine()) {
            selectionStart = cursor.getLeftColumn();
        }
        if (line == cursor.getRightLine()) {
            selectionEnd = cursor.getRightColumn();
        }
        mPaint.setColor(color);
        if (hasSelectionOnLine) {
            if (endIndex <= selectionStart || startIndex >= selectionEnd) {
                drawText(canvas, mBuffer, startIndex, endIndex - startIndex, offsetX, baseline);
            } else {
                if (startIndex <= selectionStart) {
                    if (endIndex >= selectionEnd) {
                        //Three regions
                        //startIndex - selectionStart
                        drawText(canvas, mBuffer, startIndex, selectionStart - startIndex, offsetX, baseline);
                        float deltaX = measureText(mBuffer, startIndex, selectionStart - startIndex);
                        //selectionStart - selectionEnd
                        mPaint.setColor(model.colorManager.getColor("textSelected"));
                        drawText(canvas, mBuffer, selectionStart, selectionEnd - selectionStart, offsetX + deltaX, baseline);
                        deltaX += measureText(mBuffer, selectionStart, selectionEnd - selectionStart);
                        //selectionEnd - endIndex
                        mPaint.setColor(color);
                        drawText(canvas, mBuffer, selectionEnd, endIndex - selectionEnd, offsetX + deltaX, baseline);
                    } else {
                        //Two regions
                        //startIndex - selectionStart
                        drawText(canvas, mBuffer, startIndex, selectionStart - startIndex, offsetX, baseline);
                        //selectionStart - endIndex
                        mPaint.setColor(model.colorManager.getColor("textSelected"));
                        drawText(canvas, mBuffer, selectionStart, endIndex - selectionStart, offsetX + measureText(mBuffer, startIndex, selectionStart - startIndex), baseline);
                    }
                } else {
                    //selectionEnd > startIndex > selectionStart
                    if (endIndex > selectionEnd) {
                        //Two regions
                        //selectionEnd - endIndex
                        drawText(canvas, mBuffer, selectionEnd, endIndex - selectionEnd, offsetX + measureText(mBuffer, startIndex, selectionEnd - startIndex), baseline);
                        //startIndex - selectionEnd
                        mPaint.setColor(model.colorManager.getColor("textSelected"));
                        drawText(canvas, mBuffer, startIndex, selectionEnd - startIndex, offsetX, baseline);
                    } else {
                        //One region
                        mPaint.setColor(model.colorManager.getColor("textSelected"));
                        drawText(canvas, mBuffer, startIndex, endIndex - startIndex, offsetX, baseline);
                    }
                }
            }
        } else {
            drawText(canvas, mBuffer, startIndex, endIndex - startIndex, offsetX, baseline);
        }
    }

    /**
     * Draw effect of edges
     *
     * @param canvas The canvas to draw
     */
    private void drawEdgeEffect(Canvas canvas) {
        boolean postDraw = false;
        if (!mVerticalEdgeGlow.isFinished()) {
            boolean bottom = userInput.view.topOrBottom;
            if (bottom) {
                canvas.save();
                canvas.translate(-view.getMeasuredWidth(), view.getMeasuredHeight());
                canvas.rotate(180, view.getMeasuredWidth(), 0);
            }
            postDraw = mVerticalEdgeGlow.draw(canvas);
            if (bottom) {
                canvas.restore();
            }
        }
        if (isWordwrap()) {
            mHorizontalGlow.finish();
        }
        if (!mHorizontalGlow.isFinished()) {
            canvas.save();
            boolean right = userInput.view.leftOrRight;
            if (right) {
                canvas.rotate(90);
                canvas.translate(0, -view.getMeasuredWidth());
            } else {
                canvas.translate(0, view.getMeasuredHeight());
                canvas.rotate(-90);
            }
            postDraw = mHorizontalGlow.draw(canvas) || postDraw;
            canvas.restore();
        }
        OverScroller scroller = getScroller();
        if (scroller.isOverScrolled()) {
            if (mVerticalEdgeGlow.isFinished() && (scroller.getCurrY() < 0 || scroller.getCurrY() >= getScrollMaxY())) {
                userInput.view.topOrBottom = scroller.getCurrY() >= getScrollMaxY();
                mVerticalEdgeGlow.onAbsorb((int) scroller.getCurrVelocity());
                postDraw = true;
            }
            if (mHorizontalGlow.isFinished() && (scroller.getCurrX() < 0 || scroller.getCurrX() >= getScrollMaxX())) {
                userInput.view.leftOrRight = scroller.getCurrX() >= getScrollMaxX();
                mHorizontalGlow.onAbsorb((int) scroller.getCurrVelocity());
                postDraw = true;
            }
        }
        if (postDraw) {
            view.postInvalidate();
        }
    }

    /**
     * Draw code block lines on screen
     *
     * @param canvas  The canvas to draw
     * @param offsetX The start x offset for text
     */
    private void drawBlockLines(Canvas canvas, float offsetX) {
        List<BlockLineModel> blocks = analyzer == null ? null : analyzer.getContent();
        if (blocks == null || blocks.isEmpty()) {
            return;
        }
        int first = getFirstVisibleRow();
        int last = getLastVisibleRow();
        boolean mark = false;
        int invalidCount = 0;
        int maxCount = Integer.MAX_VALUE;
        if (analyzer != null) {
            maxCount = analyzer.mSuppressSwitch;
        }
        int mm = binarySearchEndBlock(first, blocks);
        int cursorIdx = cursorPosition;
        for (int curr = mm; curr < blocks.size(); curr++) {
            BlockLineModel block = blocks.get(curr);
            if (hasVisibleRegion(block.startLine, block.endLine, first, last)) {
                try {
                    CharSequence lineContent = mText.getLine(block.endLine);
                    float offset1 = measureText(lineContent, 0, block.endColumn);
                    lineContent = mText.getLine(block.startLine);
                    float offset2 = measureText(lineContent, 0, block.startColumn);
                    float offset = Math.min(offset1, offset2);
                    float centerX = offset + offsetX;
                    RectF mRect = new RectF();
                    mRect.top = Math.max(0, getRowBottom(block.startLine) - getOffsetY());
                    mRect.bottom = Math.min(view.getHeight(), getRowTop(block.endLine) - getOffsetY());
                    mRect.left = centerX - mDpUnit * mBlockLineWidth / 2;
                    mRect.right = centerX + mDpUnit * mBlockLineWidth / 2;

                    drawColor(canvas, curr == cursorIdx ? model.colorManager.getColor("blockLineCurrent") : model.colorManager.getColor("blockLine"), mRect);
                } catch (IndexOutOfBoundsException e) {
                    //Ignored
                    //Because the exception usually occurs when the content changed.
                }
                mark = true;
            } else if (mark) {
                if (invalidCount >= maxCount)
                    break;
                invalidCount++;
            }
        }
    }

    /**
     * Get the color of EdgeEffect
     *
     * @return The color of EdgeEffect.
     */
    public int getEdgeEffectColor() {
        return mVerticalEdgeGlow.getColor();
    }

    /**
     * Set the color of EdgeEffect
     *
     * @param color The color of EdgeEffect
     */
    public void setEdgeEffectColor(int color) {
        mVerticalEdgeGlow.setColor(color);
        mHorizontalGlow.setColor(color);
    }

    /**
     * Get EdgeEffect for vertical direction
     *
     * @return EdgeEffect
     */
    public MaterialEdgeEffect getVerticalEdgeEffect() {
        return mVerticalEdgeGlow;
    }

    /**
     * Get EdgeEffect for horizontal direction
     *
     * @return EdgeEffect
     */
    public MaterialEdgeEffect getHorizontalEdgeEffect() {
        return mHorizontalGlow;
    }

    /**
     * Find the smallest code block that cursor is in
     *
     * @return The smallest code block index.
     * If cursor is not in any code block,just -1.
     */
    private int findCursorBlock() {
        List<BlockLineModel> blocks = analyzer == null ? null : analyzer.getContent();
        if (blocks == null || blocks.isEmpty()) {
            return -1;
        }
        return findCursorBlock(blocks);
    }

    /**
     * Find the cursor code block internal
     *
     * @param blocks Current code blocks
     * @return The smallest code block index.
     * If cursor is not in any code block,just -1.
     */
    private int findCursorBlock(List<BlockLineModel> blocks) {
        int line = cursor.getLeftLine();
        int min = binarySearchEndBlock(line, blocks);
        int max = blocks.size() - 1;
        int minDis = Integer.MAX_VALUE;
        int found = -1;
        int invalidCount = 0;
        int maxCount = Integer.MAX_VALUE;
        if (analyzer != null) {
            maxCount = analyzer.mSuppressSwitch;
        }
        for (int i = min; i <= max; i++) {
            BlockLineModel block = blocks.get(i);
            if (block.endLine >= line && block.startLine <= line) {
                int dis = block.endLine - block.startLine;
                if (dis < minDis) {
                    minDis = dis;
                    found = i;
                }
            } else if (minDis != Integer.MAX_VALUE) {
                invalidCount++;
                if (invalidCount >= maxCount) {
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Find the first code block that maybe seen on screen
     * Because the code blocks is sorted by its end line position
     * we can use binary search to quicken this process in order to decrease
     * the time we use on finding
     *
     * @param firstVis The first visible line
     * @param blocks   Current code blocks
     * @return The block we found.Always a valid index(Unless there is no block)
     */
    private int binarySearchEndBlock(int firstVis, List<BlockLineModel> blocks) {
        //end > firstVis
        int left = 0, right = blocks.size() - 1, mid, row;
        int max = right;
        while (left <= right) {
            mid = (left + right) / 2;
            if (mid < 0) return 0;
            if (mid > max) return max;
            row = blocks.get(mid).endLine;
            if (row > firstVis) {
                right = mid - 1;
            } else if (row < firstVis) {
                left = mid + 1;
            } else {
                left = mid;
                break;
            }
        }
        return Math.max(0, Math.min(left, max));
    }

    /**
     * Measure text width with editor's text paint
     *
     * @param src   Source characters array
     * @param index Start index in array
     * @param count Count of characters
     * @return The width measured
     */
    public float measureText(char[] src, int index, int count) {
        int tabCount = 0;
        for (int i = 0; i < count; i++) {
            if (src[index + i] == '\t') {
                tabCount++;
            }
        }
        float extraWidth = mFontCache.measureChar(' ', mPaint) * getTabWidth() - mFontCache.measureChar('\t', mPaint);
        return mFontCache.measureText(src, index, index + count, mPaint) + tabCount * extraWidth;
    }

    /**
     * Measure text width with editor's text paint
     *
     * @param text  Source string
     * @param index Start index in array
     * @param count Count of characters
     * @return The width measured
     */
    private float measureText(CharSequence text, int index, int count) {
        int tabCount = 0;
        for (int i = 0; i < count; i++) {
            if (text.charAt(index + i) == '\t') {
                tabCount++;
            }
        }
        float extraWidth = mFontCache.measureChar(' ', mPaint) * getTabWidth() - mFontCache.measureChar('\t', mPaint);
        return mFontCache.measureText(text, index, index + count, mPaint) + tabCount * extraWidth;
    }

    //private int counter;

    /**
     * Draw text on the given position
     *
     * @param canvas Canvas to draw
     * @param src    Source of characters
     * @param index  The index in array
     * @param count  Count of characters
     * @param offX   Offset x for paint
     * @param offY   Offset y for paint(baseline)
     */
    private void drawText(Canvas canvas, char[] src, int index, int count, float offX, float offY) {
        //counter++;
        int end = index + count;
        int st = index;
        if (mCharPaint) {
            for (int i = index; i < end; i++) {
                if (src[i] == '\t') {
                    canvas.drawText(src, st, i - st, offX, offY, mPaint);
                    offX = offX + measureText(src, st, i - st + 1);
                    st = i + 1;
                } else if (src[i] == '/') {
                    canvas.drawText(src, st, i - st + 1, offX, offY, mPaint);
                    offX = offX + measureText(src, st, i - st + 1);
                    st = i + 1;
                }
            }
        } else {
            for (int i = index; i < end; i++) {
                if (src[i] == '\t') {
                    canvas.drawText(src, st, i - st, offX, offY, mPaint);
                    offX = offX + measureText(src, st, i - st + 1);
                    st = i + 1;
                }
            }
        }
        if (st < end) {
            canvas.drawText(src, st, end - st, offX, offY, mPaint);
        }
    }

    /**
     * Find first visible character
     */
    private float[] findFirstVisibleChar(float initialPosition, int left, int right, char[] chars) {
        float width = 0f;
        float target = mFontCache.measureChar(' ', mPaint) * getTabWidth() * 1.1f;
        while (left < right && initialPosition + width < -target) {
            float single = mFontCache.measureChar(chars[left], mPaint);
            if (chars[left] == '\t') {
                single = mFontCache.measureChar(' ', mPaint) * getTabWidth();
            }
            width += single;
            left++;
        }
        return new float[]{left, initialPosition + width};
    }

    /**
     * Read out characters to mChars for the given line
     *
     * @param line Line going to draw or measure
     */
    private void prepareLine(int line) {
        int length = mText.getColumnCount(line);
        if (length >= mBuffer.length) {
            mBuffer = new char[length + 100];
        }
        mText.getLineChars(line, mBuffer);
    }

    /**
     * Draw background for row
     */
    private void drawRowBackground(Canvas canvas, int color, int row) {
        RectF mRect = new RectF();
        mRect.top = getRowTop(row) - getOffsetY();
        mRect.bottom = getRowBottom(row) - getOffsetY();
        mRect.left = 0;
        mRect.right = model.background.right;
        drawColor(canvas, color, mRect);
    }

    /**
     * Draw rect on screen
     * Will not do any thing if color is zero
     *
     * @param canvas Canvas to draw
     * @param color  Color of rect
     * @param rect   Rect to draw
     */
    // TODO COPY FROM WIDGET VIEW
    public void drawColor(Canvas canvas, int color, RectF rect) {
        if (color != 0) {
            mPaint.setColor(color);
            canvas.drawRect(rect, mPaint);
        }
    }

    /**
     * Draw rect on screen
     * Will not do any thing if color is zero
     *
     * @param canvas Canvas to draw
     * @param color  Color of rect
     * @param rect   Rect to draw
     */
    private void drawColor(Canvas canvas, int color, Rect rect) {
        if (color != 0) {
            mPaint.setColor(color);
            canvas.drawRect(rect, mPaint);
        }
    }

    /**
     * Commit a tab to cursor
     */
    protected void commitTab() {
        if (mConnection != null && isEditable()) {
            mConnection.view.commitTextInternal("\t", true);
        }
    }


    /**
     * Whether span map is valid
     */
    private boolean isSpanMapPrepared(boolean insert, int delta) {

        SpanMap map = analyzer.getSpanMap();
        boolean rv = false;
        if (map != null) {
            if (insert) {
                rv=(map.size() == getLineCount() - delta);
            } else {
                rv=(map.size() == getLineCount() + delta);
            }
        }

        return rv;
    }

    /**
     * Exit select mode after text changed
     */
    private void exitSelectModeIfNeeded() {
        if (!cursor.isSelected()) {
            mTextActionPresenter.onExit();
        }
    }

    /**
     * Apply new position of auto completion window
     */
    private void updateCompletionWindowPosition() {
        float panelX = updateCursorAnchor() + mDpUnit * 20;
        float[] rightLayoutOffset = mLayout.getCharLayoutOffset(cursor.getRightLine(), cursor.getRightColumn());
        float panelY = rightLayoutOffset[0] - getOffsetY() + getRowHeight() / 2f;
        float restY = view.getHeight() - panelY;
        if (restY > mDpUnit * 200) {
            restY = mDpUnit * 200;
        } else if (restY < mDpUnit * 100) {
            float offset = 0;
            while (restY < mDpUnit * 100) {
                restY += getRowHeight();
                panelY -= getRowHeight();
                offset += getRowHeight();
            }
            getScroller().startScroll(getOffsetX(), getOffsetY(), 0, (int) offset, 0);
        }
        completionWindow.updatePosition(panelX,panelY,restY,view.getWidth(),mDpUnit);
    }

    /**
     * Update the information of cursor
     * Such as the position of cursor on screen(For input method that can go to any position on screen like PC input method)
     *
     * @return The offset x of right cursor on view
     */
    public float updateCursorAnchor() {
        CursorAnchorInfo.Builder builder = mAnchorInfoBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.reset();
            mMatrix.set(view.getMatrix());
            int[] b = new int[2];
            view.getLocationOnScreen(b);
            mMatrix.postTranslate(b[0], b[1]);
            builder.setMatrix(mMatrix);
            builder.setSelectionRange(cursor.getLeft(), cursor.getRight());
        }
        int l = cursor.getRightLine();
        int column = cursor.getRightColumn();
        prepareLine(l);
        boolean visible = true;
        float x = 0;
        x = x + mLayout.getCharLayoutOffset(l, column)[1];
        x = x - getOffsetX();
        if (x < 0) {
            visible = false;
            x = 0;
        }
        builder.setInsertionMarkerLocation(x, getRowTop(l) - getOffsetY(), getRowBaseline(l) - getOffsetY(), getRowBottom(l) - getOffsetY(), visible ? CursorAnchorInfo.FLAG_HAS_VISIBLE_REGION : CursorAnchorInfo.FLAG_HAS_INVISIBLE_REGION);
        mInputMethodManager.updateCursorAnchorInfo(view, builder.build());
        return x;
    }

    /**
     * @see #setLineInfoTextSize(float)
     */
    public float getLineInfoTextSize() {
        return mLineInfoTextSize;
    }

    /**
     * Set text size for line info panel
     *
     * @param size Text size for line information, <strong>unit is SP</strong>
     */
    public void setLineInfoTextSize(float size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        mLineInfoTextSize = size;
    }

    /**
     * @see CodeEditor#setAutoCompletionOnComposing(boolean)
     */
    public boolean isAutoCompletionOnComposing() {
        return mCompletionOnComposing;
    }

    /**
     * Specify whether show auto completion window even the input method is in composing state.
     * <p>
     * This is useful when the user uses an input method that does not support the attitude {@link EditorInfo#TYPE_TEXT_FLAG_NO_SUGGESTIONS}
     * <p>
     * Note:
     * Composing texts are marked by an underline. When this switch is set to false, the editor
     * will not provide auto completion when there is any composing text in editor.
     * <p>
     * This is enabled by default now
     */
    public void setAutoCompletionOnComposing(boolean completionOnComposing) {
        mCompletionOnComposing = completionOnComposing;
    }

    /**
     * Specify whether we should provide any auto completion
     * <p>
     * If this is set to false, the completion window will never show and auto completion item
     * provider will never be called.
     */
    public void setAutoCompletionEnabled(boolean autoCompletionEnabled) {
        mAutoCompletionEnabled = autoCompletionEnabled;
        if (!autoCompletionEnabled) {
            completionWindow.view.hide();
        }
    }

    /**
     * @see CodeEditor#setAutoCompletionEnabled(boolean)
     */
    public boolean isAutoCompletionEnabled() {
        return mAutoCompletionEnabled;
    }

    /**
     * Sets non-printable painting flags.
     * Specify where they should be drawn.
     * <p>
     * Flags can be mixed.
     *
     * @param flags Flags
     * @see #FLAG_DRAW_WHITESPACE_LEADING
     * @see #FLAG_DRAW_WHITESPACE_INNER
     * @see #FLAG_DRAW_WHITESPACE_TRAILING
     * @see #FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE
     * @see #FLAG_DRAW_LINE_SEPARATOR
     */
    public void setNonPrintablePaintingFlags(int flags) {
        this.mNonPrintableOptions = flags;
        view.invalidate();
    }

    /**
     * @see #setNonPrintablePaintingFlags(int)
     * @see #FLAG_DRAW_WHITESPACE_LEADING
     * @see #FLAG_DRAW_WHITESPACE_INNER
     * @see #FLAG_DRAW_WHITESPACE_TRAILING
     * @see #FLAG_DRAW_WHITESPACE_FOR_EMPTY_LINE
     * @see #FLAG_DRAW_LINE_SEPARATOR
     */
    public int getNonPrintableFlags() {
        return mNonPrintableOptions;
    }

    /**
     * Make the selection visible
     */
    public void ensureSelectionVisible() {
        ensurePositionVisible(getCursor().getRightLine(), getCursor().getRightColumn());
    }

    /**
     * Make the given character position visible
     *
     * @param line   Line of char
     * @param column Column of char
     */
    public void ensurePositionVisible(int line, int column) {
        float[] layoutOffset = mLayout.getCharLayoutOffset(line, column);
        // x offset is the left of character
        float xOffset = layoutOffset[1];
        // y offset is the bottom of row
        float yOffset = layoutOffset[0];

        float targetY = getOffsetY();
        float targetX = getOffsetX();

        if (yOffset - getRowHeight() < getOffsetY()) {
            //top invisible
            targetY = yOffset - getRowHeight() * 1.1f;
        }
        if (yOffset > view.getHeight() + getOffsetY()) {
            //bottom invisible
            targetY = yOffset - view.getHeight() + getRowHeight() * 0.1f;
        }
        float charWidth = column == 0 ? 0 : measureText(mText.getLine(line), column - 1, 1);
        if (xOffset < getOffsetX()) {
            targetX = xOffset - charWidth * 0.2f;
        }
        if (xOffset + charWidth > getOffsetX() + view.getWidth()) {
            targetX = xOffset + charWidth * 0.8f - view.getWidth();
        }

        targetX = Math.max(0, Math.min(getScrollMaxX(), targetX));
        targetY = Math.max(0, Math.min(getScrollMaxY(), targetY));

        if (targetY == getOffsetY() && targetX == getOffsetX()) {
            view.invalidate();
            return;
        }

        boolean animation = true;
        if (System.currentTimeMillis() - mLastMakeVisible < 100) {
            animation = false;
        }
        mLastMakeVisible = System.currentTimeMillis();
        if (animation) {
            getScroller().forceFinished(true);
            getScroller().startScroll(getOffsetX(), getOffsetY(), (int) (targetX - getOffsetX()), (int) (targetY - getOffsetY()));
            if (Math.abs(getOffsetY() - targetY) > mDpUnit * 100) {
                userInput.view.notifyScrolled();
            }
        } else {
            getScroller().startScroll(getOffsetX(), getOffsetY(), (int) (targetX - getOffsetX()), (int) (targetY - getOffsetY()), 0);
        }

        view.invalidate();
    }

    /**
     * Whether there is clip
     *
     * @return whether clip in clip board
     */
    public boolean hasClip() {
        return mClipboardManager.hasPrimaryClip();
    }

    /**
     * Get 1dp = ?px
     *
     * @return 1dp in pixel
     */
    public float getDpUnit() {
        return mDpUnit;
    }

    /**
     * Get scroller from EventHandler
     * You would better not use it for your own scrolling
     *
     * @return The scroller
     */
    public OverScroller getScroller() {
        return userInput.view.getScroller();
    }

    /**
     * Whether the position is over max Y position
     *
     * @param posOnScreen Y position on view
     * @return Whether over max Y
     */
    public boolean isOverMaxY(float posOnScreen) {
        return posOnScreen + getOffsetY() > mLayout.getLayoutHeight();
    }

    /**
     * Determine character position using positions in scroll coordinate
     *
     * @param xOffset Horizontal position in scroll coordinate
     * @param yOffset Vertical position in scroll coordinate
     * @return IntPair. first is line and second is column
     * @see IntPair
     */
    public long getPointPosition(float xOffset, float yOffset) {
        return mLayout.getCharPositionForLayoutOffset(xOffset, yOffset);
    }

    /**
     * Determine character position using positions on view
     *
     * @param x X on view
     * @param y Y on view
     * @return IntPair. first is line and second is column
     * @see IntPair
     */
    public long getPointPositionOnScreen(float x, float y) {
        return getPointPosition(x + getOffsetX(), y + getOffsetY());
    }

    /**
     * Get max scroll y
     *
     * @return max scroll y
     */
    public int getScrollMaxY() {
        //return Math.max(0, mLayout.getLayoutHeight() - view.getHeight() / 2); TODO
        return mLayout.getLayoutHeight();
    }

    /**
     * Get max scroll x
     *
     * @return max scroll x
     */
    public int getScrollMaxX() {
        return (int) Math.max(0, mLayout.getLayoutWidth() - view.getWidth() / 2f);
    }

    /**
     * @see CodeEditor#setHighlightSelectedText(boolean)
     */
    public boolean isHighlightSelectedText() {
        return mHighlightSelectedText;
    }

    public void setHighlightSelectedText(boolean highlightSelected) {
        mHighlightSelectedText = highlightSelected;
        view.invalidate();
    }

    /**
     * Get SearcherController
     *
     * @return SearcherController
     */
    public SearcherController getSearcher() {
        return searcher;
    }

    /**
     * Set selection around the given position
     * It will try to set selection as near as possible (Exactly the position if that position exists)
     */
    public void setSelectionAround(int line, int column) {
        if (line < getLineCount()) {
            int columnCount = mText.getColumnCount(line);
            if (column > columnCount) {
                column = columnCount;
            }
            setSelection(line, column);
        } else {
            setSelection(getLineCount() - 1, mText.getColumnCount(getLineCount() - 1));
        }
    }

    /**
     * Format text Async
     *
     * @return Whether the format task is scheduled
     */
    public synchronized boolean formatCodeAsync() {
        if (mFormatThread != null || (mListener != null && mListener.onRequestFormat(this, true))) {
            return false;
        }
        mFormatThread = new TextFormatter(mText, mLanguage, this);
        mFormatThread.start();
        return true;
    }

    /**
     * Get tab width
     *
     * @return tab width
     */
    public int getTabWidth() {
        if ( cursor == null ) {
            return 0;
        }
        return cursor.getTabWidth();
    }

    /**
     * Set max and min text size that can be used by user zooming.
     * <p>
     * Unit is px.
     */
    public void setScaleTextSizes(float minSize, float maxSize) {
        if (minSize > maxSize) {
            throw new IllegalArgumentException("min size can not be bigger than max size");
        }
        if (minSize < 2f) {
            throw new IllegalArgumentException("min size must be at least 2px");
        }
        userInput.view.minSize = minSize;
        userInput.view.maxSize = maxSize;
    }

    /**
     * @see CodeEditor#setInputType(int)
     */
    public int getInputType() {
        return mInputType;
    }

    /**
     * Specify input type for the editor
     * <p>
     * Zero for default input type
     *
     * @see EditorInfo#inputType
     */
    public void setInputType(int inputType) {
        mInputType = inputType;
    }

    /**
     * Undo last action
     */
    public void undo() {
        mText.undo();
    }

    /**
     * Redo last action
     */
    public void redo() {
        mText.redo();
    }

    /**
     * Whether can undo
     *
     * @return whether can undo
     */
    public boolean canUndo() {
        return mText.canUndo();
    }

    /**
     * Whether can redo
     *
     * @return whether can redo
     */
    public boolean canRedo() {
        return mText.canRedo();
    }

    /**
     * @return Enabled/Disabled
     * @see CodeEditor#setUndoEnabled(boolean)
     */
    public boolean isUndoEnabled() {
        return mUndoEnabled;
    }

    /**
     * Enable / disabled undo manager
     *
     * @param enabled Enable/Disable
     */
    public void setUndoEnabled(boolean enabled) {
        mUndoEnabled = enabled;
        if (mText != null) {
            mText.setUndoEnabled(enabled);
        }
    }

    /**
     * @return Whether drag
     * @see CodeEditor#setDrag(boolean)
     */
    public boolean isDrag() {
        return mDrag;
    }

    /**
     * Set whether drag mode
     * drag:no fling scroll
     *
     * @param drag Whether drag
     */
    public void setDrag(boolean drag) {
        mDrag = drag;
        if (drag && !userInput.view.getScroller().isFinished()) {
            userInput.view.getScroller().forceFinished(true);
        }
    }

    /**
     * Start search action mode
     */
    public void beginSearchMode() {
        class SearchActionMode implements ActionMode.Callback {

            @Override
            public boolean onCreateActionMode(ActionMode p1, Menu p2) {
                mStartedActionMode = ACTION_MODE_SEARCH_TEXT;
                p2.add(0, 0, 0, R.string.next).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                p2.add(0, 1, 0, R.string.last).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                p2.add(0, 2, 0, R.string.replace).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                p2.add(0, 3, 0, R.string.replaceAll).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                SearchView sv = new SearchView(view.getContext());
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String text) {
                        getSearcher().gotoNext();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String text) {
                        getSearcher().search(text);
                        return false;
                    }

                });
                p1.setCustomView(sv);
                sv.performClick();
                sv.setQueryHint(view.getContext().getString(R.string.text_to_search));
                sv.setIconifiedByDefault(false);
                sv.setIconified(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode am, MenuItem p2) {
                switch (p2.getItemId()) {
                    case 1:
                        getSearcher().gotoLast();
                        break;
                    case 0:
                        getSearcher().gotoNext();
                        break;
                    case 2:
                    case 3:
                        final boolean replaceAll = p2.getItemId() == 3;
                        final EditText et = new EditText(view.getContext());
                        et.setHint(R.string.replacement);
                        new AlertDialog.Builder(view.getContext())
                                .setTitle(replaceAll ? R.string.replaceAll : R.string.replace)
                                .setView(et)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.replace, (dialog, which) -> {
                                    if (replaceAll) {
                                        getSearcher().replaceAll(et.getText().toString());
                                    } else {
                                        getSearcher().replaceThis(et.getText().toString());
                                    }
                                    am.finish();
                                    dialog.dismiss();
                                })
                                .show();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode p1) {
                mStartedActionMode = ACTION_MODE_NONE;
                getSearcher().stopSearch();
            }

        }
        ActionMode.Callback callback = new SearchActionMode();
        view.startActionMode(callback);
    }

    public UserInputView getEventHandler() {
        return userInput.view;
    }



    /**
     * @return Typeface of text
     * @see CodeEditor#setTypefaceText(Typeface)
     */
    public Typeface getTypefaceText() {
        return mPaint.getTypeface();
    }

    /**
     * Set text's typeface
     *
     * @param typefaceText New typeface
     */
    public void setTypefaceText(Typeface typefaceText) {
        if (typefaceText == null) {
            typefaceText = Typeface.DEFAULT;
        }
        mPaint.setTypeface(typefaceText);
        mFontCache.clearCache();
        if (2 * mPaint.measureText("/") != mPaint.measureText("//")) {
            Log.w(Logger.LOG_TAG, "Font issue:Your font is painting '/' and '//' differently, which will cause the editor to render slowly than other fonts.");
            mCharPaint = true;
        } else {
            mCharPaint = false;
        }
        mTextMetrics = mPaint.getFontMetricsInt();
        mLayout.createLayout(this);
        view.invalidate();
    }

    /**
     * Get CursorController
     * Internal method!
     *
     * @return CursorController of text
     */
    public CursorController getCursor() {
        return cursor;
    }

    /**
     * Get line count
     *
     * @return line count
     */
    public int getLineCount() {
        return mText.getLineCount();
    }

    /**
     * Get first visible line on screen
     *
     * @return first visible line
     */
    public int getFirstVisibleLine() {
        return mLayout.getLineNumberForRow(getFirstVisibleRow());
    }

    /**
     * Get first visible row on screen
     *
     * @return first visible row
     */
    public int getFirstVisibleRow() {
        return (int) Math.max(0, getOffsetY() / getRowHeight());
    }

    /**
     * Get last visible row on screen
     *
     * @return last visible row
     */
    public int getLastVisibleRow() {
        return (int) Math.max(0, (getOffsetY() + view.getHeight()) / getRowHeight());
    }

    /**
     * Whether this row is visible on screen
     *
     * @param row RowModel to check
     * @return Whether visible
     */
    public boolean isRowVisible(int row) {
        return (getFirstVisibleRow() <= row && row <= getLastVisibleRow());
    }

    /**
     * Get baseline directly
     *
     * @param row RowModel
     * @return baseline y offset
     */
    public int getRowBaseline(int row) {
        return getRowHeight() * (row + 1) - mTextMetrics.descent;
    }

    /**
     * Get row height
     *
     * @return height of single row
     */
    public int getRowHeight() {
        return mTextMetrics.descent - mTextMetrics.ascent;
    }

    /**
     * Get row top y offset
     *
     * @param row RowModel
     * @return top y offset
     */
    public int getRowTop(int row) {
        return getRowHeight() * row;
    }

    /**
     * Get row bottom y offset
     *
     * @param row RowModel
     * @return Bottom y offset
     */
    public int getRowBottom(int row) {
        return getRowHeight() * (row + 1);
    }

    /**
     * Get scroll x
     *
     * @return scroll x
     */
    public int getOffsetX() {
        return userInput.view.getScroller().getCurrX();
    }

    /**
     * Get scroll y
     *
     * @return scroll y
     */
    public int getOffsetY() {
        return userInput.view.getScroller().getCurrY();
    }

    /**
     * @return Whether editable
     * @see CodeEditor#setEditable(boolean)
     */
    public boolean isEditable() {
        return mEditable;
    }

    /**
     * Set whether text can be edited
     *
     * @param editable Editable
     */
    public void setEditable(boolean editable) {
        mEditable = editable;
        if (!editable) {
            hideSoftInput();
        }
    }

    /**
     * Allow scale text size by thumb
     *
     * @param scale Whether allow
     */
    public void setScalable(boolean scale) {
        mScalable = scale;
    }

    /**
     * @return Whether allow to scale
     * @see CodeEditor#setScalable(boolean)
     */
    public boolean isScalable() {
        return mScalable;
    }

    public void setBlockLineEnabled(boolean enabled) {
        mBlockLineEnabled = enabled;
        view.invalidate();
    }

    public boolean isBlockLineEnabled() {
        return mBlockLineEnabled;
    }

    /**
     * Get the target cursor to move when shift is pressed
     */
    private CharPosition getSelectingTarget() {
        if (cursor.left().equals(mLockedSelection)) {
            return cursor.right();
        } else {
            return cursor.left();
        }
    }

    /**
     * Make sure the moving selection is visible
     */
    private void ensureSelectingTargetVisible() {
        if (cursor.left().equals(mLockedSelection)) {
            // Ensure right selection visible
            ensureSelectionVisible();
        } else {
            ensurePositionVisible(cursor.getLeftLine(), cursor.getLeftColumn());
        }
    }

    /**
     * Move the selection down
     * If the auto complete panel is shown,move the selection in panel to next
     */
    public void moveSelectionDown() {
        if (mLockedSelection == null) {
            if (completionWindow.view.isShowing()) {
                completionWindow.moveDown();
                return;
            }
            long pos = cursor.getDownOf(IntPair.pack(cursor.getLeftLine(), cursor.getLeftColumn()));
            setSelection(IntPair.getFirst(pos), IntPair.getSecond(pos));
        } else {
            completionWindow.view.hide();
            long pos = cursor.getDownOf(getSelectingTarget().toIntPair());
            setSelectionRegion(mLockedSelection.line, mLockedSelection.column, IntPair.getFirst(pos), IntPair.getSecond(pos), false);
            ensureSelectingTargetVisible();
        }
    }

    /**
     * Move the selection up
     * If Auto complete panel is shown,move the selection in panel to last
     */
    public void moveSelectionUp() {
        if (mLockedSelection == null) {
            if (completionWindow.view.isShowing()) {
                completionWindow.moveUp();
                return;
            }
            long pos = cursor.getUpOf(IntPair.pack(cursor.getLeftLine(), cursor.getLeftColumn()));
            setSelection(IntPair.getFirst(pos), IntPair.getSecond(pos));
        } else {
            completionWindow.view.hide();
            long pos = cursor.getUpOf(getSelectingTarget().toIntPair());
            setSelectionRegion(mLockedSelection.line, mLockedSelection.column, IntPair.getFirst(pos), IntPair.getSecond(pos), false);
            ensureSelectingTargetVisible();
        }
    }

    /**
     * Move the selection left
     */
    public void moveSelectionLeft() {
        if (mLockedSelection == null) {
            CursorController c = getCursor();
            int line = c.getLeftLine();
            int column = c.getLeftColumn();
            long pos = cursor.getLeftOf(IntPair.pack(line, column));
            int lineAfter = IntPair.getFirst(pos);
            int columnAfter = IntPair.getSecond(pos);
            setSelection(lineAfter, columnAfter);
            if (line == lineAfter) {
                int toLeft = column - columnAfter;
                if (completionWindow.view.isShowing()) {
                    String prefix = completionWindow.getPrefix();
                    if (prefix.length() > toLeft) {
                        prefix = prefix.substring(0, prefix.length() - toLeft);
                        completionWindow.setPrefix(prefix);
                    } else {
                        completionWindow.view.hide();
                    }
                }
                if (column - 1 <= 0) {
                    completionWindow.view.hide();
                }
            }
        } else {
            completionWindow.view.hide();
            long pos = cursor.getLeftOf(getSelectingTarget().toIntPair());
            setSelectionRegion(mLockedSelection.line, mLockedSelection.column, IntPair.getFirst(pos), IntPair.getSecond(pos), false);
            ensureSelectingTargetVisible();
        }
    }

    /**
     * Move the selection right
     */
    public void moveSelectionRight() {
        if (mLockedSelection == null) {
            CursorController c = getCursor();
            int line = c.getLeftLine();
            int column = c.getLeftColumn();
            int c_column = getText().getColumnCount(line);
            long pos = cursor.getRightOf(IntPair.pack(line, column));
            int lineAfter = IntPair.getFirst(pos);
            int columnAfter = IntPair.getSecond(pos);
            setSelection(lineAfter, columnAfter);
            if (line == lineAfter) {
                char ch = (columnAfter - 1 < c_column && columnAfter - 1 >= 0) ? mText.charAt(lineAfter, columnAfter - 1) : '\0';
                if (!isEmoji(ch) && completionWindow.view.isShowing()) {
                    if (!mLanguage.isAutoCompleteChar(ch)) {
                        completionWindow.view.hide();
                    } else {
                        String prefix = completionWindow.getPrefix() + ch;
                        completionWindow.setPrefix(prefix);
                    }
                }
            }
        } else {
            completionWindow.view.hide();
            long pos = cursor.getRightOf(getSelectingTarget().toIntPair());
            setSelectionRegion(mLockedSelection.line, mLockedSelection.column, IntPair.getFirst(pos), IntPair.getSecond(pos), false);
            ensureSelectingTargetVisible();
        }
    }

    /**
     * Move selection to end of line
     */
    public void moveSelectionEnd() {
        if (mLockedSelection == null) {
            int line = cursor.getLeftLine();
            setSelection(line, getText().getColumnCount(line));
        } else {
            int line = getSelectingTarget().line;
            setSelectionRegion(mLockedSelection.line, mLockedSelection.column, line, getText().getColumnCount(line), false);
            ensureSelectingTargetVisible();
        }
    }

    /**
     * Move selection to start of line
     */
    public void moveSelectionHome() {
        if (mLockedSelection == null) {
            setSelection(cursor.getLeftLine(), 0);
        } else {
            setSelectionRegion(mLockedSelection.line, mLockedSelection.column, getSelectingTarget().line, 0, false);
            ensureSelectingTargetVisible();
        }
    }

    /**
     * Move selection to given position
     *
     * @param line   The line to move
     * @param column The column to move
     */
    public void setSelection(int line, int column) {
        setSelection(line, column, true);
    }

    /**
     * Move selection to given position
     *
     * @param line          The line to move
     * @param column        The column to move
     * @param makeItVisible Make the character visible
     */
    public void setSelection(int line, int column, boolean makeItVisible) {
        if (column > 0 && isEmoji(mText.charAt(line, column - 1))) {
            column++;
            if (column > mText.getColumnCount(line)) {
                column--;
            }
        }
        cursor.set(line, column);
        if (mHighlightCurrentBlock) {
            cursorPosition = findCursorBlock();
        }
        updateCursor();
        if (makeItVisible) {
            ensurePositionVisible(line, column);
        } else {
            view.invalidate();
        }
        cursor.blink.onSelectionChanged();
        if (mTextActionPresenter != null) {
            mTextActionPresenter.onExit();
        }
    }

    /**
     * Select all text
     */
    public void selectAll() {
        setSelectionRegion(0, 0, getLineCount() - 1, getText().getColumnCount(getLineCount() - 1));
    }

    /**
     * Set selection region with a call to {@link CodeEditor#ensureSelectionVisible()}
     *
     * @param lineLeft    Line left
     * @param columnLeft  Column Left
     * @param lineRight   Line right
     * @param columnRight Column right
     */
    public void setSelectionRegion(int lineLeft, int columnLeft, int lineRight, int columnRight) {
        setSelectionRegion(lineLeft, columnLeft, lineRight, columnRight, true);
    }

    /**
     * Set selection region
     *
     * @param lineLeft         Line left
     * @param columnLeft       Column Left
     * @param lineRight        Line right
     * @param columnRight      Column right
     * @param makeRightVisible Whether make right cursor visible
     */
    public void setSelectionRegion(int lineLeft, int columnLeft, int lineRight, int columnRight, boolean makeRightVisible) {
        int start = getText().getCharIndex(lineLeft, columnLeft);
        int end = getText().getCharIndex(lineRight, columnRight);
        if (start == end) {
            setSelection(lineLeft, columnLeft);
            return;
        }
        if (start > end) {
            setSelectionRegion(lineRight, columnRight, lineLeft, columnLeft, makeRightVisible);
            Log.w(Logger.LOG_TAG, "setSelectionRegion() error: start > end:start = " + start + " end = " + end + " lineLeft = " + lineLeft + " columnLeft = " + columnLeft + " lineRight = " + lineRight + " columnRight = " + columnRight);
            return;
        }
        boolean lastState = cursor.isSelected();
        if (columnLeft > 0) {
            int column = columnLeft - 1;
            char ch = mText.charAt(lineLeft, column);
            if (isEmoji(ch)) {
                columnLeft++;
                if (columnLeft > mText.getColumnCount(lineLeft)) {
                    columnLeft--;
                }
            }
        }
        if (columnRight > 0) {
            int column = columnRight;
            char ch = mText.charAt(lineRight, column);
            if (isEmoji(ch)) {
                columnRight++;
                if (columnRight > mText.getColumnCount(lineRight)) {
                    columnRight--;
                }
            }
        }
        cursor.setLeft(lineLeft, columnLeft);
        cursor.setRight(lineRight, columnRight);
        updateCursor();
        completionWindow.view.hide();
        if (makeRightVisible) {
            ensurePositionVisible(lineRight, columnRight);
        } else {
            view.invalidate();
        }
        cursor.blink.onSelectionChanged();
        if (!lastState && cursor.isSelected() && mStartedActionMode != ACTION_MODE_SEARCH_TEXT) {
            mTextActionPresenter.onBeginTextSelect();
        }
        userInput.notifyTouchedSelectionHandlerLater();
    }

    /**
     * Move to next page
     */
    public void movePageDown() {
        userInput.view.onScroll(null, null, 0, view.getHeight());
        completionWindow.view.hide();
    }

    /**
     * Move to previous page
     */
    public void movePageUp() {
        userInput.view.onScroll(null, null, 0, -view.getHeight());
        completionWindow.view.hide();
    }

    /**
     * Paste text from clip board
     */
    public void pasteText() {
        try {
            if (!mClipboardManager.hasPrimaryClip() || mClipboardManager.getPrimaryClip() == null) {
                return;
            }
            ClipData.Item data = mClipboardManager.getPrimaryClip().getItemAt(0);
            CharSequence text = data.getText();
            if (text != null && mConnection != null) {
                mConnection.view.commitText(text, 0);
            }
            notifyExternalCursorChange();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Copy text to clip board
     */
    public void copyText() {
        try {
            if (cursor.isSelected()) {
                String clip = getText().subContent(cursor.getLeftLine(),
                        cursor.getLeftColumn(),
                        cursor.getRightLine(),
                        cursor.getRightColumn()).toString();
                mClipboardManager.setPrimaryClip(ClipData.newPlainText(clip, clip));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Copy text to clipboard and delete them
     */
    public void cutText() {
        copyText();
        if (cursor.isSelected()) {
            cursor.onDeleteKeyPressed();
            notifyExternalCursorChange();
        }
    }

    /**
     * @return Text displaying, the result is read-only. You should not make changes to this object as it is used internally
     * @see CodeEditor#setText(CharSequence)
     */
    @NonNull
    public ContentMap getText() {
        return mText;
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text the new text you want to display
     */
    public void setText(@Nullable CharSequence text) {

        if (text == null) {
            text = "";
        }

        if (mText != null) {
            mText.removeContentListener(this);
            mText.setLineListener(null);
        }
        mText = new ContentMap(text,this);
        boolean isAutoIndented = CursorModel.DEFAULT_ISAUTO_IDENT;
        if ( cursor != null ) {
            isAutoIndented = cursor.isAutoIndent();
        }
        cursor = mText.getCursor();
        cursor.setAutoIndent(isAutoIndented);
        cursor.setLanguage(mLanguage);
        userInput.view.reset();
        mText.addContentListener(this);
        mText.setUndoEnabled(mUndoEnabled);
        mText.setLineListener(this);

        if (analyzer != null) {
            analyzer.setCallback(null);
            analyzer.shutdown();
        }
        analyzer = (CodeAnalyzer) mLanguage.analyzer;
        CodeAnalyzerResultColor result = ((CodeAnalyzerResultColor)analyzer.getResult("color"));
        if ( result != null ) {
            result.theme = getColorScheme();
        }
        analyzer.setCallback(this);
        analyzer.clear();
        analyzer.analyze(getText());

        view.requestLayout();

        if (mListener != null) {
            mListener.onNewTextSet(this);
        }
        if (mInputMethodManager != null) {
            mInputMethodManager.restartInput(view);
        }
        mLayout.createLayout(this);
        view.invalidate();


    }
    /**
     * Set the editor's text size in sp unit. This value must be > 0
     *
     * @param textSize the editor's text size in <strong>Sp</strong> units.
     */
    public void setTextSize(float textSize) {
        Context context = view.getContext();
        Resources res;

        if (context == null) {
            res = Resources.getSystem();
        } else {
            res = context.getResources();
        }

        setTextSizePx(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, res.getDisplayMetrics()));
    }

    /**
     * Get the paint of the editor
     * You should not change text size and other attributes that are related to text measuring by the object
     *
     * @return The paint which is used by the editor now
     */
    @NonNull
    public Paint getTextPaint() {
        return mPaint;
    }

    /**
     * Get the ColorScheme object of this editor
     * You can config colors of some regions, texts and highlight text
     *
     * @return ColorScheme object using
     */
    @NonNull
    public ColorSchemeExtension getColorScheme() {
        return (ColorSchemeExtension) model.plugins.get("color");
    }

    /**
     * Move selection to line start with scrolling
     *
     * @param line Line to jump
     */
    public void jumpToLine(int line) {
        setSelection(line, 0);
    }

    /**
     * Get analyze result.
     * <strong>Do not make changes to it or read concurrently</strong>
     */
    @NonNull
    public CodeAnalyzerResultColor getTextAnalyzeResult() {
        return (CodeAnalyzerResultColor) analyzer.getResult("color");
    }

    /**
     * Hide auto complete window if shown
     */
    public void hideAutoCompleteWindow() {
        if (completionWindow != null) {
            completionWindow.view.hide();
        }
    }

    /**
     * Get cursor code block index
     *
     * @return index of cursor's code block
     */
    public int getBlockIndex() {
        return cursorPosition;
    }


    //-------------------------------------------------------------------------------
    //-------------------------IME Interaction---------------------------------------
    //-------------------------------------------------------------------------------

    /**
     * Display soft input method for self
     */
    public void showSoftInput() {
        if (isEditable() && view.isEnabled()) {
            if (view.isInTouchMode()) {
                view.requestFocusFromTouch();
            }
            if (!view.hasFocus()) {
                view.requestFocus();
            }
            mInputMethodManager.showSoftInput(view, 0);
        }
        view.invalidate();
    }

    /**
     * Hide soft input
     */
    public void hideSoftInput() {
        mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Set request needed to update when editor updates selection
     */
    public void setExtracting(@Nullable ExtractedTextRequest request) {
        mExtracting = request;

    }

    /**
     * Extract text in editor for input method
     */
    public ExtractedText extractText(@NonNull ExtractedTextRequest request) {
        CursorController cur = getCursor();
        ExtractedText text = new ExtractedText();
        int selBegin = cur.getLeft();
        int selEnd = cur.getRight();
        int startOffset;
        if (request.hintMaxChars == 0) {
            request.hintMaxChars = UserInputConnexionView.TEXT_LENGTH_LIMIT;
        }
        /*if (selEnd - selBegin > request.hintMaxChars) {
            startOffset = selBegin;
        } else {
            int redundantLength = (request.hintMaxChars - (selEnd - selBegin)) / 2;
            startOffset = selBegin - redundantLength;
        }*/
        startOffset = 0;
        //startOffset = Math.max(0, startOffset);
        text.text = mConnection.getTextRegion(startOffset, startOffset + request.hintMaxChars, request.flags);
        text.startOffset = startOffset;
        text.selectionStart = selBegin - startOffset;
        text.selectionEnd = selEnd - startOffset;
        if (selBegin != selEnd) {
            text.flags |= ExtractedText.FLAG_SELECTING;
        }
        return text;
    }

    /**
     * Send cursor position in text and on screen to input method
     */
    public void updateCursor() {
        updateCursorAnchor();
        updateExtractedText();
        if (!mText.isInBatchEdit()) {
            updateSelection();
        }
    }

    //-------------------------------------------------------------------------------
    //------------------------Internal Callbacks-------------------------------------
    //-------------------------------------------------------------------------------

    /**
     * Get using InputMethodManager
     */
    public InputMethodManager getInputMethodManager() {
        return mInputMethodManager;
    }

    /**
     * Called by CodeEditorInputConnection
     */
    public void onCloseConnection() {
        setExtracting(null);
        view.invalidate();
    }



    @Override
    public void beforeReplace(ContentMap content) {
        mWait = true;
        mLayout.beforeReplace(content);
        if (mListener != null) {
            mListener.beforeReplace(this, content);
        }
    }

    @Override
    public void afterInsert(ContentMap content, int startLine, int startColumn, int endLine, int endColumn, CharSequence insertedContent) {

        SpanMap sm = analyzer.getSpanMap();

        // Update spans
        if (isSpanMapPrepared(true, endLine - startLine)) {
            if (startLine == endLine) {
                sm.get(startLine).insertContent(Span.obtain(startColumn, endColumn - startColumn, 0));
            } else {
                sm.insertContent(startLine, startColumn, endLine, endColumn);
            }
        }

        cursor.blink.onSelectionChanged();
        mLayout.afterInsert(content, startLine, startColumn, endLine, endColumn, insertedContent);

        // Notify input method
        updateCursor();
        mWait = false;
        // Visibility & State shift
        exitSelectModeIfNeeded();

        // Auto completion
        if (isAutoCompletionEnabled()) {
            if ((mConnection.model.composingLine == -1 || mCompletionOnComposing) && endColumn != 0 && startLine == endLine) {
                int end = endColumn;
                while (endColumn > 0) {
                    if (mLanguage.isAutoCompleteChar(content.charAt(endLine, endColumn - 1))) {
                        endColumn--;
                    } else {
                        break;
                    }
                }
                if (end > endColumn) {
                    String line = content.getLineString(endLine);
                    String prefix = line.substring(endColumn, end);
                    completionWindow.setPrefix(prefix);
                    completionWindow.view.show();
                } else {
                    postHideCompletionWindow();
                }
            } else {
                postHideCompletionWindow();
            }
            if (completionWindow.view.isShowing()) {
                updateCompletionWindowPosition();
            }
        } else {
            completionWindow.view.hide();
        }

        updateCursorAnchor();
        ensureSelectionVisible();
        // Notify to update highlight
        analyzer.analyze(mText);
        userInput.hideInsertHandle();
        // Notify listener
        if (mListener != null) {
            mListener.afterInsert(this, mText, startLine, startColumn, endLine, endColumn, insertedContent);
        }

    }

    @Override
    public void afterDelete(ContentMap content, int startLine, int startColumn, int endLine, int endColumn, CharSequence deletedContent) {

        if (isSpanMapPrepared(false, endLine - startLine)) {
            analyzer.getSpanMap().removeContent(startLine,startColumn,endLine,endColumn);
        }

        cursor.blink.onSelectionChanged();
        mLayout.afterDelete(content, startLine, startColumn, endLine, endColumn, deletedContent);

        updateCursor();
        exitSelectModeIfNeeded();

        if (isAutoCompletionEnabled()) {
            if (mConnection.model.composingLine == -1 && completionWindow.view.isShowing()) {
                if (startLine != endLine || startColumn != endColumn - 1) {
                    postHideCompletionWindow();
                }
                String prefix = completionWindow.getPrefix();
                if (prefix == null || prefix.length() - 1 <= 0) {
                    postHideCompletionWindow();
                } else {
                    prefix = prefix.substring(0, prefix.length() - 1);
                    updateCompletionWindowPosition();
                    completionWindow.setPrefix(prefix);
                }
            }
        } else {
            completionWindow.view.hide();
        }

        if (!mWait) {
            updateCursorAnchor();
            ensureSelectionVisible();
            analyzer.analyze(mText);
            userInput.hideInsertHandle();
        }
        if (mListener != null) {
            mListener.afterDelete(this, mText, startLine, startColumn, endLine, endColumn, deletedContent);
        }

    }

    @Override
    public void onFormatFail(final Throwable throwable) {
        if (mListener != null && !mListener.onFormatFail(this, throwable)) {
            view.post(() -> Toast.makeText(view.getContext(), throwable.toString(), Toast.LENGTH_SHORT).show());
        }
        mFormatThread = null;
    }

    @Override
    public void onRemove(ContentMap content, ContentLineController line) {
        mLayout.onRemove(content, line);
    }

    @Override
    public void onFormatSucceed(CharSequence originalText, final CharSequence newText) {
        if (mListener != null) {
            mListener.onFormatSucceed(this);
        }
        mFormatThread = null;
        if (originalText == mText) {
            view.post(() -> {
                int line = cursor.getLeftLine();
                int column = cursor.getLeftColumn();
                mText.replace(0, 0, getLineCount() - 1, mText.getColumnCount(getLineCount() - 1), newText);
                getScroller().forceFinished(true);
                completionWindow.view.hide();
                setSelectionAround(line, column);
            });
        }
    }

    public void onEndTextSelect() {
        showTextActionPopup();
    }

    public void onEndGestureInteraction() {
        showTextActionPopup();
    }

    @Override
    public void onAnalyzeDone(CodeAnalyzer analyzer) {
        Logger.debug();
        if (mHighlightCurrentBlock) {
                cursorPosition = findCursorBlock();
        }
        view.postInvalidate();
    }

    //-------------------------------------------------------------------------------
    //-------------------------Inner classes-----------------------------------------
    //-------------------------------------------------------------------------------

    /**
     * Mode for presenting text actions
     */
    public enum TextActionMode {

        /**
         * In this way, the editor shows a windows inside the editor to present actions
         */
        POPUP_WINDOW,
        /**
         * Beautified text action window by @RandunuRtx
         */
        POPUP_WINDOW_2,
        /**
         * In this way, the editor starts a {@link ActionMode} to present actions
         */
        ACTION_MODE

    }


    /**
     * Interface for various ways to present text action panel
     */
    public interface EditorTextActionPresenter {

        /**
         * Selected text is clicked
         *
         * @param event Event
         */
        void onSelectedTextClicked(MotionEvent event);

        /**
         * Text selection, gesture interaction is over
         */
        void onTextSelectionEnd();

        /**
         * Notify that the position of panel should be updated.
         * If the presenter is displayed in editor's viewport, it should update
         * its position
         */
        void onUpdate();

        /**
         * Notify that the position of panel should be updated.
         * If the presenter is displayed in editor's viewport, it should update
         * its position
         *
         * @param updateReason {@link TextComposeBasePopup#DISMISS} {@link TextComposeBasePopup#DRAG} {@link TextComposeBasePopup#SCROLL}
         */
        void onUpdate(int updateReason);

        /**
         * Start the presenter
         */
        void onBeginTextSelect();


        /**
         * Exit the presenter
         * @return Whether action is executed. Return true if this has cause UI change such as
         * popup window hides and action mode exits
         */
        boolean onExit();

        /**
         * Called by editor to check whether it should draw handles of cursor
         */
        boolean shouldShowCursor();

    }



}
