package sima.core.scheduler.multithread;

import sima.core.exception.NotSchedulableTimeException;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class DiscreteTimeMultiThreadScheduler extends MultiThreadScheduler {

    // Variables.

    /**
     * Lock use for pass at the next time step.
     */
    private final Object stepLock;

    /**
     * The currentTime of the simulation.
     */
    private long currentTime;

    /**
     * Maps for each time of the simulation executables which are not agent actions.
     */
    private final Map<Long, LinkedList<Executable>> mapExecutable;

    /**
     * The runnable which for each step, wait for that all executables of the step has been executed and call
     * the method {@link #executeNextExecutable()} to pass to the next step time.
     */
    private StepFinishWatcher stepFinishWatcher;

    // Constructors.

    /**
     * @param endSimulation    the end of the simulation
     * @param nbExecutorThread the number of executor thread
     * @throws IllegalArgumentException if the endSimulationTime or the nbExecutorThread is less than 1.
     */
    public DiscreteTimeMultiThreadScheduler(long endSimulation, int nbExecutorThread) {
        super(endSimulation, nbExecutorThread);

        mapExecutable = new ConcurrentHashMap<>();

        stepLock = new Object();
    }

    // Methods.

    @Override
    public synchronized boolean start() {
        if (!isStarted && !isKilled) {
            setStarted();

            updateSchedulerWatcherOnSchedulerStarted();

            executor = Executors.newFixedThreadPool(nbExecutorThread);

            executeNextExecutable();

            // ORDER VERY IMPORTANT -> always start the thread after executeNextExecutable()
            startStepFinishWatcherThread();

            return true;
        } else
            return false;
    }

    private void startStepFinishWatcherThread() {
        stepFinishWatcher = new StepFinishWatcher();
        Thread finishExecutionWatcher = new Thread(stepFinishWatcher);
        finishExecutionWatcher.start();
    }

    private void setStarted() {
        isStarted = true;
    }

    @Override
    public synchronized boolean kill() {
        if (!isKilled) {
            setKilled();
            executorShutdown();
            killStepFinishWatcher();
            mapExecutable.clear();
            updateSchedulerWatcherOnSchedulerKilled();

            return true;
        } else
            return false;
    }

    private void setKilled() {
        isStarted = false;
        isKilled = true;
    }

    private void killStepFinishWatcher() {
        if (stepFinishWatcher != null)
            stepFinishWatcher.kill();
    }

    private void executorShutdown() {
        if (executor != null)
            executor.shutdownNow();
    }

    /**
     * Search and execute next executables to execute in the simulation.
     * <p>
     * Set the {@link #currentTime} to the next time find.
     * <p>
     * If there is no others executable to execute. Finish the simulation.
     * <p>
     * This method is not thread safe, however, it is never called in parallel way.
     */
    private void executeNextExecutable() {
        executorThreadList.clear();

        long nextTime;

        // Creates executor for all other executables.
        Set<Long> setStepTimeSet = mapExecutable.keySet();
        TreeSet<Long> sortedStepTimeSet = new TreeSet<>(setStepTimeSet);

        if (sortedStepTimeSet.isEmpty()) {
            // No executable find to execute -> end of the simulation.
            endByNoExecutableToExecution();
        } else {
            nextTime = sortedStepTimeSet.first();
            mapExecutable.get(nextTime).forEach(
                    executable -> executorThreadList.add(new DiscreteTimeExecutorThread(executable)));

            currentTime = nextTime;

            // Verify if the next time is always in the simulation.
            if (currentTime <= getEndSimulation()) {
                // Remove all executables which have been taken in account.
                mapExecutable.remove(currentTime);

                executorThreadList.forEach(executorThread -> executor.execute(executorThread));
            } else {
                // End of the simulation reach.
                endByReachEndSimulationTime();
            }
        }
    }

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has
     * finish by no executable to execute
     */
    private void endByNoExecutableToExecution() {
        updateSchedulerWatcherOnNoExecutableToExecute();

        kill();
    }

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has
     * finish by reaching the end time of the simulation.
     */
    private void endByReachEndSimulationTime() {
        updateSchedulerWatcherOnSimulationEndTimeReach();

        kill();
    }

    @Override
    public void scheduleExecutable(Executable executable, long waitingTime, Scheduler.ScheduleMode scheduleMode,
                                   long nbRepetitions, long executionTimeStep) {
        if (waitingTime < 1)
            throw new IllegalArgumentException("Waiting time cannot be less than 1.");

        if (!isKilled())
            addExecutableWithScheduleMode(executable, waitingTime, scheduleMode, nbRepetitions, executionTimeStep);
    }

    /**
     * Add the {@link Executable} in function of the {@link sima.core.scheduler.Scheduler.ScheduleMode}.
     * <p>
     * If the scheduleMode is equal to {@link sima.core.scheduler.Scheduler.ScheduleMode#REPEATED} or
     * {@link sima.core.scheduler.Scheduler.ScheduleMode#INFINITELY}, the executable is add the number of times that it
     * must be added. It is the same instance which is added at each times, there is no copy of the {@code Executable}.
     *
     * @param executable    the executable to add
     * @param waitingTime   the waiting time before execute the action
     * @param scheduleMode  the schedule mode
     * @param nbRepetitions the number of times that the action must be repeated if the
     *                      {@link sima.core.scheduler.Scheduler.ScheduleMode} is equal to
     *                      {@link sima.core.scheduler.Scheduler.ScheduleMode#REPEATED}
     */
    private void addExecutableWithScheduleMode(Executable executable, long waitingTime, Scheduler.ScheduleMode scheduleMode,
                                               long nbRepetitions, long executionTimeStep) {
        switch (scheduleMode) {
            case ONCE -> addExecutableAtTime(executable, currentTime + waitingTime);
            case REPEATED -> {
                if (nbRepetitions < 1)
                    throw new IllegalArgumentException("NbRepeated must be greater or equal to 1");

                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                long time = currentTime + waitingTime;
                addExecutableAtTime(executable, time);
                for (int i = 1; i < nbRepetitions; i++) {
                    time += executionTimeStep;
                    addExecutableAtTime(executable, time);
                }
            }
            case INFINITELY -> {
                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                long time = currentTime + waitingTime;
                addExecutableAtTime(executable, time);
                while (time <= getEndSimulation()) {
                    time += executionTimeStep;
                    addExecutableAtTime(executable, time);
                }
            }
        }
    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {
        if (simulationSpecificTime < 1)
            throw new IllegalArgumentException("SimulationSpecificTime must be greater or equal to 1");

        if (simulationSpecificTime <= currentTime)
            throw new NotSchedulableTimeException("SimulationSpecificTime is already passed");

        if (!isKilled())
            addExecutableAtTime(executable, simulationSpecificTime);
    }

    /**
     * Add the executable in the list which contains all executable which must be executed at the specified time.
     *
     * @param executable the executable to add
     * @param time       the time where the executable must be executed
     */
    private void addExecutableAtTime(Executable executable, long time) {
        LinkedList<Executable> executables = mapExecutable.computeIfAbsent(time, k -> new LinkedList<>());
        synchronized (executables) {
            executables.add(executable);
            executables.sort(Comparator.comparingInt(Object::hashCode));
        }
    }

    @Override
    public long getCurrentTime() {
        if (isRunning())
            return currentTime;
        else if (!isKilled)
            return currentTime;
        else
            return -1;
    }

    // Inner classes.

    private class DiscreteTimeExecutorThread extends OneExecutableExecutorThread {

        // Variables.

        private final DiscreteTimeMultiThreadScheduler scheduler;

        // Constructors.

        public DiscreteTimeExecutorThread(Executable executable) {
            super(executable);

            scheduler = DiscreteTimeMultiThreadScheduler.this;
        }

        // Methods.

        @Override
        public void run() {
            super.run();

            synchronized (scheduler.stepLock) {
                isFinished = true;
                scheduler.stepLock.notifyAll();
            }
        }
    }

    private class StepFinishWatcher implements Runnable {

        // Variables.

        private final DiscreteTimeMultiThreadScheduler scheduler;

        private boolean stopped = false;

        // Constructors.

        public StepFinishWatcher() {
            scheduler = DiscreteTimeMultiThreadScheduler.this;
        }

        // Methods.

        @Override
        public void run() {
            synchronized (scheduler.stepLock) {
                while (!stopped) {
                    while (!allExecutionsFinished()) {
                        try {
                            scheduler.stepLock.wait();

                            if (stopped) break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            stopped = true;
                            break;
                        }
                    }

                    if (!stopped)
                        scheduler.executeNextExecutable();
                }
            }
        }

        /**
         * @return true if all {@link DiscreteTimeExecutorThread} in {@link #executorThreadList} have finished, else false.
         */
        private boolean allExecutionsFinished() {
            // DiscreteTimeMultiThreadScheduler.executorThreadList.isEmpty() -> impossible case

            for (ExecutorThread discreteTimeExecutorThread : scheduler.executorThreadList) {
                if (!discreteTimeExecutorThread.isFinished()) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Kill the thread.
         */
        public void kill() {
            synchronized (scheduler.stepLock) {
                stopped = true;
                scheduler.stepLock.notifyAll();
            }
        }
    }
}
