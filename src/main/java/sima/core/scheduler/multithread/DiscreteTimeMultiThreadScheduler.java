package sima.core.scheduler.multithread;

import sima.core.agent.AgentIdentifier;
import sima.core.scheduler.Action;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.exception.NotSchedulableTimeException;

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
     * Maps for each time of the simulation agent actions.
     */
    private final Map<Long, Map<AgentIdentifier, LinkedList<Executable>>> mapAgentExecutable;

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

        this.mapAgentExecutable = new ConcurrentHashMap<>();

        this.mapExecutable = new ConcurrentHashMap<>();

        this.stepLock = new Object();
    }

    // Methods.

    @Override
    public synchronized boolean start() {
        if (!this.isStarted) {
            this.isStarted = true;

            this.updateSchedulerWatcherOnSchedulerStarted();

            this.executor = Executors.newFixedThreadPool(this.nbExecutorThread);

            // ORDER VERY IMPORTANT -> always instantiate this.stepFinishWatcher before this.executeNextExecutable()
            this.stepFinishWatcher = new StepFinishWatcher();

            this.executeNextExecutable();

            // ORDER VERY IMPORTANT -> always start the thread after this.executeNextExecutable()
            Thread finishExecutionWatcher = new Thread(this.stepFinishWatcher);
            finishExecutionWatcher.start();

            return true;
        } else
            return false;
    }

    @Override
    public synchronized boolean kill() {
        if (this.isStarted) {
            this.isStarted = false;

            this.executor.shutdownNow();

            this.stepFinishWatcher.kill();

            this.mapAgentExecutable.clear();

            this.mapExecutable.clear();

            this.updateSchedulerWatcherOnSchedulerKilled();

            return true;
        } else
            return false;
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
        this.executorThreadList.clear();

        long nextTime = -1;

        // Creates executors for agent actions.
        Set<Long> setKeyMapActionAgent = this.mapAgentExecutable.keySet();
        List<Long> sortedKeyMapActionAgent = new ArrayList<>(setKeyMapActionAgent);
        sortedKeyMapActionAgent.sort(Comparator.comparingLong(l -> l));

        // Verifies if there is action agent to execute.
        if (sortedKeyMapActionAgent.size() > 0) {
            // Set the next time.
            nextTime = sortedKeyMapActionAgent.get(0);

            // Fill the executor thread list.
            Map<AgentIdentifier, LinkedList<Executable>> mapAgentActionList = this.mapAgentExecutable.get(nextTime);
            mapAgentActionList.forEach(((agentIdentifier, actions) -> {
                DiscreteTimeExecutorThread discreteTimeExecutorThread = new DiscreteTimeExecutorThread(actions);
                executorThreadList.add(discreteTimeExecutorThread);
            }));
        }

        // Creates executor for all other executables.
        Set<Long> setKeyMapExecutable = this.mapExecutable.keySet();
        List<Long> sorterKeyMapExecutable = new ArrayList<>(setKeyMapExecutable);
        sorterKeyMapExecutable.sort(Comparator.comparingLong(l -> l));

        // Verifies if there is others executable to execute.
        if (sorterKeyMapExecutable.size() > 0) {
            if (nextTime == -1) {
                // There is no agent actions.
                // Set the next time.
                nextTime = sorterKeyMapExecutable.get(0);

                // Fill the executor thread list.
                DiscreteTimeExecutorThread discreteTimeExecutorThread = new DiscreteTimeExecutorThread(this.mapExecutable.get(nextTime));
                this.executorThreadList.add(discreteTimeExecutorThread);
            } else {
                // Already set the nextTime.
                long n = sorterKeyMapExecutable.get(0);
                if (nextTime == n) {
                    // Same time that the time of all agent action taken.
                    DiscreteTimeExecutorThread discreteTimeExecutorThread = new DiscreteTimeExecutorThread(this.mapExecutable.get(nextTime));
                    this.executorThreadList.add(discreteTimeExecutorThread);
                } else {
                    if (n < nextTime) {
                        // We must execute theses executables before execute agent action taken before.
                        nextTime = n;

                        /* We clear the current executorThreadList because it contains actions agent that it must not
                         be executed for the moment.*/
                        this.executorThreadList.clear();

                        DiscreteTimeExecutorThread discreteTimeExecutorThread = new DiscreteTimeExecutorThread(this.mapExecutable.get(nextTime));
                        this.executorThreadList.add(discreteTimeExecutorThread);
                    }
                    // else -> (n > nextTime) We do nothing
                }
            }
        }

        if (this.executorThreadList.isEmpty()) {
            // No executable find to execute -> end of the simulation.
            this.endByNoExecutableToExecution();
        } else {
            this.currentTime = nextTime;

            // Verify if the next time is always in the simulation.
            if (this.currentTime <= this.getEndSimulation()) {
                // Remove all executables which have been taken in account.
                this.mapAgentExecutable.remove(this.currentTime);
                this.mapExecutable.remove(this.currentTime);

                this.executorThreadList.forEach(executorThread -> this.executor.execute(executorThread));
            } else {
                // End of the simulation reach.
                this.endByReachEndSimulationTime();
            }
        }
    }

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has
     * finish by no executable to execute
     */
    private void endByNoExecutableToExecution() {
        this.updateSchedulerWatcherOnNoExecutableToExecute();

        this.kill();
    }

    /**
     * Kill itself and notify all {@link sima.core.scheduler.Scheduler.SchedulerWatcher} that the {@link Scheduler} has
     * finish by reaching the end time of the simulation.
     */
    private void endByReachEndSimulationTime() {
        this.updateSchedulerWatcherOnSimulationEndTimeReach();

        this.kill();
    }

    @Override
    public void scheduleExecutable(Executable executable, long waitingTime, Scheduler.ScheduleMode scheduleMode,
                                   long nbRepetitions, long executionTimeStep) {
        if (waitingTime < 1)
            throw new IllegalArgumentException("Waiting time cannot be less than 1.");

        if (executable instanceof Action) {
            Action action = (Action) executable;
            if (action.getExecutorAgent() != null) {
                // Agent action
                this.addAgentActionWithScheduleMode(action, waitingTime, scheduleMode, nbRepetitions, executionTimeStep);
            } else {
                // Not agent action
                this.addExecutableWithScheduleMode(action, waitingTime, scheduleMode, nbRepetitions, executionTimeStep);
            }
        } else
            // +1 because we does not add an action on the current time.
            this.addExecutableWithScheduleMode(executable, waitingTime, scheduleMode, nbRepetitions, executionTimeStep);
    }

    /**
     * Add the {@link Action} in function of the {@link sima.core.scheduler.Scheduler.ScheduleMode}.
     * <p>
     * If the scheduleMode is equal to {@link sima.core.scheduler.Scheduler.ScheduleMode#REPEATED} or
     * {@link sima.core.scheduler.Scheduler.ScheduleMode#INFINITELY}, the action is add the number of times that it must
     * be added. It is the same instance which is added at each times, there is no copy of the {@code Action}.
     *
     * @param action            the action to add
     * @param waitingTime       the waiting time before execute the action
     * @param scheduleMode      the schedule mode
     * @param nbRepetitions     the number of times that the action must be repeated if the
     *                          {@link sima.core.scheduler.Scheduler.ScheduleMode} is equal to
     *                          {@link sima.core.scheduler.Scheduler.ScheduleMode#REPEATED}
     * @param executionTimeStep the time between each execution of a repeated action
     */
    private void addAgentActionWithScheduleMode(Action action, long waitingTime, Scheduler.ScheduleMode scheduleMode,
                                                long nbRepetitions, long executionTimeStep) {
        switch (scheduleMode) {
            case ONCE -> this.addAgentActionAtTime(action, this.currentTime + waitingTime);
            case REPEATED -> {
                if (nbRepetitions < 1)
                    throw new IllegalArgumentException("NbRepeated must be greater or equal to 1");

                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                long time = this.currentTime + waitingTime;
                this.addAgentActionAtTime(action, time);
                for (int i = 1; i < nbRepetitions; i++) {
                    time += executionTimeStep;
                    this.addAgentActionAtTime(action, time);
                }
            }
            case INFINITELY -> {
                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                long time = this.currentTime + waitingTime;
                this.addAgentActionAtTime(action, time);
                while (time <= this.getEndSimulation()) {
                    time += executionTimeStep;
                    this.addAgentActionAtTime(action, time);
                }
            }
        }
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
            case ONCE -> this.addExecutableAtTime(executable, this.currentTime + waitingTime);
            case REPEATED -> {
                if (nbRepetitions < 1)
                    throw new IllegalArgumentException("NbRepeated must be greater or equal to 1");

                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                long time = this.currentTime + waitingTime;
                this.addExecutableAtTime(executable, time);
                for (int i = 1; i < nbRepetitions; i++) {
                    time += executionTimeStep;
                    this.addExecutableAtTime(executable, time);
                }
            }
            case INFINITELY -> {
                if (executionTimeStep < 1)
                    throw new IllegalArgumentException("ExecutionTimeStep must be greater or equal to 1");

                long time = this.currentTime + waitingTime;
                this.addExecutableAtTime(executable, time);
                while (time <= this.getEndSimulation()) {
                    time += executionTimeStep;
                    this.addExecutableAtTime(executable, time);
                }
            }
        }
    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {
        if (simulationSpecificTime < 1)
            throw new IllegalArgumentException("SimulationSpecificTime must be greater or equal to 1");

        if (simulationSpecificTime <= this.currentTime)
            throw new NotSchedulableTimeException("SimulationSpecificTime is already passed");

        if (simulationSpecificTime > this.getEndSimulation())
            // We does not take in account the executable but not throw an exception because the agent must not know
            // that it is in a simulation therefore does not know the end of the simulation.
            return;

        if (executable instanceof Action) {
            Action action = (Action) executable;
            if (action.getExecutorAgent() != null) {
                // Agent action
                this.addAgentActionAtTime(action, simulationSpecificTime);
            } else {
                // Not agent action
                this.addExecutableAtTime(action, simulationSpecificTime);
            }
        } else
            this.addExecutableAtTime(executable, simulationSpecificTime);
    }

    /**
     * Add the action in the list associated to the executor agent at the specified time of the simulation.
     *
     * @param action the agent action to add
     * @param time   the time where the action must be executed
     */
    private void addAgentActionAtTime(Action action, long time) {
        Map<AgentIdentifier, LinkedList<Executable>> mapAgentAction = this.mapAgentExecutable
                .computeIfAbsent(time, k -> new ConcurrentHashMap<>());

        if (action.getExecutorAgent() == null)
            throw new IllegalArgumentException("The action does not have executor agent");

        LinkedList<Executable> agentActions = mapAgentAction
                .computeIfAbsent(action.getExecutorAgent(), k -> new LinkedList<>());

        // Synchronized on the action list of the agent.
        synchronized (agentActions) {
            agentActions.add(action);
            agentActions.sort(Comparator.comparingInt(a -> ((Action) a).getExecutorAgent().hashCode()));
        }
    }

    /**
     * Add the executable in the list which contains all executable which must be executed at the specified time.
     *
     * @param executable the executable to add
     * @param time       the time where the executable must be executed
     */
    private void addExecutableAtTime(Executable executable, long time) {
        LinkedList<Executable> executables = this.mapExecutable.computeIfAbsent(time, k -> new LinkedList<>());
        synchronized (executables) {
            executables.add(executable);
            executables.sort(Comparator.comparingInt(Object::hashCode));
        }
    }

    @Override
    public long getCurrentTime() {
        return currentTime;
    }

    // Getters and Setters.

    public List<ExecutorThread> getExecutorThreadList() {
        return Collections.unmodifiableList(this.executorThreadList);
    }

    // Inner classes.

    private class DiscreteTimeExecutorThread extends ExecutorThread {

        // Variables.

        private final Queue<Executable> executables;

        // Constructors.

        public DiscreteTimeExecutorThread(Queue<Executable> executables) {
            this.executables = executables;
        }

        // Methods.

        @Override
        public void run() {
            this.executables.forEach(Executable::execute);

            synchronized (DiscreteTimeMultiThreadScheduler.this.stepLock) {
                this.isFinished = true;
                DiscreteTimeMultiThreadScheduler.this.stepLock.notifyAll();
            }
        }
    }

    private class StepFinishWatcher implements Runnable {

        private boolean stopped = false;

        @Override
        public void run() {
            synchronized (DiscreteTimeMultiThreadScheduler.this.stepLock) {
                while (!this.stopped) {
                    try {
                        while (!this.allExecutionsFinished()) {
                            try {
                                DiscreteTimeMultiThreadScheduler.this.stepLock.wait();

                                if (this.stopped)
                                    break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (EmptyExecutorException e) {
                        // Empty list of executable -> end of simulation.
                        this.stopped = true;
                    }

                    if (!this.stopped)
                        DiscreteTimeMultiThreadScheduler.this.executeNextExecutable();
                }
            }
        }

        /**
         * @return true if all {@link DiscreteTimeExecutorThread} in {@link #executorThreadList} have finished, else false.
         */
        private boolean allExecutionsFinished() throws EmptyExecutorException {
            if (DiscreteTimeMultiThreadScheduler.this.executorThreadList.isEmpty())
                throw new EmptyExecutorException();

            for (ExecutorThread discreteTimeExecutorThread : DiscreteTimeMultiThreadScheduler.this.executorThreadList) {
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
            synchronized (DiscreteTimeMultiThreadScheduler.this.stepLock) {
                this.stopped = true;
                DiscreteTimeMultiThreadScheduler.this.stepLock.notifyAll();
            }
        }

        // Inner class

        private class EmptyExecutorException extends Exception {

            // Constructors.

            public EmptyExecutorException() {
            }
        }
    }
}
