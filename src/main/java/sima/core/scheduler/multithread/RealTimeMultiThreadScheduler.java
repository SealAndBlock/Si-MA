package sima.core.scheduler.multithread;

import sima.core.exception.NotSchedulableTimeException;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeMultiThreadScheduler extends MultiThreadScheduler {

    // Variables.

    /**
     * The date when the scheduler has been started.
     */
    private long beginTime;

    /**
     * The number of executor thread which are running.
     */
    private int runningExecutorCounter;

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
        if (!isStarted && !isKilled) {
            isStarted = true;

            runningExecutorCounter = 0;

            notifyOnSchedulerStarted();

            executor = Executors.newScheduledThreadPool(nbExecutorThread);

            Thread finishWatcherThread = new Thread(new FinishWatcher(getEndSimulation()));
            finishWatcherThread.start();

            beginTime = System.currentTimeMillis();

            if (!executorThreadList.isEmpty()) {
                executorThreadList.forEach(executorThread ->
                        getExecutor().schedule(executorThread, ((RealTimeExecutorThread) executorThread).getDelay(), TimeUnit.MILLISECONDS));

            } else {
                endByNoExecutableToExecution();
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
        notifyOnNoExecutableToExecute();

        kill();
    }

    private void endByEndSimulationReach() {
        notifyOnSimulationEndTimeReach();

        kill();
    }

    @Override
    public synchronized boolean kill() {
        if (!isKilled) {
            isStarted = false;
            isKilled = true;

            if (executor != null) {
                executor.shutdownNow();
                executor = null;

                while (getRunningExecutorCounter() != 0) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }

            executorThreadList.clear();

            notifyOnSchedulerKilled();

            return true;
        } else
            return false;
    }

    @Override
    public void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode,
                                   long nbRepetitions, long executionTimeStep) {
        if (waitingTime < 1)
            throw new IllegalArgumentException("Waiting time cannot be less than 1.");

        if (!this.isKilled()) {
            switch (scheduleMode) {
                case ONCE -> {
                    // We can schedule executable after the end (they will not be executed)

                    RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable, waitingTime);
                    executorThreadList.add(realTimeExecutorThread);
                    if (getExecutor() != null) {
                        getExecutor().schedule(realTimeExecutorThread, waitingTime, TimeUnit.MILLISECONDS);
                    }
                }
                case REPEATED -> {
                    if (nbRepetitions < 1)
                        throw new IllegalArgumentException("NbRepeated must be greater or equal to 1");

                    if (executionTimeStep < 1)
                        throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                    for (int i = 0; i < nbRepetitions; i++) {
                        if (waitingTime + (i * executionTimeStep) > getEndSimulation())
                            break;

                        RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable,
                                waitingTime + (i * executionTimeStep));
                        executorThreadList.add(realTimeExecutorThread);
                        if (getExecutor() != null)
                            getExecutor().schedule(realTimeExecutorThread, waitingTime + (i * executionTimeStep), TimeUnit.MILLISECONDS);
                    }
                }
                case INFINITELY -> {
                    if (executionTimeStep < 1)
                        throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                    long currentTime = getCurrentTime();
                    for (long time = getExecutor() != null ? currentTime + waitingTime : waitingTime;
                         time <= getEndSimulation(); time += executionTimeStep) {
                        RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable,
                                time);
                        executorThreadList.add(realTimeExecutorThread);
                        if (getExecutor() != null)
                            getExecutor().schedule(realTimeExecutorThread, time, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {
        if (simulationSpecificTime < 1)
            throw new IllegalArgumentException("SimulationSpecificTime must be greater or equal to 1");

        if (simulationSpecificTime <= getCurrentTime())
            throw new NotSchedulableTimeException("SimulationSpecificTime is already passed");

        if (!this.isKilled()) {
            RealTimeExecutorThread realTimeExecutorThread;
            if (getExecutor() != null) {
                long timeToBeExecuted = simulationSpecificTime - getCurrentTime();
                realTimeExecutorThread = new RealTimeExecutorThread(executable, timeToBeExecuted);
                executorThreadList.add(realTimeExecutorThread);
                getExecutor().schedule(realTimeExecutorThread, timeToBeExecuted, TimeUnit.MILLISECONDS);
            } else {
                realTimeExecutorThread = new RealTimeExecutorThread(executable, simulationSpecificTime);
                executorThreadList.add(realTimeExecutorThread);
            }
        }
    }

    /**
     * @return the time elapsed between the start of the scheduler and the call of the method. The result is in
     * milliseconds. If the scheduler is not started, return 0.
     */
    @Override
    public long getCurrentTime() {
        if (isRunning())
            return System.currentTimeMillis() - beginTime;
        else if (!isKilled)
            return 0;
        else
            return -1;
    }

    private synchronized void incrementRunningExecutorCounter() {
        runningExecutorCounter++;
    }

    private synchronized void decrementRunningExecutorCounterAndNotifyAll() {
        runningExecutorCounter--;
        notifyAll();
    }

    private synchronized int getRunningExecutorCounter() {
        return runningExecutorCounter;
    }

    // Getters and Setters.

    public ScheduledExecutorService getExecutor() {
        return (ScheduledExecutorService) executor;
    }

    // Inner class.

    public class RealTimeExecutorThread extends MultiThreadScheduler.ExecutorThread {

        // variables.

        private final Executable executable;

        private final long delay;

        private final RealTimeMultiThreadScheduler scheduler;

        // Constructors.

        public RealTimeExecutorThread(Executable executable, long delay) {
            this.executable = executable;
            this.delay = delay;

            scheduler = RealTimeMultiThreadScheduler.this;
        }

        // Methods.

        @Override
        public void run() {
            boolean executed = verifyAndExecute();

            if (executed) {
                scheduler.executorThreadList.remove(this);

                if (scheduler.endSimulationReach()) {
                    notifyEndByReachEndSimulation();
                } else if (noExecutableToExecute()) {
                    notifyEndByNoExecutableToExecute();
                }
            }

            isFinished = true;
        }

        private boolean noExecutableToExecute() {
            return scheduler.executorThreadList.isEmpty();
        }

        private void notifyEndByReachEndSimulation() {
            if (scheduler.isRunning()) {
                scheduler.endByEndSimulationReach();
            }
            // Else the Scheduler has already be killed.

        }

        private void notifyEndByNoExecutableToExecute() {
            if (scheduler.isRunning()) {
                scheduler.endByNoExecutableToExecution();
            } // Else the Scheduler has already be killed.
        }

        /**
         * Verifies if all conditions are satisfied to execute the {@link Executable}. If it is the case, the
         * {@Code Executable} is executed and returns true, else the {@Code Executable} is not executed and returns
         * false.
         *
         * @return true if the {@link Executable} has been executed, else false.
         */
        private boolean verifyAndExecute() {
            synchronized (scheduler) {
                if (scheduler.isKilled()) {
                    return false;
                }

                scheduler.incrementRunningExecutorCounter();
            }

            executable.execute();

            scheduler.decrementRunningExecutorCounterAndNotifyAll();

            return true;
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

        private final RealTimeMultiThreadScheduler scheduler;

        // Constructors.

        public FinishWatcher(long timeToWait) {
            this.timeToWait = timeToWait;

            this.scheduler = RealTimeMultiThreadScheduler.this;
        }

        // Methods.

        @Override
        public void run() {
            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException ignored) {
            }

            notifyWatcherAndKillScheduler();
        }

        private void notifyWatcherAndKillScheduler() {
            synchronized (scheduler) {
                if (scheduler.isRunning()) {
                    scheduler.notifyOnSimulationEndTimeReach();
                    scheduler.kill();
                }
            }
        }
    }
}
