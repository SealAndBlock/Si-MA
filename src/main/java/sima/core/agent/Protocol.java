package sima.core.agent;

import sima.core.environment.Event;

import java.util.Optional;

/**
 * This class represents a protocol. A protocol is an algorithm that an {@link AbstractAgent} can use and thanks to the
 * {@link ProtocolManipulator}, the agent via the {@link Behavior} can change the behavior of the protocol by changing
 * the current {@link #protocolManipulator}.
 */
public abstract class Protocol {

    // Variables.

    /**
     * The protocol manipulator.
     */
    private Optional<ProtocolManipulator> protocolManipulator;

    // Constructors.

    /**
     * Create a protocol with a protocol manipulator which not be null. Throws a {@link NullPointerException} if the
     * protocol manipulator is null.
     *
     * @param protocolManipulator the protocol manipulator (must be not null)
     * @throws NullPointerException if the protocol manipulator is null.
     */
    protected Protocol(ProtocolManipulator protocolManipulator) {
        this.protocolManipulator = Optional.of(protocolManipulator);
    }

    // Methods.

    /**
     * Call when an event occurs and that the {@link Event#getProtocolTargeted()} is the protocol.
     *
     * @param event the occurred event
     */
    public abstract void processEvent(Event event);

    // Getters and Setters.

    public ProtocolManipulator getProtocolManipulator() {
        return protocolManipulator.get();
    }

    /**
     * @param protocolManipulator the protocol manipulator (must be not null)
     * @throws NullPointerException if the protocol manipulator is null
     */
    public void setProtocolManipulator(ProtocolManipulator protocolManipulator) {
        this.protocolManipulator = Optional.of(protocolManipulator);
    }
}
