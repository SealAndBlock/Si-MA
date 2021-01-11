package sima.core.simulation;

import sima.core.agent.AbstractAgent;
import sima.core.environment.Environment;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.scheduler.Scheduler;

import java.util.Set;

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
     * @param scheduler    the scheduler
     * @param agents       the agent set
     * @param environments the environment set
     * @param simaWatcher  the siam watcher
     * @throws NullPointerException if one or more fields is/are null except for simaWatcher
     */
    public static void runTestingSimulation(Scheduler scheduler, Set<AbstractAgent> agents,
                                            Set<Environment> environments, SimaWatcher simaWatcher)
            throws SimaSimulationFailToStartRunningException {
        runSimulation(scheduler, agents, environments, null, simaWatcher);
    }

}
