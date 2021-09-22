package sima.core.environment.event;

import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;

/**
 * Use to manipulated and send event.
 */
public interface EventGenerator {
    
    /**
     * Schedule the call of the method {@link SimaAgent#processEvent(Event)} to the {@link SimaAgent} targeted after the specified delay.
     *
     * @param target the agent targeted
     * @param event  the event to process
     * @param delay  the delay to wait before call the method processEvent
     *
     * @throws IllegalArgumentException if the delay is less than {@link sima.core.scheduler.Scheduler#NOW}
     */
    void processEventOn(AgentIdentifier target, Event event, long delay);
}
