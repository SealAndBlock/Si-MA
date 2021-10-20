package sima.core.simulation;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.exception.SimaSimulationAlreadyRunningException;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.exception.SimaSimulationIsNotRunningException;
import sima.core.scheduler.Scheduler;
import sima.core.simulation.configuration.parser.ConfigurationParser;
import sima.core.utils.SimaLogger;

import java.util.*;

public final class SimaSimulation {

    // Static.

    private static final Object LOCK = new Object();
    public static final SimaLogger SimaLog = new SimaLogger(SimaSimulation.class);
    private static SimaSimulation simaSimulation;

    // Variables

    private Scheduler scheduler;
    private SimulationSchedulerWatcher schedulerWatcher;

    private AgentManager agentManager;

    private Map<String, Environment> environments;

    private SimaSimulationWatcher simaWatcher;

    // Constructors.

    private SimaSimulation() {
    }

    // Methods.

    /**
     * @param configurationJsonPath the configuration file path
     *
     * @throws SimaSimulationFailToStartRunningException if sima simulation does not success to run
     */
    public static void runSimulation(String configurationJsonPath) throws SimaSimulationFailToStartRunningException {
        try {
            var configurationParser = new ConfigurationParser(configurationJsonPath);
            ConfigurationParser.ConfigurationBundle bundle;
            bundle = configurationParser.parseSimulation();
            runSimulation(bundle.getScheduler(), bundle.getAllAgents(), bundle.getAllEnvironments(), bundle.getSimulationSetup(),
                          bundle.getSimaWatcher());
        } catch (Exception e) {
            throw new SimaSimulationFailToStartRunningException(
                    "Fail parse SimaSimulation Json configuration file : " + configurationJsonPath, e);
        }
    }

    /**
     * Try to run a Simulation. All instances of needed to run a simulation must be created and pass in argument. In that way, this method only make
     * the start of the simulation.
     * <p>
     * The set {@code allAgents} contains all the agents of the simulation. However, all these agents are not adding in any environment. To bind agent
     * and environments, you must make it in the {@link SimulationSetup}.
     * <p>
     * The {@link SimulationSetup} is called at the end of the method, after all agents and environments has been added in the simulation. In that way
     * is possible to create and add new instances of agents and environment in the {@code SimulationSetup}.
     * <p>
     * This method is thread safe.
     *
     * @param scheduler       the scheduler of the simulation
     * @param allAgents       the set of all instances of agents in the simulation
     * @param allEnvironments the set of all instances of environments in the simulation
     * @param simulationSetup the {@link SimulationSetup}
     * @param simaWatcher     the simulation watcher
     *
     * @throws SimaSimulationFailToStartRunningException if exception is thrown during the start of the simulation
     */
    public static void runSimulation(Scheduler scheduler, Set<SimaAgent> allAgents, Set<Environment> allEnvironments, SimulationSetup simulationSetup,
                                     SimaWatcher simaWatcher)
            throws SimaSimulationFailToStartRunningException {
        synchronized (LOCK) {
            if (!simaSimulationIsRunning())
                try {
                    createNewSimaSimulationSingletonInstance();
                    simaSimulationAddSimaWatcher(simaWatcher);
                    simaSimulationSetScheduler(scheduler);
                    simaSimulationAddAllAgents(allAgents);
                    simaSimulationAddEnvironments(allEnvironments);
                    simaSimulationStartAllAgents();
                    simaSimulationExecuteSimulationSetup(simulationSetup);
                    simaSimulationNotifyOnSimulationStarted();
                    simaSimulationStartScheduler();
                    SimaLog.info("SimaSimulation RUN");
                } catch (Exception e) {
                    killSimulation();
                    throw new SimaSimulationFailToStartRunningException(e);
                }
            else {
                SimaLog.error("Simulation already running");
                throw new SimaSimulationFailToStartRunningException(new SimaSimulationAlreadyRunningException());
            }
        }
    }

    /**
     * Create a new instance of {@link SimaSimulation} only if there is no instance of it.
     * <p>
     * <strong>WARNING!</strong> This method is not thread safe.
     */
    private static void createNewSimaSimulationSingletonInstance() {
        // Create the singleton.
        if (simaSimulation == null)
            simaSimulation = new SimaSimulation();

    }

    private static void simaSimulationAddSimaWatcher(SimaWatcher simaWatcher) {
        if (simaSimulation.simaWatcher == null)
            simaSimulation.simaWatcher = new SimaSimulationWatcher();

        if (simaWatcher != null)
            simaSimulation.simaWatcher.addSimaWatcher(simaWatcher);
    }

    /**
     * @param scheduler the scheduler to set to the simulation
     *
     * @throws NullPointerException if the scheduler is null
     */
    private static void simaSimulationSetScheduler(Scheduler scheduler) {
        simaSimulation.scheduler = Optional.of(scheduler).get();
        simaSimulation.schedulerWatcher = new SimulationSchedulerWatcher();
        simaSimulation.scheduler.addSchedulerWatcher(simaSimulation.schedulerWatcher);
    }

    /**
     * Create a new instance of {@link #agentManager} and add all agents in the specified set in the agent manager.
     * <p>
     * This method must be called even if allAgents is null are empty because this method create a new instance of {@link #agentManager}.
     *
     * @param allAgents the set of agents to add.
     *
     * @throws NullPointerException if one agent is null.
     */
    private static void simaSimulationAddAllAgents(Set<SimaAgent> allAgents) {
        createNewAgentManager();
        if (allAgents != null && !allAgents.isEmpty())
            addAllAgents(allAgents);
    }

    private static void createNewAgentManager() {
        if (simaSimulation.agentManager == null)
            simaSimulation.agentManager = new LocalAgentManager();
    }

    private static void addAllAgents(Set<SimaAgent> allAgents) {
        for (SimaAgent agent : allAgents) {
            addAgent(Optional.of(agent).get());
        }
    }

    /**
     * Create a new instance of {@link #environments} and map all environments in with as key the environment name and as value the environment
     * instance.
     *
     * @param allEnvironments the set of environments to add.
     *
     * @throws NullPointerException if the specified set is null or if one environment is null.
     */
    private static void simaSimulationAddEnvironments(Set<Environment> allEnvironments) {
        if (allEnvironments == null || allEnvironments.isEmpty())
            throw new IllegalArgumentException("A SimaSimulation needs to have at least one environment to work");

        createNewMapEnvironment();
        addAllEnvironments(allEnvironments);
    }

    private static void createNewMapEnvironment() {
        if (simaSimulation.environments == null)
            simaSimulation.environments = new HashMap<>();
    }

    /**
     * Add all environments contained in the specified set. If several environment have the same name, throws a {@link IllegalArgumentException}.
     *
     * @param allEnvironments all environments to add
     */
    private static void addAllEnvironments(Set<Environment> allEnvironments) {
        for (Environment environment : allEnvironments)
            addEnvironment(environment); // Impossible that there is two environments with same hash because they are already in a set
    }

    /**
     * Create a new instance of the specified {@link SimulationSetup} class and call the method {@link SimulationSetup#setupSimulation()}.
     *
     * @param simulationSetup the {@link SimulationSetup}
     */
    private static void simaSimulationExecuteSimulationSetup(SimulationSetup simulationSetup) {
        if (simulationSetup != null) {
            simulationSetup.setupSimulation();
            SimaLog.info("SimulationSetup " + simulationSetup.getClass() + " EXECUTED");
        }
    }

    /**
     * Start all agents in managed by {@link #agentManager}.
     */
    private static void simaSimulationStartAllAgents() {
        for (SimaAgent agent : simaSimulation.agentManager.getAllAgents()) {
            if (!agent.isStarted())
                agent.start();
        }
    }

    private static void simaSimulationNotifyOnSimulationStarted() {
        if (simaSimulation.simaWatcher != null)
            simaSimulation.simaWatcher.notifyOnSimulationStarted();
    }

    private static void simaSimulationStartScheduler() {
        simaSimulation.scheduler.start();
    }

    /**
     * Kill the Simulation. After this call, the call of the method {@code runSimulation} is possible.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     */
    public static void killSimulation() {
        synchronized (LOCK) {
            if (simaSimulationIsRunning()) {
                if (simaSimulation != null && simaSimulation.scheduler != null)
                    simaSimulationKillScheduler();
                if (simaSimulation != null && simaSimulation.simaWatcher != null)
                    simaSimulationNotifyOnSimulationKilled();
                LOCK.notifyAll();
                destroySimaSimulationSingleton();
            }
        }
    }

    private static void destroySimaSimulationSingleton() {
        if (simaSimulation != null)
            SimaLog.info("SimaSimulation KILLED");

        simaSimulation = null;
    }

    private static void simaSimulationNotifyOnSimulationKilled() {
        simaSimulation.simaWatcher.notifyOnSimulationKilled();
    }

    private static void simaSimulationKillScheduler() {
        simaSimulation.scheduler.kill();
    }

    /**
     * Block until the simulation is kill. In other words, wait until the method {@link #killSimulation()} be called.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     */
    public static void waitEndSimulation() {
        synchronized (LOCK) {
            while (simaSimulationIsRunning())
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
        }
    }

    /**
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     *
     * @return true if the simulation is running, else false.
     */
    public static boolean simaSimulationIsRunning() {
        synchronized (LOCK) {
            return simaSimulation != null;
        }
    }

    /**
     * @return the scheduler of the simulation. Never returns null.
     */
    public static Scheduler getScheduler() {
        verifySimaSimulationIsRunning();
        return simaSimulation.scheduler;
    }

    /**
     * @return the current time of the simulation.
     *
     * @see Scheduler#getCurrentTime()
     */
    public static long getCurrentTime() {
        verifySimaSimulationIsRunning();
        return simaSimulation.scheduler.getCurrentTime();
    }

    /**
     * @param agent - the agent to add
     */
    public static void addAgent(SimaAgent agent) {
        verifySimaSimulationIsRunning();
        if (simaSimulation.agentManager.addAgent(agent))
            SimaLog.info(agent + " ADDED in SimaSimulation");
    }

    /**
     * Finds in the {@link #agentManager} the agent which as the same {@link AgentIdentifier} than the specified agent identifier.
     *
     * @param agentIdentifier the identifier of the wanted agent
     *
     * @return the agent associate to the identifier, returns null if the agent is not found.
     *
     * @throws NullPointerException if the agentIdentifier is null.
     */
    public static SimaAgent getAgent(AgentIdentifier agentIdentifier) {
        verifySimaSimulationIsRunning();
        return simaSimulation.findAgent(agentIdentifier);
    }

    public static SimaAgent getAgent(long uniqueId) {
        verifySimaSimulationIsRunning();
        return simaSimulation.findAgent(uniqueId);
    }

    /**
     * Verifies if the environment name is not already know by the simulation. If it not the case, add the environment in the simulation and returns
     * true, else do nothing and returns false.
     *
     * @param environment the environment to add
     */
    public static void addEnvironment(Environment environment) {
        verifySimaSimulationIsRunning();
        if (!simaSimulation.environments.containsKey(environment.getEnvironmentName())) {
            simaSimulation.environments.put(environment.getEnvironmentName(), environment);
            SimaLog.info(environment + " ADDED in SimaSimulation");
        }
    }

    /**
     * @return the list of all environments of the simulation.
     */
    public static @NotNull Set<Environment> getAllEnvironments() {
        verifySimaSimulationIsRunning();
        return new HashSet<>(simaSimulation.environments.values());
    }

    /**
     * @param environmentName the environment of the wanted environment
     *
     * @return the environment of the simulation which has the specified name. If no environment is find, returns null.
     */
    public static Environment getEnvironment(String environmentName) {
        verifySimaSimulationIsRunning();
        return simaSimulation.findEnvironment(environmentName);
    }

    /**
     * @param environmentName the environment of the wanted environment
     *
     * @return the environment of the simulation which has the specified name. If no environment is find, returns null.
     */
    private Environment findEnvironment(String environmentName) {
        if (environmentName == null)
            return null;

        return this.environments.get(environmentName);
    }

    /**
     * Search among all environments of the simulation where the agent is evolving.
     * <p>
     * If there is no environment where the agent is evolving, returns an empty list.
     *
     * @param agentIdentifier the agent identifier
     *
     * @return a list of all environments where the agent is evolving.
     */
    public static @NotNull List<Environment> getAgentEnvironment(AgentIdentifier agentIdentifier) {
        verifySimaSimulationIsRunning();

        if (agentIdentifier == null)
            return Collections.emptyList();

        List<Environment> environments = new ArrayList<>();
        Set<Map.Entry<String, Environment>> entrySet = simaSimulation.environments.entrySet();
        for (Map.Entry<String, Environment> entry : entrySet) {
            var environment = entry.getValue();
            if (environment.isEvolving(agentIdentifier)) {
                environments.add(environment);
            }
        }

        return environments;
    }

    public static @NotNull Scheduler.TimeMode getTimeMode() {
        verifySimaSimulationIsRunning();
        return simaSimulation.scheduler.getTimeMode();
    }

    public static @NotNull Scheduler.SchedulerType getSchedulerType() {
        verifySimaSimulationIsRunning();
        return simaSimulation.scheduler.getSchedulerType();
    }

    /**
     * Verifies if the SimaSimulation is running, if it is not the case, throws a {@link SimaSimulationIsNotRunningException}.
     */
    private static void verifySimaSimulationIsRunning() {
        if (!simaSimulationIsRunning())
            throw new SimaSimulationIsNotRunningException();
    }

    /**
     * Finds in the {@link #agentManager} the agent which as the same {@link AgentIdentifier} than the specified agent identifier.
     *
     * @param agentIdentifier the identifier of the wanted agent
     *
     * @return the agent associate to the identifier, returns null if the agent is not found.
     *
     * @throws NullPointerException if the agentIdentifier is null.
     */
    private SimaAgent findAgent(AgentIdentifier agentIdentifier) {
        return agentManager.getAgent(agentIdentifier);
    }

    private SimaAgent findAgent(long uniqueId) {
        return agentManager.getAgent(uniqueId);
    }

    // Inner classes.

    public interface SimaWatcher {

        /**
         * Call back method, called when the simulation is started with a method run.
         */
        void notifyOnSimulationStarted();

        /**
         * Call back method, called when the simulation is killed with the method {@link SimaSimulation#killSimulation()}.
         */
        void notifyOnSimulationKilled();
    }

    public static class SimaSimulationWatcher implements SimaWatcher {

        // Variables.

        private final List<SimaWatcher> otherWatchers;

        // Constructors.

        public SimaSimulationWatcher() {
            this.otherWatchers = new ArrayList<>();
        }

        // Methods.

        public void addSimaWatcher(SimaWatcher simaWatcher) {
            this.otherWatchers.add(simaWatcher);
        }

        @Override
        public void notifyOnSimulationStarted() {
            this.otherWatchers.forEach(SimaWatcher::notifyOnSimulationStarted);
        }

        @Override
        public void notifyOnSimulationKilled() {
            this.otherWatchers.forEach(SimaWatcher::notifyOnSimulationKilled);
        }
    }

    /**
     * The simulation scheduler watcher. Only wait the notification of the Scheduler to kill the Simulation.
     */
    public static class SimulationSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Constructors.

        // Methods.

        @Override
        public void schedulerStarted() {
            // Do nothing because already done in the Simulation during the start.
        }

        @Override
        public void schedulerKilled() {
            killSimulation();
        }

        @Override
        public void simulationEndTimeReach() {
            killSimulation();
        }

        @Override
        public void noExecutableToExecute() {
            killSimulation();
        }
    }
}
