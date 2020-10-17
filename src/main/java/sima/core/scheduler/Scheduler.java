package sima.core.scheduler;

import sima.core.agent.AbstractAgent;
import sima.core.environment.event.Event;

/**
 * Provides methods to schedule {@link Event}, {@link Action} or {@link Controller} during the simulation.
 */
public interface Scheduler {

    /**
     * The waiting time for schedule something now.
     */
    long NOW = 1;

    /**
     * Add the scheduler watcher. If the scheduler watcher is already added, nothing is done.
     *
     * @param schedulerWatcher the scheduler watcher
     * @return true if the schedulerWatcher has been added, else false.
     */
    boolean addSchedulerWatcher(SchedulerWatcher schedulerWatcher);

    /**
     * @param schedulerWatcher the scheduler watcher to remove
     */
    void removeSchedulerWatcher(SchedulerWatcher schedulerWatcher);

    /**
     * Start the scheduler.
     * <p>
     * If the scheduler has been started, it cannot be started an second time and returns false.
     * <p>
     * A {@code Scheduler} can be re started if it at been kill.
     *
     * @return true if the {@code Scheduler} is started, else false.
     */
    boolean start();

    /**
     * Kill the scheduler.
     * <p>
     * Only kill the scheduler if it was already started before. If the scheduler was not started, nothing is done and
     * returns false.
     * <p>
     * After to be killed, the scheduler can be restarted with the method start, however all information contains in
     * the scheduler are clear except {@link SchedulerWatcher}.
     *
     * @return true if the {@code Scheduler} has been killed, else false.
     */
    boolean kill();

    /**
     * Schedule the execution of the {@link Executable}. In other words, schedule the moment when the method
     * {@link Executable#execute()} is called and execute. The waiting time is the number of time unit that the
     * scheduler must wait after the call of this method to execute the {@code Executable}. The {@link ScheduleMode}
     * define if the {@code Executable} will be executed once time or in repeated way or in infinitely way. If the
     * {@code ScheduleMode} is {@link ScheduleMode#REPEATED}, then the specified nbRepetitions and executionTimeStep are
     * not ignored.
     * <p>
     * The waitingTime must be greater or equal to 1, it is not possible to schedule an {@code Executable} on the
     * {@code currentTime}. If it is not the case throws {@link IllegalArgumentException}.
     * <p>
     * The nbRepetitions must be  greater or equal to 1, else throws {@link IllegalArgumentException}.
     *
     * @param executable        the executable to schedule
     * @param waitingTime       the waiting time before begin the schedule of the executable (greater or equal to 1)
     * @param scheduleMode      the schedule mode
     * @param nbRepetitions     the number of times that the executable will be repeated, only take in account if
     *                          scheduleMode equal {@link ScheduleMode#REPEATED}.
     * @param executionTimeStep the time between each execution of the executable if the schedule mode is
     *                          {@link ScheduleMode#REPEATED} (greater or equal to 1 if in repeated mod)
     * @throws IllegalArgumentException if waitingTime, nbRepetitions or executionTimeStep is less than 1.
     */
    void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode, int nbRepetitions,
                            int executionTimeStep);

    /**
     * Schedule the execution of the {@link Executable} at a specific time in the simulation. In other words, schedule
     * the moment in the simulation when the method {@link Executable#execute()} is called and execute.
     * <p>
     * If the simulationSpecificTime is less or equal to the {@code currentTime}, throws
     * {@link IllegalArgumentException}.
     * <p>
     * If the simulationSpecificTime is greater than the end of the simulation, nothing id done.
     *
     * @param executable             the executable to schedule
     * @param simulationSpecificTime the specific time of the simulation when the executable is execute (greater or
     *                               equal to 0 if in repeated mod)
     * @throws IllegalArgumentException                                  if the simulationSpecificTime is less than 0.
     * @throws sima.core.scheduler.exception.NotSchedulableTimeException if the simulationSpecificTime is already pass.
     */
    void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime);

    /**
     * Schedule one time the executable.
     *
     * @param executable  the executable to schedule
     * @param waitingTime the waiting time before begin the schedule of the executable (greater or equal to 1)
     * @throws IllegalArgumentException                                  if the waitingTime is less than 1.
     * @throws sima.core.scheduler.exception.NotSchedulableTimeException if the simulationSpecificTime is greater than
     *                                                                   the terminate time of the simulation
     * @see #scheduleExecutable(Executable, long, ScheduleMode, int, int)
     */
    default void scheduleExecutableOnce(Executable executable, long waitingTime) {
        this.scheduleExecutable(executable, waitingTime, ScheduleMode.ONCE, -1, -1);
    }

    /**
     * Schedule repeatedly the execution of the {@link Executable}. The time between each execution is the
     * executionTimeStep and the number of repetitions is nbRepetitions
     * <p>
     * The waitingTime must be greater or equal to 1, it is not possible to schedule an {@code Executable} on the
     * {@code currentTime}.
     *
     * @param executable        the executable to schedule
     * @param waitingTime       the waiting time before begin the schedule of the executable (greater or equal to 1)
     * @param nbRepetitions     the number of times that the executable will be repeated
     * @param executionTimeStep the time between each execution (greater or equal to 1 if in repeated mod)
     * @throws IllegalArgumentException if waitingTime, nbRepetitions or executionTimeStep is less than 1.
     * @see #scheduleExecutable(Executable, long, ScheduleMode, int, int)
     */
    default void scheduleExecutableRepeated(Executable executable, long waitingTime, int nbRepetitions,
                                            int executionTimeStep) {
        this.scheduleExecutable(executable, waitingTime, ScheduleMode.REPEATED, nbRepetitions, executionTimeStep);
    }

    /**
     * Schedule infinitely the execution of the {@link Executable}.
     * <p>
     * <strong>Remark,</strong> if the waitingTime is equal to 0, the {@code Executable} will be executed at the
     * {@code currentTime + 1} because it is not possible to schedule an {@code Executable} in the current time.
     *
     * @param executable        the executable to schedule
     * @param waitingTime       the waiting time before begin the schedule of the executable (greater or equal to 1)
     * @param executionTimeStep the time between each execution (greater or equal to 1 if in repeated mod)
     * @throws sima.core.scheduler.exception.NotSchedulableTimeException if the simulationSpecificTime is greater than
     *                                                                   the terminate time of the simulation
     * @throws IllegalArgumentException                                  if waitingTime or executionTimeStep is less
     *                                                                   than 1.
     */
    default void scheduleExecutableInfinitely(Executable executable, long waitingTime, int executionTimeStep) {
        this.scheduleExecutable(executable, waitingTime, ScheduleMode.INFINITELY, -1, executionTimeStep);
    }

    /**
     * Schedule the to send of the {@link Event}. In other words, schedule the moment when the method
     * {@link sima.core.agent.AbstractAgent#processEvent(Event)} of the {@link Event#getReceiver()} agent is called.
     * Therefore, the specified {@code Event} must have a not null receiver, else throws an
     * {@link NullPointerException}.
     * <p>
     * The waitingTime must be greater or equal to 1, it is not possible to schedule an {@code Executable} on the
     * {@code currentTime}.
     *
     * @param event       the event to schedule
     * @param waitingTime the time to wait before send the event (greater or equal to 1 if in repeated mod)
     * @throws IllegalArgumentException if the waitingTime is less than 1 or if the event receiver is null.
     */
    default void scheduleEvent(Event event, long waitingTime) {
        if (event.getReceiver() != null) {
            Action eventAction = new Action(event.getReceiver()) {
                @Override
                public void execute() {
                    AbstractAgent receiver = null; // TODO get the receiver agent from the simulation
                    receiver.processEvent(event);
                }
            };
            this.scheduleExecutableOnce(eventAction, waitingTime);
        } else
            throw new IllegalArgumentException("The Event receiver is null");
    }

    /**
     * @return the current time of the simulation.
     */
    long getCurrentTime();

    /**
     * Enum to specify how many time an {@link Action} or a {@link Controller} can be schedule.
     */
    enum ScheduleMode {
        ONCE, REPEATED, INFINITELY
    }

    interface SchedulerWatcher {

        /**
         * Called when the {@link Scheduler} is started with the method {@link #start()}.
         */
        void schedulerStarted();

        /**
         * Called whe the {@link Scheduler} is killed with the method {@link #kill()}.
         */
        void schedulerKilled();

        /**
         * Call when the {@link Scheduler} has reach the end simulation time.
         */
        void simulationEndTimeReach();

        /**
         * Call when the {@link Scheduler} has not anymore {@link Executable} to execute.
         */
        void noExecutableToExecute();
    }
}
