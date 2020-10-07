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
     * The {@link ProtocolIdentifier} of the protocol.
     */
    private ProtocolIdentifier protocolIdentifier;

    // Variables.

    /**
     * A tag for the protocol to allow its identification among all other protocols which can have the same class.
     * <p>
     * In other word, if an agent use two instance of a same protocol class, both protocols must imperatively have
     * two different tag.
     */
    private final String protocolTag;

    /**
     * The protocol manipulator. Must be not null.
     */
    private ProtocolManipulator protocolManipulator;

    // Constructors.

    /**
     * Create a protocol with a tag and an array of arguments.
     * <p>
     * This constructors set the protocolManipulator with the method {@link #getDefaultProtocolManipulator()}. This
     * method must never returns null, however, if it is the case, a {@link NullPointerException} is thrown.
     * <p>
     * If the tag is null, throws a {@link NullPointerException}.
     * <p>
     * This constructor must always be implemented by inherited class. In that way, the java reflexivity can be used.
     *
     * @param protocolTag the tag of the protocol (must be not null)
     * @param args        the array of arguments to transfer to the protocol
     * @throws NullPointerException if the protocol tag and/or the protocol manipulator is null
     */
    protected Protocol(String protocolTag, String[] args) {
        this.protocolTag = Optional.of(protocolTag).get();

        this.protocolManipulator = Optional.of(this.getDefaultProtocolManipulator()).get();

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
     * Returns the {@link ProtocolIdentifier} of the protocol. The protocol identifier must allow an agent to
     * identify which protocol is called and for two different agents which use the same set of protocols, for a same
     * instance of a {@link ProtocolIdentifier}, the method {@link AbstractAgent#getProtocol(ProtocolIdentifier)}
     * must returns the same protocol for both agents.
     *
     * @return the {@link ProtocolIdentifier} of the protocol. It never returns null.
     */
    public ProtocolIdentifier getIdentifier() {
        if (this.protocolIdentifier == null) {
            this.protocolIdentifier = new ProtocolIdentifier(this.getClass().getName(), this.protocolTag);
        }

        return protocolIdentifier;
    }

    /**
     * Returns the default protocol manipulator of the protocol. This method never returns null. If the implementation
     * is not correct and this method returns null, the risk is that some methods throw a {@link NullPointerException}.
     *
     * @return the default protocol manipulator of the protocol, never returns null.
     */
    protected abstract ProtocolManipulator getDefaultProtocolManipulator();

    /**
     * Reset the default manipulator of the protocol. In that way, the property {@link #protocolManipulator} is never
     * null.
     */
    public void resetDefaultProtocolManipulator() {
        this.protocolManipulator = Optional.of(this.getDefaultProtocolManipulator()).get();
    }

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
