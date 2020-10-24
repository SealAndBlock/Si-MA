package sima.core.scheduler;

import sima.core.scheduler.exception.NotSchedulableTimeException;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeMultiThreadScheduler implements Scheduler {

    // Variables.

    /**
     * The date when the scheduler has been started.
     */
    private long beginTime;

    /**
     * True if the {@link Scheduler} is started, else false.
     */
    private boolean isStarted = false;

    /**
     * The end of the simulation.
     * <p>
     * In that case, the real time that the simulation must run.
     */
    private final long endSimulation;

    /**
     * The number of thread use to execute all {@link Executable}.
     */
    private final int nbExecutorThread;

    /**
     * The list of all {@link sima.core.scheduler.Scheduler.SchedulerWatcher}.
     */
    private final List<SchedulerWatcher> schedulerWatchers;

    private ScheduledExecutorService executor;

    // Constructors.

    /**
     * @param endSimulation    the end of the simulation
     * @param nbExecutorThread the number of executor thread
     * @throws IllegalArgumentException if the endSimulationTime or the nbExecutorThread is less than 1.
     */
    public RealTimeMultiThreadScheduler(long endSimulation, int nbExecutorThread) {
        this.endSimulation = endSimulation;
        if (this.endSimulation < 1)
            throw new IllegalArgumentException("The end simulation time must be greater or equal to 1.");

        this.nbExecutorThread = nbExecutorThread;
        if (this.nbExecutorThread < 1)
            throw new IllegalArgumentException("The number of executor thread must be greater or equal to 1.");

        this.schedulerWatchers = new Vector<>();
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

    private void updateSchedulerWatcherOnSchedulerStarted() {
        this.schedulerWatchers.forEach(SchedulerWatcher::schedulerStarted);
    }

    private void updateSchedulerWatcherOnSchedulerKilled() {
        this.schedulerWatchers.forEach(SchedulerWatcher::schedulerKilled);
    }

    private void updateSchedulerWatcherOnSimulationEndTimeReach() {
        this.schedulerWatchers.forEach(SchedulerWatcher::simulationEndTimeReach);
    }

    private void updateSchedulerWatcherOnNoExecutableToExecute() {
        this.schedulerWatchers.forEach(SchedulerWatcher::noExecutableToExecute);
    }

    @Override
    public boolean start() {
        if (!this.isStarted) {
            this.isStarted = true;

            this.updateSchedulerWatcherOnSchedulerStarted();

            this.executor = Executors.newScheduledThreadPool(this.nbExecutorThread);

            return true;
        } else
            return false;
    }

    @Override
    public boolean kill() {
        if (this.isStarted) {
            this.isStarted = false;

            this.executor.shutdownNow();

            this.updateSchedulerWatcherOnSchedulerKilled();

            return true;
        } else
            return false;
    }

    @Override
    public void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode,
                                   long nbRepetitions, long executionTimeStep) {
        if (waitingTime < 1)
            throw new IllegalArgumentException("Waiting time cannot be less than 1.");

        switch (scheduleMode) {
            case ONCE -> this.executor.schedule(executable::execute, waitingTime, TimeUnit.MILLISECONDS);
            case REPEATED -> {
                for (int i = 0; i < nbRepetitions; i++) {
                    this.executor.schedule(executable::execute, waitingTime + (i * executionTimeStep),
                            TimeUnit.MILLISECONDS);
                }
            }
            case INFINITELY -> {
                long currentTime = this.getCurrentTime();
                for (int i = 0; currentTime + (i * executionTimeStep) <= this.endSimulation; i++) {
                    this.executor.schedule(executable::execute, waitingTime + (i * executionTimeStep),
                            TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {
        if (simulationSpecificTime < 1)
            throw new IllegalArgumentException("SimulationSpecificTime must be greater or equal to 1");

        if (simulationSpecificTime <= this.getCurrentTime())
            throw new NotSchedulableTimeException("SimulationSpecificTime is already passed");

        this.executor.schedule(executable::execute, simulationSpecificTime - this.getCurrentTime(),
                TimeUnit.MILLISECONDS);
    }

    /**
     * @return the time elapsed between the start of the scheduler and the call of the method. The result is in
     * milliseconds.
     */
    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis() - this.beginTime;
    }
}
