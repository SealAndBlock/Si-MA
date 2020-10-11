package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentInfo;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventCatcher;
import sima.core.environment.event.Message;
import sima.core.environment.event.NoProtocolEvent;
import sima.core.environment.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.environment.exception.UnknownEventException;

import java.util.*;

/**
 * Represents an sima.core.environment where {@link AbstractAgent} evolves. An sima.core.environment can be the representation of the physic
 * layer of the communication. An sima.core.environment determine if two agents can communicate or not.
 * <p>
 * For example, you can create an sima.core.environment where sima.core.agent are mobil and move. Each sima.core.agent has a maximum scope and can
 * communicate only with sima.core.agent which are in this scope.
 * <p>
 * With the method {@link #sendEvent(Event)}, it is possible to send {@link Event}. This method first verifies if the
 * sima.core.agent sender is evolving in the sima.core.environment and after that, the methods
 * {@link #eventCanBeSentTo(AbstractAgent, Event)} and {@link #scheduleEventReceptionToOneAgent(AbstractAgent, Event)}
 * are called.
 * <p>
 * First, the method verifies if the {@code Event} can be sent to the specified sima.core.agent receiver, it is in the method that
 * the network and the connection between agents are simulated.
 * <p>
 * If the {@code Event} can be sent to the specified sima.core.agent receiver, therefore the second method is called to schedule
 * the moment when the sima.core.agent receiver will receive the {@code Event}.
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
    private final Set<AbstractAgent> evolvingAgents;

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
     * Add the sima.core.agent in the sima.core.environment. The sima.core.agent can be not accept in the sima.core.environment, in that case, the methods
     * returns false. If the sima.core.agent has already been accepted in the sima.core.environment, this method does not accept the sima.core.agent
     * and returns false.
     *
     * @param evolvingAgent the sima.core.agent which want evolve in the sima.core.environment.
     * @return true if the sima.core.environment accept the sima.core.agent, else false.
     */
    public boolean acceptAgent(AbstractAgent evolvingAgent) {
        if (evolvingAgent != null && this.agentCanBeAccepted(evolvingAgent)) {
            return this.evolvingAgents.add(evolvingAgent);
        } else {
            return false;
        }
    }

    /**
     * Verifies if the sima.core.agent can be accepted and evolving in the sima.core.environment. This method is called in the method
     * {@link #acceptAgent(AbstractAgent)}.
     *
     * @param abstractAgent the sima.core.agent to verify
     * @return true if the sima.core.agent can be accepted in the sima.core.environment, else false.
     * @see #acceptAgent(AbstractAgent)
     */
    protected abstract boolean agentCanBeAccepted(AbstractAgent abstractAgent);

    /**
     * Make that the sima.core.agent is leaving the sima.core.environment. If the sima.core.agent is not evolving in the sima.core.environment, nothing is done.
     * Calls the method {@link #agentIsLeaving(AbstractAgent)} before remove the sima.core.agent from the sima.core.environment.
     *
     * @param leavingAgent the leaving sima.core.agent
     */
    public void leave(AbstractAgent leavingAgent) {
        if (this.isEvolving(leavingAgent)) {
            this.agentIsLeaving(leavingAgent);
            this.evolvingAgents.remove(leavingAgent);
        }
    }

    /**
     * Call back method called when an sima.core.agent is leaving the sima.core.environment, in other word, when the method
     * {@link #leave(AbstractAgent)} is called. This method is called before the sima.core.agent is removed from the list of
     * evolving sima.core.agent.
     *
     * @param leavingAgent the leaving sima.core.agent
     */
    protected abstract void agentIsLeaving(AbstractAgent leavingAgent);

    /**
     * Verifies if the sima.core.agent is evolving in the sima.core.environment. An sima.core.agent is evolving in the sima.core.environment if it is in the
     * list of evolving sima.core.agent, therefore it possible to verify if an sima.core.agent is evolving in the sima.core.environment by calling
     * the method {@link #getEvolvingAgentsInfo()} and see if the sima.core.agent is contained is the returned list.
     *
     * @param agent the sima.core.agent to verify
     * @return true if the sima.core.agent is evolving in the sima.core.environment, else false.
     * @see #getEvolvingAgentsInfo()
     */
    public boolean isEvolving(AbstractAgent agent) {
        return agent != null && this.evolvingAgents.contains(agent);
    }

    /**
     * Finds the sima.core.agent which is evolving in the sima.core.environment and which has the same uuid of the specified uuid. If the
     * sima.core.agent is not find, returns null.
     *
     * @param agentID the uuid of the wanted sima.core.agent
     * @return the sima.core.agent which has the uuid specified. If the sima.core.agent is not find in the sima.core.environment, returns null.
     * @throws NotEvolvingAgentInEnvironmentException if there is no sima.core.agent with the specify uuid evolving in the
     *                                                sima.core.environment
     */
    protected AbstractAgent getAgent(UUID agentID) throws NotEvolvingAgentInEnvironmentException {
        if (agentID == null)
            return null;

        for (AbstractAgent agent : this.evolvingAgents) {
            if (agent.getUUID().equals(agentID)) {
                return agent;
            }
        }

        throw new NotEvolvingAgentInEnvironmentException("No sima.core.agent with the uuid " + agentID + " is evolving in the " +
                "sima.core.environment.");
    }

    /**
     * @return the list of all {@link AgentInfo} of all agents evolving in the sima.core.environment, if there is no sima.core.agent,
     * returns an empty list but never null.
     */
    public List<AgentInfo> getEvolvingAgentsInfo() {
        return this.evolvingAgents.stream().map(AbstractAgent::getInfo)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Send an {@link Event} to the {@link Event#getReceiver()}.
     * <p>
     * This method verifies in first if the {@link Event#getSender()} is evolving in the sima.core.environment and do the same
     * for the receiver. If it is not the case, a {@link NotEvolvingAgentInEnvironmentException} is thrown.
     * <p>
     * After that, if the receiver is not null, then the event is destined to one sima.core.agent and the function
     * {@link #verifyAndScheduleEvent(AbstractAgent, Event)} is called to try to send the {@code Event} to the sima.core.agent
     * receiver.
     * <p>
     * If the sima.core.agent receiver is null, therefore the method {@link #sendEventWithoutReceiver(Event)} is called to
     * manage which agents must receive the {@code Event}.
     *
     * @param event the event to send
     * @throws NotEvolvingAgentInEnvironmentException if the sender and/or the receiver sima.core.agent are not evolving in the
     *                                                sima.core.environment
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
                    // Event destined for one identified sima.core.agent.
                    // getAgent() detects if the sima.core.agent is evolving or not in the sima.core.environment
                    AbstractAgent receiver = this.getAgent(event.getReceiver());
                    this.verifyAndScheduleEvent(receiver, event);
                }
            } else
                throw new NotEvolvingAgentInEnvironmentException("The sender sima.core.agent " + sender + " is not evolving in " +
                        "the sima.core.environment " + this.getEnvironmentName());
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
     * @param receiver the sima.core.agent receiver
     * @param event    the event to send to the receiver
     * @return true if the event can be sent to the receiver, else false.
     */
    protected abstract boolean eventCanBeSentTo(AbstractAgent receiver, Event event);

    /**
     * Schedules the moment when the sima.core.agent receiver will receive the event. In other words, schedules the moment when
     * the sima.core.agent receiver will call the method {@link AbstractAgent#processEvent(Event)}.
     *
     * @param receiver the sima.core.agent receiver
     * @param event    the event to send to the receiver
     */
    protected abstract void scheduleEventReceptionToOneAgent(AbstractAgent receiver, Event event);

    /**
     * First verifies if the event can be sent to the sima.core.agent receiver with the function
     * {@link #eventCanBeSentTo(AbstractAgent, Event)}. If it is the case, calls the function
     * {@link #scheduleEventReceptionToOneAgent(AbstractAgent, Event)} to schedule the moment when the sima.core.agent receiver
     * will receive the {@code Event}.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} when the sender has been correctly identified and
     * that the receiver of the {@code Event} is not null.
     *
     * @param receiver the sima.core.agent receiver
     * @param event    the event to receiver
     * @throws UnknownEventException if the event is not a {@link Message} or a {@link NoProtocolEvent}
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
     * @return the unique name of the sima.core.environment, cannot be null.
     */
    public String getEnvironmentName() {
        return this.environmentName;
    }
}