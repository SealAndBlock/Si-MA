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
 * With the method {@link #sendEvent(Event)}, it is possible to send two types of {@link Event}:
 * <blockquote><pre>
 *     - {@link Message}
 *     - {@link GeneralEvent}
 * </pre></blockquote>
 * <p>
 * For both types of event it is possible to determine the condition of if an event can be sent or not and schedule
 * when the receiver agent(s) received the event. In that way it is easy to simulate all type of environment and
 * network.
 * <p>
 * You can for example simulate a mobile environment where agents move and have communication range, is that way, an
 * agent can send a message to an other agent only if the second agent is at range. This feature can be implemented in
 * the method {@link #messageCanBeSent(Message)}.
 * <p>
 * After that, if the send it message is possible, therefore it is possible to simulate the time transfer with the
 * method {@link #scheduleMessageReceptionToOneAgent(AbstractAgent, Message)}.
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
     * After that, if the receiver is not null, then the event is destined to one agent, in the other case, the event is
     * a broadcast event destined to all agents in the environment.
     * <p>
     * In function of the type of the {@link Event}, the methods called are different. Indeed if the event is a
     * {@link Message}, therefore the method called is {@link #verifyAndScheduleMessage(AbstractAgent, Message)} which
     * uses the method {@link #messageCanBeSent(Message)} and the method
     * {@link #scheduleMessageReceptionToOneAgent(AbstractAgent, Message)}.
     * <p>
     * If the event is a {@link GeneralEvent}, therefore the method called is
     * {@link #verifyAndScheduleGeneralEvent(AbstractAgent, GeneralEvent)} which uses the method
     * {@link #generalEventCanBeSent(GeneralEvent)} and the method
     * {@link #scheduleGeneralEventReceptionToOneAgent(AbstractAgent, GeneralEvent)}.
     *
     * @param event the event to send
     * @throws NotEvolvingAgentInEnvironmentException if the sender and/or the receiver agent are not evolving in the
     *                                                environment
     * @see #verifyAndScheduleMessage(AbstractAgent, Message)
     * @see #verifyAndScheduleGeneralEvent(AbstractAgent, GeneralEvent)
     * @see #messageCanBeSent(Message)
     * @see #generalEventCanBeSent(GeneralEvent)
     * @see #scheduleMessageReceptionToOneAgent(AbstractAgent, Message)
     * @see #scheduleGeneralEventReceptionToOneAgent(AbstractAgent, GeneralEvent)
     */
    public void sendEvent(Event event) throws NotEvolvingAgentInEnvironmentException {
        if (event != null) {
            UUID senderID = event.getSender();
            AbstractAgent sender = this.getAgent(senderID);

            if (this.isEvolving(sender)) {
                if (event.getReceiver() == null) {
                    // Broadcast event
                    this.evolvingAgents.forEach(agent -> this.verifyAndScheduleEvent(agent, event));
                } else {
                    // Event destined for one identified agent.
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
     * Verifies if the event is an instance of a {@link Message} or of a {@link GeneralEvent}. If it is not the case
     * throws an {@link UnknownEventException}.
     * <p>
     * After that, calls in function of the type of the event, the method
     * {@link #verifyAndScheduleMessage(AbstractAgent, Message)} if the event is a {@code Message} or
     * {@link #verifyAndScheduleGeneralEvent(AbstractAgent, GeneralEvent)} if the event is a {@code GeneralEvent}.
     *
     * @param receiver the agent receiver
     * @param event    the event to receiver
     * @throws UnknownEventException if the event is not a {@link Message} or a {@link GeneralEvent}
     * @see #sendEvent(Event)
     * @see #verifyAndScheduleMessage(AbstractAgent, Message)
     * @see #verifyAndScheduleGeneralEvent(AbstractAgent, GeneralEvent)
     */
    private void verifyAndScheduleEvent(AbstractAgent receiver, Event event) {
        if (event instanceof Message) {
            this.verifyAndScheduleMessage(receiver, (Message) event);
        } else if (event instanceof GeneralEvent) {
            this.verifyAndScheduleGeneralEvent(receiver, (GeneralEvent) event);
        } else {
            throw new UnknownEventException("The event: " + event);
        }
    }

    /**
     * Verifies if the message can be sent or not. If the message is null, returns false.
     * <p>
     * In this method, the {@link Environment} verifies if the message can be sent or not. It is in this method that for
     * example it can be simulated that two agents are not within range to send messages to each other. It can be also
     * simulate that randomly, messages are not sent, etc.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} when all verifications have been done and that
     * the sender and the receiver have been correctly identified.
     *
     * @param message the message to send
     * @return true if the message can be sent, else false.
     * @see #sendEvent(Event)
     * @see #verifyAndScheduleMessage(AbstractAgent, Message)
     */
    protected abstract boolean messageCanBeSent(Message message);

    /**
     * This method schedules the reception of the message for the specified agent.
     * <p>
     * It is in this methods where it is specified at what time the message is received by the specified agent, in
     * other word, this method schedule the call of the method {@link AbstractAgent#processEvent(Event)} of the agent.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} after that the method
     * {@link #messageCanBeSent(Message)} has returned true.
     *
     * @param receiver the agent receiver
     * @param message  the message to receive
     * @see #sendEvent(Event)
     * @see #messageCanBeSent(Message)
     * @see #verifyAndScheduleMessage(AbstractAgent, Message)
     */
    protected abstract void scheduleMessageReceptionToOneAgent(AbstractAgent receiver, Message message);

    /**
     * This method verifies if the message can be sent with the method {@link #messageCanBeSent(Message)} and if the
     * message can be sent, then calls the method {@link #scheduleMessageReceptionToOneAgent(AbstractAgent, Message)}
     * to schedule the reception of the message by the receiver agent.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} if the event is an instance of a {@link Message}
     *
     * @param receiver the receiver agent
     * @param message  the message to receive
     * @see #sendEvent(Event)
     * @see #messageCanBeSent(Message)
     * @see #scheduleMessageReceptionToOneAgent(AbstractAgent, Message)
     */
    private void verifyAndScheduleMessage(AbstractAgent receiver, Message message) {
        if (this.messageCanBeSent(message))
            this.scheduleMessageReceptionToOneAgent(receiver, message);
    }

    /**
     * Verifies if the general event can be sent or not. If the general event is null, returns false.
     * <p>
     * In this method, the {@link Environment} verifies if the general event can be sent or not. It is in this method
     * that it is possible or not to an agent to receive the event or not (for example, the agent is to far to received
     * the event)
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} when all verifications
     * have been done and that the sender and the receiver have been correctly identified.
     *
     * @param event the event to send
     * @return true if the event can be sent, else false.
     * @see #sendEvent(Event)
     * @see #verifyAndScheduleGeneralEvent(AbstractAgent, GeneralEvent)
     */
    protected abstract boolean generalEventCanBeSent(GeneralEvent event);

    /**
     * This method schedules the reception of the general event for the specified agent.
     * <p>
     * It is in this methods where it is specified at what time the general event is received by the specified agent, in
     * other word, this method schedule the call of the method {@link AbstractAgent#processEvent(Event)} of the agent.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)}  after that the method
     * {@link #generalEventCanBeSent(GeneralEvent)} has returned true.
     *
     * @param receiver the agent receiver
     * @param event    the event to receive
     * @see #sendEvent(Event)
     * @see #generalEventCanBeSent(GeneralEvent)
     * @see #verifyAndScheduleGeneralEvent(AbstractAgent, GeneralEvent)
     */
    protected abstract void scheduleGeneralEventReceptionToOneAgent(AbstractAgent receiver, GeneralEvent event);

    /**
     * This method verifies if the general event can be sent with the method
     * {@link #generalEventCanBeSent(GeneralEvent)}} and if the general event can be sent, then calls the method
     * {@link #scheduleGeneralEventReceptionToOneAgent(AbstractAgent, GeneralEvent)} to schedule the reception of the
     * event by the receiver agent.
     * <p>
     * This method is called in the method {@link #sendEvent(Event)} if the event is an instance of a
     * {@link GeneralEvent}
     *
     * @param receiver the receiver agent
     * @param event    the event to receive
     * @see #sendEvent(Event)
     * @see #generalEventCanBeSent(GeneralEvent)
     * @see #scheduleGeneralEventReceptionToOneAgent(AbstractAgent, GeneralEvent)
     */
    private void verifyAndScheduleGeneralEvent(AbstractAgent receiver, GeneralEvent event) {
        if (this.generalEventCanBeSent(event))
            this.scheduleGeneralEventReceptionToOneAgent(receiver, event);
    }

    // Getters ans Setters.

    /**
     * @return the unique name of the environment, cannot be null.
     */
    public String getEnvironmentName() {
        return this.environmentName;
    }
}