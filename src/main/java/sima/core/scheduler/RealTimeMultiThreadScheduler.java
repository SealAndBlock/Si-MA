package sima.core.scheduler;

import sima.core.scheduler.exception.NotSchedulableTimeException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeMultiThreadScheduler extends MultiThreadScheduler {

    // Variables.

    /**
     * The date when the scheduler has been started.
     */
    private long beginTime;

    // Constructors.

    /**
     * @param endSimulation    the end of the simulation
     * @param nbExecutorThread the number of executor thread
     * @throws IllegalArgumentException if the endSimulationTime or the nbExecutorThread is less than 1.
     */
    public RealTimeMultiThreadScheduler(long endSimulation, int nbExecutorThread) {
        super(endSimulation, nbExecutorThread);
    }

    // Methods.

    @Override
    public synchronized boolean start() {
        if (!this.isStarted) {
            this.isStarted = true;

            this.updateSchedulerWatcherOnSchedulerStarted();

            this.executor = Executors.newScheduledThreadPool(this.nbExecutorThread);
            this.getExecutor().scheduleAtFixedRate(new FinishWatcher(), 1, 1, TimeUnit.SECONDS);

            this.beginTime = System.currentTimeMillis();

            return true;
        } else
            return false;
    }

    @Override
    public synchronized boolean kill() {
        if (this.isStarted) {
            this.isStarted = false;

            this.executor.shutdownNow();

            this.updateSchedulerWatcherOnSchedulerKilled();

            this.executorThreadList.clear();

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
            case ONCE -> {
                RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable);
                this.executorThreadList.add(realTimeExecutorThread);
                this.getExecutor().schedule(realTimeExecutorThread, waitingTime, TimeUnit.MILLISECONDS);
            }
            case REPEATED -> {
                for (int i = 0; i < nbRepetitions; i++) {
                    RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable);
                    this.executorThreadList.add(realTimeExecutorThread);
                    this.getExecutor().schedule(realTimeExecutorThread, waitingTime + (i * executionTimeStep),
                            TimeUnit.MILLISECONDS);
                }
            }
            case INFINITELY -> {
                long currentTime = this.getCurrentTime();
                for (int i = 0; currentTime + (i * executionTimeStep) <= this.endSimulation; i++) {
                    RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable);
                    this.executorThreadList.add(realTimeExecutorThread);
                    this.getExecutor().schedule(realTimeExecutorThread, waitingTime + (i * executionTimeStep),
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

        RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable);
        this.executorThreadList.add(realTimeExecutorThread);
        this.getExecutor().schedule(realTimeExecutorThread, simulationSpecificTime - this.getCurrentTime(),
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

    // Getters and Setters.

    @Override
    public ScheduledExecutorService getExecutor() {
        return (ScheduledExecutorService) this.executor;
    }

    // Inner class.

    private class RealTimeExecutorThread extends MultiThreadScheduler.ExecutorThread {

        // variables.

        private final Executable executable;

        // Constructors.

        public RealTimeExecutorThread(Executable executable) {
            this.executable = executable;
        }

        // Methods.

        @Override
        public void run() {
            this.executable.execute();

            RealTimeMultiThreadScheduler.this.executorThreadList.remove(this);
            if (RealTimeMultiThreadScheduler.this.executorThreadList.isEmpty()) {
                synchronized (RealTimeMultiThreadScheduler.this) {
                    if (RealTimeMultiThreadScheduler.this.isStarted) {
                        RealTimeMultiThreadScheduler.this.updateSchedulerWatcherOnNoExecutableToExecute();
                        RealTimeMultiThreadScheduler.this.kill();
                    } // Else the Scheduler has already be killed.
                }
            }

            this.isFinished = true;
        }
    }

    private class FinishWatcher implements Runnable {

        // Variables.

        // Constructors.

        // Methods.

        @Override
        public void run() {
            if (RealTimeMultiThreadScheduler.this.getCurrentTime() > RealTimeMultiThreadScheduler.this.endSimulation) {
                synchronized (RealTimeMultiThreadScheduler.this) {
                    if (RealTimeMultiThreadScheduler.this.isStarted) {
                        RealTimeMultiThreadScheduler.this.updateSchedulerWatcherOnSimulationEndTimeReach();
                        RealTimeMultiThreadScheduler.this.kill();
                    }
                }
            }
        }
    }
}
