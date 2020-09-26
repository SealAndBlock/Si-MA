package sima.core.protocol;

import sima.core.agent.AbstractAgent;
import sima.core.behavior.Behavior;
import sima.core.environment.event.Event;

import java.util.Optional;

/**
 * This class represents a protocol. A protocol is an algorithm that an {@link AbstractAgent} can use and thanks to the
 * {@link ProtocolManipulator}, the agent via the {@link Behavior} can change the behavior of the protocol by changing
 * the current {@link #protocolManipulator}.
 */
public abstract class Protocol {

    // Singletons.

    /**
     * The thread lock for manipulate the static variable {@link #PROTOCOL_IDENTIFICATOR}.
     */
    private static final Object PROTOCOL_IDENTIFICATOR_LOCK = new Object();

    /**
     * The {@link ProtocolIdentificator} of the protocol.
     */
    private static ProtocolIdentificator PROTOCOL_IDENTIFICATOR;

    // Variables.

    /**
     * The protocol manipulator. Must be not null.
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
     * Returns the {@link ProtocolIdentificator} of the protocol. The protocol identificator must allow an agent to
     * identify which protocol is called and for two different agents which use the same set of protocols, for a same
     * instance of a {@link ProtocolIdentificator}, the method {@link AbstractAgent#getProtocol(ProtocolIdentificator)}
     * must returns the same protocol for both agents.
     * <p>
     * The base implementation is the use of a singleton of {@link ProtocolIdentificator} instantiates at the first call
     * of the method.
     * <p>
     * This method is thread safe and the thread lock is the static variable {@link #PROTOCOL_IDENTIFICATOR_LOCK}.
     *
     * @return the {@link ProtocolIdentificator} of the protocol. It never returns null.
     */
    public ProtocolIdentificator getIdentificator() {
        synchronized (PROTOCOL_IDENTIFICATOR_LOCK) {
            if (PROTOCOL_IDENTIFICATOR == null) {
                PROTOCOL_IDENTIFICATOR = new ProtocolIdentificator(this.getClass().getName());
            }

            return PROTOCOL_IDENTIFICATOR;
        }
    }

    /**
     * Call when an event occurs and that the {@link Event#getProtocolTargeted()} is the protocol.
     *
     * @param event the occurred event
     */
    public abstract void processEvent(Event event);

    /**
     * Reset the default manipulator of the protocol. In that way, the property {@link #protocolManipulator} is never
     * null.
     */
    public abstract void resetDefaultProtocolManipulator();

    // Getters and Setters.

    public ProtocolManipulator getProtocolManipulator() {
        return protocolManipulator.get();
    }

    /**
     * Set {@link #protocolManipulator}. In addition to this, the method
     * {@link ProtocolManipulator#setManipulatedProtocol(Protocol)} is called to set as manipulated protocol for the new
     * protocol manipulator the current protocol.
     *
     * @param protocolManipulator the protocol manipulator (must be not null)
     * @throws NullPointerException if the protocol manipulator is null
     */
    public void setProtocolManipulator(ProtocolManipulator protocolManipulator) {
        this.protocolManipulator = Optional.of(protocolManipulator);
        ProtocolManipulator pM = this.protocolManipulator.get();
        pM.setManipulatedProtocol(this);
    }
}
