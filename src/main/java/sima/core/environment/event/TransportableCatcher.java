package sima.core.environment.event;

public interface TransportableCatcher {
    
    /**
     * Method called when a {@link Transportable} must be processed.
     *
     * @param transportable to process
     */
    void processTransportable(Transportable transportable);
    
}
