package sima.core.scheduler;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

public abstract class MultiThreadScheduler implements Scheduler {

    // Variables.

    /**
     * True if the {@link Scheduler} is started, else false.
     */
    protected boolean isStarted = false;

    /**
     * The end of the simulation.
     */
    protected final long endSimulation;

    /**
     * The number of thread use to execute all {@link Executable}.
     */
    protected final int nbExecutorThread;

    /**
     * The list of all {@link sima.core.scheduler.Scheduler.SchedulerWatcher}.
     */
    private final List<SchedulerWatcher> schedulerWatchers;

    protected final List<ExecutorThread> executorThreadList;

    protected ExecutorService executor;

    // Constructors.

    public MultiThreadScheduler(long endSimulation, int nbExecutorThread) {
        this.endSimulation = endSimulation;
        if (this.endSimulation < 1)
            throw new IllegalArgumentException("The end simulation time must be greater or equal to 1.");

        this.nbExecutorThread = nbExecutorThread;
        if (this.nbExecutorThread < 1)
            throw new IllegalArgumentException("The number of executor thread must be greater or equal to 1.");

        this.schedulerWatchers = new Vector<>();

        this.executorThreadList = new Vector<>();
    }

    // Methods.

    @Override
    public synchronized boolean addSchedulerWatcher(SchedulerWatcher schedulerWatcher) {
        if (this.schedulerWatchers.contains(schedulerWatcher))
            return false;

        return this.schedulerWatchers.add(schedulerWatcher);
    }

    @Override
    public void removeSchedulerWatcher(SchedulerWatcher schedulerWatcher) {
        this.schedulerWatchers.remove(schedulerWatcher);
    }

    protected void updateSchedulerWatcherOnSchedulerStarted() {
        this.schedulerWatchers.forEach(SchedulerWatcher::schedulerStarted);
    }

    protected void updateSchedulerWatcherOnSchedulerKilled() {
        this.schedulerWatchers.forEach(SchedulerWatcher::schedulerKilled);
    }

    protected void updateSchedulerWatcherOnSimulationEndTimeReach() {
        this.schedulerWatchers.forEach(SchedulerWatcher::simulationEndTimeReach);
    }

    protected void updateSchedulerWatcherOnNoExecutableToExecute() {
        this.schedulerWatchers.forEach(SchedulerWatcher::noExecutableToExecute);
    }

    // Getters and Setters.

    public boolean isStarted() {
        return isStarted;
    }

    protected void setStarted(boolean started) {
        isStarted = started;
    }

    protected ExecutorService getExecutor() {
        return executor;
    }

    protected void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    protected List<ExecutorThread> getExecutorThreadList() {
        return executorThreadList;
    }

    // Inner classes.

    protected abstract class ExecutorThread implements Runnable {

        // Variables.

        protected boolean isFinished = false;

        // Getters and Setters.

        public boolean isFinished() {
            return isFinished;
        }

        public void setFinished(boolean finished) {
            isFinished = finished;
        }
    }
}
