package sima.core.scheduler.multithread;

import org.jetbrains.annotations.NotNull;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import static sima.core.simulation.SimaSimulation.SIMA_LOG;

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
    public String toString() {
        return "[Scheduler - " + this.getClass().getName() + "]";
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
        schedulerWatchers.forEach(SchedulerWatcher::schedulerKilled);
    }

    protected void notifyOnSimulationEndTimeReach() {
        SIMA_LOG.info(this + " SIMULATION END TIME REACH");
        schedulerWatchers.forEach(SchedulerWatcher::simulationEndTimeReach);
    }

    protected void notifyOnNoExecutableToExecute() {
        SIMA_LOG.info(this + " NO EXECUTABLE TO EXECUTE at time " + getCurrentTime());
        schedulerWatchers.forEach(SchedulerWatcher::noExecutableToExecute);
    }

    /**
     * Instantiates {@link #executor}.
     */
    protected abstract void createNewExecutor();

    protected void setStarted() {
        isStarted = true;
        SIMA_LOG.info(this + " STARTED");
    }

    protected void setKilled() {
        isStarted = false;
        isKilled = true;
        SIMA_LOG.info(this + " KILLED");
    }

    /**
     * Shutdown the executor and set {@link #executor} to null.
     */
    protected void shutdownExecutor() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
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

    @Override
    public @NotNull SchedulerType getSchedulerType() {
        return SchedulerType.MULTI_THREAD;
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
            try {
                executable.execute();
            } catch (Exception e) {
                SIMA_LOG.error("Execution of the executable " + executable + " FAILED", e);
            }
        }
    }

    /**
     * Executable which encapsulates an other executable which must be executed in repetitively way.
     */
    protected class RepeatedExecutable implements Executable {

        // Variables.

        private final Scheduler scheduler = MultiThreadScheduler.this;
        private final Executable executable;
        private final long nbNextExecutions;
        private final long executionTimeStep;

        // Constructors.

        public RepeatedExecutable(Executable executable, long nbNextExecutions, long executionTimeStep) {
            if (nbNextExecutions < 0)
                throw new IllegalArgumentException("nbNextExecutions cannot be less than 0");

            if (executionTimeStep < 1)
                throw new IllegalArgumentException("nextExecutionStep cannot be less than 1");

            this.executable = executable;
            this.executionTimeStep = executionTimeStep;
            this.nbNextExecutions = nbNextExecutions;
        }

        // Methods.

        @Override
        public void execute() {
            executable.execute();
            if (nbNextExecutions > 1)
                scheduler.scheduleExecutableOnce(
                        new RepeatedExecutable(executable, nbNextExecutions - 1, executionTimeStep), executionTimeStep);
        }
    }

    /**
     * Executable which encapsulates an other executable which must be executed in infinite way.
     */
    protected class InfiniteExecutable implements Executable {

        // Variables.

        private final Scheduler scheduler = MultiThreadScheduler.this;
        private final Executable executable;
        private final long executionTimeStep;

        // Constructors.

        public InfiniteExecutable(Executable executable, long executionTimeStep) {
            this.executable = executable;
            this.executionTimeStep = executionTimeStep;
        }

        // Methods.

        @Override
        public void execute() {
            executable.execute();
            scheduler.scheduleExecutableOnce(new InfiniteExecutable(executable, executionTimeStep), executionTimeStep);
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
