package sima.core.scheduler;

import sima.core.agent.AgentIdentifier;
import sima.core.scheduler.exception.NotSchedulableTimeException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadScheduler implements Scheduler {

    // Variables.

    /**
     * Lock use for pass at the next time step.
     */
    private final Object stepLock;

    private long currentTime;

    private long endSimulationTime;

    private boolean isStarted = false;

    private int nbExecutorThread;

    private boolean allIsDone = false;

    /**
     * Maps for each time of the simulation agent actions.
     */
    private final Map<Long, Map<AgentIdentifier, LinkedList<Executable>>> mapAgentExecutable;

    /**
     * Maps for each time of the simulation executables which are not agent actions.
     */
    private final Map<Long, LinkedList<Executable>> mapExecutable;

    private List<ExecutorThread> executorThreadList;

    private StepFinishWatcher stepFinishWatcher;

    private ExecutorService executor;

    // Constructors.

    public MultiThreadScheduler() {
        this.mapAgentExecutable = new ConcurrentHashMap<>();

        this.mapExecutable = new ConcurrentHashMap<>();

        this.stepLock = new Object();
    }

    @Override
    public synchronized void start() {
        if (!this.isStarted) {
            this.executor = Executors.newFixedThreadPool(this.nbExecutorThread);
            this.isStarted = true;

            this.stepFinishWatcher = new StepFinishWatcher();
            Thread finishExecutionWatcher = new Thread(this.stepFinishWatcher);
            finishExecutionWatcher.start();

            this.executeNextExecutable();
        }
    }

    /**
     * Search and execute next executables to execute in the simulation.
     * <p>
     * Set the {@link #currentTime} to the next time find.
     * <p>
     * If there is no others executable to execute. Finish the simulation.
     */
    private void executeNextExecutable() {
        if (this.executorThreadList == null)
            this.executorThreadList = new ArrayList<>();
        else
            this.executorThreadList.clear();

        long nextTime = -1;

        // Create executors for agent actions.
        Set<Long> setKeyMapActionAgent = this.mapAgentExecutable.keySet();
        List<Long> sortedKeyMapActionAgent = new ArrayList<>(setKeyMapActionAgent);
        sortedKeyMapActionAgent.sort(Comparator.comparingLong(l -> l));
        if (sortedKeyMapActionAgent.size() > 0) {
            // Set the next time.
            nextTime = sortedKeyMapActionAgent.get(0);

            // Fill the executor thread list.
            Map<AgentIdentifier, LinkedList<Executable>> mapAgentActionList = this.mapAgentExecutable.get(nextTime);
            mapAgentActionList.forEach(((agentIdentifier, actions) -> {
                ExecutorThread executorThread = new ExecutorThread(actions);
                executorThreadList.add(executorThread);
            }));
        }

        // Create executor for all other executables.
        Set<Long> setKeyMapExecutable = this.mapExecutable.keySet();
        List<Long> sorterKeyMapExecutable = new ArrayList<>(setKeyMapExecutable);
        sorterKeyMapExecutable.sort(Comparator.comparingLong(l -> l));
        if (sorterKeyMapExecutable.size() > 0) {
            if (nextTime == -1) {
                // There is no agent actions.
                // Set the next time.
                nextTime = sorterKeyMapExecutable.get(0);

                // Fill the executor thread list.
                ExecutorThread executorThread = new ExecutorThread(this.mapExecutable.get(nextTime));
                this.executorThreadList.add(executorThread);
            } else {
                // Already set the nextTime.
                long n = sorterKeyMapExecutable.get(0);
                if (nextTime == n) {
                    // Same time that the time of all agent action taken.
                    ExecutorThread executorThread = new ExecutorThread(this.mapExecutable.get(nextTime));
                    this.executorThreadList.add(executorThread);
                } else {
                    if (n < nextTime) {
                        // We must execute this executable before execute agent action taken.
                        nextTime = n;

                        /* We clear the current executorThreadList because it contains actions agent that it must not
                         be executed for the moment.*/
                        executorThreadList.clear();

                        ExecutorThread executorThread = new ExecutorThread(this.mapExecutable.get(nextTime));
                        executorThreadList.add(executorThread);
                    }
                    // else -> (n > nextTime) We do nothing
                }
            }
        }

        if (executorThreadList.isEmpty()) {
            // No executable find to execute -> end of the simulation.
            this.allIsDone = true;
            // TODO Call end of simulation
        } else {
            this.currentTime = nextTime;

            // Remove all executables which have been taken in account.
            this.mapAgentExecutable.remove(this.currentTime);
            this.mapExecutable.remove(this.currentTime);

            executorThreadList.forEach(executorThread -> this.executor.execute(executorThread));
        }
    }

    // Methods.
    @Override
    public void scheduleExecutable(Executable executable, long waitingTime, ScheduleMode scheduleMode,
                                   int executionTimeStep) {
        if (executable instanceof Action) {
            Action action = (Action) executable;
            if (action.getExecutorAgent() != null) {
                // Agent action
                this.addAgentActionAtTime(action, this.currentTime + waitingTime);
            } else {
                // Not agent action
                this.addExecutableAtTime(action, this.currentTime + waitingTime);
            }
        } else
            this.addExecutableAtTime(executable, this.currentTime + waitingTime);
    }

    @Override
    public void scheduleExecutableAtSpecificTime(Executable executable, long simulationSpecificTime) {
        if (simulationSpecificTime <= this.currentTime)
            throw new NotSchedulableTimeException("SimulationSpecificTime is already passed");

        if (simulationSpecificTime > this.endSimulationTime)
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

    // Inner classes.

    private class ExecutorThread implements Runnable {

        // Variables.

        private final Queue<Executable> executables;

        private boolean isFinished = false;

        // Constructors.

        public ExecutorThread(Queue<Executable> executables) {
            this.executables = executables;
        }

        // Methods.

        @Override
        public void run() {
            this.executables.forEach(Executable::execute);

            synchronized (MultiThreadScheduler.this.stepLock) {
                this.isFinished = true;
                MultiThreadScheduler.this.stepLock.notifyAll();
            }
        }

        // Getters and Setters.

        public boolean isFinished() {
            return isFinished;
        }
    }

    private class StepFinishWatcher implements Runnable {

        private boolean stopped = false;

        @Override
        public void run() {
            while (!this.stopped)
                synchronized (MultiThreadScheduler.this.stepLock) {
                    while (!this.allExecutionsFinished()) {
                        try {
                            MultiThreadScheduler.this.stepLock.wait();

                            if (this.stopped)
                                break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    MultiThreadScheduler.this.executeNextExecutable();
                }
        }

        /**
         * @return true if all {@link ExecutorThread} in {@link #executorThreadList} have finished, else false.
         */
        private boolean allExecutionsFinished() {
            for (ExecutorThread executorThread : MultiThreadScheduler.this.executorThreadList) {
                if (!executorThread.isFinished()) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Kill the thread.
         */
        public void kill() {
            synchronized (MultiThreadScheduler.this.stepLock) {
                this.stopped = true;
                MultiThreadScheduler.this.stepLock.notifyAll();
            }
        }
    }
}
