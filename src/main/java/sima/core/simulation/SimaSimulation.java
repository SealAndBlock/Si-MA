package sima.core.simulation;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.scheduler.MultiThreadScheduler;
import sima.core.scheduler.Scheduler;
import sima.core.simulation.exception.EnvironmentConstructionException;
import sima.core.simulation.exception.SimaSimulationAlreadyRunningException;
import sima.core.simulation.exception.SimulationSetupConstructionException;
import sima.core.simulation.exception.TwoAgentWithSameIdentifierException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimaSimulation {

    // Constants.

    /**
     * The number of thread of the {@link MultiThreadScheduler}.
     */
    private static final int NB_THREAD_MULTI_THREAD_SCHEDULER = 5;

    // Singleton.

    private static SimaSimulation SIMA_SIMULATION;

    // Variables

    private SimulationSchedulerWatcher schedulerWatcher;

    private TimeMode timeMode;

    private AgentManager agentManager;

    private Scheduler scheduler;

    private HashMap<String, Environment> environments;

    // Constructors.

    private SimaSimulation() {
    }

    // Methods.

    public synchronized static void runSimulation(TimeMode simulationTimeMode, SchedulerType simulationSchedulerType,
                                                  long endSimulation,
                                                  Class<? extends Environment>[] environments,
                                                  Class<? extends SimulationSetup> simulationSetupClass) {
        // Create the singleton.
        if (SIMA_SIMULATION == null)
            SIMA_SIMULATION = new SimaSimulation();
        else
            throw new SimaSimulationAlreadyRunningException();

        // Creates the agent manager.
        SIMA_SIMULATION.agentManager = new LocalAgentManager();

        // Update time mode.
        SIMA_SIMULATION.timeMode = simulationTimeMode;

        // Create the Scheduler.
        switch (simulationSchedulerType) {
            case MONO_THREAD -> throw new UnsupportedOperationException("Mono thread simulation unsupported.");
            case MULTI_THREAD -> SIMA_SIMULATION.scheduler = new MultiThreadScheduler(endSimulation,
                    NB_THREAD_MULTI_THREAD_SCHEDULER);
        }

        // Add a scheduler watcher.
        SIMA_SIMULATION.schedulerWatcher = new SimulationSchedulerWatcher();
        SIMA_SIMULATION.scheduler.addSchedulerWatcher(SIMA_SIMULATION.schedulerWatcher);

        // Create and add environments.
        if (environments.length < 1)
            throw new IllegalArgumentException("The simulation need to have at least 1 environments");

        for (Class<? extends Environment> environmentClass : environments) {
            try {
                Constructor<? extends Environment> constructor = environmentClass.getConstructor(String.class,
                        String[].class);
                Environment env = constructor.newInstance(environmentClass.getName(), null);

                if (SIMA_SIMULATION.findEnvironment(env.getEnvironmentName()) == null)
                    SIMA_SIMULATION.environments.put(env.getEnvironmentName(), env);
                else
                    throw new IllegalArgumentException("Two environments with the same class");

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException e) {
                throw new EnvironmentConstructionException(e);
            }
        }

        // Create the SimSetup and calls the method setup.
        try {
            Constructor<? extends SimulationSetup> simSetupConstructor = simulationSetupClass.getConstructor();
            SimulationSetup simulationSetup = simSetupConstructor.newInstance();
            simulationSetup.setupSimulation();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            throw new SimulationSetupConstructionException(e);
        }
    }

    /**
     * @return the scheduler of the simulation. Never returns null.
     */
    public static Scheduler getScheduler() {
        return SIMA_SIMULATION.scheduler;
    }

    /**
     * @return the current time of the simulation.
     * @see Scheduler#getCurrentTime()
     */
    public static long currentTime() {
        return SIMA_SIMULATION.scheduler.getCurrentTime();
    }

    /**
     * Finds in the {@link #agentManager} the agent which as the same {@link AgentIdentifier} than the specified agent
     * identifier.
     *
     * @param agentIdentifier the identifier of the wanted agent
     * @return the agent associate to the identifier, returns null if the agent is not found.
     * @throws NullPointerException                if the agentIdentifier is null.
     * @throws TwoAgentWithSameIdentifierException if there is in the agent manager two agents with the same identifier
     */
    public static AbstractAgent getAgentFromIdentifier(AgentIdentifier agentIdentifier) {
        return SIMA_SIMULATION.findAgent(agentIdentifier);
    }

    /**
     * Finds in the {@link #agentManager} the agent which as the same {@link AgentIdentifier} than the specified agent
     * identifier.
     *
     * @param agentIdentifier the identifier of the wanted agent
     * @return the agent associate to the identifier, returns null if the agent is not found.
     * @throws NullPointerException                if the agentIdentifier is null.
     * @throws TwoAgentWithSameIdentifierException if there is in the agent manager two agents with the same identifier
     */
    private AbstractAgent findAgent(AgentIdentifier agentIdentifier) {
        if (agentIdentifier == null)
            throw new NullPointerException("The agent identifier cannot be null.");

        List<AbstractAgent> agents = this.agentManager.getAllAgents();
        AbstractAgent res = null;
        for (AbstractAgent agent : agents) {
            if (agent.getAgentIdentifier().equals(agentIdentifier)) {
                if (res == null)
                    res = agent;
                else
                    throw new TwoAgentWithSameIdentifierException("Agent1 = " + res + " Agent2 = " + agent);
            }
        }

        return res;
    }

    /**
     * @return the list of all environments of the simulation.
     */
    public static List<Environment> getAllEnvironments() {
        return SIMA_SIMULATION.environments.keySet().stream().collect(ArrayList::new, (list, s) ->
                list.add(SIMA_SIMULATION.environments.get(s)), ArrayList::addAll);
    }

    /**
     * @param environmentName the environment of the wanted environment
     * @return the environment of the simulation which has the specified name. If no environment is find, returns null.
     */
    public static Environment getEnvironmentFromName(String environmentName) {
        return SIMA_SIMULATION.findEnvironment(environmentName);
    }

    /**
     * @param environmentName the environment of the wanted environment
     * @return the environment of the simulation which has the specified name. If no environment is find, returns null.
     */
    private Environment findEnvironment(String environmentName) {
        if (environmentName == null)
            throw new NullPointerException("The environment name cannot be null");

        return this.environments.get(environmentName);
    }

    // Enum.

    /**
     * Time mode of the simulation.
     */
    public enum TimeMode {
        REAL_TIME, DISCRETE_TIME;
    }

    /**
     * Type of the scheduler. Mono-Thread or Multi-Thread.
     */
    public enum SchedulerType {
        MULTI_THREAD, MONO_THREAD;
    }

    // Inner classes.

    /**
     * The simulation scheduler watcher.
     */
    private static class SimulationSchedulerWatcher implements Scheduler.SchedulerWatcher {

        @Override
        public void schedulerStarted() {

        }

        @Override
        public void schedulerKilled() {

        }

        @Override
        public void simulationEndTimeReach() {

        }

        @Override
        public void noExecutableToExecute() {

        }
    }
}
