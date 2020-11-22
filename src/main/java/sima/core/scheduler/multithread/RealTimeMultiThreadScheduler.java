package sima.core.scheduler.multithread;

import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;
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

    private long endSimulationRealDate = -1;

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

            this.endSimulationRealDate = -1;

            this.updateSchedulerWatcherOnSchedulerStarted();

            this.executor = Executors.newScheduledThreadPool(this.nbExecutorThread);
            this.getExecutor().scheduleAtFixedRate(new FinishWatcher(), 1, 1, TimeUnit.SECONDS);

            this.beginTime = System.currentTimeMillis();

            if (!this.executorThreadList.isEmpty()) {
                this.executorThreadList.forEach(executorThread -> this.getExecutor().schedule(executorThread,
                        ((RealTimeExecutorThread) executorThread).getDelay(), TimeUnit.MILLISECONDS));
            } else {
                this.endByNoExecutableToExecution();
            }

            return true;
        } else
            return false;
    }

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has
     * finish by no executable to execute
     */
    private void endByNoExecutableToExecution() {
        this.updateSchedulerWatcherOnNoExecutableToExecute();

        this.kill();
    }

    @Override
    public synchronized boolean kill() {
        if (this.isStarted) {
            this.isStarted = false;

            this.endSimulationRealDate = System.currentTimeMillis();

            this.executor.shutdownNow();
            this.executor = null;

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
                if (this.getExecutor() != null && this.getCurrentTime() + waitingTime > this.getEndSimulation())
                    return;
                else if (this.getExecutor() == null && waitingTime > this.getEndSimulation())
                    return;

                RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable, waitingTime);
                this.executorThreadList.add(realTimeExecutorThread);
                if (this.getExecutor() != null)
                    this.getExecutor().schedule(realTimeExecutorThread, waitingTime, TimeUnit.MILLISECONDS);
            }
            case REPEATED -> {
                if (nbRepetitions < 1)
                    throw new IllegalArgumentException("NbRepeated must be greater or equal to 1");

                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                for (int i = 0; i < nbRepetitions; i++) {
                    if (waitingTime + (i * executionTimeStep) > this.getEndSimulation())
                        break;

                    RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable,
                            waitingTime + (i * executionTimeStep));
                    this.executorThreadList.add(realTimeExecutorThread);
                    if (this.getExecutor() != null)
                        this.getExecutor().schedule(realTimeExecutorThread, waitingTime + (i * executionTimeStep),
                                TimeUnit.MILLISECONDS);
                }
            }
            case INFINITELY -> {
                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                for (long time = this.getExecutor() != null ? this.getCurrentTime() + waitingTime : waitingTime;
                     time <= this.getEndSimulation(); time += executionTimeStep) {
                    RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable,
                            time);
                    this.executorThreadList.add(realTimeExecutorThread);
                    if (this.getExecutor() != null)
                        this.getExecutor().schedule(realTimeExecutorThread, time,
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

        RealTimeExecutorThread realTimeExecutorThread;
        if (this.getExecutor() != null) {
            realTimeExecutorThread = new RealTimeExecutorThread(executable,
                    simulationSpecificTime - this.getCurrentTime());
            this.executorThreadList.add(realTimeExecutorThread);
            this.getExecutor().schedule(realTimeExecutorThread, -this.getCurrentTime(),
                    TimeUnit.MILLISECONDS);
        } else {
            realTimeExecutorThread = new RealTimeExecutorThread(executable, simulationSpecificTime);
            this.executorThreadList.add(realTimeExecutorThread);
        }
    }

    /**
     * @return the time elapsed between the start of the scheduler and the call of the method. The result is in
     * milliseconds. If the scheduler is not started, return 0.
     */
    @Override
    public long getCurrentTime() {
        if (this.endSimulationRealDate == -1)
            // Simulation not killed
            return System.currentTimeMillis() - this.beginTime;
        else
            // Simulation killed
            return this.endSimulationRealDate - this.beginTime;
    }

    // Getters and Setters.

    @Override
    public ScheduledExecutorService getExecutor() {
        return (ScheduledExecutorService) this.executor;
    }

    // Inner class.

    public class RealTimeExecutorThread extends MultiThreadScheduler.ExecutorThread {

        // variables.

        private final Executable executable;

        private final long delay;

        // Constructors.

        public RealTimeExecutorThread(Executable executable, long delay) {
            this.executable = executable;
            this.delay = delay;
        }

        // Methods.

        @Override
        public void run() {
            this.executable.execute();

            RealTimeMultiThreadScheduler.this.executorThreadList.remove(this);
            if (RealTimeMultiThreadScheduler.this.executorThreadList.isEmpty()) {
                synchronized (RealTimeMultiThreadScheduler.this) {
                    if (RealTimeMultiThreadScheduler.this.isStarted) {
                        if (RealTimeMultiThreadScheduler.this.getCurrentTime()
                                >= RealTimeMultiThreadScheduler.this.getEndSimulation()) {
                            RealTimeMultiThreadScheduler.this.updateSchedulerWatcherOnSimulationEndTimeReach();
                        } else {
                            RealTimeMultiThreadScheduler.this.updateSchedulerWatcherOnNoExecutableToExecute();
                        }
                        RealTimeMultiThreadScheduler.this.kill();
                    } // Else the Scheduler has already be killed.
                }
            }

            this.isFinished = true;
        }

        // Getters and Setters.

        public Executable getExecutable() {
            return executable;
        }

        public long getDelay() {
            return delay;
        }
    }

    private class FinishWatcher implements Runnable {

        // Variables.

        // Constructors.

        // Methods.

        @Override
        public void run() {
            if (RealTimeMultiThreadScheduler.this.getCurrentTime() > RealTimeMultiThreadScheduler.this.getEndSimulation()) {
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
