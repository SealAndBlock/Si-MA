package sima.core.environment.event;

/**
 * The interface that a class which want catch event must implement.
 */
public interface EventCatcher {
    
    /**
     * Method called when an {@link Event} occurred
     *
     * @param event the occurred event
     */
    void processEvent(Event event);
    
}
