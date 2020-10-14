package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventCatcher;
import sima.core.environment.event.Message;
import sima.core.environment.event.NoProtocolEvent;
import sima.core.environment.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.environment.exception.UnknownEventException;

import java.util.*;

/**
 * Represents an {@code Environment} where {@link AbstractAgent} evolves. An {@code Environment} can be the
 * representation of the physic layer of the communication. An {@code Environment} determine if two agents can
 * communicate or not.
 * <p>
 * For example, you can create an {@code Environment} where agent are mobil and move. Each agent has a maximum scope and
 * can communicate only with sima.core.agent which are in this scope.
 * <p>
 * With the method {@link #sendEvent(Event)}, it is possible to send {@link Event}. This method first verifies if the
 * sima.core.agent sender is evolving in the sima.core.environment and after that, the methods
 * {@link #eventCanBeSentTo(AgentIdentifier, Event)} and {@link #scheduleEventReceptionToOneAgent(AgentIdentifier, Event)}
 * are called.
 * <p>
 * First, the method verifies if the {@code Event} can be sent to the specified sima.core.agent receiver, it is in the
 * method that the network and the connection between agents are simulated.
 * <p>
 * If the {@code Event} can be sent to the specified sima.core.agent receiver, therefore the second method is called to
 * schedule the moment when the sima.core.agent receiver will receive the {@code Event}.
 *
 * @author guilr
 */
public abstract class Environment implements EventCatcher {

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
    protected Environment(String environmentName, Map<String, String> args) {
        this.environmentName = environmentName;

        if (this.environmentName == null)
            throw new NullPointerException("The sima.core.environment name cannot be null.");

        this.evolvingAgents = new HashSet<>();

        if (args != null)
            this.processArgument(args);
    }

    // Methods.

    /**
     * Method called in the constructors. It is this method which make all treatment associated to all arguments
     * received.
     *
     * @param args arguments map (map argument name with the argument)
     */
    protected abstract void processArgument(Map<String, String> args);

    /**
     * Add the sima.core.agent in the sima.core.environment. The sima.core.agent can be not accept in the
     * {@link Environment}, in that case, the methods returns false. If the sima.core.agent has already been accepted in
     * the {@code Environment}, this method does not accept the {@code AgentIdentifier} and returns false.
     *
     * <strong>WARNING!</strong> An agent is only identify by the couple {@link AbstractAgent#getUUID()} and
     * {@link AbstractAgent#getAgentName()}. When the environment accept the agent, the only significant values are
     * {@link AgentIdentifier#getAgentUUID()} and {@link AgentIdentifier#getAgentName()}, the other values of
     * {@link AgentIdentifier} can change during the time after the acceptation. In that way, it must be ask the agent
     * directly with the method {@link AbstractAgent#getInfo()} if we want update value.
     *
     * @param evolvingAgentIdentifier the {@link AgentIdentifier} of the agent which will evolve in the environment
     * @return true if the {@code Environment} accept the sima.core.agent, else false.
     */
    public boolean acceptAgent(AgentIdentifier evolvingAgentIdentifier) {
        if (evolvingAgentIdentifier != null && this.agentCanBeAccepted(evolvingAgentIdentifier)) {
            return this.evolvingAgents.add(evolvingAgentIdentifier);
        } else {
            return false;
        }
    }

    /**
     * Verifies if the agent can be accepted and evolving in the {@link Environment}. This method is called
     * in the method {@link #acceptAgent(AgentIdentifier)}.
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
     *
     * @param leavingAgentIdentifier the leaving sima.core.agent
     */
    public void leave(AgentIdentifier leavingAgentIdentifier) {
        if (this.isEvolving(leavingAgentIdentifier)) {
            this.agentIsLeaving(leavingAgentIdentifier);
            this.evolvingAgents.remove(leavingAgentIdentifier);
        }
    }

    /**
     * Call back method called when an agent is leaving the {@link Environment}, in other word, when the method
     * {@link #leave(AgentIdentifier)} is called. This method is called before the sima.core.agent is removed from the
     * list of evolving agent.
     *
     * @param leavingAgentIdentifier the leaving sima.core.agent
     */
    protected abstract void agentIsLeaving(AgentIdentifier leavingAgentIdentifier);

    /**
     * Verifies if the agent is evolving in the {@link Environment}. An agent is evolving in the {@code Environment} if
     * it is in the list of evolving sima.core.agent, therefore it possible to verify if an agent is evolving in the
     * {@code Environment} by calling the method {@link #getEvolvingAgentIdentifiers()} and see if the agent is contained is
     * the returned list.
     *
     * @param agent the agent to verify
     * @return true if the agent is evolving in the {@code Environment}, else false.
     * @see #getEvolvingAgentIdentifiers()
     */
    public boolean isEvolving(AgentIdentifier agent) {
        return agent != null && this.evolvingAgents.contains(agent);
    }

    /**
     * Finds the agent which is evolving in the {@link Environment} and which has the same uuid of the specified uuid.
     * If the agent is not find, returns null.
     *
     * <strong>WARNING!</strong> The {@link AgentIdentifier} returns is the {@code AgentIdentifier} which was accepted when the
     * method {@link #acceptAgent(AgentIdentifier)} has been called. If we want update {@code AgentIdentifier}, we must call
     * the method {@link AbstractAgent#getInfo()}.
     * <p>
     *
     * @param agentID the uuid of the wanted sima.core.agent
     * @return the agent which has the uuid specified. If the agent is not find in the {@code Environment}, returns
     * null.
     * @throws NotEvolvingAgentInEnvironmentException if there is no sima.core.agent with the specify uuid evolving in
     *                                                the {@code Environment}
     */
    protected AgentIdentifier getAgentIdentifier(UUID agentID) throws NotEvolvingAgentInEnvironmentException {
        // TODO after the addition of the Simulation object, it will be possible to take via the Simulation the update AgentIdentifier

        if (agentID == null)
            return null;

        for (AgentIdentifier agent : this.evolvingAgents) {
            if (agent.getAgentUUID().equals(agentID)) {
                return agent;
            }
        }

        throw new NotEvolvingAgentInEnvironmentException("No sima.core.agent with the uuid " + agentID + " is evolving " +
                "in the sima.core.environment.");
    }

    /**
     * @return the list of all {@link AgentIdentifier} of all agents evolving in the sima.core.environment, if there is no sima.core.agent,
     * returns an empty list but never null.
     */
    public List<AgentIdentifier> getEvolvingAgentIdentifiers() {
        return this.evolvingAgents.stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Send an {@link Event} to the {@link Event#getReceiver()}.
     * <p>
     * This method verifies in first if the {@link Event#getSender()} is evolving in the sima.core.environment and do the same
     * for the receiver. If it is not the case, a {@link NotEvolvingAgentInEnvironmentException} is thrown.
     * <p>
     * After that, if the receiver is not null, then the event is destined to one sima.core.agent and the function
     * {@link #verifyAndScheduleEvent(AgentIdentifier, Event)} is called to try to send the {@code Event} to the sima.core.agent
     * receiver.
     * <p>
     * If the sima.core.agent receiver is null, therefore the method {@link #sendEventWithoutReceiver(Event)} is called to
     * manage which agents must receive the {@code Event}.
     *
     * @param event the event to send
     * @throws NotEvolvingAgentInEnvironmentException if the sender and/or the receiver agent are not evolving in the
     *                                                {@link Environment}.
     */
    public void sendEvent(Event event) throws NotEvolvingAgentInEnvironmentException {
        if (event != null) {
            AgentIdentifier sender = event.getSender();

            if (this.isEvolving(sender)) {
                if (event.getReceiver() == null) {
                    // No receiver for the event
                    this.sendEventWithoutReceiver(event);
                } else {
                    // Event destined for one identified agent.
                    // getAgent() detects if the sima.core.agent is evolving or not in the sima.core.environment
                    AgentIdentifier receiver = event.getReceiver();
                    if (this.isEvolving(receiver))
                        this.verifyAndScheduleEvent(receiver, event);
                    else
                        throw new NotEvolvingAgentInEnvironmentException("The receiver agent " + receiver + " is " +
                                "not evolving in the environment " + this.getEnvironmentName());
                }
            } else
                throw new NotEvolvingAgentInEnvironmentException("The sender sima.core.agent " + sender + " is not " +
                        "evolving in the sima.core.environment " + this.getEnvironmentName());
        } else
            throw new NullPointerException("The sent event is null");
    }

    /**
     * Method called in the function {@link #sendEvent(Event)} when the sender is correctly identified but the receiver
     * is null.
     *
     * @param event the event without receiver to send
     */
    protected abstract void sendEventWithoutReceiver(Event event);

    /**
     * This method verifies if it is possible to send the event to the specified sima.core.agent. Return true if the event can be
     * sent to the receiver, else false.
     * <p>
     * It is in this method that we can simulate the network link. For example, If two agents are not connected, maybe
     * the event cannot be sent to the sima.core.agent receiver.
     *
     * @param receiver the agent receiver
     * @param event    the event to send to the receiver
     * @return true if the event can be sent to the receiver, else false.
     */
    protected abstract boolean eventCanBeSentTo(AgentIdentifier receiver, Event event);

    /**
     * Schedules the moment when the sima.core.agent receiver will receive the event. In other words, schedules the moment when
     * the sima.core.agent receiver will call the method {@link AbstractAgent#processEvent(Event)}.
     *
     * @param receiver the sima.core.agent receiver
     * @param event    the event to send to the receiver
     */
    protected abstract void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event);

    /**
     * First verifies if the event can be sent to the sima.core.agent receiver with the function
     * {@link #eventCanBeSentTo(AgentIdentifier, Event)}. If it is the case, calls the function
     * {@link #scheduleEventReceptionToOneAgent(AgentIdentifier, Event)} to schedule the moment when the sima.core.agent receiver
     * will receive the {@code Event}.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} when the sender has been correctly identified and
     * that the receiver of the {@code Event} is not null.
     *
     * @param receiver the sima.core.agent receiver
     * @param event    the event to receiver
     * @throws UnknownEventException if the event is not a {@link Message} or a {@link NoProtocolEvent}
     * @see #sendEvent(Event)
     * @see #eventCanBeSentTo(AgentIdentifier, Event)
     * @see #scheduleEventReceptionToOneAgent(AgentIdentifier, Event)
     */
    private void verifyAndScheduleEvent(AgentIdentifier receiver, Event event) {
        if (this.eventCanBeSentTo(receiver, event)) {
            this.scheduleEventReceptionToOneAgent(receiver, event);
        }
    }

    // Getters ans Setters.

    /**
     * @return the unique name of the sima.core.environment, cannot be null.
     */
    public String getEnvironmentName() {
        return this.environmentName;
    }
}