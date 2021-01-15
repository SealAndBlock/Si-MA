package sima.core.simulation;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.exception.EnvironmentConstructionException;
import sima.core.exception.SimaSimulationAlreadyRunningException;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.exception.SimaSimulationIsNotRunningException;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.scheduler.multithread.RealTimeMultiThreadScheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class SimaSimulation {

    // Static.

    private static SimaSimulation SIMA_SIMULATION;

    private static final Object LOCK = new Object();

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
     * Try to run a Simulation. All instances of needed to run a simulation must be create and pass in argument. In
     * that way, this method only make the start of the simulation.
     * <p>
     * The set {@code allAgents} contains all the agents of the simulation. However all this agents are not adding in
     * any environment. To bind agent and environments, you must make it in the {@link SimulationSetup}.
     * <p>
     * The {@link SimulationSetup} is called at the end of the method, after all agents and environments has been added
     * in the simulation. In that way it possible to create and add new instances of agents and environment in the
     * {@code SimulationSetup}.
     * <p>
     * This method is thread safe.
     *
     * @param scheduler            the scheduler of the simulation
     * @param allAgents            the set of all instances of agents in the simulation
     * @param allEnvironments      the set of all instances of environments in the simulation
     * @param simulationSetupClass the {@link SimulationSetup} class
     * @param simaWatcher          the simulation watcher
     * @throws SimaSimulationFailToStartRunningException if exception is thrown during the start of the simulation
     */
    public static void runSimulation(Scheduler scheduler, Set<AbstractAgent> allAgents,
                                     Set<Environment> allEnvironments,
                                     Class<? extends SimulationSetup> simulationSetupClass,
                                     SimaWatcher simaWatcher) throws SimaSimulationFailToStartRunningException {
        synchronized (LOCK) {
            if (!simaSimulationIsRunning())
                try {
                    createNewSimaSimulationSingletonInstance();
                    simaSimulationAddSimaWatcher(simaWatcher);
                    simaSimulationSetScheduler(scheduler);
                    simaSimulationAddAgents(allAgents);
                    simaSimulationAddEnvironments(allEnvironments);
                    simaSimulationStartAllAgents();
                    simaSimulationCreateAndExecuteSimulationSetup(simulationSetupClass);
                    simaSimulationNotifyOnSimulationStarted();
                    simaSimulationStartScheduler();
                } catch (Exception e) {
                    killSimulation();
                    throw new SimaSimulationFailToStartRunningException(e);
                }
            else
                throw new SimaSimulationFailToStartRunningException(new SimaSimulationAlreadyRunningException());
        }
    }

    /**
     * Run a simulation.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     * 
     * @see #runSimulation(Scheduler, Set, Set, Class, SimaWatcher)
     *
     * @param simulationTimeMode      the simulation time mode
     * @param simulationSchedulerType the simulation scheduler type
     * @param endSimulation           the end of the simulation
     * @param environments            the array of environments classes
     * @param simulationSetupClass    the simulation setup class
     */
    public static void runSimulation(Scheduler.TimeMode simulationTimeMode, Scheduler.SchedulerType simulationSchedulerType,
                                     int nbExecutorThread,
                                     long endSimulation,
                                     List<Class<? extends Environment>> environments,
                                     Class<? extends SimulationSetup> simulationSetupClass,
                                     Scheduler.SchedulerWatcher schedulerWatcher,
                                     SimaWatcher simaWatcher) throws SimaSimulationFailToStartRunningException {

        if (verifiesEnvironmentList(environments))
            throw new SimaSimulationFailToStartRunningException(new IllegalArgumentException("The simulation need to have at least 1 environments"));

        try {
            runSimulation(createScheduler(simulationTimeMode, simulationSchedulerType, nbExecutorThread, endSimulation, schedulerWatcher),
                    null,
                    createAllEnvironments(environments),
                    simulationSetupClass,
                    simaWatcher);
        } catch (Exception e) {
            throw new SimaSimulationFailToStartRunningException(e);
        }
    }

    /**
     * Create a new instance of {@link SimaSimulation} only if there is no instance of it.
     * <p>
     * <strong>WARNING!</strong> This method is not thread safe.
     */
    private static void createNewSimaSimulationSingletonInstance() {
        // Create the singleton.
        if (SIMA_SIMULATION == null)
            SIMA_SIMULATION = new SimaSimulation();

    }

    private static void simaSimulationAddSimaWatcher(SimaWatcher simaWatcher) {
        if (SIMA_SIMULATION.simaWatcher == null)
            SIMA_SIMULATION.simaWatcher = new SimaSimulationWatcher();

        if (simaWatcher != null)
            SIMA_SIMULATION.simaWatcher.addSimaWatcher(simaWatcher);
    }

    /**
     * @param scheduler the scheduler to set to the simulation
     * @throws NullPointerException if the scheduler is null
     */
    private static void simaSimulationSetScheduler(Scheduler scheduler) {
        SIMA_SIMULATION.scheduler = Optional.of(scheduler).get();
        SIMA_SIMULATION.schedulerWatcher = new SimulationSchedulerWatcher();
        SIMA_SIMULATION.scheduler.addSchedulerWatcher(SIMA_SIMULATION.schedulerWatcher);
    }

    /**
     * Create a new instance of {@link #agentManager} and add all agents in the specified set in the agent manager.
     * <p>
     * This method must be called even if allAgents is null are empty because this method create a new instance of
     * {@link #agentManager}.
     *
     * @param allAgents the set of agents to add.
     * @throws NullPointerException if one agent is null.
     */
    private static void simaSimulationAddAgents(Set<AbstractAgent> allAgents) {
        createNewAgentManager();
        if (allAgents != null && !allAgents.isEmpty())
            addAllAgents(allAgents);
    }

    private static void createNewAgentManager() {
        if (SIMA_SIMULATION.agentManager == null)
            SIMA_SIMULATION.agentManager = new LocalAgentManager();
    }

    private static void addAllAgents(Set<AbstractAgent> allAgents) {
        for (AbstractAgent agent : allAgents) {
            SIMA_SIMULATION.agentManager.addAgent(Optional.of(agent).get());
        }
    }

    /**
     * Create a new instance of {@link #environments} and map all environments in with as key the environment name and
     * as value the environment instance.
     *
     * @param allEnvironments the set of environments to add.
     * @throws NullPointerException if the specified set is null or if one environment is null.
     */
    private static void simaSimulationAddEnvironments(Set<Environment> allEnvironments) {
        if (allEnvironments.isEmpty())
            throw new IllegalArgumentException("A SimaSimulation needs to have at least one environment to work");

        createNewMapEnvironment();
        addAllEnvironments(allEnvironments);
    }

    private static void createNewMapEnvironment() {
        if (SIMA_SIMULATION.environments == null)
            SIMA_SIMULATION.environments = new HashMap<>();
    }

    /**
     * Add all environments contained in the specified set. If several environment have the same name, throws a
     * {@link IllegalArgumentException}.
     *
     * @param allEnvironments all environments to add
     */
    private static void addAllEnvironments(Set<Environment> allEnvironments) {
        for (Environment environment : allEnvironments) {
            boolean added = addEnvironment(environment);
            if (!added)
                throw new IllegalArgumentException("Two environments with the same name. The problematic name : "
                        + environment.getEnvironmentName());
        }
    }

    /**
     * Try to create a new instance of the {@link SimulationSetup} specified class. if the instantiation failed, kill
     * the simulation by calling the method {@link #killSimulation()} and returns null.
     *
     * @param simulationSetupClass the class of the SimulationSetup
     * @return a new instance of the {@link SimulationSetup} specified class. If the instantiation failed, returns null.
     */
    private static SimulationSetup createSimulationSetup(Class<? extends SimulationSetup> simulationSetupClass) {
        try {
            Constructor<? extends SimulationSetup> simSetupConstructor =
                    simulationSetupClass.getConstructor(Map.class);
            return simSetupConstructor.newInstance((Map<String, String>) null);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Create a new instance of the specified {@link SimulationSetup} class and call the method
     * {@link SimulationSetup#setupSimulation()}.
     *
     * @param simulationSetupClass the class of the SimulationSetup
     * @throws SimaSimulationFailToStartRunningException if problem during the instantiation of the simulation setup
     */
    private static void simaSimulationCreateAndExecuteSimulationSetup(Class<? extends SimulationSetup> simulationSetupClass)
            throws SimaSimulationFailToStartRunningException {
        if (simulationSetupClass != null) {
            SimulationSetup simulationSetup = createSimulationSetup(simulationSetupClass);
            if (simulationSetup == null)
                throw new SimaSimulationFailToStartRunningException("Simulation Setup fail to be instantiate");
            simulationSetup.setupSimulation();
        }
    }

    /**
     * Start all agents in managed by {@link #agentManager}.
     */
    private static void simaSimulationStartAllAgents() {
        for (AbstractAgent agent : SIMA_SIMULATION.agentManager.getAllAgents()) {
            if (!agent.isStarted())
                agent.start();
        }
    }

    private static void simaSimulationNotifyOnSimulationStarted() {
        if (SIMA_SIMULATION.simaWatcher != null)
            SIMA_SIMULATION.simaWatcher.notifyOnSimulationStarted();
    }

    private static void simaSimulationStartScheduler() {
        SIMA_SIMULATION.scheduler.start();
    }

    /**
     * @param environments the environment list to verify
     * @return true if the list is not null and not empty, else false.
     */
    private static boolean verifiesEnvironmentList(List<Class<? extends Environment>> environments) {
        return environments == null || environments.isEmpty();
    }

    /**
     * Create a set which contains all new instances created for each {@code Environment} classes contains in the
     * specified environment class list.
     * <p>
     * If the specified list contains several same {@code Environment} class, then a {@link IllegalArgumentException} is
     * thrown because the the environment name compute for each environment is the name of the {@code Environment}
     * class.
     *
     * @param environments the list of environment class
     * @return a set of all instances of each specified environment class.
     * @throws IllegalArgumentException         if the specified list contains several same {@code Environment} class
     * @throws EnvironmentConstructionException if one environment construction failed
     */
    private static @NotNull Set<Environment> createAllEnvironments(List<Class<? extends Environment>> environments) {
        Set<Environment> environmentSet = new HashSet<>();
        for (Class<? extends Environment> environmentClass : environments)
            try {
                Environment env = createEnvironment(environmentClass);

                if (!environmentSet.add(env))
                    throw new IllegalArgumentException("Two environments with the same name");

            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException e) {
                throw new EnvironmentConstructionException(e);
            }

        return environmentSet;
    }

    /**
     * @param environmentClass the environment class
     * @return a new instance of the specified {@code Environment} class.
     * @throws NoSuchMethodException     if the environment class does not have the correct constructor
     * @throws InstantiationException    if the class cannot be instantiate
     * @throws IllegalAccessException    if the environment constructor is not accessible
     * @throws InvocationTargetException if the environment construction thrown an exception
     */
    @NotNull
    private static Environment createEnvironment(Class<? extends Environment> environmentClass)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<? extends Environment> constructor = environmentClass.getConstructor(String.class,
                Map.class);
        return constructor.newInstance(environmentClass.getName(), null);
    }

    private static @NotNull Scheduler createScheduler(Scheduler.TimeMode simulationTimeMode,
                                                      Scheduler.SchedulerType simulationSchedulerType,
                                                      int nbExecutorThread,
                                                      long endSimulation,
                                                      Scheduler.SchedulerWatcher... schedulerWatchers) {
        if (simulationTimeMode == null)
            throw new NullPointerException("Null simulationTimeMode");

        if (simulationSchedulerType == null)
            throw new NullPointerException("Null simulationSchedulerType");

        Scheduler scheduler = null;
        switch (simulationTimeMode) {
            case REAL_TIME -> scheduler = createRealTimeScheduler(simulationSchedulerType, nbExecutorThread, endSimulation, scheduler);
            case DISCRETE_TIME -> scheduler = createDiscreteTimeScheduler(simulationSchedulerType, nbExecutorThread, endSimulation, scheduler);
        }

        for (Scheduler.SchedulerWatcher schedulerWatcher : schedulerWatchers) {
            scheduler.addSchedulerWatcher(schedulerWatcher);
        }

        return scheduler;
    }

    private static Scheduler createDiscreteTimeScheduler(Scheduler.SchedulerType simulationSchedulerType,
                                                         int nbExecutorThread, long endSimulation, Scheduler scheduler) {
        switch (simulationSchedulerType) {
            case MONO_THREAD -> throw new UnsupportedOperationException("Discrete Time Mono thread simulation" +
                    " unsupported.");
            case MULTI_THREAD -> scheduler = new DiscreteTimeMultiThreadScheduler(endSimulation, nbExecutorThread);
        }
        return scheduler;
    }

    private static Scheduler createRealTimeScheduler(Scheduler.SchedulerType simulationSchedulerType,
                                                     int nbExecutorThread, long endSimulation, Scheduler scheduler) {
        switch (simulationSchedulerType) {
            case MONO_THREAD -> throw new UnsupportedOperationException("Real Time Mono thread simulation" +
                    " unsupported.");
            case MULTI_THREAD -> scheduler = new RealTimeMultiThreadScheduler(endSimulation, nbExecutorThread);
        }
        return scheduler;
    }

    /**
     * Kill the Simulation. After this call, the call of the method
     * {@link #runSimulation(Scheduler.TimeMode, Scheduler.SchedulerType, int, long, List, Class, Scheduler.SchedulerWatcher, SimaWatcher)}  is
     * possible.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     */
    public static void killSimulation() {
        synchronized (LOCK) {
            if (SimaSimulation.simaSimulationIsRunning()) {
                if (SIMA_SIMULATION != null && SIMA_SIMULATION.scheduler != null)
                    simaSimulationKillScheduler();
                if (SIMA_SIMULATION != null && SIMA_SIMULATION.simaWatcher != null)
                    simaSimulationNotifyOnSimulationKilled();
                LOCK.notifyAll();
                destroySimaSimulationSingleton();
            }
        }
    }

    private static void destroySimaSimulationSingleton() {
        SIMA_SIMULATION = null;
    }

    private static void simaSimulationNotifyOnSimulationKilled() {
        SIMA_SIMULATION.simaWatcher.notifyOnSimulationKilled();
    }

    private static void simaSimulationKillScheduler() {
        SIMA_SIMULATION.scheduler.kill();
    }

    /**
     * Block until the simulation is kill. In other words, wait until the method {@link #killSimulation()} be called.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     */
    public static void waitEndSimulation() {
        synchronized (LOCK) {
            if (SimaSimulation.simaSimulationIsRunning())
                try {
                    LOCK.wait();
                } catch (InterruptedException ignored) {
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
            return SIMA_SIMULATION != null;
        }
    }

    /**
     * @return the scheduler of the simulation. Never returns null.
     */
    public static Scheduler getScheduler() {
        verifySimaSimulationIsRunningAndThrowsException();
        return SIMA_SIMULATION.scheduler;
    }

    /**
     * @return the current time of the simulation.
     * @see Scheduler#getCurrentTime()
     */
    public static long getCurrentTime() {
        verifySimaSimulationIsRunningAndThrowsException();
        return SIMA_SIMULATION.scheduler.getCurrentTime();
    }

    /**
     * @param agent - the agent to add
     * @return true if the agent has been added, else false.
     */
    public static boolean addAgent(AbstractAgent agent) {
        verifySimaSimulationIsRunningAndThrowsException();
        return SIMA_SIMULATION.agentManager.addAgent(agent);
    }

    /**
     * Finds in the {@link #agentManager} the agent which as the same {@link AgentIdentifier} than the specified agent
     * identifier.
     *
     * @param agentIdentifier the identifier of the wanted agent
     * @return the agent associate to the identifier, returns null if the agent is not found.
     * @throws NullPointerException if the agentIdentifier is null.
     */
    public static AbstractAgent getAgent(AgentIdentifier agentIdentifier) {
        verifySimaSimulationIsRunningAndThrowsException();
        return SIMA_SIMULATION.findAgent(agentIdentifier);
    }

    /**
     * Finds in the {@link #agentManager} the agent which as the same {@link AgentIdentifier} than the specified agent
     * identifier.
     *
     * @param agentIdentifier the identifier of the wanted agent
     * @return the agent associate to the identifier, returns null if the agent is not found.
     * @throws NullPointerException if the agentIdentifier is null.
     */
    private AbstractAgent findAgent(AgentIdentifier agentIdentifier) {
        if (agentIdentifier == null)
            return null;

        List<AbstractAgent> agents = this.agentManager.getAllAgents();
        AbstractAgent res = null;
        for (AbstractAgent agent : agents)
            if (agent.getAgentIdentifier().equals(agentIdentifier)) {
                res = agent;
                break;
            }

        return res;
    }

    /**
     * Verifies if the environment name is not already know by the simulation. If it not the case, add the environment
     * in the simulation and returns true, else do nothing and returns false.
     *
     * @param environment the environment to add
     * @return true if the environment has been added, else false.
     */
    public static boolean addEnvironment(Environment environment) {
        verifySimaSimulationIsRunningAndThrowsException();
        if (!SIMA_SIMULATION.environments.containsKey(environment.getEnvironmentName())) {
            SIMA_SIMULATION.environments.put(environment.getEnvironmentName(), environment);
            return true;
        } else
            return false;
    }

    /**
     * @return the list of all environments of the simulation.
     */
    public static @NotNull Set<Environment> getAllEnvironments() {
        verifySimaSimulationIsRunningAndThrowsException();
        return new HashSet<>(SIMA_SIMULATION.environments.values());
    }

    /**
     * @param environmentName the environment of the wanted environment
     * @return the environment of the simulation which has the specified name. If no environment is find, returns null.
     */
    public static Environment getEnvironment(String environmentName) {
        verifySimaSimulationIsRunningAndThrowsException();
        return SIMA_SIMULATION.findEnvironment(environmentName);
    }

    /**
     * @param environmentName the environment of the wanted environment
     * @return the environment of the simulation which has the specified name. If no environment is find, returns null.
     */
    private Environment findEnvironment(String environmentName) {
        if (environmentName == null)
            return null;

        return this.environments.get(environmentName);
    }

    public static @NotNull Scheduler.TimeMode getTimeMode() {
        verifySimaSimulationIsRunningAndThrowsException();
        return SIMA_SIMULATION.scheduler.getTimeMode();
    }

    public static @NotNull Scheduler.SchedulerType getSchedulerType() {
        verifySimaSimulationIsRunningAndThrowsException();
        return SIMA_SIMULATION.scheduler.getSchedulerType();
    }

    /**
     * Verifies if the SimaSimulation is running, if it is not the case, throws a
     * {@link SimaSimulationIsNotRunningException}.
     */
    private static void verifySimaSimulationIsRunningAndThrowsException() {
        if (!simaSimulationIsRunning())
            throw new SimaSimulationIsNotRunningException();
    }

    // Inner classes.

    public interface SimaWatcher {

        /**
         * Call back method, called when the simulation is started with a method run.
         */
        void notifyOnSimulationStarted();

        /**
         * Call back method, called when the simulation is killed with the method
         * {@link SimaSimulation#killSimulation()}.
         */
        void notifyOnSimulationKilled();
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
    private static class SimulationSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Constructors.

        public SimulationSchedulerWatcher() {
        }

        // Methods.

        @Override
        public void schedulerStarted() {
        }

        @Override
        public void schedulerKilled() {
            SimaSimulation.killSimulation();
        }

        @Override
        public void simulationEndTimeReach() {
            SimaSimulation.killSimulation();
        }

        @Override
        public void noExecutableToExecute() {
            SimaSimulation.killSimulation();
        }
    }
}
