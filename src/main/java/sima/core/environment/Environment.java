package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentInfo;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventCatcher;
import sima.core.environment.event.GeneralEvent;
import sima.core.environment.event.Message;
import sima.core.environment.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.environment.exception.UnknownEventException;

import java.util.*;

/**
 * Represents an environment where {@link AbstractAgent} evolves. An environment can be the representation of the physic
 * layer of the communication. An environment determine if two agents can communicate or not.
 * <p>
 * For example, you can create an environment where agent are mobil and move. Each agent has a maximum scope and can
 * communicate only with agent which are in this scope.
 * <p>
 * With the method {@link #sendEvent(Event)}, it is possible to send {@link Event}. This method first verifies if the
 * agent sender is evolving in the environment and after that, the methods
 * {@link #eventCanBeSentTo(AbstractAgent, Event)} and {@link #scheduleEventReceptionToOneAgent(AbstractAgent, Event)}
 * are called.
 * <p>
 * First, the method verifies if the {@code Event} can be sent to the specified agent receiver, it is in the method that
 * the network and the connection between agents are simulated.
 * <p>
 * If the {@code Event} can be sent to the specified agent receiver, therefore the second method is called to schedule
 * the moment when the agent receiver will receive the {@code Event}.
 *
 * @author guilr
 */
public abstract class Environment implements EventCatcher {

    // Variables.

    /**
     * The unique name of the environment.
     */
    private final String environmentName;

    /**
     * The set of evolving agents.
     */
    private final Set<AbstractAgent> evolvingAgents;

    // Constructors.

    public Environment(String environmentName) {
        this.environmentName = environmentName;

        if (this.environmentName == null)
            throw new NullPointerException("The environment name cannot be null.");

        this.evolvingAgents = new HashSet<>();
    }

    // Methods.

    /**
     * Add the agent in the environment. The agent can be not accept in the environment, in that case, the methods
     * returns false. If the agent has already been accepted in the environment, this method does not accept the agent
     * and returns false.
     *
     * @param evolvingAgent the agent which want evolve in the environment.
     * @return true if the environment accept the agent, else false.
     */
    public boolean acceptAgent(AbstractAgent evolvingAgent) {
        if (evolvingAgent != null && this.agentCanBeAccepted(evolvingAgent)) {
            return this.evolvingAgents.add(evolvingAgent);
        } else {
            return false;
        }
    }

    /**
     * Verifies if the agent can be accepted and evolving in the environment. This method is called in the method
     * {@link #acceptAgent(AbstractAgent)}.
     *
     * @param abstractAgent the agent to verify
     * @return true if the agent can be accepted in the environment, else false.
     * @see #acceptAgent(AbstractAgent)
     */
    protected abstract boolean agentCanBeAccepted(AbstractAgent abstractAgent);

    /**
     * Make that the agent is leaving the environment. If the agent is not evolving in the environment, nothing is done.
     * Calls the method {@link #agentIsLeaving(AbstractAgent)} before remove the agent from the environment.
     *
     * @param leavingAgent the leaving agent
     */
    public void leave(AbstractAgent leavingAgent) {
        if (this.isEvolving(leavingAgent)) {
            this.agentIsLeaving(leavingAgent);
            this.evolvingAgents.remove(leavingAgent);
        }
    }

    /**
     * Call back method called when an agent is leaving the environment, in other word, when the method
     * {@link #leave(AbstractAgent)} is called. This method is called before the agent is removed from the list of
     * evolving agent.
     *
     * @param leavingAgent the leaving agent
     */
    protected abstract void agentIsLeaving(AbstractAgent leavingAgent);

    /**
     * Verifies if the agent is evolving in the environment. An agent is evolving in the environment if it is in the
     * list of evolving agent, therefore it possible to verify if an agent is evolving in the environment by calling
     * the method {@link #getEvolvingAgentsInfo()} and see if the agent is contained is the returned list.
     *
     * @param agent the agent to verify
     * @return true if the agent is evolving in the environment, else false.
     * @see #getEvolvingAgentsInfo()
     */
    public boolean isEvolving(AbstractAgent agent) {
        return agent != null && this.evolvingAgents.contains(agent);
    }

    /**
     * Finds the agent which is evolving in the environment and which has the same uuid of the specified uuid. If the
     * agent is not find, returns null.
     *
     * @param agentID the uuid of the wanted agent
     * @return the agent which has the uuid specified. If the agent is not find in the environment, returns null.
     * @throws NotEvolvingAgentInEnvironmentException if there is no agent with the specify uuid evolving in the
     *                                                environment
     */
    protected AbstractAgent getAgent(UUID agentID) throws NotEvolvingAgentInEnvironmentException {
        if (agentID == null)
            return null;

        for (AbstractAgent agent : this.evolvingAgents) {
            if (agent.getUUID().equals(agentID)) {
                return agent;
            }
        }

        throw new NotEvolvingAgentInEnvironmentException("No agent with the uuid " + agentID + " is evolving in the " +
                "environment.");
    }

    /**
     * @return the list of all {@link AgentInfo} of all agents evolving in the environment, if there is no agent,
     * returns an empty list but never null.
     */
    public List<AgentInfo> getEvolvingAgentsInfo() {
        return this.evolvingAgents.stream().map(AbstractAgent::getInfo)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Send an {@link Event} to the {@link Event#getReceiver()}.
     * <p>
     * This method verifies in first if the {@link Event#getSender()} is evolving in the environment and do the same
     * for the receiver. If it is not the case, a {@link NotEvolvingAgentInEnvironmentException} is thrown.
     * <p>
     * After that, if the receiver is not null, then the event is destined to one agent and the function
     * {@link #verifyAndScheduleEvent(AbstractAgent, Event)} is called to try to send the {@code Event} to the agent
     * receiver.
     * <p>
     * If the agent receiver is null, therefore the method {@link #sendEventWithoutReceiver(Event)} is called to
     * manage which agents must receive the {@code Event}.
     *
     * @param event the event to send
     * @throws NotEvolvingAgentInEnvironmentException if the sender and/or the receiver agent are not evolving in the
     *                                                environment
     */
    public void sendEvent(Event event) throws NotEvolvingAgentInEnvironmentException {
        if (event != null) {
            UUID senderID = event.getSender();
            AbstractAgent sender = this.getAgent(senderID);

            if (this.isEvolving(sender)) {
                if (event.getReceiver() == null) {
                    // No receiver for the event
                    this.sendEventWithoutReceiver(event);
                } else {
                    // Event destined for one identified agent.
                    // getAgent() detects if the agent is evolving or not in the environment
                    AbstractAgent receiver = this.getAgent(event.getReceiver());
                    this.verifyAndScheduleEvent(receiver, event);
                }
            } else
                throw new NotEvolvingAgentInEnvironmentException("The sender agent " + sender + " is not evolving in " +
                        "the environment " + this.getEnvironmentName());
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
     * This method verifies if it is possible to send the event to the specified agent. Return true if the event can be
     * sent to the receiver, else false.
     * <p>
     * It is in this method that we can simulate the network link. For example, If two agents are not connected, maybe
     * the event cannot be sent to the agent receiver.
     *
     * @param receiver the agent receiver
     * @param event    the event to send to the receiver
     * @return true if the event can be sent to the receiver, else false.
     */
    protected abstract boolean eventCanBeSentTo(AbstractAgent receiver, Event event);

    /**
     * Schedules the moment when the agent receiver will receive the event. In other words, schedules the moment when
     * the agent receiver will call the method {@link AbstractAgent#processEvent(Event)}.
     *
     * @param receiver the agent receiver
     * @param event    the event to send to the receiver
     */
    protected abstract void scheduleEventReceptionToOneAgent(AbstractAgent receiver, Event event);

    /**
     * First verifies if the event can be sent to the agent receiver with the function
     * {@link #eventCanBeSentTo(AbstractAgent, Event)}. If it is the case, calls the function
     * {@link #scheduleEventReceptionToOneAgent(AbstractAgent, Event)} to schedule the moment when the agent receiver
     * will receive the {@code Event}.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} when the sender has been correctly identified and
     * that the receiver of the {@code Event} is not null.
     *
     * @param receiver the agent receiver
     * @param event    the event to receiver
     * @throws UnknownEventException if the event is not a {@link Message} or a {@link GeneralEvent}
     * @see #sendEvent(Event)
     * @see #eventCanBeSentTo(AbstractAgent, Event)
     * @see #scheduleEventReceptionToOneAgent(AbstractAgent, Event)
     */
    private void verifyAndScheduleEvent(AbstractAgent receiver, Event event) {
        if (this.eventCanBeSentTo(receiver, event)) {
            this.scheduleEventReceptionToOneAgent(receiver, event);
        }
    }

    // Getters ans Setters.

    /**
     * @return the unique name of the environment, cannot be null.
     */
    public String getEnvironmentName() {
        return this.environmentName;
    }
}