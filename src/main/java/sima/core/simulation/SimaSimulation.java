package sima.core.simulation;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.exception.EnvironmentConstructionException;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.exception.SimaSimulationSetupConstructionException;
import sima.core.exception.TwoAgentWithSameIdentifierException;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.scheduler.multithread.RealTimeMultiThreadScheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SimaSimulation {

    // Constants.

    /**
     * The number of thread of the {@link DiscreteTimeMultiThreadScheduler}.
     */
    private static final int NB_THREAD_MULTI_THREAD_SCHEDULER = 8;

    // Singleton.

    protected static SimaSimulation SIMA_SIMULATION;

    protected static final Object LOCK = new Object();

    // Constants

    // Variables

    protected Scheduler scheduler;
    protected Scheduler.TimeMode timeMode;
    protected SimulationSchedulerWatcher mainSchedulerWatcher;

    protected AgentManager agentManager;

    protected Map<String, Environment> environments;

    protected SimaSimulationWatcher simaWatcher;

    // Constructors.

    protected SimaSimulation() {
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
            if (simaSimulationIsRunning())
                try {
                    createNewSimaSimulationSingletonInstance();
                    simaSimulationAddSimaWatcher(simaWatcher);
                    simaSimulationSetScheduler(scheduler);
                    simaSimulationAddAgents(allAgents);
                    simaSimulationAddEnvironments(allEnvironments);
                    simaSimulationCreateAndExecuteSimulationSetup(simulationSetupClass);
                    simaSimulationStartScheduler();
                    simaSimulationNotifyOnSimulationStarted();
                } catch (Exception e) {
                    throw new SimaSimulationFailToStartRunningException(e);
                }
        }
    }

    /**
     * Create a new instance of {@link SimaSimulation} only if there is no instance of it.
     * <p>
     * <strong>WARNING!</strong> This method is not thread safe.
     */
    protected static void createNewSimaSimulationSingletonInstance() {
        // Create the singleton.
        if (SIMA_SIMULATION == null)
            SIMA_SIMULATION = new SimaSimulation();

    }

    protected static void simaSimulationAddSimaWatcher(SimaWatcher simaWatcher) {
        if (simaWatcher != null)
            SIMA_SIMULATION.simaWatcher.addSimaWatcher(simaWatcher);
    }

    /**
     * @param scheduler the scheduler to set to the simulation
     * @throws NullPointerException if the scheduler is null
     */
    protected static void simaSimulationSetScheduler(Scheduler scheduler) {
        SIMA_SIMULATION.scheduler = Optional.of(scheduler).get();
        SIMA_SIMULATION.timeMode = SIMA_SIMULATION.scheduler.getTimeMode();
        SIMA_SIMULATION.mainSchedulerWatcher = new SimulationSchedulerWatcher();
        SIMA_SIMULATION.scheduler.addSchedulerWatcher(SIMA_SIMULATION.mainSchedulerWatcher);
    }

    /**
     * Create a new instance of {@link #agentManager} and add all agents in the specified set in the agent manager.
     *
     * @param allAgents the set of agents to add.
     * @throws NullPointerException if the specified set is null or if one agent is null.
     */
    protected static void simaSimulationAddAgents(Set<AbstractAgent> allAgents) {
        SIMA_SIMULATION.agentManager = new LocalAgentManager();
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
    protected static void simaSimulationAddEnvironments(Set<Environment> allEnvironments) {
        SIMA_SIMULATION.environments = new HashMap<>();
        for (Environment environment : allEnvironments) {
            if (!SIMA_SIMULATION.environments.containsKey(environment.getEnvironmentName())) {
                SIMA_SIMULATION.environments.put(environment.getEnvironmentName(), environment);
            } else {
                throw new IllegalArgumentException("Two environments with the same name. The problematic name : "
                        + environment.getEnvironmentName());
            }
        }
    }

    /**
     * Try to create a new instance of the {@link SimulationSetup} specified class. if the instantiation failed, kill
     * the simulation by calling the method {@link #killSimulation()} and returns null.
     *
     * @param simulationSetupClass the class of the SimulationSetup
     * @return a new instance of the {@link SimulationSetup} specified class. If the instantiation failed, returns null.
     */
    @NotNull
    protected static SimulationSetup constructSimulationSetup(Class<? extends SimulationSetup> simulationSetupClass) {
        try {
            Constructor<? extends SimulationSetup> simSetupConstructor =
                    simulationSetupClass.getConstructor(Map.class);
            return simSetupConstructor.newInstance((Map<String, String>) null);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            SimaSimulation.killSimulation();
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
    protected static void simaSimulationCreateAndExecuteSimulationSetup(Class<? extends SimulationSetup> simulationSetupClass)
            throws SimaSimulationFailToStartRunningException {
        if (simulationSetupClass != null) {
            SimulationSetup simulationSetup = constructSimulationSetup(simulationSetupClass);
            if (simulationSetup == null)
                throw new SimaSimulationFailToStartRunningException("Simulation Setup fail to be instantiate");
            simulationSetup.setupSimulation();
        }
    }

    protected static void simaSimulationNotifyOnSimulationStarted() {
        SIMA_SIMULATION.simaWatcher.simulationStarted();
    }

    protected static void simaSimulationStartScheduler() {
        SIMA_SIMULATION.scheduler.start();
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
     * @deprecated use {@link #runSimulation(Scheduler, Set, Set, Class, SimaWatcher)}
     */
    public static void runSimulation(Scheduler.TimeMode simulationTimeMode, Scheduler.SchedulerType simulationSchedulerType,
                                     long endSimulation,
                                     List<Class<? extends Environment>> environments,
                                     Class<? extends SimulationSetup> simulationSetupClass,
                                     Scheduler.SchedulerWatcher schedulerWatcher,
                                     SimaWatcher simaWatcher) {
        synchronized (LOCK) {
            // Create the singleton.
            createNewSimaSimulationSingletonInstance();

            // Add a SimaWatcher.
            SIMA_SIMULATION.simaWatcher = new SimaSimulationWatcher();

            simaSimulationAddSimaWatcher(simaWatcher);

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
            SIMA_SIMULATION.mainSchedulerWatcher = new SimulationSchedulerWatcher();
            SIMA_SIMULATION.scheduler.addSchedulerWatcher(schedulerWatcher);
            SIMA_SIMULATION.scheduler.addSchedulerWatcher(SIMA_SIMULATION.mainSchedulerWatcher);

            // Create and add environments.
            if (environments == null || environments.size() < 1) {
                SimaSimulation.killSimulation();
                throw new IllegalArgumentException("The simulation need to have at least 1 environments");
            }

            SIMA_SIMULATION.environments = new HashMap<>();
            for (Class<? extends Environment> environmentClass : environments) {
                try {
                    Constructor<? extends Environment> constructor = environmentClass.getConstructor(String.class,
                            Map.class);
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

            // Create the SimaSetup and calls the method setup.
            if (simulationSetupClass != null) {
                SimulationSetup simulationSetup = constructSimulationSetup(simulationSetupClass);
                if (simulationSetup == null)
                    throw new SimaSimulationSetupConstructionException();
                simulationSetup.setupSimulation();
            }

            // Notify simulation watchers.
            simaSimulationNotifyOnSimulationStarted();

            // Start the scheduler ad the end (ORDER VERY IMPORTANT)
            simaSimulationStartScheduler();
        }
    }

    /**
     * Kill the Simulation. After this call, the call of the method
     * {@link #runSimulation(Scheduler.TimeMode, Scheduler.SchedulerType, long, List, Class, Scheduler.SchedulerWatcher, SimaWatcher)}  is
     * possible.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     */
    public static void killSimulation() {
        synchronized (LOCK) {
            if (SimaSimulation.simaSimulationIsRunning()) {
                if (SIMA_SIMULATION != null && SIMA_SIMULATION.scheduler != null)
                    SIMA_SIMULATION.scheduler.kill();

                if (SIMA_SIMULATION != null && SIMA_SIMULATION.simaWatcher != null)
                    SIMA_SIMULATION.simaWatcher.simulationKilled();

                LOCK.notifyAll();

                SIMA_SIMULATION = null;
            }
        }
    }

    /**
     * Block until the simulation is kill. In other words, wait until the method {@link #killSimulation()} be called.
     * <p>
     * This method is thread safe and synchronized on the lock {@link #LOCK}.
     */
    public static void waitKillSimulation() {
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
        return SIMA_SIMULATION.environments.keySet().stream().collect(ArrayList::new, (list, s) -> list.add(SIMA_SIMULATION.environments.get(s)), ArrayList::addAll);
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

    public static Scheduler.TimeMode timeMode() {
        return SIMA_SIMULATION.timeMode;
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

    protected static class SimaSimulationWatcher implements SimaWatcher {

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
