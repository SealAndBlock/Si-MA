package sima.basic.broadcast;

import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.Message;
import sima.basic.transport.MessageTransportProtocol;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.event.transport.TransportableInEvent;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.ProtocolManipulator;
import sima.core.protocol.TransportableIntendedToProtocol;

import java.util.Map;

public class SimpleBroadcastProtocol extends MessageTransportProtocol implements Broadcaster {
    
    // Constructors.
    
    public SimpleBroadcastProtocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    private BroadcastMessage createBroadcastMessage(TransportableIntendedToProtocol content) {
        return new BroadcastMessage(getAgentOwner().getAgentIdentifier(), content, getIdentifier());
    }
    
    @Override
    public void broadcast(TransportableIntendedToProtocol content) {
        for (AgentIdentifier agent : getEnvironment().getEvolvingAgentIdentifiers()) {
            transport(agent, createBroadcastMessage(content));
        }
    }
    
    @Override
    public void receive(Message message) {
        if (message instanceof BroadcastMessage broadcastMessage)
            deliver(broadcastMessage);
        else
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
            TransportableIntendedToProtocol content = broadcastMessage.getContent();
            var intendedProtocolIdentifier = content.getIntendedProtocol();
            if (getAgentOwner().getProtocol(intendedProtocolIdentifier) != null)
                getAgentOwner().getProtocol(intendedProtocolIdentifier).processEventTransportable(content);
            else
                throw new UnknownProtocolForAgentException(
                        "The agent " + getAgentOwner() + " does know the protocol identify by " + intendedProtocolIdentifier);
        } else
            throw new UnsupportedOperationException(
                    getClass() + " does not support the delivery of other type of " + Message.class + " than " + BroadcastMessage.class);
    }
    
    /**
     * Does not treat any sort of {@link TransportableInEvent}. Throws an {@link UnsupportedOperationException}.
     *
     * @param transportableInEvent to process
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void processEventTransportable(TransportableInEvent transportableInEvent) {
        throw new UnsupportedOperationException(SimpleBroadcastProtocol.class + " does not treat " + TransportableInEvent.class);
    }
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }
}
