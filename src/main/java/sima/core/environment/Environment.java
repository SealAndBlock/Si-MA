package sima.core.environment;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentInfo;

import java.util.List;

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
public interface Environment {

    /**
     * Add the agent in the environment. The agent can be not accept in the environment, in that case, the methods
     * returns false. If the agent has already been accepted in the environment, this method does not accept the agent
     * and returns false.
     *
     * @param evolvingAgent the agent which want evolve in the environment.
     * @return true if the environment accept the agent, else false.
     */
    boolean acceptAgent(AbstractAgent evolvingAgent);

    /**
     * Make that the agent is leaving the environment.
     *
     * @param leavingAgent the leaving agent
     */
    void leave(AbstractAgent leavingAgent);

    /**
     * @param agent the agent to verify
     * @return true if the agent is evolving in the environment, else false.
     */
    boolean isEvolving(AbstractAgent agent);

    /**
     * @return the list of all {@link AgentInfo} of all agents evolving in the environment, if there is no agent, returns an empty list but never
     * null.
     */
    List<AgentInfo> getListOfEvolvingAgent();

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
    void sendMessage(Message message);

    /**
     * Triggers a {@link GeneralEvent} in the environment.
     *
     * @param event the general event to trigger
     * @see GeneralEvent
     */
    void triggerGeneralEvent(GeneralEvent event);

    /**
     * @return the unique name of the environment, cannot be null.
     */
    String getName();
}