package sima.core.protocol;

import sima.core.agent.SimaAgent;
import sima.core.behavior.Behavior;
import sima.core.environment.event.EventProcessor;
import sima.core.environment.event.transport.EventTransportableProcessor;

import java.util.Map;
import java.util.Optional;

/**
 * This class represents a sima.core.protocol. A sima.core.protocol is an algorithm that an {@link SimaAgent} can use and thanks to the {@link
 * ProtocolManipulator}, the sima.core.agent via the {@link Behavior} can change the sima.core.behavior of the sima.core.protocol by changing the
 * current {@link #protocolManipulator}.
 * <p>
 * All inherited class of {@link Protocol} must have this constructor <strong>Protocol(String protocolTag, String[] args)</strong>. In that way,
 * it allows to use the java reflexivity.
 */
public abstract class Protocol implements EventProcessor, EventTransportableProcessor {
    
    // Singletons.
    
    /**
     * The {@link ProtocolIdentifier} of the sima.core.protocol.
     */
    private ProtocolIdentifier protocolIdentifier;
    
    // Variables.
    
    /**
     * A tag for the {@link Protocol} to allow its identification among all other protocols which can have the same class.
     * <p>
     * In other word, if a {@link SimaAgent} use two instance of a same sima.core.protocol class, both protocols must imperatively have two
     * different tag.
     */
    private final String protocolTag;
    
    private final SimaAgent agentOwner;
    
    /**
     * The default protocol manipulator.
     */
    private final ProtocolManipulator defaultProtocolManipulator;
    
    /**
     * The sima.core.protocol manipulator. Must be not null.
     */
    private ProtocolManipulator protocolManipulator;
    
    // Constructors.²
    
    /**
     * Create a {@link Protocol} with a unique tag, an agent owner and an map of arguments.
     * <p>
     * This constructors set the protocolManipulator with the method {@link #createDefaultProtocolManipulator()}. This method must never returns
     * null, however, if it is the case, a {@link NullPointerException} is thrown.
     * <p>
     * If the tag or the agent owner is null, throws a {@link NullPointerException}.
     * <p>
     * This constructor must always be implemented by inherited class. In that way, the java reflexivity can be used.
     *
     * @param protocolTag the tag of the sima.core.protocol (must be not null)
     * @param agentOwner  the agent which use the instance of the protocol (must be not null)
     * @param args        arguments map (map argument name with the argument)
     *
     * @throws NullPointerException if the sima.core.protocol tag or the sima.core.protocol manipulator or the agent owner is null.
     */
    protected Protocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        this.protocolTag = Optional.of(protocolTag).get();
        this.agentOwner = Optional.of(agentOwner).get();
        defaultProtocolManipulator = Optional.of(createDefaultProtocolManipulator()).get();
        protocolManipulator = defaultProtocolManipulator;
    }
    
    // Methods.
    
    @Override
    public String toString() {
        return "Protocol [" +
                "protocolIdentifier=" + protocolIdentifier +
                ", protocolTag=" + protocolTag +
                ", agentOwner=" + agentOwner +
                ", protocolManipulator=" + protocolManipulator +
                ']';
    }
    
    /**
     * Returns the {@link ProtocolIdentifier} of the sima.core.protocol. The sima.core.protocol identifier must allow an sima.core.agent to
     * identify which sima.core.protocol is called and for two different agents which use the same set of protocols, for a same instance of a
     * {@link ProtocolIdentifier}, the method {@link SimaAgent#getProtocol(ProtocolIdentifier)} must returns the same sima.core.protocol for both
     * agents.
     *
     * @return the {@link ProtocolIdentifier} of the sima.core.protocol. It never returns null.
     */
    public ProtocolIdentifier getIdentifier() {
        if (protocolIdentifier == null)
            protocolIdentifier = new ProtocolIdentifier(getClass(), protocolTag);
        
        return protocolIdentifier;
    }
    
    /**
     * Returns the default sima.core.protocol manipulator of the sima.core.protocol. This method never returns null. If the implementation is not
     * correct and this method returns null, the risk is that some methods throw a {@link NullPointerException}.
     * <p>
     * If subclasses do not return a non null ProtocolManipulator, the constructor {@link Protocol#Protocol(String, SimaAgent, Map)} will throws
     * a NullPointerException.
     *
     * @return the default sima.core.protocol manipulator of the sima.core.protocol, never returns null.
     *
     * @see Protocol#Protocol(String, SimaAgent, Map)
     */
    protected abstract ProtocolManipulator createDefaultProtocolManipulator();
    
    /**
     * Reset the default manipulator of the sima.core.protocol. In that way, the property {@link #protocolManipulator} is never null.
     */
    public void resetDefaultProtocolManipulator() {
        protocolManipulator = defaultProtocolManipulator;
    }
    
    // Getters and Setters.
    
    public String getProtocolTag() {
        return protocolTag;
    }
    
    public SimaAgent getAgentOwner() {
        return agentOwner;
    }
    
    public ProtocolManipulator getDefaultProtocolManipulator() {
        return defaultProtocolManipulator;
    }
    
    public ProtocolManipulator getProtocolManipulator() {
        return protocolManipulator;
    }
    
    /**
     * Set {@link #protocolManipulator}. In addition to this, the method {@link ProtocolManipulator#setManipulatedProtocol(Protocol)} is called
     * to set as manipulated sima.core.protocol for the new sima.core.protocol manipulator the current sima.core.protocol.
     *
     * @param protocolManipulator the sima.core.protocol manipulator (must be not null)
     *
     * @throws NullPointerException if the sima.core.protocol manipulator is null
     */
    public void setProtocolManipulator(ProtocolManipulator protocolManipulator) {
        this.protocolManipulator = Optional.of(protocolManipulator).get();
        this.protocolManipulator.setManipulatedProtocol(this);
        this.protocolManipulator.resetState();
    }
}
