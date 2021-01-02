package sima.core.scheduler.multithread;

import sima.core.exception.NotSchedulableTimeException;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RealTimeMultiThreadScheduler extends MultiThreadScheduler {

    // Variables.

    /**
     * The date when the scheduler has been started.
     */
    private long beginTime;

    /**
     * The number of executor thread which are running.
     */
    private AtomicInteger runningExecutor;

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

            this.runningExecutor = new AtomicInteger(0);

            this.updateSchedulerWatcherOnSchedulerStarted();

            this.executor = Executors.newScheduledThreadPool(this.nbExecutorThread);

            Thread finishWatcherThread = new Thread(new FinishWatcher(this.getEndSimulation()));
            finishWatcherThread.start();

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

    private void endByEndSimulationReach() {
        this.updateSchedulerWatcherOnSimulationEndTimeReach();

        this.kill();
    }

    @Override
    public synchronized boolean kill() {
        if (this.isStarted) {
            this.isStarted = false;

            ExecutorService tmpExecutorService = this.executor;
            this.executor = null;

            tmpExecutorService.shutdownNow();
            while (this.runningExecutor.get() != 0) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {
                }
            }

            this.executorThreadList.clear();

            this.updateSchedulerWatcherOnSchedulerKilled();

            return true;
        } else
            return false;
    }

    @Override
    public synchronized void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode,
                                                long nbRepetitions, long executionTimeStep) {
        if (waitingTime < 1)
            throw new IllegalArgumentException("Waiting time cannot be less than 1.");

        switch (scheduleMode) {
            case ONCE -> {
                // We can schedule executable after the end (they will not be executed)

                RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable, waitingTime);
                this.executorThreadList.add(realTimeExecutorThread);
                if (this.getExecutor() != null) {
                    this.getExecutor().schedule(realTimeExecutorThread, waitingTime, TimeUnit.MILLISECONDS);
                }
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
                        this.getExecutor().schedule(realTimeExecutorThread, waitingTime + (i * executionTimeStep), TimeUnit.MILLISECONDS);
                }
            }
            case INFINITELY -> {
                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                long currentTime = this.getCurrentTime();
                for (long time = this.getExecutor() != null ? currentTime + waitingTime : waitingTime;
                     time <= this.getEndSimulation(); time += executionTimeStep) {
                    RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable,
                            time);
                    this.executorThreadList.add(realTimeExecutorThread);
                    if (this.getExecutor() != null)
                        this.getExecutor().schedule(realTimeExecutorThread, time, TimeUnit.MILLISECONDS);
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
            long timeToBeExecuted = simulationSpecificTime - this.getCurrentTime();
            realTimeExecutorThread = new RealTimeExecutorThread(executable, timeToBeExecuted);
            this.executorThreadList.add(realTimeExecutorThread);
            this.getExecutor().schedule(realTimeExecutorThread, timeToBeExecuted, TimeUnit.MILLISECONDS);
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
        if (this.isRunning())
            return System.currentTimeMillis() - this.beginTime;
        else
            return -1;
    }

    // Getters and Setters.

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
            this.executeExecutable();

            RealTimeMultiThreadScheduler.this.executorThreadList.remove(this);

            if (this.isEndSimulation()) {
                this.notifyEndByReachEndSimulation();
            } else if (this.noExecutableToExecute()) {
                this.notifyEndByNoExecutableToExecute();
            }

            this.isFinished = true;
        }

        private boolean noExecutableToExecute() {
            return RealTimeMultiThreadScheduler.this.executorThreadList.isEmpty();
        }

        private boolean isEndSimulation() {
            return RealTimeMultiThreadScheduler.this.getCurrentTime()
                    >= RealTimeMultiThreadScheduler.this.getEndSimulation();
        }

        private void notifyEndByReachEndSimulation() {
            synchronized (RealTimeMultiThreadScheduler.this) {
                if (RealTimeMultiThreadScheduler.this.isStarted) {
                    RealTimeMultiThreadScheduler.this.endByEndSimulationReach();
                }
                // Else the Scheduler has already be killed.
            }
        }

        private void notifyEndByNoExecutableToExecute() {
            synchronized (RealTimeMultiThreadScheduler.this) {
                if (RealTimeMultiThreadScheduler.this.isStarted) {
                    RealTimeMultiThreadScheduler.this.endByNoExecutableToExecution();
                } // Else the Scheduler has already be killed.
            }
        }

        private void executeExecutable() {
            RealTimeMultiThreadScheduler.this.runningExecutor.incrementAndGet();

            this.executable.execute();

            RealTimeMultiThreadScheduler.this.runningExecutor.decrementAndGet();

            synchronized (RealTimeMultiThreadScheduler.this) {
                RealTimeMultiThreadScheduler.this.notifyAll();
            }
        }

        // Getters and Setters.

        public long getDelay() {
            return delay;
        }
    }

    private class FinishWatcher implements Runnable {

        // Variables.

        /**
         * Time to sleep before wake up and kill the scheduler.
         * <p>
         * In milliseconds.
         */
        private final long timeToWait;

        // Constructors.

        public FinishWatcher(long timeToWait) {
            this.timeToWait = timeToWait;
        }

        // Methods.

        @Override
        public void run() {
            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException ignored) {
            }

            this.notifyWatcherAndKillScheduler();
        }

        private void notifyWatcherAndKillScheduler() {
            synchronized (RealTimeMultiThreadScheduler.this) {
                if (RealTimeMultiThreadScheduler.this.isStarted) {
                    RealTimeMultiThreadScheduler.this.updateSchedulerWatcherOnSimulationEndTimeReach();
                    RealTimeMultiThreadScheduler.this.kill();
                }
            }
        }
    }
}
