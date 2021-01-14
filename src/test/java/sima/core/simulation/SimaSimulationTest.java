package sima.core.simulation;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.scheduler.LongTimeExecutableTesting;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.SchedulerWatcherTesting;
import sima.core.scheduler.WaitSchedulerWatcher;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SimaSimulationTest extends SimaTest {

    // Static.

    private static final long END_SIMULATION = 1_000;
    private static final int NB_EXECUTOR_THREAD = 8;
    private static Scheduler SCHEDULER;
    private static SchedulerWatcherTesting SCHEDULER_WATCHER;
    private static WaitSchedulerWatcher WAIT_SCHEDULER_WATCHER;

    private static AbstractAgent A_0;
    private static AbstractAgent A_1;
    private static Set<AbstractAgent> ALL_AGENTS;

    private static Environment ENV_0;
    private static Set<Environment> ALL_ENVIRONMENTS;
    private static Set<Environment> SAME_NAME_ENVIRONMENT_SET;

    private static SimaWatcherTesting SIMA_WATCHER;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        SCHEDULER = new DiscreteTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREAD);
        SCHEDULER_WATCHER = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(SCHEDULER_WATCHER);
        WAIT_SCHEDULER_WATCHER = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(WAIT_SCHEDULER_WATCHER);

        A_0 = new AgentTesting("A_0", 0, null);
        A_1 = new AgentTesting("A_1", 1, null);

        ALL_AGENTS = new HashSet<>();
        ALL_AGENTS.add(A_0);
        ALL_AGENTS.add(A_1);

        ENV_0 = new EnvironmentTesting(0);

        ALL_ENVIRONMENTS = new HashSet<>();
        ALL_ENVIRONMENTS.add(ENV_0);

        Environment sameNameEnv0 = new EnvironmentTesting(1);
        Environment sameNameEnv1 = new EnvironmentTesting(1);
        SAME_NAME_ENVIRONMENT_SET = new HashSet<>();
        SAME_NAME_ENVIRONMENT_SET.add(sameNameEnv0);
        SAME_NAME_ENVIRONMENT_SET.add(sameNameEnv1);

        SIMA_WATCHER = new SimaWatcherTesting();

        SimaSimulation.waitEndSimulation();
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
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithNullAllAgentsNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, null, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithEmptyAllAgentsNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, new HashSet<>(), ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, null));
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
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithSetWhichContainsSeveralEnvironmentWithSameNameThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, SAME_NAME_ENVIRONMENT_SET,
                        SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithNullSimulationSetupClassNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                null, null));
    }

    @Test
    public void runSimulationWithSimulationSetupWhichHasNotCorrectConstructorThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                        WrongSimulationSetup.class, null));
    }

    @Test
    public void runSimulationWithNullSimaWatcherNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, null));
    }

    @Test
    public void runSimulationWithNotNullSimaWatcherNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER));
    }

    @Test
    public void runSimulationStartAndStopDirectlyWithEmptyScheduler() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER);

            assertFalse(SimaSimulation.simaSimulationIsRunning());
            assertEquals(1, SIMA_WATCHER.getPassToOnSimStarted());
            assertEquals(1, SIMA_WATCHER.getPassToInSimKilled());
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void simaSimulationIsRunningReturnsFalseIfNoSimaSimulationHasBeenRun() {
        assertFalse(SimaSimulation.simaSimulationIsRunning());
    }

    @Test
    public void simaSimulationIsRunningReturnsTrueIfSimaSimulationIsRunning() {
        scheduleLongExecutable();
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER);
            assertTrue(SimaSimulation.simaSimulationIsRunning());
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    private void scheduleLongExecutable() {
        SCHEDULER.scheduleExecutable(new LongTimeExecutableTesting(), Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
    }

    @Test
    public void waitEndSimulationWaitUntilTheEndOfSimaSimulation() {
        scheduleLongExecutable();
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER);
            assertTrue(SimaSimulation.simaSimulationIsRunning());
            SimaSimulation.waitEndSimulation();
            assertFalse(SimaSimulation.simaSimulationIsRunning());
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void runSimulationPendingASimulationIsAlreadyRunningThrowsException() {
        scheduleLongExecutable();
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER);

            verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                    () -> assertThrows(SimaSimulationFailToStartRunningException.class,
                            () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                                    SimulationSetupTesting.class, SIMA_WATCHER)));
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void killSimulationKillTheSimulation() {
        scheduleLongExecutable();
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER);
            SimaSimulation.killSimulation();
            assertEquals(1, SIMA_WATCHER.getPassToInSimKilled());
            assertEquals(1, SCHEDULER_WATCHER.isPassToSchedulerKilled);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    // Inner class.

    private static class WrongSimulationSetup implements SimulationSetup {

        // Constructor.

        public WrongSimulationSetup() {
        }

        // Methods.

        @Override
        public void setupSimulation() {

        }
    }
}
