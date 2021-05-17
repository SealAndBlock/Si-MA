package sima.core.environment.event;

import sima.core.agent.AgentIdentifier;

/**
 * Use to manipulated and send event.
 */
public interface EventSender {

    /**
     * Sends the {@link Event} to the agent receiver. In that way the {@code Event} must have a receiver not null. If it
     * is not the case, throws an {@link IllegalArgumentException}.
     *
     * @param event the event to send
     * @throws IllegalArgumentException if the event has a null agent receiver
     */
    void sendEvent(Event event);

    /**
     * Broadcast the {@link Event}. This method must not take care about the agent receiver (it can be null) and chose
     * all receivers which must receive the Event. THe best way is to use the method {@link #sendEvent(Event)} during
     * the broadcast. However {@link #sendEvent(Event)} method only accept {@code Event} with not null receiver,
     * therefore the best way to implement it its to clone the specifier event with the method {@link
     * Event#duplicateWithNewReceiver(AgentIdentifier)} for each determined receiver.
     * <pre>Example: {@code
     * broadcastEvent(Event e) {
     *     for (AgentIdentifier receiver : agentList) {
     *          if (canReceiveEvent(receiver) {
     *              sendEvent(e.cloneAndSetReceiver(receiver));
     *          }
     *     }
     * }
     * }</pre>
     *
     * @param event the event to broadcast.
     */
    void broadcastEvent(Event event);
}
