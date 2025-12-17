package animation;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A manager for all your events
 * @since 1.0.0
 * @author Yukurotei
 */
public class EventManager {
    private final ConcurrentLinkedQueue<Event> events = new ConcurrentLinkedQueue<>();
    /**
     * Add an event to the manager
     * @param event - The event
     * @since 1.0.0
     * @author Yukurotei
     */
    public void addEvent(Event event) {
        events.add(event);
    }

    /**
     * Internal dont worry
     * @hidden
     * @since 1.0.0
     * @author Yukurotei
     */
    public void update(float timePassed) {
        for (Event event : events) {
            event.trigger(timePassed);
        }
    }
}
