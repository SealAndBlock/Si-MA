package sima.core.simulation;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.exception.SimaSimulationIsNotRunningException;
import sima.core.scheduler.LongTimeExecutableTesting;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.SchedulerWatcherTesting;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SimaSimulationTest extends SimaTest {

    // Static.

    private static final long END_SIMULATION = 1_000;
    private static final int NB_EXECUTOR_THREAD = 8;
    private static Scheduler SCHEDULER;
    private static SchedulerWatcherTesting SCHEDULER_WATCHER;

    private static AbstractAgent A_0;
    private static Set<AbstractAgent> ALL_AGENTS;

    private static Environment ENV_0;
    private static Environment NOT_ADDED_ENVIRONMENT_1;
    private static Environment NOT_ADDED_ENVIRONMENT_2;
    private static Set<Environment> ALL_ENVIRONMENTS;
    private static Set<Environment> SAME_NAME_ENVIRONMENT_SET;

    private static SimaWatcherTesting SIMA_WATCHER;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        SCHEDULER = new DiscreteTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREAD);
        SCHEDULER_WATCHER = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(SCHEDULER_WATCHER);

        A_0 = new AgentTesting("A_0", 0, null);

        ALL_AGENTS = new HashSet<>();
        ALL_AGENTS.add(A_0);

        ENV_0 = new EnvironmentTesting(0);
        NOT_ADDED_ENVIRONMENT_1 = new EnvironmentTesting(1);
        NOT_ADDED_ENVIRONMENT_2 = new EnvironmentTesting(2);

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
        schedulerScheduleLongExecutable();
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER);
            assertTrue(SimaSimulation.simaSimulationIsRunning());
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    @Test
    public void waitEndSimulationWaitUntilTheEndOfSimaSimulation() {
        schedulerScheduleLongExecutable();
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
        schedulerScheduleLongExecutable();
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
        schedulerScheduleLongExecutable();
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

    @Test
    public void getSchedulerThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getScheduler);
    }

    @Test
    public void getSchedulerNotNullValueIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();

        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNotNull(SimaSimulation.getScheduler())
        );
    }

    @Test
    public void getCurrentTimeThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getCurrentTime);
    }

    @Test
    public void getCurrentTimeReturnsPositiveValueIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();

        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(0 <= SimaSimulation.getCurrentTime()));
    }

    @Test
    public void addAgentThrowsExceptionIfSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.addAgent(A_0));
    }

    @Test
    public void addAgentReturnsTrueIfAgentHasNotBeenAlreadyAdded() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.addAgent(A_0)));
    }

    @Test
    public void addAgentReturnsFalseIfAgentHasNotBeenAlreadyAdded() {
        runSimulationWithLongExecutable();
        SimaSimulation.addAgent(A_0);
        verifyPreConditionAndExecuteTest(() -> SimaSimulation.simaSimulationIsRunning()
                        && SimaSimulation.getAgent(A_0.getAgentIdentifier()) != null,
                () -> assertFalse(SimaSimulation.addAgent(A_0)));
    }

    @Test
    public void getAgentFromIdentifierThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.getAgent(A_0.getAgentIdentifier()));
    }

    @Test
    public void getAgentFromIdentifierWithNullAgentIdentifierReturnsNull() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(SimaSimulation.getAgent(null)));
    }

    @Test
    public void getAgentFromIdentifierReturnsNullIfTheAgentIsNotPresent() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(SimaSimulation.getAgent(A_0.getAgentIdentifier())));
    }

    @Test
    public void getAgentFromIdentifierReturnsTheCorrespondingAgentToTheAgentIdentifierIfItIsPresent() {
        runSimulationWithAgentAdded();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> {
                    AbstractAgent a = SimaSimulation.getAgent(A_0.getAgentIdentifier());
                    assertNotNull(a);
                    assertSame(a, A_0);
                });
    }

    @Test
    public void addEnvironmentThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1));
    }

    @Test
    public void addEnvironmentReturnsTrueForANotAddedEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1)));
    }

    @Test
    public void addEnvironmentReturnsFalseWithAlreadyAddedEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertFalse(SimaSimulation.addEnvironment(ENV_0)));
    }

    @Test
    public void addEnvironmentCanAddSeveralEnvironmentWithSameClassButDifferentNameIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1)
                        && SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_2)));
    }

    @Test
    public void getAllEnvironmentsThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getAllEnvironments);
    }

    @Test
    public void getAllEnvironmentsNeverReturnsNullIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNotNull(SimaSimulation.getAllEnvironments()));
    }

    @Test
    public void getAllEnvironmentReturnsASetWhichContainsAddedEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.getAllEnvironments().contains(ENV_0)));
    }

    @Test
    public void getEnvironmentThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.getEnvironment(ENV_0.getEnvironmentName()));
    }

    @Test
    public void getEnvironmentReturnsNullWithNullEnvironmentNameIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(SimaSimulation.getEnvironment(null)));
    }

    @Test
    public void getEnvironmentReturnsNullWithNotAssociatedNameWithEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(SimaSimulation.getEnvironment(EnvironmentTesting.class.getName())));
    }

    @Test
    public void getEnvironmentReturnsNotNullWithAnAssociatedNameWithEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1);
        verifyPreConditionAndExecuteTest(() -> SimaSimulation.simaSimulationIsRunning()
                        && SimaSimulation.getAllEnvironments().contains(NOT_ADDED_ENVIRONMENT_1),
                () -> {
                    Environment env = SimaSimulation.getEnvironment(NOT_ADDED_ENVIRONMENT_1.getEnvironmentName());
                    assertNotNull(env);
                    assertSame(env, NOT_ADDED_ENVIRONMENT_1);
                });
    }

    @Test
    public void getTimeModeThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getTimeMode);
    }

    @Test
    public void getTimeModeReturnsTheCorrectTimeModeIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertEquals(SCHEDULER.getTimeMode(), SimaSimulation.getTimeMode()));
    }

    @Test
    public void getSchedulerTypeThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getSchedulerType);
    }

    @Test
    public void getSchedulerTypeReturnsTheCorrectSchedulerTypeIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertEquals(SCHEDULER.getSchedulerType(), SimaSimulation.getSchedulerType()));
    }

    @Test
    public void runSimulationWithJsonConfigurationWithNullFileThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                     () -> SimaSimulation.runSimulation(null));
    }

    @Test
    public void runSimulationWithJsonConfigurationWithEmptyFileThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                     () -> SimaSimulation.runSimulation(""));
    }

    @Test
    public void runSimulationWithJsonConfigurationTest1() {
        try {
            SimaSimulation.runSimulation("src/test/resources/config/testConfig1.json");
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    // Methods.

    private void runSimulationWithLongExecutable() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, null, ALL_ENVIRONMENTS, SimulationSetupWithLongExecutable.class, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    private void runSimulationWithAgentAdded() {
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS, SimulationSetupWithLongExecutable.class, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

    private void schedulerScheduleLongExecutable() {
        SCHEDULER.scheduleExecutableOnce(new LongTimeExecutableTesting(), Scheduler.NOW);
    }

    // Inner class.

    private static class WrongSimulationSetup extends SimulationSetup {

        // Constructor.

        public WrongSimulationSetup() {
            super(null);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        public void setupSimulation() {

        }
    }

    private static class SimulationSetupWithLongExecutable extends SimulationSetup {

        // Constructors.

        public SimulationSetupWithLongExecutable(Map<String, String> dummy) {
            super(dummy);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        public void setupSimulation() {
            SimaSimulation.getScheduler().scheduleExecutableOnce(new LongTimeExecutableTesting(), Scheduler.NOW);
        }
    }
}
