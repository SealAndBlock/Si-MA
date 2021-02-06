package sima.core.scheduler.multithread;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.NotSchedulableTimeException;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
     * The runnable which for each step, wait for that all executables of the step has been executed and call the method
     * {@link #executeNextStep()} to pass to the next step time.
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
            createNewExecutor();
            executeNextStep();
            startStepFinishWatcher();  // ORDER IMPORTANT -> startStepFinishWatcher() after executeNextStep()
            notifyOnSchedulerStarted();
            return true;
        } else
            return false;
    }

    /**
     * Instantiates {@link #executor}.
     */
    @Override
    protected void createNewExecutor() {
        executor = Executors.newFixedThreadPool(nbExecutorThread);
    }

    /**
     * Start the thread of {@link #stepFinishWatcher}.
     */
    private void startStepFinishWatcher() {
        stepFinishWatcher = new StepFinishWatcher();
        Thread finishExecutionWatcher = new Thread(stepFinishWatcher);
        finishExecutionWatcher.start();
    }


    @Override
    public synchronized boolean kill() {
        if (!isKilled) {
            setKilled();
            shutdownExecutor();
            killStepFinishWatcher();
            mapExecutable.clear();
            executorThreadList.clear();
            notifyOnSchedulerKilled();
            return true;
        } else
            return false;
    }

    private void killStepFinishWatcher() {
        if (stepFinishWatcher != null)
            stepFinishWatcher.kill();
    }

    /**
     * Search and execute next executables of the next step in the simulation.
     * <p>
     * Set the {@link #currentTime} to the next time find.
     * <p>
     * If there is no others executable to execute. Finish the simulation.
     * <p>
     * If the end simulation is reach. Finish the simulation.
     * <p>
     * This method is not thread safe, however, it is never called in parallel way.
     */
    private void executeNextStep() {
        executorThreadList.clear();

        TreeSet<Long> sortedStepTimeSet = getSortedStepTimeSet();
        if (sortedStepTimeSet.isEmpty()) {
            endByNoExecutableToExecution();
        } else {
            long nextTime = sortedStepTimeSet.first();
            prepareExecutorThread(nextTime);
            currentTime = nextTime;

            if (!endSimulationReach()) {
                removeExecutablesFor(currentTime);
                executeALlExecutorThreads();
            } else {
                endByReachEndSimulationTime();
            }
        }
    }

    @NotNull
    private TreeSet<Long> getSortedStepTimeSet() {
        Set<Long> setStepTimeSet = mapExecutable.keySet();
        return new TreeSet<>(setStepTimeSet);
    }

    /**
     * Removed all {@code Executables} of {@link #mapExecutable} which must be executed at the specified time.
     *
     * @param time the time of each executable must be removed
     */
    private void removeExecutablesFor(long time) {
        mapExecutable.remove(time);
    }

    /**
     * Give to the {@link #executor} all {@link sima.core.scheduler.multithread.MultiThreadScheduler.ExecutorThread} in
     * the list {@link #executorThreadList}.
     */
    private void executeALlExecutorThreads() {
        executorThreadList.forEach(executorThread -> executor.execute(executorThread));
    }

    private void prepareExecutorThread(long nextTime) {
        mapExecutable.get(nextTime).forEach(
                executable -> executorThreadList.add(new DiscreteTimeExecutorThread(executable)));
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

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has
     * finish by reaching the end time of the simulation.
     */
    private void endByReachEndSimulationTime() {
        if (!isKilled()) {
            notifyOnSimulationEndTimeReach();
            kill();
        }
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
     * If the scheduleMode is equal to {@link sima.core.scheduler.Scheduler.ScheduleMode#REPEATED} or {@link
     * sima.core.scheduler.Scheduler.ScheduleMode#INFINITE}, the executable is add the number of times that it must be
     * added. It is the same instance which is added at each times, there is no copy of the {@code Executable}.
     *
     * @param executable    the executable to add
     * @param waitingTime   the waiting time before execute the action
     * @param scheduleMode  the schedule mode
     * @param nbRepetitions the number of times that the action must be repeated if the {@link
     *                      sima.core.scheduler.Scheduler.ScheduleMode} is equal to {@link
     *                      sima.core.scheduler.Scheduler.ScheduleMode#REPEATED}
     */
    private void addExecutableWithScheduleMode(Executable executable, long waitingTime,
                                               Scheduler.ScheduleMode scheduleMode,
                                               long nbRepetitions, long executionTimeStep) {
        switch (scheduleMode) {
            case ONCE -> addExecutableAtTime(executable, currentTime + waitingTime);
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

    private void addInfiniteExecutable(Executable executable, long waitingTime, long executionTimeStep) {
        long time = currentTime + waitingTime;
        addExecutableAtTime(new InfiniteExecutable(executable, executionTimeStep), time);
    }

    private void addRepeatedExecutable(Executable executable, long waitingTime, long nbRepetitions,
                                       long executionTimeStep) {
        long time = currentTime + waitingTime;
        addExecutableAtTime(new RepeatedExecutable(executable, nbRepetitions, executionTimeStep), time);
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
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private void addExecutableAtTime(Executable executable, long time) {
        LinkedList<Executable> executables = mapExecutable.computeIfAbsent(time, k -> new LinkedList<>());
        synchronized (executables) {
            executables.add(executable);
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

    @Override
    public @NotNull TimeMode getTimeMode() {
        return TimeMode.DISCRETE_TIME;
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

            setFinished();
        }

        /**
         * Set {@link #isFinished} at true and notifyAll thread waiting on the {@link Scheduler} stepLock.
         */
        private void setFinished() {
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
                    while (!allExecutionsFinished())
                        try {
                            scheduler.stepLock.wait();
                            if (stopped) break;
                        } catch (InterruptedException e) {
                            stopped = true;
                            break;
                        }

                    if (!stopped)
                        scheduler.executeNextStep();
                }
            }
        }

        /**
         * @return true if all {@link DiscreteTimeExecutorThread} in {@link #executorThreadList} have finished, else
         * false.
         */
        private boolean allExecutionsFinished() {
            for (ExecutorThread discreteTimeExecutorThread : scheduler.executorThreadList)
                if (!discreteTimeExecutorThread.isFinished())
                    return false;

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
