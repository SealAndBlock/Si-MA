package sima.core.environment.event;

import sima.core.protocol.ProtocolIdentificator;

import java.io.Serializable;
import java.util.UUID;

/**
 * A {@link BroadcastMessage} is a {@link Message} with no receiver (the receiver is null).
 */
public abstract class BroadcastMessage extends Message {

    // Constructors.

    public BroadcastMessage(UUID sender, ProtocolIdentificator protocolTargeted, Serializable content) {
        super(sender, null, protocolTargeted, content);
    }
}
