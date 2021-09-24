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

public class SimpleBroadcastProtocol extends Protocol implements MessageBroadcaster, MessageReceiver {
    
    // Variables.
    
    private MessageTransportProtocol messageTransport;
    
    private Environment environment;
    
    private final Set<BroadcastMessage> messageReceived;
    
    // Constructors.
    
    public SimpleBroadcastProtocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
        messageReceived = new HashSet<>();
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
        Message toSend = Optional.ofNullable(message).orElseThrow(() -> new IllegalArgumentException(Message.class + " to broadcast must be " +
                "not null"));
        for (AgentIdentifier agent : environment.getEvolvingAgentIdentifiers()) {
            messageTransport.send(agent, createBroadcastMessage(toSend));
        }
    }
    
    @Override
    public void receive(Message message) {
        if (message instanceof BroadcastMessage broadcastMessage) {
            deliver(broadcastMessage);
            if (messageReceived.add(broadcastMessage)) {
                reBroadcast(broadcastMessage);
            }
        } else
            throw new UnsupportedOperationException(
                    getClass() + " does not support the reception of other type of " + Message.class + " than " + BroadcastMessage.class);
    }
    
    /**
     * Re broadcast a {@link BroadcastMessage} that we just receive.
     * <p>
     * Re broadcast to all except the owner agent.
     *
     * @param broadcastMessage the message to re broadcast
     */
    protected void reBroadcast(BroadcastMessage broadcastMessage) {
        for (AgentIdentifier agent : environment.getEvolvingAgentIdentifiers()) {
            if (!isAgentOwner(agent))
                messageTransport.send(agent, broadcastMessage);
        }
    }
    
    /**
     * @param agent the agent to compare
     *
     * @return true if the agent is {@link #getAgentOwner()}, else false.
     */
    private boolean isAgentOwner(AgentIdentifier agent) {
        return agent.equals(getAgentOwner().getAgentIdentifier());
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
