package sima.standard.broadcast.basic;

import sima.standard.broadcast.MessageBroadcaster;
import sima.standard.environment.message.Message;
import sima.standard.environment.message.MessageReceiver;
import sima.standard.transport.MessageTransportProtocol;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A class which implements message broadcasting.
 * <p>
 * This broadcast is a Best-Effort Broadcast. A Best-Effort Broadcast has three properties:
 * <p>
 * <ul>
 * <li><strong>Validity:</strong> if a correct process <i>p</i> broadcast a message <i>m</i> then every correct process eventually deliver <i>m</i>
 * </li>
 * <li><strong>No duplication:</strong> No message is deliver more than once</li>
 * <li><strong>No creation:</strong> if a process deliver a message <i>m</i> with a sender <i>s</i>, then <i>m</i> was previously broadcast by
 * <i>s</i></li>
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
        sendToAll(createBroadcastMessage(isNotNull(message)));
    }

    /**
     * Verify if the message is not null. If it is not the case, throws {@link IllegalArgumentException}
     *
     * @param message the message to verify
     *
     * @return the message if the specified message is not null.
     *
     * @throws IllegalArgumentException if the message is null
     */
    protected Message isNotNull(Message message) {
        message = Optional
                .ofNullable(message)
                .orElseThrow(() -> new IllegalArgumentException(Message.class + " to broadcast must be " + "not null"));
        return message;
    }

    /**
     * Take all members of the group membership and send to each the specified {@link BroadcastMessage}.
     *
     * @param broadcastMessage the broadcast message to send
     */
    protected void sendToAll(BroadcastMessage broadcastMessage) {
        for (AgentIdentifier agent : getGroupMemberShip()) {
            messageTransport.send(agent, broadcastMessage);
        }
    }

    /**
     * Returns the list of all agents which are in the group membership. For the moment, use the method {@link
     * Environment#getEvolvingAgentIdentifiers()}.
     *
     * @return the list of all agents which are in the group membership.
     */
    public List<AgentIdentifier> getGroupMemberShip() {
        return environment.getEvolvingAgentIdentifiers();
    }

    @Override
    public void receive(Message message) {
        if (isAcceptedEvent(message))
            deliver(acceptedMessageClass().cast(message));
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
        if (isAcceptedEvent(message)) {
            var content = acceptedMessageClass().cast(message).getMessage();
            var intendedProtocol = getAgentOwner().getProtocol(content.getIntendedProtocol());
            if (intendedProtocol != null)
                intendedProtocol.processEvent(content);
            else
                throw new UnknownProtocolForAgentException(
                        "The agent " + getAgentOwner() + " does not know the protocol identify by " + content.getIntendedProtocol());
        }
    }

    @Override
    public void processEvent(Event event) {
        if (isAcceptedEvent(event))
            receive(acceptedMessageClass().cast(event));
    }

    /**
     * Verify if the specified {@link Event} is an accepted {@link Event}. For {@link BasicBroadcast}, an accepted {@link Event} is only an {@link
     * Event} which is an instance of {@link BroadcastMessage}.
     *
     * @param event the event to verify
     *
     * @return true if the specified {@link Event} is an accepted {@link Event}, else false.
     */
    protected boolean isAcceptedEvent(Event event) {
        if (acceptedMessageClass().isInstance(event))
            return true;
        else
            throw new UnsupportedOperationException(
                    getClass() + " does not support other type of " + Event.class + " than " + acceptedMessageClass());
    }

    /**
     * @return the class of {@link Event} that are accepted.
     */
    protected Class<? extends BroadcastMessage> acceptedMessageClass() {
        return BroadcastMessage.class;
    }

    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }

    // Getters and setters.


    public MessageTransportProtocol getMessageTransport() {
        return messageTransport;
    }

    public void setMessageTransport(MessageTransportProtocol messageTransport) {
        this.messageTransport = messageTransport;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
