package sima.core.simulation;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.scheduler.multithread.RealTimeMultiThreadScheduler;
import sima.core.simulation.exception.EnvironmentConstructionException;
import sima.core.simulation.exception.SimaSimulationAlreadyRunningException;
import sima.core.simulation.exception.SimulationSetupConstructionException;
import sima.core.simulation.exception.TwoAgentWithSameIdentifierException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class SimaSimulation {

    // Constants.

    /**
     * The number of thread of the {@link DiscreteTimeMultiThreadScheduler}.
     */
    private static final int NB_THREAD_MULTI_THREAD_SCHEDULER = 8;

    // Singleton.

    private static SimaSimulation SIMA_SIMULATION;

    // Variables

    private SimulationSchedulerWatcher schedulerWatcher;

    private SimaSimulationWatcher simaWatcher;

    private TimeMode timeMode;

    private AgentManager agentManager;

    private Scheduler scheduler;

    private HashMap<String, Environment> environments;

    // Constructors.

    private SimaSimulation() {
    }

    // Methods.

    /**
     * Kill the Simulation. After this call, the call of the method
     * {@link #runSimulation(TimeMode, SchedulerType, long, Set, Class, Scheduler.SchedulerWatcher, SimaWatcher)} is
     * possible.
     */
    public synchronized static void killSimulation() {
        if (SIMA_SIMULATION.scheduler != null)
            SIMA_SIMULATION.scheduler.kill();

        SIMA_SIMULATION.simaWatcher.simulationKilled();

        SIMA_SIMULATION = null;
    }

    /**
     * Run a simulation.
     *
     * @param simulationTimeMode      the simulation time mode
     * @param simulationSchedulerType the simulation scheduler type
     * @param endSimulation           the end of the simulation
     * @param environments            the array of environments classes
     * @param simulationSetupClass    the simulation setup class
     */
    public synchronized static void runSimulation(TimeMode simulationTimeMode, SchedulerType simulationSchedulerType,
                                                  long endSimulation,
                                                  Set<Class<? extends Environment>> environments,
                                                  Class<? extends SimulationSetup> simulationSetupClass,
                                                  Scheduler.SchedulerWatcher schedulerWatcher,
                                                  SimaWatcher simaWatcher) {
        // Create the singleton.
        if (SIMA_SIMULATION == null)
            SIMA_SIMULATION = new SimaSimulation();
        else
            throw new SimaSimulationAlreadyRunningException();


        // Creates the agent manager.
        SIMA_SIMULATION.agentManager = new LocalAgentManager();

        // Update time mode.
        SIMA_SIMULATION.timeMode = simulationTimeMode;
        switch (SIMA_SIMULATION.timeMode) {
            case REAL_TIME -> {
                switch (simulationSchedulerType) {
                    case MONO_THREAD -> {
                        SimaSimulation.killSimulation();
                        throw new UnsupportedOperationException("Real Time Mono thread simulation" +
                                " unsupported.");
                    }
                    case MULTI_THREAD -> SIMA_SIMULATION.scheduler = new RealTimeMultiThreadScheduler(endSimulation,
                            NB_THREAD_MULTI_THREAD_SCHEDULER);
                }
            }
            case DISCRETE_TIME -> {
                // Create the Scheduler.
                switch (simulationSchedulerType) {
                    case MONO_THREAD -> {
                        SimaSimulation.killSimulation();
                        throw new UnsupportedOperationException("Discrete Time Mono thread simulation" +
                                " unsupported.");
                    }
                    case MULTI_THREAD -> SIMA_SIMULATION.scheduler = new DiscreteTimeMultiThreadScheduler(endSimulation,
                            NB_THREAD_MULTI_THREAD_SCHEDULER);
                }
            }
        }

        // Add a scheduler watcher.
        SIMA_SIMULATION.schedulerWatcher = new SimulationSchedulerWatcher();
        SIMA_SIMULATION.schedulerWatcher.addSchedulerWatcher(schedulerWatcher);
        SIMA_SIMULATION.scheduler.addSchedulerWatcher(SIMA_SIMULATION.schedulerWatcher);

        // Create and add environments.
        if (environments == null || environments.size() < 1) {
            SimaSimulation.killSimulation();
            throw new IllegalArgumentException("The simulation need to have at least 1 environments");
        }

        for (Class<? extends Environment> environmentClass : environments) {
            try {
                Constructor<? extends Environment> constructor = environmentClass.getConstructor(String.class,
                        String[].class);
                Environment env = constructor.newInstance(environmentClass.getName(), null);

                if (SIMA_SIMULATION.findEnvironment(env.getEnvironmentName()) == null)
                    SIMA_SIMULATION.environments.put(env.getEnvironmentName(), env);
                else {
                    SimaSimulation.killSimulation();
                    throw new IllegalArgumentException("Two environments with the same name");
                }

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException e) {
                SimaSimulation.killSimulation();
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
            SimaSimulation.killSimulation();
            throw new SimulationSetupConstructionException(e);
        }

        // Create the SimaWatcher.
        SIMA_SIMULATION.simaWatcher = new SimaSimulationWatcher();

        if (simaWatcher != null)
            SIMA_SIMULATION.simaWatcher.addSimaWatcher(simaWatcher);

        SIMA_SIMULATION.simaWatcher.simulationStarted();
    }

    public static boolean simulationIsRunning() {
        return SIMA_SIMULATION != null;
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

    public static TimeMode timeMode() {
        return SIMA_SIMULATION.timeMode;
    }

    // Enum.

    /**
     * Time mode of the simulation.
     */
    public enum TimeMode {
        REAL_TIME, DISCRETE_TIME
    }

    /**
     * Type of the scheduler. Mono-Thread or Multi-Thread.
     */
    public enum SchedulerType {
        MULTI_THREAD, MONO_THREAD
    }

    // Inner classes.

    public interface SimaWatcher {

        /**
         * Call back method, called when the simulation is started with a method run.
         */
        void simulationStarted();

        /**
         * Call back method, called when the simulation is killed with the method
         * {@link SimaSimulation#killSimulation()}.
         */
        void simulationKilled();
    }

    private static class SimaSimulationWatcher implements SimaWatcher {

        // Variables.

        private final Vector<SimaWatcher> otherWatchers;

        // Constructors.

        public SimaSimulationWatcher() {
            this.otherWatchers = new Vector<>();
        }

        // Methods.

        public void addSimaWatcher(SimaWatcher simaWatcher) {
            this.otherWatchers.add(simaWatcher);
        }

        @Override
        public void simulationStarted() {
            this.otherWatchers.forEach(SimaWatcher::simulationStarted);
        }

        @Override
        public void simulationKilled() {
            this.otherWatchers.forEach(SimaWatcher::simulationKilled);
        }
    }

    /**
     * The simulation scheduler watcher.
     */
    private static class SimulationSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Variables.

        private final Vector<Scheduler.SchedulerWatcher> otherWatchers;

        // Constructors.

        public SimulationSchedulerWatcher() {
            this.otherWatchers = new Vector<>();
        }

        // Methods.

        public void addSchedulerWatcher(Scheduler.SchedulerWatcher schedulerWatcher) {
            this.otherWatchers.add(schedulerWatcher);
        }

        @Override
        public void schedulerStarted() {
            this.otherWatchers.forEach(Scheduler.SchedulerWatcher::schedulerStarted);
        }

        @Override
        public void schedulerKilled() {
            this.otherWatchers.forEach(Scheduler.SchedulerWatcher::schedulerKilled);
        }

        @Override
        public void simulationEndTimeReach() {
            this.otherWatchers.forEach(Scheduler.SchedulerWatcher::simulationEndTimeReach);
        }

        @Override
        public void noExecutableToExecute() {
            this.otherWatchers.forEach(Scheduler.SchedulerWatcher::noExecutableToExecute);
        }
    }
}
