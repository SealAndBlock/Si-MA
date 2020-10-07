package sima.core.protocol;

import sima.core.agent.AbstractAgent;
import sima.core.behavior.Behavior;
import sima.core.environment.event.EventCatcher;

import java.util.Optional;

/**
 * This class represents a protocol. A protocol is an algorithm that an {@link AbstractAgent} can use and thanks to the
 * {@link ProtocolManipulator}, the agent via the {@link Behavior} can change the behavior of the protocol by changing
 * the current {@link #protocolManipulator}.
 * <p>
 * All inherited class of {@link Protocol} must have this constructor <strong>Protocol(String protocolTag,
 * String[] args)</strong>. In that way, it allows to use the java reflexivity.
 */
public abstract class Protocol implements EventCatcher {

    // Singletons.

    /**
     * The thread lock for manipulate the static variable {@link #PROTOCOL_IDENTIFIER}.
     */
    private static final Object PROTOCOL_IDENTIFIER_LOCK = new Object();

    /**
     * The {@link ProtocolIdentifier} of the protocol.
     */
    private static ProtocolIdentifier PROTOCOL_IDENTIFIER;

    // Variables.

    /**
     * A tag for the protocol to allow its identification among all other protocols which can have the same class.
     */
    private final String protocolTag;

    /**
     * The protocol manipulator. Must be not null.
     */
    private ProtocolManipulator protocolManipulator;

    // Constructors.

    /**
     * Create a protocol with a tag and a protocol manipulator which not be null. Throws a {@link NullPointerException}
     * if the protocol tag or the protocol manipulator is null.
     * <p>
     * This constructor must always call by inherited class in their constructor. An inherited class of
     * {@link Protocol} must always have the same form of constructor: <strong>Protocol(Sting protocolTag, String[]
     * <p>
     * args)</strong>.
     *
     * @param protocolTag         the tag of the protocol (must be not null)
     * @param protocolManipulator the protocol manipulator (must be not null)
     * @throws NullPointerException if the protocol tag and/or the protocol manipulator is null
     */
    protected Protocol(String protocolTag, ProtocolManipulator protocolManipulator, String[] args) {
        this.protocolTag = protocolTag;
        if (this.protocolTag == null)
            throw new NullPointerException();

        this.protocolManipulator = Optional.of(protocolManipulator).get();

        if (args != null)
            this.processArgument(args);
    }

    // Methods.

    /**
     * Method called in the constructors. It is this method which make all treatment associated to all arguments
     * received.
     *
     * @param args arguments array
     */
    protected abstract void processArgument(String[] args);

    /**
     * Returns the {@link ProtocolIdentifier} of the protocol. The protocol identificator must allow an agent to
     * identify which protocol is called and for two different agents which use the same set of protocols, for a same
     * instance of a {@link ProtocolIdentifier}, the method {@link AbstractAgent#getProtocol(ProtocolIdentifier)}
     * must returns the same protocol for both agents.
     * <p>
     * The base implementation is the use of a singleton of {@link ProtocolIdentifier} instantiates at the first call
     * of the method.
     * <p>
     * This method is thread safe and the thread lock is the static variable {@link #PROTOCOL_IDENTIFIER_LOCK}.
     *
     * @return the {@link ProtocolIdentifier} of the protocol. It never returns null.
     */
    public ProtocolIdentifier getIdentifier() {
        synchronized (PROTOCOL_IDENTIFIER_LOCK) {
            if (PROTOCOL_IDENTIFIER == null) {
                PROTOCOL_IDENTIFIER = new ProtocolIdentifier(this.getClass().getName(), this.protocolTag);
            }

            return PROTOCOL_IDENTIFIER;
        }
    }

    /**
     * Reset the default manipulator of the protocol. In that way, the property {@link #protocolManipulator} is never
     * null.
     */
    public abstract void resetDefaultProtocolManipulator();

    // Getters and Setters.

    public String getProtocolTag() {
        return protocolTag;
    }

    public ProtocolManipulator getProtocolManipulator() {
        return protocolManipulator;
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
        this.protocolManipulator = Optional.of(protocolManipulator).get();
        this.protocolManipulator.setManipulatedProtocol(this);
    }
}
