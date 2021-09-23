package sima.core.environment.event.transport;

/**
 * Interface which all processing of {@link TransportableInEvent}.
 */
public interface EventTransportableProcessor {
    
    /**
     * Method called when an {@link TransportableInEvent} must be treated.
     *
     * @param transportableInEvent to process
     */
    void processEventTransportable(TransportableInEvent transportableInEvent);
    
}
