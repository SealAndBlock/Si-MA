package sima.core.environment.exchange.transport;

public interface TransportableCatcher {
    
    /**
     * Method called when a {@link Transportable} must be processed.
     *
     * @param transportable to process
     */
    void processTransportable(Transportable transportable);
    
}
