package sima.core.simulation;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.exception.*;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.scheduler.multithread.RealTimeMultiThreadScheduler;
import sima.core.simulation.configuration.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class SimaSimulation {

    // Static.

    private static final Object LOCK = new Object();
    public static Logger SIMA_LOG = LoggerFactory.getLogger(SimaSimulation.class);
    private static SimaSimulation SIMA_SIMULATION;

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

    public static void runSimulation(String configurationJsonPath) throws SimaSimulationFailToStartRunningException {
        Set<Environment> environments;
        Set<AbstractAgent> agents;
        Map<String, BehaviorJson> mapBehaviors;
        Map<String, ProtocolJson> mapProtocols;
        Map<String, Environment> mapEnvironments = new HashMap<>();
        Scheduler scheduler;

        try {
            SimaSimulationJson simaSimulationJson = parseConfiguration(configurationJsonPath);
            environments = createAllEnvironments(simaSimulationJson, mapEnvironments);
            mapBehaviors = extractMapBehaviors(simaSimulationJson);
            mapProtocols = extractMapProtocols(simaSimulationJson);
            agents = createAllAgents(simaSimulationJson, mapBehaviors, mapProtocols, mapEnvironments);
        } catch (Exception e) {
            SIMA_LOG.error("Fail parse SimaSimulation Json configuration file " + configurationJsonPath, e);
            throw new SimaSimulationFailToStartRunningException(e);
        }
    }

    private static Map<String, BehaviorJson> extractMapBehaviors(SimaSimulationJson simaSimulationJson) throws ConfigurationException {
        Map<String, BehaviorJson> mapBehaviors = new HashMap<>();
        for (BehaviorJson behaviorJson : simaSimulationJson.getBehaviors()) {
            mapBehaviors.put(Optional.ofNullable(behaviorJson.getId()).orElseThrow(() -> new ConfigurationException("BehaviorId cannot be null")), behaviorJson);
        }
        return mapBehaviors;
    }

    private static Map<String, ProtocolJson> extractMapProtocols(SimaSimulationJson simaSimulationJson) throws ConfigurationException {
        Map<String, ProtocolJson> mapProtocols = new HashMap<>();
        for (ProtocolJson protocolJson : simaSimulationJson.getProtocols()) {
            mapProtocols.put(Optional.ofNullable(protocolJson.getId()).orElseThrow(() -> new ConfigurationException("ProtocolId cannot be null")), protocolJson);
        }
        return mapProtocols;
    }

    /**
     * Create all agents instance and bind with it all environments, behaviors and protocols which it needs.
     * @param simaSimulationJson the simaSimulationJson
     * @param mapBehaviors the map behaviors
     * @param mapProtocols the map protocols
     * @param mapEnvironments the map environments
     * @return a set which contains all instances of agents created from the configuration file.
     */
    private static @NotNull  Set<AbstractAgent> createAllAgents(SimaSimulationJson simaSimulationJson,
                                                      Map<String, BehaviorJson> mapBehaviors,
                                                      Map<String, ProtocolJson> mapProtocols,
                                                      Map<String, Environment> mapEnvironments)
            throws NoSuchMethodException, InstantiationException,
            ConfigurationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        Set<AbstractAgent> agentSet = new HashSet<>();
        for (AgentJson agentJson : simaSimulationJson.getAgents()) {
            verifyAgentNumberToCreate(agentJson.getNumberToCreate());
            createAgent(agentJson, agentSet, mapBehaviors, mapProtocols, mapEnvironments);
        }

        return null;
    }

    /**
     * Verifies if the numberToCreate specified is greater or equal to 1. If it is not the case, throws an
     * {@link ConfigurationException}, else nothing is done.
     *
     * @param numberToCreate the numberToCreate to verify
     * @throws ConfigurationException if numberToCreate is equal or less to 0.
     */
    private static void verifyAgentNumberToCreate(int numberToCreate) throws ConfigurationException {
        if (numberToCreate < 1)
            throw new ConfigurationException("Cannot have a number create of agent less or equal to 0");
    }

    /**
     * @param agentJson       the json agent configuration
     * @param agents          the set where the agent will be added after be created
     * @param mapBehaviors    the behaviorJson map
     * @param mapProtocols    the protocolJson map
     * @param mapEnvironments the environment map
     */
    private static void createAgent(AgentJson agentJson, Set<AbstractAgent> agents, Map<String, BehaviorJson> mapBehaviors,
                                    Map<String, ProtocolJson> mapProtocols, Map<String, Environment> mapEnvironments)
            throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, ConfigurationException, ClassNotFoundException {

        for (int i = 0; i < agentJson.getNumberToCreate(); i++) {
            AbstractAgent agent = createAgentAndAddInSet(agents, agentJson, i);
            associateAgentAndBehaviors(agent, agentJson, mapBehaviors);
            associateAgentAndProtocol(agent, agentJson, mapProtocols);
            associateAgentAndEnvironments(agent, agentJson, mapEnvironments);
        }
    }

    private static void associateAgentAndBehaviors(AbstractAgent agent, AgentJson agentJson, Map<String, BehaviorJson> mapBehaviors)
            throws ConfigurationException, ClassNotFoundException {

        for (String behaviorId : agentJson.getBehaviors()) {
            BehaviorJson behaviorJson = Optional.ofNullable(mapBehaviors.get(behaviorId))
                    .orElseThrow(() -> new ConfigurationException("BehaviorId " + behaviorId + " not found"));
            addBehaviorToAgent(agent, behaviorJson);
        }
    }

    private static void addBehaviorToAgent(AbstractAgent agent, BehaviorJson behaviorJson)
            throws ClassNotFoundException, ConfigurationException {

        if (!agent.addBehavior(extractClassForName(behaviorJson.getBehaviorClass()), parseArgs(behaviorJson)))
            throw new ConfigurationException("Unable to add behavior " + behaviorJson.getBehaviorClass() + " to agent " + agent);
    }

    private static void associateAgentAndProtocol(AbstractAgent agent, AgentJson agentJson, Map<String, ProtocolJson> mapProtocols)
            throws ConfigurationException, ClassNotFoundException {

        for (String protocolId : agentJson.getBehaviors()) {
            ProtocolJson protocolJson = Optional.ofNullable(mapProtocols.get(protocolId))
                    .orElseThrow(() -> new ConfigurationException("ProtocolId " + protocolId + " not found"));
            addProtocolToAgent(agent, protocolJson);
        }
    }

    private static void addProtocolToAgent(AbstractAgent agent, ProtocolJson protocolJson)
            throws ConfigurationException, ClassNotFoundException {

        if (!agent.addProtocol(extractClassForName(protocolJson.getProtocolClass()), protocolJson.getTag(), parseArgs(protocolJson)))
            throw new ConfigurationException("Unable to add protocol " + protocolJson.getProtocolClass() + " to agent " + agent);
    }

    private static void associateAgentAndEnvironments(AbstractAgent agent, AgentJson agentJson, Map<String, Environment> mapEnvironments)
            throws ConfigurationException {

        for (String environmentId : agentJson.getEnvironments()) {
            Environment environment = Optional.ofNullable(mapEnvironments.get(environmentId))
                    .orElseThrow(() -> new ConfigurationException("EnvironmentId " + environmentId + " not found"));
            agentJoinEnvironment(agent, environment);
        }
    }

    private static void agentJoinEnvironment(AbstractAgent agent, Environment environment) throws ConfigurationException {
        if (!agent.joinEnvironment(environment)) {
            throw new ConfigurationException("Agent " + agent + " unable to join the Environment " + environment);
        }
    }

    @NotNull
    private static AbstractAgent createAgentAndAddInSet(Set<AbstractAgent> agents, AgentJson agentJson, int i)
            throws ClassNotFoundException, ConfigurationException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {

        AbstractAgent agent = createAgent(extractClassForName(agentJson.getAgentClass()), String.format(agentJson.getNamePattern(), i), i, parseArgs(agentJson));
        addAgentInAgentSet(agents, agent);
        return agent;
    }

    private static void addAgentInAgentSet(Set<AbstractAgent> agents, AbstractAgent agent) throws ConfigurationException {
        if (!agents.add(agent))
            throw new ConfigurationException("Fail to add agent is agent set -> Two agent with same hashCode. Agent not added = " + agent);
    }

    private static @NotNull AbstractAgent createAgent(Class<? extends AbstractAgent> agentClass, String agentName,
                                                      int agentNumberId, Map<String, String> args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<? extends AbstractAgent> constructor = agentClass.getConstructor(String.class, Integer.class, Map.class);
        return constructor.newInstance(agentName, agentNumberId, args);
    }

    /**
     * Create all instance of all environments define in the Json configuration file and in the same way, fill the
     * specified mapEnvironment by mapping EnvironmentId with the Environment instance.
     *
     * @param simulationJson  the simulationJson
     * @param mapEnvironments the map of environments which will maps "IdEnvironment" -> "Environment"
     * @return a set which contains all instances of {@link Environment}.
     */
    @NotNull
    private static Set<Environment> createAllEnvironments(SimaSimulationJson simulationJson, Map<String, Environment> mapEnvironments)
            throws ConfigurationException, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {

        Set<Environment> environments = new HashSet<>();
        for (EnvironmentJson environmentJson : simulationJson.getEnvironments()) {
            Environment environment = createEnvironmentAndAddInSet(environments, environmentJson, parseArgs(environmentJson));
            mapEnvironments.put(Optional.ofNullable(environmentJson.getId()).orElseThrow(() -> new ConfigurationException("EnvironmentId cannot be null")), environment);
        }
        return environments;
    }

    private static Environment createEnvironmentAndAddInSet(Set<Environment> environments, EnvironmentJson environmentJson,
                                                            Map<String, String> args)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException {

        Environment environment = createEnvironment(
                extractClassForName(environmentJson.getName()),
                environmentJson.getName(),
                args.isEmpty() ? null : args);
        environments.add(environment);
        return environment;
    }

    private static <T> Class<? extends T> extractClassForName(String className) throws ClassNotFoundException {
        return (Class<? extends T>) Class.forName(className);
    }

    @NotNull
    private static SimaSimulationJson parseConfiguration(String configurationJsonPath) throws IOException {
        return ConfigurationParser.parseConfiguration(configurationJsonPath);
    }

    private static Map<String, String> parseArgs(ArgumentativeJsonObject argumentativeJsonObject)
            throws ConfigurationException {

        Map<String, String> args = new HashMap<>();
        for (List<String> argsCouple : argumentativeJsonObject.getArgs()) {
            if (argsCouple.size() == 2) {
                args.put(Optional.of(argsCouple.get(0)).get(), argsCouple.get(1));
            } else {
                throw new ConfigurationException("Wrong format for argument. In Json a args is an array of only 2 values: the args name and its value");
            }
        }
        return args.isEmpty() ? null : args;
    }

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
                    simaSimulationAddAllAgents(allAgents);
                    simaSimulationAddEnvironments(allEnvironments);
                    simaSimulationStartAllAgents();
                    simaSimulationCreateAndExecuteSimulationSetup(simulationSetupClass);
                    simaSimulationNotifyOnSimulationStarted();
                    simaSimulationStartScheduler();
                    SIMA_LOG.info("SimaSimulation RUN");
                } catch (Exception e) {
                    killSimulation();
                    throw new SimaSimulationFailToStartRunningException(e);
                }
            else {
                SIMA_LOG.error("Simulation already running");
                throw new SimaSimulationFailToStartRunningException(new SimaSimulationAlreadyRunningException());
            }
        }
    }

    /**
     * Run a simulation.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     *
     * @param simulationTimeMode      the simulation time mode
     * @param simulationSchedulerType the simulation scheduler type
     * @param endSimulation           the end of the simulation
     * @param environments            the array of environments classes
     * @param simulationSetupClass    the simulation setup class
     * @see #runSimulation(Scheduler, Set, Set, Class, SimaWatcher)
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
    private static void simaSimulationAddAllAgents(Set<AbstractAgent> allAgents) {
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
            addAgent(Optional.of(agent).get());
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
            if (!addEnvironment(environment))
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
    private static SimulationSetup createSimulationSetup(Class<? extends SimulationSetup> simulationSetupClass)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Constructor<? extends SimulationSetup> simSetupConstructor =
                simulationSetupClass.getConstructor(Map.class);
        return simSetupConstructor.newInstance((Map<String, String>) null);
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
        if (simulationSetupClass != null)
            try {
                SimulationSetup simulationSetup = createSimulationSetup(simulationSetupClass);
                executeSimulationSetup(simulationSetup);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new SimaSimulationFailToStartRunningException("Simulation Setup fail to be instantiate", e);
            }
    }

    private static void executeSimulationSetup(SimulationSetup simulationSetup) {
        simulationSetup.setupSimulation();
        SIMA_LOG.info("SimulationSetup EXECUTED");
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
     * @param args             environment args
     * @return a new instance of the specified {@code Environment} class.
     * @throws NoSuchMethodException     if the environment class does not have the correct constructor
     * @throws InstantiationException    if the class cannot be instantiate
     * @throws IllegalAccessException    if the environment constructor is not accessible
     * @throws InvocationTargetException if the environment construction thrown an exception
     * @throws NullPointerException      if environmentName is nulls
     */
    @NotNull
    private static Environment createEnvironment(Class<? extends Environment> environmentClass, String environmentName,
                                                 Map<String, String> args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<? extends Environment> constructor = environmentClass.getConstructor(String.class,
                Map.class);
        return constructor.newInstance(Optional.of(environmentName).get(), args);
    }

    /**
     * Call the method {@link #createEnvironment(Class, String, Map)} with null arguments.
     *
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
        return createEnvironment(environmentClass, environmentClass.getName(), null);
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
        if (SIMA_SIMULATION != null)
            SIMA_LOG.info("SimaSimulation KILLED");

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
        boolean added = SIMA_SIMULATION.agentManager.addAgent(agent);
        if (added)
            SIMA_LOG.info(agent + " ADDED in SimaSimulation");
        return added;
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
            SIMA_LOG.info(environment + " ADDED in SimaSimulation");
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
     * @param environmentName the environment of the wanted environment
     * @return the environment of the simulation which has the specified name. If no environment is find, returns null.
     */
    private Environment findEnvironment(String environmentName) {
        if (environmentName == null)
            return null;

        return this.environments.get(environmentName);
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
