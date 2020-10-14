package sima.core.scheduler;

import sima.core.environment.event.Event;

public interface Scheduler {

    /**
     * Schedule the execution of the {@link Executable}. In other words, schedule the moment when the method
     * {@link Executable#execute()} is called and execute. The waiting time is the number of time unit that the
     * scheduler must wait after the call of this method to execute the {@code Executable}.
     *
     * @param executable  the executable to schedule
     * @param waitingTime the time to wait before execute the executable
     * @throws IllegalArgumentException if the waitingTime is less than 0.
     */
    void scheduleExecutable(Executable executable, long waitingTime);

    /**
     * Schedule the execution of the {@link Executable}. In other words, schedule the moment when the method
     * {@link Executable#execute()} is called and execute. The waiting time is the number of time unit that the
     * scheduler must wait after the call of this method to execute the {@code Executable}. The {@link ScheduleMode}
     * define if the {@code Executable} will be executed once time or in repeated way or in infinitely way. If the
     * {@code ScheduleMode} is {@link ScheduleMode#REPEATED}, then the specified executionTimeStep is the time between
     * each execution of the {@link Executable}. For other mods, this parameter is ignored.
     *
     * @param executable        the executable to schedule
     * @param waitingTime       the waiting time before begin the schedule of the executable
     * @param scheduleMode      the schedule mode
     * @param executionTimeStep the time before each execution of the executable if the schedule mode is
     *                          {@link ScheduleMode#REPEATED}
     */
    void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode, int executionTimeStep);

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

    /**
     * Enum to specify how many time an {@link Action} or a {@link Controller} can be schedule.
     */
    enum ScheduleMode {
        ONCE, REPEATED, INFINITELY;
    }
}
