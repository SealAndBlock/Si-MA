package sima.core.scheduler.multithread;

import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

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
     * True if the {@link Scheduler} is killed, else false.
     */
    protected boolean isKilled = false;

    /**
     * The end of the simulation.
     */
    private final long endSimulation;

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

    protected MultiThreadScheduler(long endSimulation, int nbExecutorThread) {
        this.endSimulation = endSimulation;
        if (endSimulation < 1)
            throw new IllegalArgumentException("The end simulation time must be greater or equal to 1.");

        this.nbExecutorThread = nbExecutorThread;
        if (nbExecutorThread < 1)
            throw new IllegalArgumentException("The number of executor thread must be greater or equal to 1.");

        schedulerWatchers = new Vector<>();

        executorThreadList = new Vector<>();
    }

    // Methods.

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
        schedulerWatchers.forEach(SchedulerWatcher::schedulerKilled);
    }

    protected void notifyOnSimulationEndTimeReach() {
        schedulerWatchers.forEach(SchedulerWatcher::simulationEndTimeReach);
    }

    protected void notifyOnNoExecutableToExecute() {
        schedulerWatchers.forEach(SchedulerWatcher::noExecutableToExecute);
    }

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

    protected static class OneExecutableExecutorThread extends ExecutorThread {

        // Variables.

        protected final Executable executable;

        // Constructors.

        public OneExecutableExecutorThread(Executable executable) {
            this.executable = executable;
        }

        // Methods.

        @Override
        public void run() {
            executable.execute();
        }
    }

    protected abstract static class ExecutorThread implements Runnable {

        // Variables.

        protected boolean isFinished = false;

        // Getters and Setters.

        public boolean isFinished() {
            return isFinished;
        }
    }
}
