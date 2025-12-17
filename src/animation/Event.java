package animation;
/**
 * An event
 * @since 1.0.0
 * @author Yukurotei
 */
public class Event {
    private final float triggerTime;
    private final Runnable action;
    private boolean hasBeenTriggered = false;

    /**
     * Creates an event
     * @param triggerTime - How long after the program starts the event runs
     * @param action - The action
     * @since 1.0.0
     * @author Yukurotei
     */
    public Event(float triggerTime, Runnable action) {
        this.triggerTime = triggerTime;
        this.action = action;
    }

    /**
     * Internal dont worry
     * @hidden
     * @since 1.0.0
     * @author Yukurotei
     */
    public void trigger(float currentTime) {
        if (!hasBeenTriggered && currentTime >= triggerTime) {
            action.run();
            hasBeenTriggered = true;
        }
    }

    /**
     * Has the Event been triggered
     * @return boolean
     * @since 1.0.0
     * @author Yukurotei
     */
    public boolean hasBeenTriggered() {
        return hasBeenTriggered;
    }
}
