package sima.basic.broadcast;

import sima.basic.environment.message.Message;

public interface MessageBroadcaster {
    
    /**
     * Broadcast the {@link Message}.
     *
     * @param message the message to broadcast
     */
    void broadcast(Message message);
    
}
