package sima.core.simulation;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.SchedulerWatcherTesting;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class SimaSimulationTest extends SimaTest {

    // Static.

    private static long END_SIMULATION = 1_000;
    private static int NB_EXECUTOR_THREAD = 8;
    private static Scheduler SCHEDULER;
    private static Scheduler.SchedulerWatcher SCHEDULER_WATCHER;

    private static AbstractAgent A_0;
    private static AbstractAgent A_1;

    private static Environment ENV_0;

    private static Set<AbstractAgent> ALL_AGENTS;
    private static Set<Environment> ALL_ENVIRONMENTS;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        SCHEDULER = new DiscreteTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREAD);
        SCHEDULER_WATCHER = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(SCHEDULER_WATCHER);

        A_0 = new AgentTesting("A_0", 0, null);
        A_1 = new AgentTesting("A_1", 1, null);

        ENV_0 = new EnvironmentTesting(0);

        ALL_AGENTS = new HashSet<>();
        ALL_AGENTS.add(A_0);
        ALL_AGENTS.add(A_1);

        ALL_ENVIRONMENTS = new HashSet<>();
        ALL_ENVIRONMENTS.add(ENV_0);
    }

    // Tests.

    @Test
    public void runSimulationWithNullSchedulerThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(null, ALL_AGENTS, ALL_ENVIRONMENTS,
                        SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithNotNullSchedulerNotFail() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void runSimulationWithNullAllAgentsNotFail() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, null, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void runSimulationWithEmptyAllAgentsNotFail() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, new HashSet<>(), ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void runSimulationWithNullAllEnvironmentsThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, null,
                        SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithEmptyAllEnvironmentsThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, new HashSet<>(),
                        SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithAllEnvironmentsNotNullAndNotEmptyNotFail() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void runSimulationWithNullSimulationSetupClassNotFail() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    null, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void runSimulationWithNullSimaWatcherNotFail() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void runSimulationStartAndStopDirectlyWithEmptyScheduler() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null);

        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

}
