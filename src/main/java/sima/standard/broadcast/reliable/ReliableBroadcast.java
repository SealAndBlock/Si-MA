package sima.standard.broadcast.reliable;

import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.standard.broadcast.basic.BasicBroadcast;
import sima.standard.environment.message.Message;
import sima.standard.transport.MessageTransportProtocol;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class which implement message broadcasting.
 * <p>
 * This broadcast is a Reliable Broadcast. A Reliable Broadcast is a Best-Effort Broadcast as {@link BasicBroadcast} with an additional property:
 * <ul>
 * <li><strong><i>Best-Effort Broadcast properties</i></strong></li>
 * <li><strong>Agreement:</strong> if a correct process deliver a message <i>m</i>, then every correct process eventually deliver <i>m</i></li>
 * </ul>
 * <p>
 * To send message, this class needs {@link MessageTransportProtocol} to transport message. It is the {@link MessageTransportProtocol} which manage
 * that the message reach the process target.
 * <p>
 * A message broadcaster need to know the group membership which is the group of all processes "connected" together. The broadcast take each member
 * and send to him the message. In this implementation, the group membership is given by the class {@link Environment} with the method {@link
 * Environment#getEvolvingAgentIdentifiers()}. However in next version, the group membership will be provided by a dedicated class.
 *
 * @see BasicBroadcast
 */
public class ReliableBroadcast extends BasicBroadcast {

    // Variables.

    private long sequence;

    private final Set<ReliableBroadcastMessage> messageReceived;

    // Constructors.

    public ReliableBroadcast(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
        sequence = 0L;
        messageReceived = new HashSet<>();
    }

    // Methods.

    private ReliableBroadcastMessage createReliableBroadcastMessage(Message message) {
        return new ReliableBroadcastMessage(sequence++, getAgentOwner().getAgentIdentifier(), message, getIdentifier());
    }

    @Override
    public void broadcast(Message message) {
        sendToAll(createReliableBroadcastMessage(isNotNull(message)));
    }

    @Override
    public void receive(Message message) {
        if (isAcceptedEvent(message)) {
            ReliableBroadcastMessage reliableBroadcastMessage = acceptedMessageClass().cast(message);
            if (messageReceived.add(reliableBroadcastMessage)) {
                if (!reliableBroadcastMessage.getSender().equals(getAgentOwner().getAgentIdentifier())) {
                    // If we are not the sender of the broadcast message
                    reSendToAll(reliableBroadcastMessage);
                }
                deliver(reliableBroadcastMessage);
            } // else already received message.
        }
    }

    /**
     * Send the {@link sima.standard.broadcast.basic.BroadcastMessage} to all others members of the {@link #getGroupMemberShip()} excepted us.
     *
     * @param broadcastMessage the {@link sima.standard.broadcast.basic.BroadcastMessage} to resend
     */
    private void reSendToAll(ReliableBroadcastMessage broadcastMessage) {
        for (AgentIdentifier agent : getGroupMemberShip()) {
            if (!agent.equals(getAgentOwner().getAgentIdentifier())) {
                getMessageTransport().send(agent, broadcastMessage);
            }
        }
    }

    @Override
    protected Class<? extends ReliableBroadcastMessage> acceptedMessageClass() {
        return ReliableBroadcastMessage.class;
    }
}
