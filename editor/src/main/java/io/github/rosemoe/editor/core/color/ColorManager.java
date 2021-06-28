package io.github.rosemoe.editor.core.color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantLock;

import io.github.rosemoe.editor.core.util.Logger;

/**
 * Class that hold the information:
 *  linenumber      -> accent1
 *  linenumberpanel -> accent2
 *  linenumberpanelText -> base00
 *  base00 -> 0xFF000000
 *  base01 -> 0xFFFF0000
 *   ...
 *
 */
public class ColorManager {

    private ReentrantLock observersLock = new ReentrantLock();
    private List<Observer> observers = new ArrayList<Observer>();
    HashMap<String, Object> colors = new HashMap<>();
    private static final Integer HIDDEN = 0;
    private static final Integer DEFAULT = HIDDEN;
    public static Integer DEFAULT_TEXT_COLOR = 0xFF999999;
    public static Integer DEFAULT_BACKGROUND_COLOR = 0xffffffff;

    public ColorManager() {
        init();
    }

    public void attach(Observer observer){
        observersLock.lock();
        observers.add(observer);
        observersLock.unlock();
    }
    public void notifyAllObservers(){
        observersLock.lock();
        for (Observer observer : observers) {
            observer.update(null,null);
        }
        observersLock.unlock();
    }

    /**
     * Reset colorManager to default colors.
     */
    public void reset() {
        init();
    }
    /**
     * ColorManager initialization.
     */
    public void init() {
        colors.put("base03",DEFAULT_BACKGROUND_COLOR);
        colors.put("base02",DEFAULT_TEXT_COLOR);
        colors.put("base01",DEFAULT_TEXT_COLOR);
        colors.put("base00",DEFAULT_TEXT_COLOR);
        colors.put("base0",DEFAULT_TEXT_COLOR);
        colors.put("base1",0xFFdddddd);
        colors.put("base2",0x10000000);
        colors.put("base3",DEFAULT_BACKGROUND_COLOR);
        // Accent colors : Theses colors are put on text for show up to user a particular meaning, purpose may vary between languages.
        /**
         * EXAMPLE: keyword.
         */
        colors.put("accent1", "textNormal");
        /**
         * EXAMPLE: Secondary keyword.
         */
        colors.put("accent2", "textNormal");
        /**
         * EXAMPLE: underline.
         */
        colors.put("accent3", "textNormal");
        /**
         * EXAMPLE: variable identifier.
         */
        colors.put("accent4", "textNormal");
        /**
         * EXAMPLE: Class identifier.
         */
        colors.put("accent5", "textNormal");
        /**
         * EXAMPLE: Function identifier.
         */
        colors.put("accent6", "textNormal");
        /**
         * EXAMPLE: Literals.
         */
        colors.put("accent7", "textNormal");
        /**
         * EXAMPLE: Punctuation.
         */
        colors.put("accent8", "textNormal");

        // specific colors.
        colors.put("textNormal", "base00");

        // temp stuff
        colors.put("currentLine", "base2");
        colors.put("textSelected", "base2");
        colors.put("textSelectedBackground", "base2");
        colors.put("wholeBackground", "base3");
        colors.put("textNormal", "base00");
        colors.put("comment", "base1");
        colors.put("matchedTextBackground", "accent1");
        colors.put("blockLine", "base2");
        colors.put("blockLineCurrent", "base2");
        colors.put("selectionInsert", "textNormal");
        colors.put("selectionHandle", "textNormal");
        colors.put("nonPrintableChar", 0x00000000);
        colors.put("underline", "accent3");
        colors.put("lineDivider", "base1");
        colors.put("autoCompleteItemCurrentPosition", "base1");
        colors.put("autoCompleteItem", "base2");
    }

    /**
     * Because Integer.parseInt do not support FFFF0000 (must be -FF0000)
     * @param hex
     * @return
     */
    public static int decodeHex(String hex) {
        if ( hex.length() %2 != 0 ) {
            hex="0"+hex;
        }
        int value = 0;
        for(int a = hex.length(); a > 0; a=a-2) {
            String no = hex.substring(a-2,a);
            int part = Integer.parseInt(no, 16);
            part = part << ( 8 * ((a+2)/2) ) ;
            value += part;
        }
        Logger.debug("hex=",hex,",color=",value);
        return value;
    }

    /**
     * Get a color following a redirection.
     * @param color color's name
     * @return value of the color
     */
    public Integer getColor(String color) {
        Object value = colors.get(color);
        if ( value == null ) {
            if ( color.indexOf('#') == 0 ) {
                return decodeHex(color.substring(1));
            }
            throw new RuntimeException("Warning requested color "+color+" is not registered");
        }
        if ( value instanceof String ) {
            return getColor((String) value);
        }
        if ( value instanceof Integer ) {
            return (Integer) value;
        }
        throw new RuntimeException("Unknow object in the map");
    }

    /**
     * Register a new color in the manager.
     * @param color color's name
     * @param value value of the color
     */
    public void register(String color, Object value) {
        Object oldValue = colors.get(color);
        colors.put(color,value);
        if ( oldValue != null &&
                oldValue != value ) {
            notifyAllObservers();
        }
    }

    /**
     * Register into the map if not in.
     * @param color
     * @param value
     */
    public void registerIfNotIn(String color, Object value) {
        if ( colors.get(color) == null ) {
            register(color, value);
        }
    }

    /**
     * We can choose to invert the color scheme.
     * Assuming Theme editor put white variant, isInverted <=> is black theme.
     */
    public void invertColorScheme() {
        String [] colors = new String[] {
                "base03", "base02", "base01", "base00",
                "base0", "base1", "base2", "base2"
        };
        for(int a = 0; a < colors.length; a=a+1){
            int i1 = a;
            int i2 = a+colors.length/2;

            String key1 = colors[i1];
            String key2 = colors[i2];
            Object value = this.colors.get(key1);
            this.colors.put(key1,this.colors.get(key2));
            this.colors.put(key2,this.colors.put(key2,value));
        }
        notifyAllObservers();
    }

    public void dump() {
        dump("");
    }
    public void dump(String offset) {
        Logger.debug(offset);
        for(String key : colors.keySet()) {
            String value = String.valueOf(colors.get(key));
            Logger.debug(offset,"key=",key,",value=",value);
        }
    }
}
