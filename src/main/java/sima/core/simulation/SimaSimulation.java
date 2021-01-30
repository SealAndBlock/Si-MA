package sima.core.simulation;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.exception.ConfigurationException;
import sima.core.exception.SimaSimulationAlreadyRunningException;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.exception.SimaSimulationIsNotRunningException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.scheduler.Controller;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.scheduler.multithread.RealTimeMultiThreadScheduler;
import sima.core.simulation.configuration.ConfigurationParser;
import sima.core.simulation.configuration.json.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static sima.core.utils.Utils.extractClassForName;
import static sima.core.utils.Utils.instantiate;

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

    /**
     * @param configurationJsonPath the configuration file path
     * @throws SimaSimulationFailToStartRunningException if sima simulation does not success to run
     */
    public static void runSimulation(String configurationJsonPath) throws SimaSimulationFailToStartRunningException {
        SimaSimulationJson simaSimulationJson;
        Set<Environment> allEnvironments;
        Set<AbstractAgent> allAgents;
        Map<String, BehaviorJson> mapBehaviors;
        Map<String, ProtocolJson> mapProtocols;
        Map<String, Environment> mapEnvironments = new HashMap<>();
        Scheduler scheduler;
        Class<? extends SimulationSetup> simulationSetupClass;
        SimaWatcher simaWatcher;

        try {
            simaSimulationJson = parseConfiguration(configurationJsonPath);
            allEnvironments = createAllEnvironments(simaSimulationJson, mapEnvironments);
            mapBehaviors = extractMapBehaviors(simaSimulationJson);
            mapProtocols = extractMapProtocols(simaSimulationJson);
            allAgents = createAllAgents(simaSimulationJson, mapBehaviors, mapProtocols, mapEnvironments);
            scheduler = createScheduler(Scheduler.TimeMode.valueOf(simaSimulationJson.getTimeMode()),
                                        Scheduler.SchedulerType.valueOf(simaSimulationJson.getSchedulerType()),
                                        simaSimulationJson.getNbThreads(), simaSimulationJson.getEndTime(),
                                        createSchedulerWatcherFromClassName(
                                                simaSimulationJson.getSchedulerWatcherClass()));
            extractAndScheduleControllers(scheduler, simaSimulationJson.getControllers());
            simulationSetupClass = simaSimulationJson.getSimulationSetupClass() == null ? null
                    : extractClassForName(simaSimulationJson.getSimulationSetupClass());
            simaWatcher = createSimaWatcherFromClassName(simaSimulationJson.getSimaWatcherClass());
        } catch (Exception e) {
            throw new SimaSimulationFailToStartRunningException(
                    "Fail parse SimaSimulation Json configuration file : " + configurationJsonPath, e);
        }

        runSimulation(scheduler, allAgents, allEnvironments, simulationSetupClass, simaWatcher);
    }

    private static void extractAndScheduleControllers(Scheduler scheduler, List<ControllerJson> controllers)
            throws ConfigurationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
                   InstantiationException, IllegalAccessException {
        if (controllers != null)
            createAndScheduleControllers(scheduler, controllers);
    }

    private static void createAndScheduleControllers(Scheduler scheduler, List<ControllerJson> controllers)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
                   InstantiationException, ConfigurationException {
        for (ControllerJson controllerJson : controllers)
            scheduleController(scheduler, controllerJson,
                               createControllerFromClassName(controllerJson.getControllerClass(),
                                                             parseArgs(controllerJson)));
    }

    private static void scheduleController(Scheduler scheduler, ControllerJson controllerJson, Controller controller) {
        switch (Scheduler.ScheduleMode.valueOf(controllerJson.getScheduleMode())) {
            case ONCE -> scheduler.scheduleExecutableOnce(controller, controllerJson.getBeginAt());
            case REPEATED -> scheduler.scheduleExecutableRepeated(controller, controllerJson.getBeginAt(),
                                                                  controllerJson.getNbRepetitions(),
                                                                  controllerJson.getRepetitionStep());
            case INFINITE -> scheduler.scheduleExecutableInfinitely(controller, controllerJson.getBeginAt(),
                                                                    controllerJson.getRepetitionStep());
        }
    }

    @SuppressWarnings("unchecked")
    private static Controller createControllerFromClassName(String controllerClassName, Map<String, String> args)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
                   InstantiationException {
        return instantiate((Class<? extends Controller>) extractClassForName(controllerClassName),
                           new Class[]{Map.class}, args);
    }

    private static SimaWatcher createSimaWatcherFromClassName(String simaWatcherClassName)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
                   IllegalAccessException {
        if (simaWatcherClassName != null && !simaWatcherClassName.isEmpty()) {
            return createSimaWatcher(extractClassForName(simaWatcherClassName));
        } else
            return null;
    }

    private static SimaWatcher createSimaWatcher(Class<? extends SimaWatcher> simaWatcherClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return instantiate(simaWatcherClass);
    }

    private static Scheduler.SchedulerWatcher createSchedulerWatcherFromClassName(String schedulerWatcherClassName)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
                   InstantiationException {
        if (schedulerWatcherClassName != null && !schedulerWatcherClassName.isEmpty())
            return createSchedulerWatcher(extractClassForName(schedulerWatcherClassName));
        else
            return null;
    }

    private static @NotNull Scheduler.SchedulerWatcher createSchedulerWatcher(
            Class<? extends Scheduler.SchedulerWatcher> schedulerWatcherClass)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return instantiate(schedulerWatcherClass);
    }

    private static @NotNull Map<String, BehaviorJson> extractMapBehaviors(SimaSimulationJson simaSimulationJson)
            throws ConfigurationException {
        Map<String, BehaviorJson> mapBehaviors = new HashMap<>();
        fillMapBehaviors(simaSimulationJson, mapBehaviors);
        return mapBehaviors;
    }

    private static void fillMapBehaviors(SimaSimulationJson simaSimulationJson, Map<String, BehaviorJson> mapBehaviors)
            throws ConfigurationException {
        if (simaSimulationJson.getBehaviors() != null)
            for (BehaviorJson behaviorJson : simaSimulationJson.getBehaviors()) {
                mapBehaviors.put(Optional.ofNullable(behaviorJson.getId())
                                         .orElseThrow(() -> new ConfigurationException("BehaviorId cannot be null")),
                                 behaviorJson);
            }
    }

    private static @NotNull Map<String, ProtocolJson> extractMapProtocols(SimaSimulationJson simaSimulationJson)
            throws ConfigurationException, ClassNotFoundException {
        Map<String, ProtocolJson> mapProtocols = new HashMap<>();
        fillMapProtocols(simaSimulationJson, mapProtocols);
        return mapProtocols;
    }

    private static void fillMapProtocols(SimaSimulationJson simaSimulationJson, Map<String, ProtocolJson> mapProtocols)
            throws ConfigurationException, ClassNotFoundException {
        if (simaSimulationJson.getProtocols() != null)
            for (ProtocolJson protocolJson : simaSimulationJson.getProtocols()) {
                mapProtocols.put(Optional.ofNullable(protocolJson.getId())
                                         .orElseThrow(() -> new ConfigurationException("ProtocolId cannot be null")),
                                 protocolJson);
                simaSimulationJson.linkIdAndObject(protocolJson.getId(),
                                                   new ProtocolIdentifier(
                                                           extractClassForName(protocolJson.getProtocolClass()),
                                                           protocolJson.getTag()));
            }
    }

    /**
     * Create all agents instance and bind with it all environments, behaviors and protocols which it needs.
     *
     * @param simaSimulationJson the simaSimulationJson
     * @param mapBehaviors       the map behaviors
     * @param mapProtocols       the map protocols
     * @param mapEnvironments    the map environments
     * @return a set which contains all instances of agents created from the configuration file.
     */
    private static @NotNull Set<AbstractAgent> createAllAgents(SimaSimulationJson simaSimulationJson,
                                                               Map<String, BehaviorJson> mapBehaviors,
                                                               Map<String, ProtocolJson> mapProtocols,
                                                               Map<String, Environment> mapEnvironments)
            throws NoSuchMethodException, InstantiationException, ConfigurationException, IllegalAccessException,
                   InvocationTargetException, ClassNotFoundException, NoSuchFieldException {
        Set<AbstractAgent> agentSet = new HashSet<>();
        createAndAssociateAllAgents(simaSimulationJson, mapBehaviors, mapProtocols, mapEnvironments, agentSet);
        return agentSet;
    }

    private static void createAndAssociateAllAgents(SimaSimulationJson simaSimulationJson,
                                                    Map<String, BehaviorJson> mapBehaviors,
                                                    Map<String, ProtocolJson> mapProtocols,
                                                    Map<String, Environment> mapEnvironments,
                                                    Set<AbstractAgent> agentSet)
            throws ConfigurationException, NoSuchMethodException, InstantiationException, NoSuchFieldException,
                   IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        if (simaSimulationJson.getAgents() != null) {
            AtomicInteger counter = new AtomicInteger(0);
            for (AgentJson agentJson : simaSimulationJson.getAgents()) {
                verifyAgentNumberToCreate(agentJson.getNumberToCreate());
                createAgent(agentJson, agentSet, mapBehaviors, mapProtocols, mapEnvironments, counter,
                            simaSimulationJson);
            }
        }
    }

    /**
     * Verifies if the numberToCreate specified is greater or equal to 1. If it is not the case, throws an {@link
     * ConfigurationException}, else nothing is done.
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
    private static void createAgent(AgentJson agentJson, Set<AbstractAgent> agents,
                                    Map<String, BehaviorJson> mapBehaviors, Map<String, ProtocolJson> mapProtocols,
                                    Map<String, Environment> mapEnvironments, AtomicInteger counter,
                                    SimaSimulationJson simaSimulationJson)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException,
                   ConfigurationException, ClassNotFoundException, NoSuchFieldException {

        for (int i = 0; i < agentJson.getNumberToCreate(); i++) {
            AbstractAgent agent = createAgentAndAddInSet(agents, agentJson, i, counter.getAndIncrement());
            associateAgentAndEnvironments(agent, agentJson, mapEnvironments);
            associateAgentAndBehaviors(agent, agentJson, mapBehaviors);
            associateAgentAndProtocol(agent, agentJson, mapProtocols, simaSimulationJson);
        }
    }

    private static void associateAgentAndBehaviors(AbstractAgent agent, AgentJson agentJson,
                                                   Map<String, BehaviorJson> mapBehaviors)
            throws ConfigurationException, ClassNotFoundException {

        if (agentJson.getBehaviors() != null)
            for (String behaviorId : agentJson.getBehaviors()) {
                BehaviorJson behaviorJson = Optional.ofNullable(mapBehaviors.get(behaviorId))
                        .orElseThrow(() -> new ConfigurationException("BehaviorId " + behaviorId + " not found"));
                addBehaviorToAgent(agent, behaviorJson);
            }
    }

    private static void addBehaviorToAgent(AbstractAgent agent, BehaviorJson behaviorJson)
            throws ClassNotFoundException, ConfigurationException {

        if (!agent.addBehavior(extractClassForName(behaviorJson.getBehaviorClass()), parseArgs(behaviorJson)))
            throw new ConfigurationException(
                    "Unable to add behavior " + behaviorJson.getBehaviorClass() + " to agent " + agent);
    }

    private static void associateAgentAndProtocol(AbstractAgent agent, AgentJson agentJson,
                                                  Map<String, ProtocolJson> mapProtocols,
                                                  SimaSimulationJson simaSimulationJson)
            throws ConfigurationException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {

        if (agentJson.getProtocols() != null) {
            addAllProtocolsToAgent(agent, agentJson, mapProtocols);
            linkProtocolDependencies(agent, agentJson, mapProtocols, simaSimulationJson);
        }
    }

    private static void addAllProtocolsToAgent(AbstractAgent agent, AgentJson agentJson,
                                               Map<String, ProtocolJson> mapProtocols)
            throws ConfigurationException, ClassNotFoundException {
        for (String protocolId : agentJson.getProtocols()) {
            ProtocolJson protocolJson = Optional.ofNullable(mapProtocols.get(protocolId))
                    .orElseThrow(() -> new ConfigurationException("ProtocolId " + protocolId + " not found"));
            addProtocolToAgent(agent, protocolJson);
        }
    }

    private static void linkProtocolDependencies(AbstractAgent agent, AgentJson agentJson,
                                                 Map<String, ProtocolJson> mapProtocols,
                                                 SimaSimulationJson simaSimulationJson)
            throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        for (String protocolId : agentJson.getProtocols()) {
            ProtocolJson protocolJson = mapProtocols.get(protocolId);
            Map<String, String> mapProtocolDependencies = protocolJson.getProtocolDependencies();
            if (mapProtocolDependencies != null && !mapProtocolDependencies.isEmpty())
                for (Map.Entry<String, String> entry : mapProtocolDependencies.entrySet()) {
                    String attributeName = entry.getKey();
                    String dependenceId = entry.getValue();
                    Object dependenceInstance = simaSimulationJson.getInstanceFromId(dependenceId, agent);
                    Protocol protocol = agent.getProtocol(protocolJson.extractProtocolIdentifier());
                    linkProtocolAttributeDependencies(protocol, attributeName, dependenceInstance);
                }
        }
    }

    /**
     * Try to set the value to the attribute.
     *
     * @param protocol the protocol to set attribute value
     * @param attributeName the protocol attribute name
     * @param dependenceInstance the value to set to the protocol attribute
     * @throws NoSuchFieldException if the attribute name does not correspond to a field of the class
     * @throws IllegalAccessException if the attribute is not accessible in set operation
     */
    private static void linkProtocolAttributeDependencies(Protocol protocol, String attributeName,
                                                          Object dependenceInstance)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = protocol.getClass().getDeclaredField(attributeName);
        field.setAccessible(true);
        field.set(protocol, dependenceInstance);
    }

    private static void addProtocolToAgent(AbstractAgent agent, ProtocolJson protocolJson)
            throws ConfigurationException, ClassNotFoundException {

        if (!agent.addProtocol(extractClassForName(protocolJson.getProtocolClass()), protocolJson.getTag(),
                               parseArgs(protocolJson)))
            throw new ConfigurationException(
                    "Unable to add protocol " + protocolJson.getProtocolClass() + " to agent " + agent);
    }

    private static void associateAgentAndEnvironments(AbstractAgent agent, AgentJson agentJson,
                                                      Map<String, Environment> mapEnvironments)
            throws ConfigurationException {

        if (agentJson.getEnvironments() != null)
            for (String environmentId : agentJson.getEnvironments()) {
                Environment environment = Optional.ofNullable(mapEnvironments.get(environmentId))
                        .orElseThrow(() -> new ConfigurationException("EnvironmentId " + environmentId + " not found"));
                agentJoinEnvironment(agent, environment);
            }
    }

    private static void agentJoinEnvironment(AbstractAgent agent, Environment environment)
            throws ConfigurationException {
        if (!agent.joinEnvironment(environment)) {
            throw new ConfigurationException("Agent " + agent + " unable to join the Environment " + environment);
        }
    }

    private static @NotNull AbstractAgent createAgentAndAddInSet(Set<AbstractAgent> agents, AgentJson agentJson,
                                                                 int agentSequenceId, int agentUniqueId)
            throws ClassNotFoundException, ConfigurationException, InvocationTargetException, NoSuchMethodException,
                   InstantiationException, IllegalAccessException {

        AbstractAgent agent = createAgent(extractClassForName(agentJson.getAgentClass()),
                                          String.format(agentJson.getNamePattern(), agentSequenceId), agentSequenceId,
                                          agentUniqueId, parseArgs(agentJson));
        addAgentInAgentSet(agents, agent);
        return agent;
    }

    private static void addAgentInAgentSet(Set<AbstractAgent> agents, AbstractAgent agent)
            throws ConfigurationException {
        if (!agents.add(agent))
            throw new ConfigurationException(
                    "Fail to add agent is agent set -> Two agent with same hashCode. Agent not added = " + agent);
    }

    private static @NotNull AbstractAgent createAgent(Class<? extends AbstractAgent> agentClass, String agentName,
                                                      int agentSequenceId, int agentUniqueId, Map<String, String> args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return instantiate(agentClass, new Class[]{String.class, int.class, int.class, Map.class}, agentName,
                           agentSequenceId, agentUniqueId, args);
    }

    /**
     * Create all instance of all environments define in the Json configuration file and in the same way, fill the
     * specified mapEnvironment by mapping EnvironmentId with the Environment instance.
     *
     * @param simulationJson  the simulationJson
     * @param mapEnvironments the map of environments which will maps "IdEnvironment" -> "Environment"
     * @return a set which contains all instances of {@link Environment}.
     */
    private static @NotNull Set<Environment> createAllEnvironments(SimaSimulationJson simulationJson,
                                                                   Map<String, Environment> mapEnvironments)
            throws ConfigurationException, ClassNotFoundException, NoSuchMethodException, InstantiationException,
                   IllegalAccessException, InvocationTargetException {

        Set<Environment> environments = new HashSet<>();
        createAndAddInSetAndMapEnvironment(simulationJson, mapEnvironments, environments);
        return environments;
    }

    private static void createAndAddInSetAndMapEnvironment(SimaSimulationJson simulationJson,
                                                           Map<String, Environment> mapEnvironments,
                                                           Set<Environment> environments)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
                   IllegalAccessException, ConfigurationException {
        if (simulationJson.getEnvironments() != null && !simulationJson.getEnvironments().isEmpty())
            for (EnvironmentJson environmentJson : simulationJson.getEnvironments()) {
                Environment environment =
                        createEnvironmentAndAddInSet(environments, environmentJson, parseArgs(environmentJson));
                mapEnvironments.put(Optional.ofNullable(environmentJson.getId())
                                            .orElseThrow(
                                                    () -> new ConfigurationException("EnvironmentId cannot be null")),
                                    environment);
                simulationJson.linkIdAndObject(environmentJson.getId(), environment);
            }
        else
            throw new ConfigurationException("The simulation need at least one environment");
    }

    private static @NotNull Environment createEnvironmentAndAddInSet(Set<Environment> environments,
                                                                     EnvironmentJson environmentJson,
                                                                     Map<String, String> args)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
                   IllegalAccessException, ConfigurationException {

        Environment environment =
                createEnvironment(extractClassForName(environmentJson.getEnvironmentClass()), environmentJson.getName(),
                                  args != null ? (args.isEmpty() ? null : args) : null);
        if (environments.add(environment))
            return environment;
        else
            throw new ConfigurationException("Two environments with the same hashCode (Probably due to the fact that "
                                                     + "they have the same name). Problematic Environment = "
                                                     + environment);
    }

    private static @NotNull SimaSimulationJson parseConfiguration(String configurationJsonPath) throws IOException {
        return ConfigurationParser.parseConfiguration(configurationJsonPath);
    }

    private static Map<String, String> parseArgs(ArgumentativeObjectJson argumentativeObjectJson)
            throws ConfigurationException {

        Map<String, String> args = new HashMap<>();
        if (argumentativeObjectJson.getArgs() != null)
            for (List<String> argsCouple : argumentativeObjectJson.getArgs())
                if (argsCouple.size() == 2)
                    args.put(Optional.of(argsCouple.get(0)).get(), argsCouple.get(1));
                else
                    throw new ConfigurationException(
                            "Wrong format for argument. In Json a args is an array of only 2 values: the args name and its value");

        return args.isEmpty() ? null : args;
    }

    /**
     * Try to run a Simulation. All instances of needed to run a simulation must be create and pass in argument. In that
     * way, this method only make the start of the simulation.
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
                                     Class<? extends SimulationSetup> simulationSetupClass, SimaWatcher simaWatcher)
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
     * Add all environments contained in the specified set. If several environment have the same name, throws a {@link
     * IllegalArgumentException}.
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
    private static @NotNull SimulationSetup createSimulationSetup(Class<? extends SimulationSetup> simulationSetupClass)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return instantiate(simulationSetupClass, new Class[]{Map.class}, (Map<String, String>) null);
    }

    /**
     * Create a new instance of the specified {@link SimulationSetup} class and call the method {@link
     * SimulationSetup#setupSimulation()}.
     *
     * @param simulationSetupClass the class of the SimulationSetup
     * @throws SimaSimulationFailToStartRunningException if problem during the instantiation of the simulation setup
     */
    private static void simaSimulationCreateAndExecuteSimulationSetup(
            Class<? extends SimulationSetup> simulationSetupClass) throws SimaSimulationFailToStartRunningException {

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
        SIMA_LOG.info("SimulationSetup " + simulationSetup.getClass() + " EXECUTED");
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
     * @param environmentClass the environment class
     * @param args             environment args
     * @return a new instance of the specified {@code Environment} class.
     * @throws NoSuchMethodException     if the environment class does not have the correct constructor
     * @throws InstantiationException    if the class cannot be instantiate
     * @throws IllegalAccessException    if the environment constructor is not accessible
     * @throws InvocationTargetException if the environment construction thrown an exception
     * @throws NullPointerException      if environmentName is nulls
     */
    private static @NotNull Environment createEnvironment(Class<? extends Environment> environmentClass,
                                                          String environmentName,
                                                          Map<String, String> args)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return instantiate(environmentClass, new Class[]{String.class, Map.class}, environmentName, args);
    }

    private static @NotNull Scheduler createScheduler(Scheduler.TimeMode simulationTimeMode,
                                                      Scheduler.SchedulerType simulationSchedulerType,
                                                      int nbExecutorThread, long endSimulation,
                                                      Scheduler.SchedulerWatcher... schedulerWatchers) {
        if (simulationTimeMode == null)
            throw new NullPointerException("Null simulationTimeMode");

        if (simulationSchedulerType == null)
            throw new NullPointerException("Null simulationSchedulerType");

        Scheduler scheduler = null;
        switch (simulationTimeMode) {
            case REAL_TIME -> scheduler =
                    createRealTimeScheduler(simulationSchedulerType, nbExecutorThread, endSimulation);
            case DISCRETE_TIME -> scheduler =
                    createDiscreteTimeScheduler(simulationSchedulerType, nbExecutorThread, endSimulation);
        }

        for (Scheduler.SchedulerWatcher schedulerWatcher : schedulerWatchers) {
            scheduler.addSchedulerWatcher(schedulerWatcher);
        }

        return scheduler;
    }

    private static @NotNull Scheduler createDiscreteTimeScheduler(Scheduler.SchedulerType simulationSchedulerType,
                                                                  int nbExecutorThread, long endSimulation) {
        Scheduler scheduler = null;
        switch (simulationSchedulerType) {
            case MONO_THREAD -> throw new UnsupportedOperationException("Discrete Time Mono thread simulation" +
                                                                                " unsupported.");
            case MULTI_THREAD -> scheduler = new DiscreteTimeMultiThreadScheduler(endSimulation, nbExecutorThread);
        }
        return scheduler;
    }

    private static @NotNull Scheduler createRealTimeScheduler(Scheduler.SchedulerType simulationSchedulerType,
                                                              int nbExecutorThread, long endSimulation) {
        Scheduler scheduler = null;
        switch (simulationSchedulerType) {
            case MONO_THREAD -> throw new UnsupportedOperationException("Real Time Mono thread simulation" +
                                                                                " unsupported.");
            case MULTI_THREAD -> scheduler = new RealTimeMultiThreadScheduler(endSimulation, nbExecutorThread);
        }
        return scheduler;
    }

    /**
     * Kill the Simulation. After this call, the call of the method {@code runSimulation} is possible.
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
        if (SIMA_SIMULATION.agentManager.addAgent(agent)) {
            SIMA_LOG.info(agent + " ADDED in SimaSimulation");
            return true;
        } else
            return false;
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
     * Verifies if the SimaSimulation is running, if it is not the case, throws a {@link
     * SimaSimulationIsNotRunningException}.
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
         * Call back method, called when the simulation is killed with the method {@link
         * SimaSimulation#killSimulation()}.
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
