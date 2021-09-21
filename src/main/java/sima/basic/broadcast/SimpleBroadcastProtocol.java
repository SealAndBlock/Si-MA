package sima.basic.broadcast;

import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.transport.TransportProtocol;
import sima.core.agent.SimpleAgent;
import sima.core.environment.exchange.event.Event;
import sima.core.environment.exchange.transport.Transportable;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

public class SimpleBroadcastProtocol extends TransportProtocol implements Broadcaster {
    
    // Constructors.
    
    public SimpleBroadcastProtocol(String protocolTag, SimpleAgent agentOwner, Map<String, String> args) {
        super(protocolTag, agentOwner, args);
    }
    
    // Methods.
    
    private BroadcastMessage createBroadcastMessage(Transportable content) {
        return new BroadcastMessage(getAgentOwner().getAgentIdentifier(), content, getIdentifier());
    }
    
    @Override
    public void broadcast(Transportable content) {
        environment.broadcastEvent(createBroadcastMessage(content));
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
        Transportable content = broadcastMessage.getContent();
        var intendedProtocolIdentifier = broadcastMessage.getProtocolIntended();
        if (getAgentOwner().getProtocol(intendedProtocolIdentifier) != null)
            getAgentOwner().getProtocol(intendedProtocolIdentifier).processTransportable(content);
        else
            throw new UnknownProtocolForAgentException(
                    "The agent " + getAgentOwner() + " does know the protocol identify by " + intendedProtocolIdentifier);
        
    }
    
    @Override
    public void processEvent(Event event) {
        if (event instanceof BroadcastMessage broadcastMessage)
            receive(broadcastMessage);
        else
            throw new UnsupportedOperationException(
                    "A" + this.getClass() + "cannot receive others types of " + Event.class + " than " + BroadcastMessage.class);
    }
    
    @Override
    protected ProtocolManipulator createDefaultProtocolManipulator() {
        return new ProtocolManipulator.DefaultProtocolManipulator(this);
    }
}
