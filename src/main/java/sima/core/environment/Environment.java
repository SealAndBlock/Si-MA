package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentInfo;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventCatcher;
import sima.core.environment.event.GeneralEvent;
import sima.core.environment.event.Message;

import java.util.*;

/**
 * Represents an environment where {@link AbstractAgent} evolves. An environment can be the representation of the physic
 * layer of the communication. An environment determine if two agents can communicate or not.
 * <p>
 * For example, you can create an environment where agent are mobil and move. Each agent has a maximum scope and can
 * communicate only with agent which are in this scope. Therefore, in method {@link #sendMessage(Message)} you can
 * verify if the {@link Message#getSender()}} and the {@link Message#getReceiver()} are in the range to send and
 * receive a message.
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
        if (evolvingAgent != null && this.canBeAccepted(evolvingAgent)) {
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
    protected abstract boolean canBeAccepted(AbstractAgent abstractAgent);

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
     */
    protected AbstractAgent getAgent(UUID agentID) {
        if (agentID == null)
            return null;

        for (AbstractAgent agent : this.evolvingAgents) {
            if (agent.getUUID().equals(agentID)) {
                return agent;
            }
        }

        return null;
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
     * Send the message to the {@link Message#getReceiver()}.
     * <p>
     * It is in this method that you can simulate for example communication failure in the communication network.
     * <p>
     * To manage communication latency, you must implement it in subclass as parameter and use these parameters in the
     * implementation of this method.
     *
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        if (message != null) {
            UUID senderID = message.getSender();

            if (this.isEvolving(this.getAgent(senderID))) {
                AbstractAgent receiver = this.getAgent(message.getReceiver());

                if (receiver != null) {
                    // Message destined for one identified agent.
                    this.sendAndScheduleMessage(receiver, message);
                } else {
                    // Broadcast Message.
                    this.evolvingAgents.forEach(agent -> {
                        this.sendAndScheduleMessage(agent, message);
                    });
                }
            }
        } else
            throw new NullPointerException("The sent message is null");
    }

    /**
     * Verifies if the message can be sent or not. If the message is null, returns false.
     * <p>
     * In this method, the {@link Environment} verifies if the message can be sent or not. It is in this method that for
     * example it can be simulated that two agents are not within range to send messages to each other. It can be also
     * simulate that randomly, messages are not sent, etc.
     * <p>
     * This method is called in the method {@link #sendMessage(Message)} when all verifications have been done and that
     * the sender and the receiver have been correctly identified.
     *
     * @param message the message to send
     * @return true if the message can be sent, else false.
     * @see #sendMessage(Message)
     * @see #sendAndScheduleMessage(AbstractAgent, Message)
     */
    protected abstract boolean messageCanBeSent(Message message);

    /**
     * This method schedules the reception of the message for the specified agent.
     * <p>
     * It is in this methods where it is specified at what time the message is received by the specified agent, in
     * other word, this method schedule the call of the method {@link AbstractAgent#processEvent(Event)} of the agent.
     * <p>
     * This method is called in the method {@link #sendMessage(Message)} after that the method
     * {@link #messageCanBeSent(Message)} has returned true.
     *
     * @param receiver the agent receiver
     * @param message  the message to receive
     * @see #sendMessage(Message)
     * @see #messageCanBeSent(Message)
     * @see #sendAndScheduleMessage(AbstractAgent, Message)
     */
    protected abstract void scheduleReceivingMessageToOneAgent(AbstractAgent receiver, Message message);

    /**
     * This method verifies if the message can be sent with the method {@link #messageCanBeSent(Message)} and if the
     * message can be sent, then calls the method {@link #scheduleReceivingMessageToOneAgent(AbstractAgent, Message)}
     * to schedule the reception of the message by the receiver agent.
     * <p>
     * This method is called in the method {@link #sendMessage(Message)}.
     *
     * @param receiver the receiver agent
     * @param message  the message to receive
     * @see #sendMessage(Message)
     * @see #messageCanBeSent(Message)
     * @see #scheduleReceivingMessageToOneAgent(AbstractAgent, Message) 
     */
    private void sendAndScheduleMessage(AbstractAgent receiver, Message message) {
        if (this.messageCanBeSent(message)) {
            this.scheduleReceivingMessageToOneAgent(receiver, message);
        }
    }

    /**
     * Triggers a {@link GeneralEvent} in the environment.
     *
     * @param event the general event to trigger
     * @see GeneralEvent
     */
    public abstract void triggerGeneralEvent(GeneralEvent event);

    // Getters ans Setters.

    /**
     * @return the unique name of the environment, cannot be null.
     */
    public String getEnvironmentName() {
        return this.environmentName;
    }
}