package sima.standard.broadcast;

import sima.standard.environment.message.Message;

public interface MessageBroadcaster {
    
    /**
     * Broadcast the {@link Message}.
     *
     * @param message the message to broadcast
     */
    void broadcast(Message message);
    
}
