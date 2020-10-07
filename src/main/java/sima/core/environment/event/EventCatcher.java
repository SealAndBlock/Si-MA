package sima.core.environment.event;

/**
 * The interface that a class which want catch event must implement.
 */
public interface EventCatcher {

    /**
     * Method called when an {@link Event} occurred and is destined for the current instance for which is called this
     * method.
     *
     * @param event the occurred event
     */
    void processEvent(Event event);

}
