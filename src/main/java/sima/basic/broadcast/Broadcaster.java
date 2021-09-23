package sima.basic.broadcast;

import sima.core.protocol.TransportableIntendedToProtocol;

public interface Broadcaster {
    
    void broadcast(TransportableIntendedToProtocol content);
    
}
