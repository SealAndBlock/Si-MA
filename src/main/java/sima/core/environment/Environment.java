package sima.core.environment;

import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventGenerator;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.exception.NotEvolvingAgentInEnvironmentException;

import java.util.*;

import static sima.core.simulation.SimaSimulation.SimaLog;

/**
 * Represents an {@code Environment} where {@link SimaAgent} evolves. An {@code Environment} can be the representation of the physic layer of the
 * communication. An {@code Environment} determine if two agents can communicate or not.
 * <p>
 * For example, you can create an {@code Environment} where agent are mobil and move. Each agent has a maximum scope and can communicate only
 * with sima.core.agent which are in this scope.
 *
 * @author guilr
 */
public abstract class Environment implements EventGenerator {
    
    // Variables.
    
    /**
     * The unique name of the {@link Environment}.
     */
    private final String environmentName;
    
    /**
     * The set of evolving {@link SimaAgent}.
     */
    private final Set<AgentIdentifier> evolvingAgents;
    
    /**
     * The map of {@link PhysicalConnectionLayer}.
     */
    private final Map<String, PhysicalConnectionLayer> physicalConnectionLayers;
    
    // Constructors.
    
    /**
     * Constructs an {@link Environment} with a unique name and a map of arguments.
     * <p>
     * All inherited classes must have this constructor to allow the use of the java reflexivity.
     *
     * @param environmentName the sima.core.environment name
     * @param args            arguments map (map argument name with the argument)
     */
    protected Environment(String environmentName, Map<String, String> args) {
        this.environmentName = Optional.of(environmentName).get();
        evolvingAgents = new HashSet<>();
        physicalConnectionLayers = new HashMap<>();
    }
    
    // Methods.
    
    @Override
    public String toString() {
        return "[Environment - " +
                "class=" + this.getClass().getName() +
                ", environmentName=" + environmentName + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Environment that)) return false;
        return getEnvironmentName().equals(that.getEnvironmentName());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getEnvironmentName());
    }
    
    /**
     * Add the sima.core.agent in the sima.core.environment. The sima.core.agent can be not accept in the {@link Environment}, in that case, the
     * methods returns false. If the sima.core.agent has already been accepted in the {@code Environment}, this method does not accept the {@code
     * AgentIdentifier} and returns false.
     * <p>
     * In this method, only the environment has conscience that the agent is evolving in it. For that the agent be notified that it is evolving
     * in the environment, it is better to use the method {@link SimaAgent#joinEnvironment(Environment)}. In this method, the agent and the
     * environment are both conscience that the agent is evolving in the environment.
     *
     * @param agentIdentifier the {@link AgentIdentifier} of the agent which will evolve in the environment
     *
     * @return true if the {@code Environment} accept the sima.core.agent, else false.
     */
    public synchronized boolean acceptAgent(AgentIdentifier agentIdentifier) {
        if (agentIdentifier != null && !isEvolving(agentIdentifier) &&
                agentCanBeAccepted(agentIdentifier)) {
            boolean added = evolvingAgents.add(agentIdentifier);
            if (added)
                SimaLog.info("Agent with identifier = " + agentIdentifier + " JOIN " + this);
            
            return added;
        } else {
            return false;
        }
    }
    
    /**
     * Verifies if the agent can be accepted and evolving in the {@link Environment}. This method is called in the method {@link
     * #acceptAgent(AgentIdentifier)}.
     *
     * @param abstractAgentIdentifier the {@link AgentIdentifier} of the agent to verify
     *
     * @return true if the agent can be accepted in the sima.core.environment, else false.
     *
     * @see #acceptAgent(AgentIdentifier)
     */
    protected abstract boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier);
    
    /**
     * Make that the sima.core.agent is leaving the sima.core.environment. If the sima.core.agent is not evolving in the {@link Environment},
     * nothing is done. Calls the method {@link #agentIsLeaving(AgentIdentifier)} before remove the agent from the {@code Environment}.
     * <p>
     * In this method the agent is not notify that it is leaving the environment. For that the agent be conscience that it leaves the
     * environment, it's recommended to use the method {@link SimaAgent#leaveEnvironment(Environment)}
     *
     * @param leavingAgentIdentifier the leaving sima.core.agent
     */
    public synchronized void leave(AgentIdentifier leavingAgentIdentifier) {
        if (isEvolving(leavingAgentIdentifier)) {
            agentIsLeaving(leavingAgentIdentifier);
            evolvingAgents.remove(leavingAgentIdentifier);
        }
    }
    
    /**
     * Call back method called when an agent is leaving the {@link Environment}, in other word, when the method {@link #leave(AgentIdentifier)}
     * is called. This method is called before the sima.core.agent is removed from the list of evolving agent.
     *
     * @param leavingAgentIdentifier the leaving sima.core.agent
     */
    protected abstract void agentIsLeaving(AgentIdentifier leavingAgentIdentifier);
    
    /**
     * Verifies if the agent is evolving in the {@link Environment}. An agent is evolving in the {@code Environment} if it is in the list of
     * evolving sima.core.agent, therefore it is possible to verify if an agent is evolving in the {@code Environment} by calling the method
     * {@link #getEvolvingAgentIdentifiers()} and see if the agent is contained is the returned list.
     *
     * @param agent the agent to verify
     *
     * @return true if the agent is evolving in the {@code Environment}, else false.
     *
     * @see #getEvolvingAgentIdentifiers()
     */
    public synchronized boolean isEvolving(AgentIdentifier agent) {
        return agent != null && evolvingAgents.contains(agent);
    }
    
    /**
     * @return the list of all {@link AgentIdentifier} of all agents evolving in the sima.core.environment, if there is no sima.core.agent,
     * returns an empty list but never null.
     */
    public synchronized List<AgentIdentifier> getEvolvingAgentIdentifiers() {
        return evolvingAgents.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Schedule the call of the method {@link sima.core.environment.event.EventProcessor#processEvent(Event)} of the {@link SimaAgent} targeted.
     *
     * @param event  the event to process
     * @param target the agent targeted
     * @param delay  the delay to wait before call the method processEvent
     *
     * @throws NotEvolvingAgentInEnvironmentException if the sender and/or the receiver agent are not evolving in the {@link Environment}.
     * @throws NullPointerException                   if the event is null
     */
    @Override
    public synchronized void processEventOn(AgentIdentifier target, Event event, long delay) {
        if (isEvolving(Optional.of(target).get())) {
            scheduleEventReception(target, event, delay);
        } else {
            throw new NotEvolvingAgentInEnvironmentException(
                    "The target " + target + " is not evolving in the environment" + this);
        }
    }
    
    /**
     * Schedules the moment when the sima.core.agent receiver will receive the event. In other words, schedules the moment when the {@link
     * SimaAgent} receiver will call the method {@link SimaAgent#processEvent(Event)}.
     *
     * @param receiver the sima.core.agent receiver
     * @param event    the event to send to the receiver
     * @param delay    the delay before the method processEvent is called
     *
     * @throws IllegalArgumentException if the delay is less than {@link sima.core.scheduler.Scheduler#NOW}
     */
    protected abstract void scheduleEventReception(AgentIdentifier receiver, Event event, long delay);
    
    /**
     * Try to map the specified {@link PhysicalConnectionLayer} with the specified name. If there is already a {@link PhysicalConnectionLayer}
     * mapped with the specified name, do nothing and returns false.
     *
     * @param name                    the name of the {@link PhysicalConnectionLayer}
     * @param physicalConnectionLayer the {@link PhysicalConnectionLayer}
     *
     * @return true if the {@link PhysicalConnectionLayer} has been mapped with the name, else false.
     *
     * @throws NullPointerException if name or physicalConnectionLayer is null
     */
    public boolean addPhysicalConnectionLayer(String name, PhysicalConnectionLayer physicalConnectionLayer) {
        return physicalConnectionLayers.putIfAbsent(
                Optional.of(name).get(),
                Optional.of(physicalConnectionLayer).get())
                == null;
    }
    
    /**
     * @param name the {@link PhysicalConnectionLayer} name
     *
     * @return the {@link PhysicalConnectionLayer} mapped with the specified name. If there is no {@link PhysicalConnectionLayer} mapped to the
     * specified name, returns null.
     */
    public PhysicalConnectionLayer getPhysicalConnectionLayer(String name) {
        return physicalConnectionLayers.get(name);
    }
    
    // Getters ans Setters.
    
    /**
     * @return the unique name of the sima.core.environment, cannot be null.
     */
    public String getEnvironmentName() {
        return environmentName;
    }
}