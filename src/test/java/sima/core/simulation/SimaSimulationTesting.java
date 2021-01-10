package sima.core.simulation;

import sima.core.environment.Environment;
import sima.core.scheduler.Scheduler;

import java.util.Map;
import java.util.Optional;

/**
 * Class use to create simulation for tests.
 */
public class SimaSimulationTesting extends SimaSimulation {

    // Methods.

    /**
     * Run a simulation by specifying all fields of a simulation.
     * <p>
     * Start the scheduler and the simulation.
     *
     * @param agentManager the agent manager
     * @param scheduler    the scheduler
     * @param timeMode     the time mode
     * @param environments the environments
     * @param simaWatcher  the siam watcher
     * @throws NullPointerException if one or more fields is/are null except for simaWatcher
     */
    public static void runTestingSimulation(AgentManager agentManager, Scheduler scheduler, Scheduler.TimeMode timeMode,
                                            Map<String, Environment> environments, SimaWatcher simaWatcher) {
        synchronized (LOCK) {
            // Create the singleton.
            createNewSingletonInstance();

            SIMA_SIMULATION.agentManager = Optional.of(agentManager).get();
            SIMA_SIMULATION.scheduler = Optional.of(scheduler).get();
            SIMA_SIMULATION.timeMode = Optional.of(timeMode).get();
            SIMA_SIMULATION.environments = Optional.of(environments).get();

            // Add a SimaWatcher.
            SIMA_SIMULATION.simaWatcher = new SimaSimulationWatcher();

            if (simaWatcher != null)
                SIMA_SIMULATION.simaWatcher.addSimaWatcher(simaWatcher);

            SIMA_SIMULATION.scheduler.start();

            SIMA_SIMULATION.simaWatcher.simulationStarted();
        }
    }

}
