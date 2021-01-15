package sima.core.scheduler.multithread;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.NotSchedulableTimeException;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static sima.core.simulation.SimaSimulation.SIMA_LOG;

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
            setStarted();
            initRunningExecutorCounter();
            createNewExecutor();
            startFinishWatcher();
            initBeginTime();
            notifyOnSchedulerStarted();

            if (!noExecutableToExecute()) {
                scheduleExecutorThread();
            } else {
                endByNoExecutableToExecution();
            }

            return true;
        } else
            return false;
    }

    /**
     * Schedule in {@link #executor} all {@code ExecutorThread} in {@link #executorThreadList}. Theses are in
     * {@link #executorThreadList} because a schedule method has been called before the start of the {@code Scheduler}.
     */
    private void scheduleExecutorThread() {
        executorThreadList.forEach(executorThread ->
                getExecutor().schedule(executorThread, ((RealTimeExecutorThread) executorThread).getDelay(), TimeUnit.MILLISECONDS));
    }

    private boolean noExecutableToExecute() {
        return executorThreadList.isEmpty();
    }

    private void initBeginTime() {
        beginTime = System.currentTimeMillis();
    }

    private void initRunningExecutorCounter() {
        runningExecutorCounter = 0;
    }

    /**
     * Start the thread of the {@link FinishWatcher} which look if the scheduler has reach the end of the simulation.
     */
    private void startFinishWatcher() {
        Thread finishWatcherThread = new Thread(new FinishWatcher(getEndSimulation()));
        finishWatcherThread.start();
    }

    @Override
    protected void createNewExecutor() {
        executor = Executors.newScheduledThreadPool(nbExecutorThread);
    }

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has
     * finish by no executable to execute
     */
    private void endByNoExecutableToExecution() {
        if (!isKilled()) {
            notifyOnNoExecutableToExecute();
            kill();
        }
    }

    private void endByEndSimulationReach() {
        if (!isKilled()) {
            notifyOnSimulationEndTimeReach();
            kill();
        }
    }

    @Override
    public synchronized boolean kill() {
        if (!isKilled) {
            setKilled();
            shutdownExecutor();
            waitRunningExecutorThreads();
            executorThreadList.clear();
            notifyOnSchedulerKilled();
            return true;
        } else
            return false;
    }

    /**
     * Wait until each {@link sima.core.scheduler.multithread.MultiThreadScheduler.ExecutorThread} which are already
     * run finish.
     */
    private void waitRunningExecutorThreads() {
        while (getRunningExecutorCounter() != 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                SIMA_LOG.error("Interrupted during waiting", e);
            }
        }
    }

    @Override
    public void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode,
                                   long nbRepetitions, long executionTimeStep) {
        if (waitingTime < 1)
            throw new IllegalArgumentException("WaitingTime cannot be less than 1.");

        if (!isKilled()) {
            switch (scheduleMode) {
                case ONCE -> addOnceExecutable(executable, waitingTime);
                case REPEATED -> {
                    if (nbRepetitions < 1)
                        throw new IllegalArgumentException("NbRepeated must be greater or equal to 1");

                    if (executionTimeStep < 1)
                        throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                    addRepeatedExecutable(executable, waitingTime, nbRepetitions, executionTimeStep);
                }
                case INFINITE -> {
                    if (executionTimeStep < 1)
                        throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                    addInfiniteExecutable(executable, waitingTime, executionTimeStep);
                }
            }
        }
    }

    private void addInfiniteExecutable(Executable executable, long waitingTime, long executionTimeStep) {
        long currentTime = getCurrentTime();
        for (long time = getExecutor() != null ? currentTime + waitingTime : waitingTime;
             time <= getEndSimulation(); time += executionTimeStep) {
            addOnceExecutable(executable, time);
        }
    }

    private void addRepeatedExecutable(Executable executable, long waitingTime, long nbRepetitions, long executionTimeStep) {
        for (int i = 0; i < nbRepetitions; i++) {
            if (waitingTime + (i * executionTimeStep) > getEndSimulation()) break;
            addOnceExecutable(executable, waitingTime + (i * executionTimeStep));
        }
    }

    private void addOnceExecutable(Executable executable, long waitingTime) {
        RealTimeExecutorThread realTimeExecutorThread = new RealTimeExecutorThread(executable, waitingTime);
        executorThreadList.add(realTimeExecutorThread);
        if (getExecutor() != null) {
            getExecutor().schedule(realTimeExecutorThread, waitingTime, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {
        if (simulationSpecificTime < 1)
            throw new IllegalArgumentException("SimulationSpecificTime must be greater or equal to 1");

        if (simulationSpecificTime <= getCurrentTime())
            throw new NotSchedulableTimeException("SimulationSpecificTime is already passed");

        if (!isKilled()) {
            if (getExecutor() != null) {
                createAndScheduleExecutorThread(executable, simulationSpecificTime);
            } else {
                createAndAddExecutorThread(executable, simulationSpecificTime);
            }
        }
    }

    private void createAndScheduleExecutorThread(Executable executable, long simulationSpecificTime) {
        long timeToBeExecuted = simulationSpecificTime - getCurrentTime();
        RealTimeExecutorThread realTimeExecutorThread = createAndAddExecutorThread(executable, timeToBeExecuted);
        getExecutor().schedule(realTimeExecutorThread, timeToBeExecuted, TimeUnit.MILLISECONDS);
    }

    /**
     * Create a new instance of {@link RealTimeExecutorThread} and add it in {@link #executorThreadList}.
     *
     * @param executable       the executable of the ExecutorThread
     * @param timeToBeExecuted the time when the ExecutorThread must be executed
     * @return a new instance of a RealTimeExecutorThread
     */
    @NotNull
    private RealTimeMultiThreadScheduler.RealTimeExecutorThread createAndAddExecutorThread(Executable executable,
                                                                                           long timeToBeExecuted) {
        RealTimeExecutorThread realTimeExecutorThread;
        realTimeExecutorThread = new RealTimeExecutorThread(executable, timeToBeExecuted);
        executorThreadList.add(realTimeExecutorThread);
        return realTimeExecutorThread;
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

    @Override
    public @NotNull TimeMode getTimeMode() {
        return TimeMode.REAL_TIME;
    }

    // Inner class.

    public class RealTimeExecutorThread extends OneExecutableExecutorThread {

        // variables.

        private final long delay;

        private final RealTimeMultiThreadScheduler scheduler;

        // Constructors.

        public RealTimeExecutorThread(Executable executable, long delay) {
            super(executable);
            this.delay = delay;
            scheduler = RealTimeMultiThreadScheduler.this;
        }

        // Methods.

        @Override
        public void run() {
            boolean executed = verifyAndExecute();

            if (executed) {
                scheduler.executorThreadList.remove(this);
                if (scheduler.endSimulationReach())
                    notifyEndByReachEndSimulation();
                else if (scheduler.noExecutableToExecute())
                    notifyEndByNoExecutableToExecute();
            }

            isFinished = true;
        }

        private void notifyEndByReachEndSimulation() {
            scheduler.endByEndSimulationReach();
        }

        private void notifyEndByNoExecutableToExecute() {
            scheduler.endByNoExecutableToExecution();
        }

        /**
         * Verifies if all conditions are satisfied to execute the {@link Executable}. If it is the case, the
         * {@code Executable} is executed and returns true, else the {@code Executable} is not executed and returns
         * false.
         *
         * @return true if the {@link Executable} has been executed, else false.
         */
        private boolean verifyAndExecute() {
            synchronized (scheduler) {
                if (scheduler.isKilled())
                    return false;

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
        private final long endSimulationTime;

        private final RealTimeMultiThreadScheduler scheduler;

        // Constructors.

        public FinishWatcher(long timeToWait) {
            this.endSimulationTime = timeToWait;

            scheduler = RealTimeMultiThreadScheduler.this;
        }

        // Methods.

        @Override
        public void run() {
            waitTheEnd();
            notifyWatcherAndKillScheduler();
        }

        /**
         * Make sleep the current Thread until the end of the simulation.
         */
        private void waitTheEnd() {
            try {
                Thread.sleep(endSimulationTime);
            } catch (InterruptedException ignored) {
            }
        }

        private void notifyWatcherAndKillScheduler() {
            synchronized (scheduler) {
                if (scheduler.isRunning()) {
                    scheduler.endByEndSimulationReach();
                }
            }
        }
    }
}
