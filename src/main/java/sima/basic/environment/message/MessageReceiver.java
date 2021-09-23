package sima.basic.environment.message;

public interface MessageReceiver {
    
    /**
     * Treats the reception of the message
     *
     * @param message to receive
     */
    void receive(Message message);
    
    /**
     * Treats the delivery of the message
     *
     * @param message to deliver
     */
    void deliver(Message message);
}
