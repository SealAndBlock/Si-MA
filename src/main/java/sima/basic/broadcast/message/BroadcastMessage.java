package sima.basic.broadcast.message;

import sima.basic.environment.message.Message;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Transportable;
import sima.core.protocol.ProtocolIdentifier;

public class BroadcastMessage extends Message {
    
    // Constructors.
    
    public BroadcastMessage(AgentIdentifier sender, Transportable content, ProtocolIdentifier protocolTargeted) {
        super(sender, null, content, protocolTargeted);
    }
}
