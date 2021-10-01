package sima.core.protocol;

import sima.core.agent.SimaAgent;
import sima.core.behavior.Behavior;
import sima.core.environment.event.EventProcessor;

import java.util.Map;
import java.util.Optional;

/**
 * This class represents a {@link Protocol}. A {@link Protocol} is an algorithm that an {@link SimaAgent} can use and thanks to the {@link
 * ProtocolManipulator}, the {@link SimaAgent} via the {@link Behavior} can change the {@link Behavior} of the {@link Protocol} by changing the
 * current {@link #protocolManipulator}.
 * <p>
 * All inherited class of {@link Protocol} must have this constructor <strong>Protocol(String protocolTag, String[] args)</strong>. In that way,
 * it allows to use the java reflexivity.
 */
public abstract class Protocol implements EventProcessor {
    
    // Singletons.
    
    /**
     * The {@link ProtocolIdentifier} of the {@link Protocol}.
     */
    private ProtocolIdentifier protocolIdentifier;
    
    // Variables.
    
    /**
     * A tag for the {@link Protocol} to allow its identification among all other protocols which can have the same class.
     * <p>
     * In other word, if a {@link SimaAgent} use two instance of a same {@link Protocol} class, both protocols must imperatively have two
     * different tag.
     */
    private final String protocolTag;
    
    private final SimaAgent agentOwner;
    
    /**
     * The default protocol manipulator.
     */
    private final ProtocolManipulator defaultProtocolManipulator;
    
    /**
     * The {@link Protocol} manipulator. Must be not null.
     */
    private ProtocolManipulator protocolManipulator;
    
    // Constructors.²
    
    /**
     * Create a {@link Protocol} with a unique tag, an agent owner and a map of arguments.
     * <p>
     * This constructors set the protocolManipulator with the method {@link #createDefaultProtocolManipulator()}. This method must never return
     * null, however, if it is the case, a {@link NullPointerException} is thrown.
     * <p>
     * If the tag or the agent owner is null, throws a {@link NullPointerException}.
     * <p>
     * This constructor must always be implemented by inherited class. In that way, the java reflexivity can be used.
     *
     * @param protocolTag the tag of the {@link Protocol} (must be not null)
     * @param agentOwner  the agent which use the instance of the protocol (must be not null)
     * @param args        arguments map (map argument name with the argument)
     *
     * @throws IllegalArgumentException if protocol or agentOwner is null or if {@link #createDefaultProtocolManipulator()} return null
     */
    protected Protocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        this.protocolTag = Optional.ofNullable(protocolTag).orElseThrow(() -> new IllegalArgumentException("The protocolTag cannot be null"));
        this.agentOwner = Optional.ofNullable(agentOwner).orElseThrow(() -> new IllegalArgumentException("The agentOwner cannot be null"));
        defaultProtocolManipulator = Optional.ofNullable(createDefaultProtocolManipulator()).orElseThrow(() -> new IllegalArgumentException(
                "The method createDefaultProtocolManipulator must not return null"));
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
     * Returns the {@link ProtocolIdentifier} of the {@link Protocol}. The {@link Protocol} identifier must allow a {@link SimaAgent} to identify
     * which {@link Protocol} is called and for two different agents which use the same set of protocols, for a same instance of a {@link
     * ProtocolIdentifier}, the method {@link SimaAgent#getProtocol(ProtocolIdentifier)} must return the same {@link Protocol} for both agents.
     *
     * @return the {@link ProtocolIdentifier} of the {@link Protocol}. It never returns null.
     */
    public ProtocolIdentifier getIdentifier() {
        if (protocolIdentifier == null)
            protocolIdentifier = new ProtocolIdentifier(getClass(), protocolTag);
        
        return protocolIdentifier;
    }
    
    /**
     * Returns the default {@link Protocol} manipulator of the {@link Protocol}. This method never returns null. If the implementation is not
     * correct and this method returns null, the risk is that some methods throw a {@link NullPointerException}.
     * <p>
     * If subclasses do not return a non-null {@link ProtocolManipulator}, the constructor {@link Protocol#Protocol(String, SimaAgent, Map)} will
     * throw a {@link NullPointerException}.
     *
     * @return the default {@link Protocol} manipulator of the {@link Protocol}, never returns null.
     *
     * @see Protocol#Protocol(String, SimaAgent, Map)
     */
    protected abstract ProtocolManipulator createDefaultProtocolManipulator();
    
    /**
     * Reset the default manipulator of the {@link Protocol}. In that way, the property {@link #protocolManipulator} is never null.
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
     * to set as manipulated {@link Protocol} for the new {@link Protocol} manipulator the current {@link Protocol}.
     *
     * @param protocolManipulator the {@link Protocol} manipulator (must be not null)
     *
     * @throws NullPointerException if the {@link Protocol} manipulator is null
     */
    public void setProtocolManipulator(ProtocolManipulator protocolManipulator) {
        this.protocolManipulator = Optional.of(protocolManipulator).get();
        this.protocolManipulator.setManipulatedProtocol(this);
        this.protocolManipulator.resetState();
    }
}
