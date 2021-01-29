package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventSender;
import sima.core.exception.NotEvolvingAgentInEnvironmentException;

import java.util.*;

import static sima.core.simulation.SimaSimulation.SIMA_LOG;

/**
 * Represents an {@code Environment} where {@link AbstractAgent} evolves. An {@code Environment} can be the
 * representation of the physic layer of the communication. An {@code Environment} determine if two agents can
 * communicate or not.
 * <p>
 * For example, you can create an {@code Environment} where agent are mobil and move. Each agent has a maximum scope and
 * can communicate only with sima.core.agent which are in this scope.
 *
 * @author guilr
 */
public abstract class Environment implements EventSender {

    // Variables.

    /**
     * The unique name of the sima.core.environment.
     */
    private final String environmentName;

    /**
     * The set of evolving agents.
     */
    private final Set<AgentIdentifier> evolvingAgents;

    // Constructors.

    /**
     * Constructs an {@link Environment} with an unique name and an map of arguments.
     * <p>
     * All inherited classes must have this constructor to allow the use of the java reflexivity.
     *
     * @param environmentName the sima.core.environment name
     * @param args            arguments map (map argument name with the argument)
     */
    @SuppressWarnings("unused")
    protected Environment(String environmentName, Map<String, String> args) {
        this.environmentName = Optional.of(environmentName).get();
        evolvingAgents = new HashSet<>();
    }

    // Methods.

    @Override
    public String toString() {
        return "[Environment - " +
                "class=" + this.getClass().getName() +
                ", environmentName=" + environmentName + "]";
    }

    /**
     * Add the sima.core.agent in the sima.core.environment. The sima.core.agent can be not accept in the {@link
     * Environment}, in that case, the methods returns false. If the sima.core.agent has already been accepted in the
     * {@code Environment}, this method does not accept the {@code AgentIdentifier} and returns false.
     * <p>
     * In this method, only the environment has conscience that the agent is evolving in it. For that the agent be
     * notify that it is evolving in the environment, it is better to use the method {@link
     * AbstractAgent#joinEnvironment(Environment)}. In this method, the agent and the environment are both conscience
     * that the agent is evolving in the environment.
     * <p>
     * If this method is called and that the concerned agent is not conscience that it has been accepted and it is now
     * evolving in the environment, then you can use the method {@link AbstractAgent#setEvolvingInEnvironment(Environment)}
     * which set the environment in the agent for that the agent know that it is evolving in the environment.
     *
     * @param evolvingAgentIdentifier the {@link AgentIdentifier} of the agent which will evolve in the environment
     * @return true if the {@code Environment} accept the sima.core.agent, else false.
     */
    public synchronized boolean acceptAgent(AgentIdentifier evolvingAgentIdentifier) {
        if (evolvingAgentIdentifier != null && agentCanBeAccepted(evolvingAgentIdentifier)) {
            boolean added = evolvingAgents.add(evolvingAgentIdentifier);
            if (added)
                SIMA_LOG.info("Agent with identifier = " + evolvingAgentIdentifier + " JOIN " + this);

            return added;
        } else {
            return false;
        }
    }

    /**
     * Verifies if the agent can be accepted and evolving in the {@link Environment}. This method is called in the
     * method {@link #acceptAgent(AgentIdentifier)}.
     *
     * @param abstractAgentIdentifier the {@link AgentIdentifier} of the agent to verify
     * @return true if the  can be accepted in the sima.core.environment, else false.
     * @see #acceptAgent(AgentIdentifier)
     */
    protected abstract boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier);

    /**
     * Make that the sima.core.agent is leaving the sima.core.environment. If the sima.core.agent is not evolving in the
     * {@link Environment}, nothing is done. Calls the method {@link #agentIsLeaving(AgentIdentifier)} before remove the
     * agent from the {@code Environment}.
     * <p>
     * In this method the agent is not notify that it is leaving the environment. For that the agent be conscience that
     * it leaves the environment, it's recommended to use the method {@link AbstractAgent#leaveEnvironment(Environment)}
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
     * Call back method called when an agent is leaving the {@link Environment}, in other word, when the method {@link
     * #leave(AgentIdentifier)} is called. This method is called before the sima.core.agent is removed from the list of
     * evolving agent.
     *
     * @param leavingAgentIdentifier the leaving sima.core.agent
     */
    protected abstract void agentIsLeaving(AgentIdentifier leavingAgentIdentifier);

    /**
     * Verifies if the agent is evolving in the {@link Environment}. An agent is evolving in the {@code Environment} if
     * it is in the list of evolving sima.core.agent, therefore it possible to verify if an agent is evolving in the
     * {@code Environment} by calling the method {@link #getEvolvingAgentIdentifiers()} and see if the agent is
     * contained is the returned list.
     *
     * @param agent the agent to verify
     * @return true if the agent is evolving in the {@code Environment}, else false.
     * @see #getEvolvingAgentIdentifiers()
     */
    public synchronized boolean isEvolving(AgentIdentifier agent) {
        return agent != null && evolvingAgents.contains(agent);
    }

    /**
     * @return the list of all {@link AgentIdentifier} of all agents evolving in the sima.core.environment, if there is
     * no sima.core.agent, returns an empty list but never null.
     */
    public synchronized List<AgentIdentifier> getEvolvingAgentIdentifiers() {
        return evolvingAgents.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Send an {@link Event} to the {@link Event#getReceiver()}.
     * <p>
     * This method verifies in first if the {@link Event#getSender()} is evolving in the sima.core.environment and do
     * the same for the receiver. If it is not the case, a {@link NotEvolvingAgentInEnvironmentException} is thrown.
     * <p>
     * After that, if the receiver is not null, then the event is destined to one sima.core.agent and the function
     * {@link #verifyAndSendEvent(AgentIdentifier, Event)} is called to try to send the {@code Event} to the
     * sima.core.agent receiver. If the receiver is null, throws {@link IllegalArgumentException}.
     *
     * @param event the event to send
     * @throws NotEvolvingAgentInEnvironmentException if the sender and/or the receiver agent are not evolving in the
     *                                                {@link Environment}.
     * @throws IllegalArgumentException               if the event has no receiver
     * @throws NullPointerException                   if the event is null
     */
    @Override
    public synchronized void sendEvent(Event event) {
        if (isEvolving(event.getSender()))
            if (event.getReceiver() == null)
                // No receiver for the event -> broadcast event
                throw new IllegalArgumentException("Event receiver null -> Impossible to known where the event "
                                                           + "must be sent");
            else
                // Event destined for one identified agent.
                // getAgent() detects if the sima.core.agent is evolving or not in the sima.core.environment
                if (isEvolving(event.getReceiver()))
                    verifyAndSendEvent(event.getReceiver(), event);
                else
                    throw new NotEvolvingAgentInEnvironmentException(
                            "The receiver agent " + event.getReceiver() + " is "
                                    + "not evolving in the environment " + getEnvironmentName());
        else
            throw new NotEvolvingAgentInEnvironmentException(
                    "The sender sima.core.agent " + event.getSender() + " is not "
                            + "evolving in the sima.core.environment " + getEnvironmentName());
    }

    /**
     * This method broadcast the event to all agents which are "physically" connected to the event sender.
     * <p>
     * This method does not take care about the event receiver. It will search all agents in the environment which are
     * physically connected to the event sender and send the event to it.
     *
     * @param event the event to spray
     * @throws NullPointerException                   if the event is null
     * @throws NotEvolvingAgentInEnvironmentException if the sender is not evolving in the environment
     */
    @Override
    public synchronized void broadcastEvent(Event event) {
        if (isEvolving(event.getSender()))
            evolvingAgents.forEach(agentIdentifier -> verifyAndSendEvent(agentIdentifier,
                                                                         event.cloneAndSetReceiver(
                                                                                 agentIdentifier)));
        else
            throw new NotEvolvingAgentInEnvironmentException(
                    "The sender sima.core.agent " + event.getSender() + " is not "
                            + "evolving in the sima.core.environment " + getEnvironmentName());
    }

    /**
     * This method verifies if it is possible to send the event to the specified {@link AbstractAgent} from the event
     * sender. Return true if the event can "physically" be sent to the receiver from the sender, else false.
     * <p>
     * It is in this method that is simulate physical connection between each agent. Therefore, if this method returns
     * false for agent receiver B and an Event with the agent sender A, it is because in the simulation, agents A and B
     * or not physically connected.
     * <p>
     * By convention, we consider that an agent is always physically connected with itself, therefore this method must
     * always returns true if the specified receiver is equal to the event sender.
     * <p>
     * This method must not pay attention to the event receiver.
     *
     * @param receiver the agent receiver
     * @param event    the event to send to the receiver
     * @return true if the event can be sent to the receiver from the sender, else false.
     */
    public abstract boolean eventCanBeSentTo(AgentIdentifier receiver, Event event);

    /**
     * Schedules the moment when the sima.core.agent receiver will receive the event. In other words, schedules the
     * moment when the sima.core.agent receiver will call the method {@link AbstractAgent#processEvent(Event)}.
     *
     * @param receiver the sima.core.agent receiver
     * @param event    the event to send to the receiver
     */
    protected abstract void scheduleEventReception(AgentIdentifier receiver, Event event);

    /**
     * First verifies if the event can be sent to the sima.core.agent receiver with the function {@link
     * #eventCanBeSentTo(AgentIdentifier, Event)}. If it is the case, calls the function {@link
     * #scheduleEventReception(AgentIdentifier, Event)} to schedule the moment when the sima.core.agent receiver will
     * receive the {@code Event}.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} when the sender has been correctly identified and
     * that the receiver of the {@code Event} is not null.
     *
     * @param receiver the sima.core.agent receiver
     * @param event    the event to receiver
     * @see #sendEvent(Event)
     * @see #eventCanBeSentTo(AgentIdentifier, Event)
     * @see #scheduleEventReception(AgentIdentifier, Event)
     */
    protected void verifyAndSendEvent(AgentIdentifier receiver, Event event) {
        if (eventCanBeSentTo(receiver, event))
            scheduleEventReception(receiver, event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Environment)) return false;
        Environment that = (Environment) o;
        return environmentName.equals(that.environmentName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(environmentName);
    }

    // Getters ans Setters.

    /**
     * @return the unique name of the sima.core.environment, cannot be null.
     */
    public String getEnvironmentName() {
        return environmentName;
    }
}