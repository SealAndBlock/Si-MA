package sima.core.environment.event.transport;

/**
 * Interface which all processing of {@link EventTransportable}.
 */
public interface EventTransportableProcessor {
    
    /**
     * Method called when an {@link EventTransportable} must be treated.
     *
     * @param eventTransportable to process
     */
    void processEventTransportable(EventTransportable eventTransportable);
    
}
