package sima.core.environment.event;

import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;

/**
 * Used to assign an {@link Event} to a target {@link SimaAgent}.
 */
public interface EventAssignor {

    /**
     * Assign the {@link Event} to a target {@link SimaAgent} to call the method {@link SimaAgent#processEvent(Event)} after the specified delay.
     * <p>
     * The initiator is only here to verify if it is killed are not. A killed agent cannot initiate {@link Event}.
     *
     * @param initiator the agent which initiate the event
     * @param target    the agent targeted
     * @param event     the event to process
     * @param delay     the delay to wait before call the method processEvent
     *
     * @throws IllegalArgumentException if the delay is less than {@link sima.core.scheduler.Scheduler#NOW}
     */
    void assignEventOn(AgentIdentifier initiator, AgentIdentifier target, Event event, long delay);
}
