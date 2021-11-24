package sima.core.scheduler.multithread;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.NotScheduleTimeException;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.executor.Executable;
import sima.core.scheduler.executor.MultiThreadExecutor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DiscreteTimeMultiThreadScheduler extends MultiThreadScheduler {

    // Variables.

    /**
     * The currentTime of the simulation.
     */
    private long currentTime;

    /**
     * Maps for each time of the simulation executables which are not agent actions.
     */
    private final Map<Long, LinkedList<Executable>> mapExecutable;

    /**
     * The runnable which for each step, wait for that all executables of the step has been executed and call the method {@link #executeNextStep()} to
     * pass to the next step time.
     */
    private StepFinishWatcher stepFinishWatcher;

    // Constructors.

    /**
     * @param endSimulation    the end of the simulation
     * @param nbExecutorThread the number of executor thread
     *
     * @throws IllegalArgumentException if the endSimulationTime or the nbExecutorThread is less than 1.
     */
    public DiscreteTimeMultiThreadScheduler(long endSimulation, int nbExecutorThread) {
        super(endSimulation, nbExecutorThread);
        mapExecutable = new ConcurrentHashMap<>();
        currentTime = 0;
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
        executor = new MultiThreadExecutor(nbExecutorThread);
    }

    /**
     * Start the thread of {@link #stepFinishWatcher}.
     */
    private void startStepFinishWatcher() {
        stepFinishWatcher = new StepFinishWatcher();
        var finishExecutionWatcher = new Thread(stepFinishWatcher);
        finishExecutionWatcher.start();
    }


    @Override
    public synchronized boolean kill() {
        if (!isKilled) {
            setKilled();
            shutdownExecutor();
            killStepFinishWatcher();
            mapExecutable.clear();
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
        TreeSet<Long> sortedStepTimeSet = getSortedStepTimeSet();
        if (sortedStepTimeSet.isEmpty()) {
            endByNoExecutableToExecution();
        } else {
            currentTime = sortedStepTimeSet.first();

            if (!endSimulationReach()) {
                // Order important
                List<Executable> toExecute = getExecutableAtTime(currentTime);
                removeExecutablesFor(currentTime);
                executeAllExecutables(toExecute);
            } else {
                endByReachEndSimulationTime();
            }
        }
    }

    private @NotNull TreeSet<Long> getSortedStepTimeSet() {
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
     * Give to the {@link #executor} all {@link Executable}s in the list map with the {@link #currentTime} in the map {@link #mapExecutable}.
     */
    private void executeAllExecutables(List<Executable> toExecute) {
        toExecute.forEach(executable -> executor.execute(executable));
    }

    private @NotNull List<Executable> getExecutableAtTime(long time) {
        return mapExecutable.get(time);
    }


    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has finish by no executable to
     * execute
     */
    private void endByNoExecutableToExecution() {
        if (!isKilled()) {
            notifyOnNoExecutableToExecute();
            kill();
        }
    }

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has finish by reaching the end
     * time of the simulation.
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
        if (executable == null)
            throw new NullPointerException("Executable cannot be null");

        if (waitingTime < 1)
            throw new IllegalArgumentException("Waiting time cannot be less than 1.");

        if (!isKilled())
            addExecutable(executable, waitingTime, scheduleMode, nbRepetitions, executionTimeStep);
    }

    /**
     * Add the executable in the list which contains all executable which must be executed at the specified time.
     *
     * @param executable the executable to add
     * @param time       the time when the executable must be executed
     */
    @Override
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    protected void addExecutableAtTime(Executable executable, long time) {
        LinkedList<Executable> executables = mapExecutable.computeIfAbsent(time, k -> new LinkedList<>());
        synchronized (executables) {
            executables.add(executable);
        }
    }

    @Override
    protected void addInfiniteExecutable(Executable executable, long waitingTime, long executionTimeStep) {
        long time = currentTime + waitingTime;
        addExecutableAtTime(new InfiniteExecutable(executable, executionTimeStep), time);
    }

    @Override
    protected void addRepeatedExecutable(Executable executable, long waitingTime, long nbRepetitions,
                                         long executionTimeStep) {
        long time = currentTime + waitingTime;
        addExecutableAtTime(new RepeatedExecutable(executable, nbRepetitions, executionTimeStep), time);
    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {
        if (executable == null)
            throw new NullPointerException("Executable cannot be null");

        if (simulationSpecificTime < 1)
            throw new IllegalArgumentException("SimulationSpecificTime must be greater or equal to 1");

        if (simulationSpecificTime <= currentTime)
            throw new NotScheduleTimeException("SimulationSpecificTime is already passed");

        if (!isKilled())
            addExecutableAtTime(executable, simulationSpecificTime);
    }

    @Override
    public long getCurrentTime() {
        if (isRunning() || !isKilled)
            return currentTime;
        else
            return -1;
    }

    @Override
    public @NotNull TimeMode getTimeMode() {
        return TimeMode.DISCRETE_TIME;
    }

    // Inner classes.

    private class StepFinishWatcher implements Runnable {

        // Variables.

        private final DiscreteTimeMultiThreadScheduler scheduler;

        private volatile boolean stopped = false;

        private Thread stepFinisherThread;

        // Constructors.

        public StepFinishWatcher() {
            scheduler = DiscreteTimeMultiThreadScheduler.this;
        }

        // Methods.

        @Override
        public void run() {
            stepFinisherThread = Thread.currentThread();
            while (!stopped) {
                try {
                    boolean isQuiescence = executor.awaitQuiescence(100);
                    if (!isQuiescence)
                        continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (!stopped)
                    scheduler.executeNextStep();
            }

        }

        /**
         * Kill the thread.
         */
        public void kill() {
            stopped = true;
            stepFinisherThread.interrupt();
        }
    }
}
