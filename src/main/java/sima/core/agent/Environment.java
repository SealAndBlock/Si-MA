package sima.core.agent;

import java.util.List;

/**
 * Represents an environment where {@link AbstractAgent} evolves. An environment can be the representation of the physic
 * layer of the communication. An environment determine if two agents can communicate or not.
 * <p>
 * For example, you can create an environment where agent are mobil and move. Each agent has a maximum scope and can
 * communicate only with agent which are in this scope. Therefore in methods {@link #sendMessage(Message)}
 * *and {@link #receiveMessage(Message)}  you can verify if the {@link Message#getSender()}} and the
 * {@link Message#getReceiver()} are in the range to send and receive message.
 *
 * @author guilr
 */
public interface Environment {

    /**
     * @return the list of all agents evolving in the environment, if there is no agent, returns an empty list but never
     * null.
     */
    List<AbstractAgent> getListOfEvolvingAgent();

    /**
     * Send the message to the {@link Message#getReceiver()}.
     * <p>
     * It is this methods that you can simulate for example communication failure in the communication network.
     * <p>
     * To manage communication latency, you must implement it in sub class as parameter and use theses parameters in the
     * implementation of this method.
     *
     * @param message the message to send
     */
    void sendMessage(Message message);

    /**
     * Received the message. Look the {@link Message#getProtocolTargeted()} and call the method
     * {@link Protocol#processEvent(Event)} }.
     * <p>
     * In the method you can also verify if the message can be received or not.
     *
     * @param message the message received
     */
    void receiveMessage(Message message);
}