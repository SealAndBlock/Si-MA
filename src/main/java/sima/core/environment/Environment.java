package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventCatcher;
import sima.core.exception.NotEvolvingAgentInEnvironmentException;

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
        this.environmentName = Optional.of(environmentName).get();
        evolvingAgents = new HashSet<>();

        if (args != null)
            processArgument(args);
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
     * <p>
     * In this method, only the environment has conscience that the agent is evolving in it. For that the agent be
     * notify that it is evolving in the environment, it is better to use the method
     * {@link AbstractAgent#joinEnvironment(Environment)}. In this method, the agent and the environment are both
     * conscience that the agent is evolving in the environment.
     * <p>
     * If this method is called and that the concerned agent is not conscience that it has been accepted and it is now
     * evolving in the environment, then you can use the method
     * {@link AbstractAgent#setEvolvingInEnvironment(Environment)} which set the environment in the agent for that the
     * agent know that it is evolving in the environment.
     *
     * @param evolvingAgentIdentifier the {@link AgentIdentifier} of the agent which will evolve in the environment
     * @return true if the {@code Environment} accept the sima.core.agent, else false.
     */
    public synchronized boolean acceptAgent(AgentIdentifier evolvingAgentIdentifier) {
        if (evolvingAgentIdentifier != null && agentCanBeAccepted(evolvingAgentIdentifier)) {
            return evolvingAgents.add(evolvingAgentIdentifier);
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
     * <p>
     * In this method the agent is not notify that it is leaving the environment. For that the agent be conscience that
     * it leaves the environment, it is recommended to use the method
     * {@link AbstractAgent#leaveEnvironment(Environment)}
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
     * Send an {@link Event} to the {@link Event#getReceiver()}.
     * <p>
     * This method verifies in first if the {@link Event#getSender()} is evolving in the sima.core.environment and do the same
     * for the receiver. If it is not the case, a {@link NotEvolvingAgentInEnvironmentException} is thrown.
     * <p>
     * After that, if the receiver is not null, then the event is destined to one sima.core.agent and the function
     * {@link #verifyAndScheduleEvent(AgentIdentifier, Event)} is called to try to send the {@code Event} to the sima.core.agent
     * receiver.
     * <p>
     * If the sima.core.agent receiver is null, therefore the method {@link #sendEventWithNullReceiver(Event)} is called to
     * manage which agents must receive the {@code Event}.
     *
     * @param event the event to send
     * @throws NotEvolvingAgentInEnvironmentException if the sender and/or the receiver agent are not evolving in the
     *                                                {@link Environment}.
     */
    public synchronized void sendEvent(Event event) throws NotEvolvingAgentInEnvironmentException {
        if (event != null) {
            AgentIdentifier sender = event.getSender();

            if (isEvolving(sender)) {
                if (event.getReceiver() == null) {
                    // No receiver for the event
                    sendEventWithNullReceiver(event);
                } else {
                    // Event destined for one identified agent.
                    // getAgent() detects if the sima.core.agent is evolving or not in the sima.core.environment
                    AgentIdentifier receiver = event.getReceiver();
                    if (isEvolving(receiver))
                        verifyAndScheduleEvent(receiver, event);
                    else
                        throw new NotEvolvingAgentInEnvironmentException("The receiver agent " + receiver + " is " +
                                "not evolving in the environment " + getEnvironmentName());
                }
            } else
                throw new NotEvolvingAgentInEnvironmentException("The sender sima.core.agent " + sender + " is not " +
                        "evolving in the sima.core.environment " + getEnvironmentName());
        } else
            throw new NullPointerException("The sent event is null");
    }

    /**
     * Method called in the function {@link #sendEvent(Event)} when the sender is correctly identified but the receiver
     * is null.
     *
     * @param event the event without receiver to send
     */
    protected abstract void sendEventWithNullReceiver(Event event);

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
     * @see #sendEvent(Event)
     * @see #eventCanBeSentTo(AgentIdentifier, Event)
     * @see #scheduleEventReceptionToOneAgent(AgentIdentifier, Event)
     */
    protected void verifyAndScheduleEvent(AgentIdentifier receiver, Event event) {
        if (eventCanBeSentTo(receiver, event)) {
            scheduleEventReceptionToOneAgent(receiver, event);
        }
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