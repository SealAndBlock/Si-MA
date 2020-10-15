package sima.core.scheduler;

import sima.core.agent.AgentIdentifier;
import sima.core.scheduler.exception.NotSchedulableTimeException;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadScheduler implements Scheduler {

    // Variables.

    private long currentTime;

    private long endSimulationTime;

    /**
     * Maps for each time of the simulation agent actions.
     */
    private final Map<Long, Map<AgentIdentifier, LinkedList<Action>>> mapAgentExecutable;

    /**
     * Maps for each time of the simulation executables which are not agent actions.
     */
    private final Map<Long, LinkedList<Executable>> mapExecutable;

    // Constructors.

    public MultiThreadScheduler() {
        this.mapAgentExecutable = new ConcurrentHashMap<>();

        this.mapExecutable = new ConcurrentHashMap<>();
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
     * @param time the time for when we want the executable list
     * @return the executable list of all executables which must be executed at the specified time if it exists, else
     * null.
     */
    private LinkedList<Executable> getExecutableList(long time) {
        return this.mapExecutable.get(time);
    }

    /**
     * Add the action in the list associated to the executor agent at the specified time of the simulation.
     *
     * @param action the agent action to add
     * @param time   the time where the action must be executed
     */
    private void addAgentActionAtTime(Action action, long time) {
        Map<AgentIdentifier, LinkedList<Action>> mapAgentAction = this.mapAgentExecutable
                .computeIfAbsent(time, k -> new ConcurrentHashMap<>());

        if (action.getExecutorAgent() == null)
            throw new IllegalArgumentException("The action does not have executor agent");

        LinkedList<Action> agentActions = mapAgentAction
                .computeIfAbsent(action.getExecutorAgent(), k -> new LinkedList<>());

        // Synchronized on the action list of the agent.
        synchronized (agentActions) {
            agentActions.add(action);
            agentActions.sort(Comparator.comparingInt(a -> a.getExecutorAgent().hashCode()));
        }
    }

    /**
     * @param agentIdentifier the agent identifier
     * @param time            the time for when we want the action agent list
     * @return the action agent list of the specified agent for the specific time if it exists, else return null.
     */
    private LinkedList<Action> getAgentActionList(AgentIdentifier agentIdentifier, long time) {
        Map<AgentIdentifier, LinkedList<Action>> mapAgentAction = this.mapAgentExecutable.get(time);
        if (mapAgentAction != null) {
            return mapAgentAction.get(agentIdentifier);
        } else
            return null;
    }

    // Inner Thread.
}
