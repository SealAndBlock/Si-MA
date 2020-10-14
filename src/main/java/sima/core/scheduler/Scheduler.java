package sima.core.scheduler;

import sima.core.environment.event.Event;

public interface Scheduler {

    /**
     * Schedule the execution of the {@link Action}. In other words, schedule the moment when the method
     * {@link Action#execute()} is called and execute. The waiting time is the number of time unit that the scheduler
     * must wait after the call of this method to execute the {@code Action}.
     *
     * @param action      the action to schedule
     * @param waitingTime the time to wait before execute the action
     * @throws IllegalArgumentException if the waitingTime is less than 0.
     */
    void scheduleAction(Action action, long waitingTime);

    /**
     * Schedule the to send of the {@link Event}. In other words, schedule the moment when the method
     * {@link sima.core.agent.AbstractAgent#processEvent(Event)} of the {@link Event#getReceiver()} agent is called.
     * Therefore, the specified {@code Event} must have a not null receiver, else throws an
     * {@link NullPointerException}.
     *
     * @param event       the event to schedule
     * @param waitingTime the time to wait before send the event
     * @throws IllegalArgumentException if the waitingTime is less than 0.
     * @throws NullPointerException     if the {@link Event#getReceiver()} is null
     */
    void scheduleEvent(Event event, long waitingTime);
}
