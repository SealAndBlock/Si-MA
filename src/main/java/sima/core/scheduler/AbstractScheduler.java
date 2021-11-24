package sima.core.scheduler;

import sima.core.scheduler.executor.Executable;

import java.util.List;
import java.util.Vector;

import static sima.core.simulation.SimaSimulation.SimaLog;

public abstract class AbstractScheduler implements Scheduler {

    // Variables.

    /**
     * True if the {@link Scheduler} is started, else false.
     */
    protected boolean isStarted = false;

    /**
     * True if the {@link Scheduler} is killed, else false.
     */
    protected boolean isKilled = false;

    /**
     * The end of the simulation.
     */
    private final long endSimulation;

    /**
     * The list of all {@link sima.core.scheduler.Scheduler.SchedulerWatcher}.
     */
    private final List<SchedulerWatcher> schedulerWatchers;

    // Constructors.

    protected AbstractScheduler(long endSimulation) {
        this.endSimulation = endSimulation;
        if (endSimulation < 1)
            throw new IllegalArgumentException("The end simulation time must be greater or equal to 1.");

        schedulerWatchers = new Vector<>();
    }

    // Methods.

    @Override
    public String toString() {
        return "AbstractScheduler{" +
                "isStarted=" + isStarted +
                ", isKilled=" + isKilled +
                ", endSimulation=" + endSimulation +
                '}';
    }

    @Override
    public synchronized boolean addSchedulerWatcher(SchedulerWatcher schedulerWatcher) {
        if (schedulerWatcher == null)
            return false;

        if (schedulerWatchers.contains(schedulerWatcher))
            return false;

        return schedulerWatchers.add(schedulerWatcher);
    }

    @Override
    public void removeSchedulerWatcher(SchedulerWatcher schedulerWatcher) {
        schedulerWatchers.remove(schedulerWatcher);
    }

    protected void notifyOnSchedulerStarted() {
        schedulerWatchers.forEach(SchedulerWatcher::schedulerStarted);
    }

    protected void notifyOnSchedulerKilled() {
        SimaLog.info(this + " SCHEDULER KILLED");
        schedulerWatchers.forEach(SchedulerWatcher::schedulerKilled);
    }

    protected void notifyOnSimulationEndTimeReach() {
        SimaLog.info(this + " SIMULATION END TIME REACH");
        schedulerWatchers.forEach(SchedulerWatcher::simulationEndTimeReach);
    }

    protected void notifyOnNoExecutableToExecute() {
        SimaLog.info(this + " NO EXECUTABLE TO EXECUTE at time " + getCurrentTime());
        schedulerWatchers.forEach(SchedulerWatcher::noExecutableToExecute);
    }

    protected void setStarted() {
        isStarted = true;
        SimaLog.info(this + " STARTED");
    }

    protected void setKilled() {
        isStarted = false;
        isKilled = true;
        SimaLog.info(this + " KILLED");
    }

    protected abstract void addExecutableAtTime(Executable executable, long time);

    protected abstract void addRepeatedExecutable(Executable executable, long waitingTime, long nbRepetitions,
                                                  long executionTimeStep);

    protected abstract void addInfiniteExecutable(Executable executable, long waitingTime, long executionTimeStep);

    /**
     * Add the {@link Executable} in function of the {@link sima.core.scheduler.Scheduler.ScheduleMode}.
     * <p>
     * If the scheduleMode is equal to {@link sima.core.scheduler.Scheduler.ScheduleMode#REPEATED} or {@link
     * sima.core.scheduler.Scheduler.ScheduleMode#INFINITE}, the executable is added the number of times that it must be added. It is the same
     * instance which is added at each time, there is no copy of the {@code Executable}.
     *
     * @param executable    the executable to add
     * @param waitingTime   the waiting time before execute the action
     * @param scheduleMode  the schedule mode
     * @param nbRepetitions the number of times that the action must be repeated if the {@link sima.core.scheduler.Scheduler.ScheduleMode} is equal to
     *                      {@link sima.core.scheduler.Scheduler.ScheduleMode#REPEATED}
     */
    protected void addExecutable(Executable executable, long waitingTime,
                                 Scheduler.ScheduleMode scheduleMode,
                                 long nbRepetitions, long executionTimeStep) {
        if (scheduleMode == ScheduleMode.ONCE)
            addExecutableAtTime(executable, getCurrentTime() + waitingTime);
        else if (scheduleMode.equals(ScheduleMode.REPEATED)) {
            verificationForRepeatedExecutable(nbRepetitions, executionTimeStep);
            addRepeatedExecutable(executable, waitingTime, nbRepetitions, executionTimeStep);
        } else if (scheduleMode.equals(ScheduleMode.INFINITE)) {
            verificationForInfiniteExecutable(executionTimeStep);
            addInfiniteExecutable(executable, waitingTime, executionTimeStep);
        }
    }

    private void verificationForRepeatedExecutable(long nbRepetitions, long executionTimeStep) {
        if (nbRepetitions < 1)
            throw new IllegalArgumentException("NbRepeated must be greater or equal to 1");

        if (executionTimeStep < 1)
            throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");
    }

    private void verificationForInfiniteExecutable(long executionTimeStep) {
        if (executionTimeStep < 1)
            throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");
    }

    // Getters.

    @Override
    public synchronized boolean isRunning() {
        return isStarted;
    }

    @Override
    public synchronized boolean isKilled() {
        return isKilled;
    }

    @Override
    public long getEndSimulation() {
        return endSimulation;
    }

    // Inner classes.

    /**
     * When this {@link Executable} is executed, it calls the methods {@link Condition#wakeup()} of its {@link Condition} (only if the condition has
     * been prepared).
     */
    protected record WakeupExecutable(Condition condition) implements Executable {

        // Variables.

        // Constructors.

        public WakeupExecutable {
            if (condition == null)
                throw new NullPointerException("Condition cannot be null");
        }

        // Methods.

        @Override
        public void execute() {
            if (condition.hasBeenPrepared())
                condition.wakeup();
        }
    }

    protected abstract class LoopExecutable implements Executable {

        // Variables.

        protected final Scheduler scheduler = AbstractScheduler.this;
        protected final Executable executable;

        // Constructors.

        protected LoopExecutable(Executable executable) {
            this.executable = executable;
        }

        // Methods.

        @Override
        public void execute() {
            executable.execute();
            scheduleNextExecution();
        }

        protected abstract void scheduleNextExecution();

        @Override
        public Object getLockMonitor() {
            return executable.getLockMonitor();
        }
    }

    /**
     * Executable which encapsulates another executable which must be executed in repetitively way.
     */
    protected class RepeatedExecutable extends LoopExecutable {

        // Variables.

        protected long nbNextExecutions;
        protected final long executionTimeStep;

        // Constructors.

        public RepeatedExecutable(Executable executable, long nbNextExecutions, long executionTimeStep) {
            super(executable);
            this.executionTimeStep = executionTimeStep;
            this.nbNextExecutions = nbNextExecutions;
        }

        // Methods.

        @Override
        protected void scheduleNextExecution() {
            if (nbNextExecutions > 1) {
                nbNextExecutions -= 1;
                scheduler.scheduleExecutableOnce(this, executionTimeStep);
            }
        }
    }

    /**
     * Executable which encapsulates another executable which must be executed in infinite way.
     */
    protected class InfiniteExecutable extends LoopExecutable {

        // Variables.

        protected final long executionTimeStep;

        // Constructors.

        public InfiniteExecutable(Executable executable, long executionTimeStep) {
            super(executable);
            this.executionTimeStep = executionTimeStep;
        }

        // Methods.

        @Override
        protected void scheduleNextExecution() {
            scheduler.scheduleExecutableOnce(this, executionTimeStep);
        }
    }
}
