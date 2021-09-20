package sima.basic.environment.message;

public interface MessageReceiver<T extends Message> {
    
    /**
     * Treats the reception of the message
     *
     * @param message to receive
     */
    void receive(T message);
    
    /**
     * Treats the delivery of the message
     *
     * @param message to deliver
     */
    void deliver(T message);
}
