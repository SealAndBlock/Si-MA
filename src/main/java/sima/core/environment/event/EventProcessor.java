package sima.core.environment.event;

/**
 * Interface which allow processing of {@link Event}.
 */
public interface EventProcessor {
    
    /**
     * Method called when an {@link sima.core.environment.event.Event} occurred
     *
     * @param event the occurred event
     */
    void processEvent(Event event);
    
}
