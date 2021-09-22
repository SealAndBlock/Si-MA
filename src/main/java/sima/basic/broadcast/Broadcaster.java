package sima.basic.broadcast;

import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.MessageReceiver;
import sima.core.environment.event.transport.EventTransportable;

public interface Broadcaster extends MessageReceiver<BroadcastMessage> {
    
    void broadcast(EventTransportable content);
    
}
