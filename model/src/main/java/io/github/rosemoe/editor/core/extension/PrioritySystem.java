package io.github.rosemoe.editor.core.extension;

/**
 * A priority system.
 */
public abstract class PrioritySystem implements Comparable {
    /**
     * Event priority declaration : WARNING they should be putted from low to high priority.
     */
    public static final int PRIORITY_LOW = -1000;
    public static final int PRIORITY_STD = 0;
    public static final int PRIORITY_HIGH = 1000;
    public int priorityRing = PrioritySystem.PRIORITY_STD;

    @Override
    public int compareTo(Object o) {
        if ( o instanceof PrioritySystem) {
            PrioritySystem p = (PrioritySystem) o;
            if ( priorityRing < p.priorityRing ) {
                return -1;
            } else if(priorityRing > p.priorityRing ) {
                return 1;
            }
            return 0;
        }
        // ex: compareTo null will result in null to be processed with less priority.
        return 1;
    }
}
