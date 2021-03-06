package sima.core.scheduler;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.exception.*;
import sima.core.scheduler.executor.Executable;
import sima.core.scheduler.executor.MultiThreadExecutor;
import sima.core.simulation.SimaSimulation;

import java.util.Optional;

/**
 * Provides methods to schedule {@link Executable} during the simulation.
 */
public interface Scheduler {

    /**
     * The waiting time for schedule something now.
     */
    long NOW = 1;

    /**
     * Add the scheduler watcher. If the scheduler watcher is already added, nothing is done and returns false.
     * <p>
     * Cannot add null.
     *
     * @param schedulerWatcher the scheduler watcher
     *
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
     * If the scheduler has been started, it cannot be started a second time and returns false.
     * <p>
     * A {@code Scheduler} cannot be restarted if it at been killed. In that case, the method returns false.
     *
     * @return true if the {@code Scheduler} is started, else false.
     */
    boolean start();

    /**
     * Kill the scheduler.
     * <p>
     * Only kill the scheduler if it was already started before. If the scheduler was not started, nothing is done and returns false.
     * <p>
     * After to be killed, the scheduler cannot be restarted with the method start. To restart a scheduler, you must create a new instance of it.
     *
     * @return true if the {@code Scheduler} has been killed, else false.
     */
    boolean kill();

    /**
     * Verifies if the scheduler is running or not. A scheduler is running after the call of the method {@link #start()} and it has some executables
     * to execute. After the call of the method {@link #kill()}, the scheduler is not running and this method returns always false.
     *
     * @return true if the scheduler is running, else false.
     */
    boolean isRunning();

    /**
     * Returns true if the scheduler has been killed, else false. A scheduler is killed after the first call of the method {@link Scheduler#kill()}.
     * When a Scheduler has been killed, it cannot be started anymore.
     *
     * @return true if the scheduler is killed, else false.
     */
    boolean isKilled();

    /**
     * Schedules the execution of the {@link Executable}. In other words, schedules the moment when the method {@link Executable#execute()} is called
     * and execute. The waiting time is the number of time unit that the scheduler must wait after the call of this method to execute the {@code
     * Executable}. The {@link ScheduleMode} define if the {@code Executable} will be executed once time or in repeated way or in infinitely way. If
     * the {@code ScheduleMode} is {@link ScheduleMode#REPEATED}, then the specified nbRepetitions and executionTimeStep are not ignored.
     * <p>
     * The waitingTime must be greater or equal to {@link #NOW}, it is not possible to schedule an {@code Executable} on the {@code currentTime}. If
     * it is not the case throws {@link IllegalArgumentException}.
     * <p>
     * The nbRepetitions and executionTimeStep must be  greater or equal to {@link #NOW} if the scheduleMode is different to {@link
     * ScheduleMode#ONCE}. If it is not the case throws {@link IllegalArgumentException}.
     * <p>
     * <strong>WARNING!</strong> If the {@link Scheduler#getTimeMode()} is equal to {@link TimeMode#REAL_TIME} the unit time used is the
     * millisecond.
     *
     * @param executable        the executable to schedule
     * @param waitingTime       the waiting time before begin the schedule of the executable (greater or equal to {@link #NOW})
     * @param scheduleMode      the schedule mode
     * @param nbRepetitions     the number of times that the executable will be repeated, only take in account if scheduleMode equal {@link
     *                          ScheduleMode#REPEATED}.
     * @param executionTimeStep the time between each execution of the executable if the schedule mode is {@link ScheduleMode#REPEATED} (greater or
     *                          equal to {@link #NOW} if in repeated mode)
     *
     * @throws NullPointerException     if the executable is null.
     * @throws IllegalArgumentException if waitingTime is less than {@link #NOW} or if nbRepetitions (for REPEATED scheduleMode) or executionTimeStep
     *                                  (for REPEATED and INFINITE) is less than {@link #NOW}.
     */
    void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode, long nbRepetitions,
                            long executionTimeStep);

    /**
     * Schedules the execution of the {@link Executable} at a specific time in the simulation. In other words, schedules the moment in the simulation
     * when the method {@link Executable#execute()} is called and execute.
     * <p>
     * If the simulationSpecificTime is less or equal to the {@code currentTime}, throws {@link IllegalArgumentException}.
     * <p>
     * If the simulationSpecificTime is greater than the end of the simulation, nothing id done.
     * <p>
     * Here the simulationSpecificTime is based on the start of the scheduler. Therefore, if the simulationSpecificTime is equal to 5, it is 5 time
     * unit (or milliseconds if REAL_TIME mode) after the start of the scheduler and not 5 after the call of this method.
     * <p>
     * <strong>WARNING!</strong> If the Simulation time mode is
     * {@link TimeMode#REAL_TIME} the unit time use is the millisecond.
     *
     * @param executable             the executable to schedule
     * @param simulationSpecificTime the specific time of the simulation when the executable is executed (greater or equal to 0 if in repeated mod)
     *
     * @throws NullPointerException     if the executable is null.
     * @throws IllegalArgumentException if the simulationSpecificTime is less than {@link #NOW}.
     * @throws NotScheduleTimeException if the simulationSpecificTime is already pass.
     */
    void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime);

    /**
     * Schedule one time the executable.
     * <p>
     * <strong>WARNING!</strong> If the Simulation time mode is
     * {@link TimeMode#REAL_TIME} the unit time use is the millisecond.
     *
     * @param executable  the executable to schedule
     * @param waitingTime the waiting time before begin the schedule of the executable (greater or equal to {@link #NOW})
     *
     * @throws NullPointerException     if the executable is null.
     * @throws IllegalArgumentException if the waitingTime is less than {@link #NOW}.
     * @see #scheduleExecutable(Executable, long, ScheduleMode, long, long)
     */
    default void scheduleExecutableOnce(Executable executable, long waitingTime) {
        scheduleExecutable(executable, waitingTime, ScheduleMode.ONCE, -1, -1);
    }

    /**
     * Schedule repeatedly the execution of the {@link Executable}. The time between each execution is the executionTimeStep and the number of
     * repetitions is nbRepetitions
     * <p>
     * The waitingTime must be greater or equal to {@link #NOW}, it is not possible to schedule an {@code Executable} on the {@code currentTime}.
     * <p>
     * <strong>WARNING!</strong> If the Simulation time mode is
     * {@link TimeMode#REAL_TIME} the unit time use is the millisecond.
     *
     * @param executable        the executable to schedule
     * @param waitingTime       the waiting time before begin the schedule of the executable (greater or equal to {@link #NOW})
     * @param nbRepetitions     the number of times that the executable will be repeated
     * @param executionTimeStep the time between each execution (greater or equal to {@link #NOW} if in repeated mod)
     *
     * @throws NullPointerException     if the executable is null.
     * @throws IllegalArgumentException if waitingTime, nbRepetitions or executionTimeStep is less than {@link #NOW}.
     * @see #scheduleExecutable(Executable, long, ScheduleMode, long, long)
     */
    default void scheduleExecutableRepeated(Executable executable, long waitingTime, long nbRepetitions,
                                            long executionTimeStep) {
        scheduleExecutable(executable, waitingTime, ScheduleMode.REPEATED, nbRepetitions, executionTimeStep);
    }

    /**
     * Schedule infinitely the execution of the {@link Executable}.
     * <p>
     * <strong>Remark,</strong> if the waitingTime is equal to 0, the {@code Executable} will be executed at the
     * {@code currentTime + {@link #NOW}} because it is not possible to schedule an {@code Executable} in the current time.
     * <p>
     * <strong>WARNING!</strong> If the Simulation time mode is
     * {@link TimeMode#REAL_TIME} the unit time use is the millisecond.
     *
     * @param executable        the executable to schedule
     * @param waitingTime       the waiting time before begin the schedule of the executable (greater or equal to {@link #NOW})
     * @param executionTimeStep the time between each execution (greater or equal to {@link #NOW} if in repeated mod)
     *
     * @throws NullPointerException     if the executable is null.
     * @throws IllegalArgumentException if waitingTime or executionTimeStep is less than {@link #NOW}.
     */
    default void scheduleExecutableInfinitely(Executable executable, long waitingTime, long executionTimeStep) {
        scheduleExecutable(executable, waitingTime, ScheduleMode.INFINITE, -1, executionTimeStep);
    }

    /**
     * Schedule the call of the method {@link SimaAgent#processEvent(Event)} of the specified {@link Event}.
     * <p>
     * The waitingTime must be greater or equal to {@link #NOW}, it is not possible to schedule an {@code Executable} on the {@code currentTime}.
     * <p>
     * <strong>WARNING!</strong> If the Simulation time mode is
     * {@link TimeMode#REAL_TIME} the unit time use is the millisecond.
     * <p>
     * The default implementation suppose that the scheduler is the scheduler of the {@link SimaSimulation} and that the simulation is running.
     *
     * @param target      the agent target
     * @param event       the event to schedule
     * @param waitingTime the time to wait before send the event (greater or equal to {@link #NOW} if in repeated mod)
     *
     * @throws NullPointerException     if the event or the target is null.
     * @throws IllegalArgumentException if the waitingTime is less than {@link #NOW} or the target is not found in the simulation
     */
    default void scheduleEvent(AgentIdentifier target, Event event, long waitingTime) {
        scheduleExecutableOnce(createExecutableFromEvent(Optional.of(target).get(), Optional.of(event).get()), waitingTime);
    }

    private @NotNull Executable createExecutableFromEvent(AgentIdentifier target, Event event) {
        final SimaAgent receiver = SimaSimulation.getAgent(target);
        if (receiver != null) {
            return new Executable() {
                @Override
                public void execute() {
                    receiver.processEvent(event);
                }

                @Override
                public Object getLockMonitor() {
                    return receiver;
                }
            };
        } else
            throw new IllegalArgumentException("SimaAgent identify by " + target + " has not been found");
    }

    /**
     * Make the current thread waiting.
     * <p>
     * This method required that the current thread which calls this method is an instance of {@link sima.core.scheduler.executor.MultiThreadExecutor.ExecutorThread}
     * and is executed by a {@link MultiThreadExecutor}. If these conditions are not respected, throws a {@link NotCorrectContextException}.
     * <p>
     * If the context is correct to call this method, the {@link Condition} pass in parameters is here to allow the wakeup of the thread. The {@code
     * Condition} will be prepared with the method {@link Condition#prepare()} by retrieve the current thread and cast it in {@code ExecutorThread} .
     * After that, another thread which has the instance of the {@code Condition} can call the method {@link Condition#wakeup()} and the waiting
     * thread will be wakeup.
     * <p>
     * This method never wakeup if the condition wakeup method is never call. To avoid infinite wait, use the method {@link #scheduleAwait(Condition,
     * long)}.
     *
     * @param condition the condition which allows the wakeup
     *
     * @throws NullPointerException       if the {@code Condition} is null
     * @throws NotCorrectContextException if the method is called out of a thread executed by another executor than a {@link MultiThreadExecutor}.
     * @throws ForcedWakeUpException      if the thread has been forced to wakeup (after {@link MultiThreadExecutor#shutdownNow()})
     * @throws InterruptedException       if the thread has been interrupted
     */
    void scheduleAwait(Condition condition) throws ForcedWakeUpException, InterruptedException;

    /**
     * Make the current thread waiting.
     * <p>
     * This method required that the current thread which calls this method is an instance of {@link sima.core.scheduler.executor.MultiThreadExecutor.ExecutorThread}
     * and is executed by a {@link MultiThreadExecutor}. If these conditions are not respected, throws a {@link NotCorrectContextException}.
     * <p>
     * If the context is correct to call this method, the {@link Condition} pass in parameters is here to allow the wakeup of the thread. The {@code
     * Condition} will be prepared with the method {@link Condition#prepare()} by retrieve the current thread and cast it in {@code ExecutorThread} .
     * After that, another thread which has the instance of the {@code Condition} can call the method {@link Condition#wakeup()} and the waiting
     * thread will be wakeup.
     * <p>
     * If the condition wakeup method is never call or call to late, after the timeout (timeout unit in function of {@link TimeMode}) the current
     * thread is wakeup.
     *
     * @param condition the condition which allows the wakeup
     * @param timeout   the timeout when the thread must be wakeup
     *
     * @throws NullPointerException       if the {@code Condition} is null
     * @throws IllegalArgumentException   if the timeout is less or equal to 0
     * @throws NotCorrectContextException if the method is called out of a thread executed by another executor than a {@link MultiThreadExecutor}.
     * @throws ForcedWakeUpException      if the thread has been forced to wakeup (after {@link MultiThreadExecutor#shutdownNow()})
     * @throws InterruptedException       if the thread has been interrupted
     * @see #scheduleAwait(Condition)
     */
    void scheduleAwait(Condition condition, long timeout) throws ForcedWakeUpException, InterruptedException;

    /**
     * Returns the current time of the simulation. If the scheduler is not started and not already killed, returns 0. If the {@code Scheduler} is
     * killed, returns -1;
     *
     * @return the current time of the simulation.
     */
    long getCurrentTime();

    /**
     * Returns the end of the simulation, must be greater or equal to {@link #NOW}.
     *
     * @return the end of the simulation.
     */
    long getEndSimulation();

    /**
     * Returns true if the {@code Scheduler} has reached the end of the simulation, else false. Reach the end of the simulation means that the current
     * time of the scheduler is greater or equal to the end simulation or that the {@code Scheduler} has been killed.
     *
     * @return true if the {@code Scheduler} has reached the end of the simulation, else false.
     */
    default boolean endSimulationReach() {
        return getCurrentTime() >= getEndSimulation() || isKilled();
    }

    /**
     * @return the Time mode of the Scheduler.
     */
    @NotNull TimeMode getTimeMode();

    /**
     * @return the Time mode of the Scheduler
     */
    @NotNull SchedulerType getSchedulerType();

    // Inner classes.

    /**
     * Enum to specify how many times an {@link Executable} can be schedule.
     */
    enum ScheduleMode {
        ONCE, REPEATED, INFINITE
    }

    /**
     * Time mode of the simulation.
     */
    enum TimeMode {
        REAL_TIME, DISCRETE_TIME
    }

    /**
     * Type of the scheduler. Mono-Thread or Multi-Thread.
     */
    enum SchedulerType {
        MULTI_THREAD, MONO_THREAD
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

    class Condition {

        // Variables.

        private MultiThreadExecutor.ExecutorThread executorThread;

        // Methods.

        /**
         * Get the actual context ant set {@link #executorThread} to the current thread.
         * <p>
         * After the call of this method, the method {@link #wakeup()} can be called.
         * <p>
         * It is not possible to call two times in a row this method else an {@link AlreadyPreparedConditionException} will be thrown. When a {@code
         * Condition} success to be prepared, to recall the method without throwing an {@code Exception} is to call the method {@link #wakeup()} and
         * after recall {@code prepare()}
         *
         * @throws ClassCastException                if the current thread is not an instance of {@link MultiThreadExecutor.ExecutorThread}
         * @throws AlreadyPreparedConditionException if the {@link Condition} has already been prepared
         * @see #wakeup()
         */
        public synchronized void prepare() {
            if (Optional.ofNullable(executorThread).isEmpty()) {
                executorThread = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
            } else
                throw new AlreadyPreparedConditionException();
        }

        /**
         * Wakeup the {@link sima.core.scheduler.executor.MultiThreadExecutor.ExecutorThread} previously prepared with {@link #prepare()}
         *
         * @throws NotPreparedConditionException if the condition has not previously been prepared
         * @see #prepare()
         */
        public synchronized void wakeup() {
            Optional.ofNullable(executorThread).orElseThrow(NotPreparedConditionException::new).wakeUp();
            executorThread = null;
        }

        /**
         * @return true if the {@link Condition} has been prepared, else false.
         */
        public synchronized boolean hasBeenPrepared() {
            return Optional.ofNullable(executorThread).isPresent();
        }
    }
}
