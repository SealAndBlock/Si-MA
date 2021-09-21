package sima.basic.broadcast;

import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.MessageReceiver;
import sima.core.environment.exchange.transport.Transportable;

public interface Broadcaster extends MessageReceiver<BroadcastMessage> {
    
    void broadcast(Transportable content);
    
}