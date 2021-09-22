package sima.basic.broadcast;

import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.event.MessageReceptionEvent;
import sima.basic.transport.TransportProtocol;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.EventTransportable;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

public class SimpleBroadcastProtocol extends TransportProtocol implements Broadcaster {
    
    // Static.
    
    public static final long WRONG_DELAY = 10L;
    
    // Constructors.
    
    public SimpleBroadcastProtocol(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    private BroadcastMessage createBroadcastMessage(EventTransportable content) {
        return new BroadcastMessage(getAgentOwner().getAgentIdentifier(), content, getIdentifier());
    }
    
    private MessageReceptionEvent createBroadcastMessageReception(BroadcastMessage broadcastMessage) {
        return new MessageReceptionEvent(broadcastMessage, broadcastMessage.getIntendedProtocol());
    }
    
    @Override
    public void broadcast(EventTransportable content) {
        for (AgentIdentifier agent : environment.getEvolvingAgentIdentifiers()) {
            // Must use the physical layer
            environment.processEventOn(agent, createBroadcastMessageReception(createBroadcastMessage(content)), WRONG_DELAY);
        }
    }
    
    @Override
    public void receive(BroadcastMessage broadcastMessage) {
        deliver(broadcastMessage);
    }
    
    /**
     * Deliver the message by extract the content of the {@link BroadcastMessage} and try to find the intended protocol in the owner agent.
     * <p>
     * If the owner agent does not know the intended agent, throws an {@link UnknownProtocolForAgentException}.
     *
     * @param broadcastMessage to deliver
     *
     * @throws UnknownProtocolForAgentException if the intended protocol is not known by the owner agent
     */
    @Override
    public void deliver(BroadcastMessage broadcastMessage) {
        EventTransportable content = broadcastMessage.getContent();
        var intendedProtocolIdentifier = broadcastMessage.getIntendedProtocol();
        if (getAgentOwner().getProtocol(intendedProtocolIdentifier) != null)
            getAgentOwner().getProtocol(intendedProtocolIdentifier).processEventTransportable(content);
        else
            throw new UnknownProtocolForAgentException(
                    "The agent " + getAgentOwner() + " does know the protocol identify by " + intendedProtocolIdentifier);
        
    }
    
    @Override
    public void processEvent(Event event) {
        if (event.getContent() instanceof BroadcastMessage broadcastMessage)
            receive(broadcastMessage);
        else
            throw new UnsupportedOperationException(
                    "A" + this.getClass() + " only treats " + Event.class + " which contains " + BroadcastMessage.class);
    }
    
    /**
     * Does not treat any sort of {@link EventTransportable}. Throws an {@link UnsupportedOperationException}.
     *
     * @param eventTransportable to process
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void processEventTransportable(EventTransportable eventTransportable) {
        throw new UnsupportedOperationException(SimpleBroadcastProtocol.class + " does not treat " + EventTransportable.class);
    }
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }
}
