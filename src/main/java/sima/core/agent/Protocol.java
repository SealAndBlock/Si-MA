package sima.core.agent;

import sima.core.environment.Event;

/**
 * Represents a protocol. A protocol has only one method {@link #processEvent(Event)} which is called when an
 * {@link Event} occurs and the {@link Event#getProtocolTargeted()} is the protocol.
 */
public interface Protocol {

    /**
     * Call when an event occurs and that the {@link Event#getProtocolTargeted()} is the protocol.
     *
     * @param event the occurred event
     */
    void processEvent(Event event);

}
