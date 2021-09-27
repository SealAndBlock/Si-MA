package sima.basic.broadcast;

import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.Message;
import sima.basic.environment.message.MessageReceiver;
import sima.basic.transport.MessageTransportProtocol;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A class which implements message broadcasting.
 * <p>
 * This broadcast is a Best-Effort Broadcast. A Best-Effort Broadcast has three properties:
 * <p>
 * <ul>
 * <li><strong>Validity:</strong> if a correct process <i>p</i> broadcast a message <i>m</i> then all correct process <i>q</i> deliver <i>m</i>.</li>
 * <li><strong>No duplication:</strong> No message is deliver more than once.</li>
 * <li><strong>No creation:</strong> if a process deliver a message <i>m</i> with a sender <i>s</i>, then <i>m</i> was previously broadcast by
 * <i>s</i>.</li>
 * </ul>
 * <p>
 * To send message, this class needs {@link MessageTransportProtocol} to transport message. It is the {@link MessageTransportProtocol} which manage
 * that the message reach the process target.
 * <p>
 * A message broadcaster need to know the group membership which is the group of all processes "connected" together. The broadcast take each member
 * and send to him the message. In this implementation, the group membership is given by the class {@link Environment} with the method
 * {@link Environment#getEvolvingAgentIdentifiers()}. However in next version, the group membership will be provided by a dedicated class.
 */
public class BasicBroadcast extends Protocol implements MessageBroadcaster, MessageReceiver {

    // Variables.

    private MessageTransportProtocol messageTransport;

    private Environment environment;

    // Constructors.

    public BasicBroadcast(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }

    // Methods.

    private BroadcastMessage createBroadcastMessage(Message message) {
        return new BroadcastMessage(getAgentOwner().getAgentIdentifier(), message, getIdentifier());
    }

    /**
     * @param message the message to broadcast
     *
     * @throws IllegalArgumentException if the message to broadcast is null
     */
    @Override
    public void broadcast(Message message) {
        Message toSend =
                Optional.ofNullable(message).orElseThrow(() -> new IllegalArgumentException(Message.class + " to broadcast must be " + "not null"));
        BroadcastMessage broadcastMessage = createBroadcastMessage(toSend);
        for (AgentIdentifier agent : environment.getEvolvingAgentIdentifiers()) {
            messageTransport.send(agent, broadcastMessage);
        }
    }

    @Override
    public void receive(Message message) {
        if (message instanceof BroadcastMessage broadcastMessage) {
            deliver(broadcastMessage);
        } else
            throw new UnsupportedOperationException(
                    getClass() + " does not support the reception of other type of " + Message.class + " than " + BroadcastMessage.class);
    }

    /**
     * Deliver the message by extract the content of the {@link BroadcastMessage} and try to find the intended protocol in the owner agent.
     * <p>
     * If the owner agent does not know the intended agent, throws an {@link UnknownProtocolForAgentException}.
     *
     * @param message to deliver
     *
     * @throws UnknownProtocolForAgentException if the intended protocol is not known by the owner agent
     */
    @Override
    public void deliver(Message message) {
        if (message instanceof BroadcastMessage broadcastMessage) {
            var content = broadcastMessage.getContent();
            var intendedProtocolIdentifier = content.getIntendedProtocol();
            var intendedProtocol = getAgentOwner().getProtocol(intendedProtocolIdentifier);
            if (intendedProtocol != null)
                intendedProtocol.processEvent(content);
            else
                throw new UnknownProtocolForAgentException(
                        "The agent " + getAgentOwner() + " does not know the protocol identify by " + intendedProtocolIdentifier);
        } else
            throw new UnsupportedOperationException(
                    getClass() + " does not support the delivery of other type of " + Message.class + " than a" + BroadcastMessage.class);
    }

    @Override
    public void processEvent(Event event) {
        if (event instanceof BroadcastMessage broadcastMessage) {
            receive(broadcastMessage);
        } else
            throw new UnsupportedOperationException(
                    getClass() + " does not support the delivery of other type of " + Event.class + " than " + BroadcastMessage.class);
    }

    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }

    // Getters and setters.

    public void setMessageTransport(MessageTransportProtocol messageTransport) {
        this.messageTransport = messageTransport;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
