package sima.core.simulation;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.exception.EnvironmentConstructionException;
import sima.core.exception.SimaSimulationAlreadyRunningException;
import sima.core.exception.SimulationSetupConstructionException;
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

    protected SimulationSchedulerWatcher mainSchedulerWatcher;

    protected SimaSimulationWatcher simaWatcher;

    protected Scheduler.TimeMode timeMode;

    protected AgentManager agentManager;

    protected Scheduler scheduler;

    protected Map<String, Environment> environments;

    // Constructors.

    protected SimaSimulation() {
    }

    // Methods.

    /**
     * Create a new instance of {@link SimaSimulation} only if there is no instance of it.
     *
     * @throws SimaSimulationAlreadyRunningException if there already is a instance of SimaSimulation
     */
    protected static void createNewSingletonInstance() {
        synchronized (LOCK) {
            // Create the singleton.
            if (SIMA_SIMULATION == null)
                SIMA_SIMULATION = new SimaSimulation();
            else
                throw new SimaSimulationAlreadyRunningException();
        }
    }

    public static void runSimulation(Scheduler scheduler, Set<AbstractAgent> allAgents,
                                     Set<Environment> allEnvironments,
                                     Class<? extends SimulationSetup> simulationSetupClass,
                                     SimaWatcher simaWatcher) {
        // TODO
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
     */
    public static void runSimulation(Scheduler.TimeMode simulationTimeMode, Scheduler.SchedulerType simulationSchedulerType,
                                     long endSimulation,
                                     List<Class<? extends Environment>> environments,
                                     Class<? extends SimulationSetup> simulationSetupClass,
                                     Scheduler.SchedulerWatcher schedulerWatcher,
                                     SimaWatcher simaWatcher) {
        synchronized (LOCK) {
            // Create the singleton.
            createNewSingletonInstance();

            // Add a SimaWatcher.
            SIMA_SIMULATION.simaWatcher = new SimaSimulationWatcher();

            if (simaWatcher != null)
                SIMA_SIMULATION.simaWatcher.addSimaWatcher(simaWatcher);

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

            if (schedulerWatcher != null)
                SIMA_SIMULATION.mainSchedulerWatcher.addSchedulerWatcher(schedulerWatcher);

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

            // Create the SimSetup and calls the method setup.
            if (simulationSetupClass != null)
                try {
                    Constructor<? extends SimulationSetup> simSetupConstructor =
                            simulationSetupClass.getConstructor(Map.class);
                    SimulationSetup simulationSetup = simSetupConstructor.newInstance((Map<String, String>) null);
                    simulationSetup.setupSimulation();
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    SimaSimulation.killSimulation();
                    throw new SimulationSetupConstructionException(e);
                }

            // Notify simulation watchers.
            SIMA_SIMULATION.simaWatcher.simulationStarted();

            // Start the scheduler ad the end (ORDER VERY IMPORTANT)
            SIMA_SIMULATION.scheduler.start();
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
            if (SimaSimulation.simulationIsRunning()) {
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
            if (SimaSimulation.simulationIsRunning())
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
    public static boolean simulationIsRunning() {
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

            SimaSimulation.killSimulation();
        }

        @Override
        public void simulationEndTimeReach() {
            this.otherWatchers.forEach(Scheduler.SchedulerWatcher::simulationEndTimeReach);

            SimaSimulation.killSimulation();
        }

        @Override
        public void noExecutableToExecute() {
            this.otherWatchers.forEach(Scheduler.SchedulerWatcher::noExecutableToExecute);

            SimaSimulation.killSimulation();
        }
    }
}
