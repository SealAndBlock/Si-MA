package sima.core.environment.physical;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;

import java.util.Map;
import java.util.Optional;

/**
 * Represent the physical connection layer in an {@link Environment}. An {@link Environment} can have several {@link PhysicalConnectionLayer} to
 * simulate different ways that how {@link sima.core.agent.SimaAgent} are physically connected. Thanks to this layer, we can for example simulate
 * the radio connection between {@link sima.core.agent.SimaAgent} and physically block the possibility to send of {@link PhysicalEvent} if two
 * {@link sima.core.agent.SimaAgent} are not physically connected.
 */
public abstract class PhysicalConnectionLayer {
    
    // Variables.
    
    private final Environment environment;
    
    private PhysicalConnectionLayer next;
    
    // Constructors.
    
    /**
     * Create a {@link PhysicalConnectionLayer} with an associated {@link Environment}.
     * <p>
     * The {@link Environment} cannot be null.
     *
     * @param environment the environment
     * @param args        arguments
     *
     * @throws IllegalArgumentException if the environment is null.
     */
    protected PhysicalConnectionLayer(Environment environment, Map<String, String> args) {
        this.environment = Optional.ofNullable(environment).orElseThrow(() -> new IllegalArgumentException("The environment cannot be null"));
    }
    
    // Methods.
    
    /**
     * Try to send the {@link PhysicalEvent} from the initiator agent to the target agent.
     * <p>
     * The algorithm used is simple. In first the {@link PhysicalConnectionLayer} decorates if needed the {@link PhysicalEvent}. After that, the
     * method {@link #canBeSent(AgentIdentifier, AgentIdentifier, PhysicalEvent)} is called to verify if the decorated {@link PhysicalEvent} can
     * be sent to the target. If it is the case then verify if the {@link PhysicalConnectionLayer} has a nextb in the {@link
     * PhysicalConnectionLayer} chain. Call this method send to the next if there is a next, else call the method {@link
     * #scheduleInEnvironment(AgentIdentifier, PhysicalEvent)} to schedule the {@link PhysicalEvent} via the {@link Environment} methods to send
     * {@link sima.core.environment.event.Event} like {@link Environment#assignEventOn(AgentIdentifier, Event, long)}.
     *
     * <pre>{@code
     * public void send(initiator, target, physicalEvent) {
     *      PhysicalEvent decoratedPhysicalEvent = decoratePhysicalEvent(physicalEvent);
     *      boolean success = canBeSent(initiator, target, decoratedPhysicalEvent);
     *      if (hasNext() && success)
     *          next.send(initiator, target, decoratedPhysicalEvent);
     *      else if (success)
     *          scheduleInEnvironment(target, decoratedPhysicalEvent);
     *      // else stop the chain.
     * }
     * }</pre>
     *
     * @param initiator     the initiator of the {@link PhysicalEvent}
     * @param target        the target of the {@link PhysicalEvent}
     * @param physicalEvent the {@link PhysicalEvent}
     *
     * @throws IllegalArgumentException if the initiator, target or physicalEvent is null
     */
    public void send(AgentIdentifier initiator, AgentIdentifier target, PhysicalEvent physicalEvent) {
        initiator = Optional.ofNullable(initiator).orElseThrow(() -> new IllegalArgumentException("The initiator cannot be null"));
        target = Optional.ofNullable(target).orElseThrow(() -> new IllegalArgumentException("The target cannot be null"));
        physicalEvent = Optional.ofNullable(physicalEvent).orElseThrow(() -> new IllegalArgumentException("The physicalEvent cannot be null"));
        
        var decoratedPhysicalEvent = decoratePhysicalEvent(physicalEvent);
        boolean success = canBeSent(initiator, target, decoratedPhysicalEvent);
        if (hasNext() && success)
            next.send(initiator, target, decoratedPhysicalEvent);
        else if (success)
            scheduleInEnvironment(target, decoratedPhysicalEvent);
        // else stop the chain.
    }
    
    /**
     * Allow the user to decorate the {@link PhysicalEvent} specified in the method {@link #send(AgentIdentifier, AgentIdentifier,
     * PhysicalEvent)}.
     *
     * @param physicalEvent the {@link PhysicalEvent} to decorate
     *
     * @return a {@link PhysicalEvent} which must be the specified {@link PhysicalEvent} with decoration are just the same {@link PhysicalEvent}.
     */
    protected abstract @NotNull PhysicalEvent decoratePhysicalEvent(PhysicalEvent physicalEvent);
    
    /**
     * @return true if the {@link PhysicalConnectionLayer} has a next. (if next is not null).
     */
    private boolean hasNext() {
        return next != null;
    }
    
    /**
     * @param a1 the agent a1
     * @param a2 the agent a2
     *
     * @return true if for this {@link PhysicalConnectionLayer}, both {@link sima.core.agent.SimaAgent} have physical connection between them.
     *
     * @throws IllegalArgumentException if a1 or a2 is null.
     */
    public abstract boolean hasPhysicalConnection(AgentIdentifier a1, AgentIdentifier a2);
    
    /**
     * Verify if the {@link PhysicalEvent} can be sent from the initiator to the target. This method is called in {@link #send(AgentIdentifier,
     * AgentIdentifier, PhysicalEvent)} and can do more than just verify if both {@link sima.core.agent.SimaAgent} have physical connection with
     * the method {@link #hasPhysicalConnection(AgentIdentifier, AgentIdentifier)}.
     *
     * @param initiator     the initiator of the {@link PhysicalEvent}
     * @param target        the target of the {@link PhysicalEvent}
     * @param physicalEvent the {@link PhysicalEvent}
     *
     * @return true if the {@link PhysicalEvent} can be sent to the target, else false.
     */
    protected abstract boolean canBeSent(AgentIdentifier initiator, AgentIdentifier target, PhysicalEvent physicalEvent);
    
    /**
     * Schedule the {@link PhysicalEvent} by using {@link Environment} methods which takes care of schedule {@link Event} like the method {@link
     * Environment#assignEventOn(AgentIdentifier, Event, long)}
     *
     * @param target        the agent which must receive the {@link PhysicalEvent}
     * @param physicalEvent the {@link PhysicalEvent}
     */
    protected abstract void scheduleInEnvironment(AgentIdentifier target, PhysicalEvent physicalEvent);
    
    // Getters and Setters.
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public PhysicalConnectionLayer getNext() {
        return next;
    }
    
    public void setNext(PhysicalConnectionLayer next) {
        this.next = next;
    }
}
