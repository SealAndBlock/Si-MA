package sima.core.simulation;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.AgentTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.exception.SimaSimulationIsNotRunningException;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.scheduler.LongTimeExecutableTesting;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.SchedulerWatcherTesting;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.simulation.specific.SpecificControllerTesting;
import sima.core.simulation.specific.SpecificProtocolWithProtocolDependencies;
import sima.core.simulation.specific.SpecificSimulationSetupTesting;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestSimaSimulation extends SimaTest {
    
    // Static.
    
    static final String PREFIX_CONFIG_PATH = "src/test/resources/config/";
    
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
        
        A_0 = new AgentTesting("A_0", 0, 0, null);
        
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
        
        SpecificControllerTesting.PASS_EXECUTE = 0;
        SpecificSimulationSetupTesting.PASS_SETUP_SIMULATION = 0;
    }
    
    // Tests.
    
    @Test
    void runSimulationWithNullSchedulerThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(null, ALL_AGENTS, ALL_ENVIRONMENTS,
                        SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithNotNullSchedulerNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithNullAllAgentsNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, null, ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithEmptyAllAgentsNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, new HashSet<>(), ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithNullAllEnvironmentsThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, null,
                        SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithEmptyAllEnvironmentsThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, new HashSet<>(),
                        SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithAllEnvironmentsNotNullAndNotEmptyNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithSetWhichContainsSeveralEnvironmentWithSameNameThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, SAME_NAME_ENVIRONMENT_SET,
                        SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithNullSimulationSetupClassNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                null, null));
    }
    
    @Test
    void runSimulationWithSimulationSetupWhichHasNotCorrectConstructorThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                        WrongSimulationSetup.class, null));
    }
    
    @Test
    void runSimulationWithNullSimaWatcherNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, null));
    }
    
    @Test
    void runSimulationWithNotNullSimaWatcherNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                SimulationSetupTesting.class, SIMA_WATCHER));
    }
    
    @Test
    void runSimulationStartAndStopDirectlyWithEmptyScheduler() {
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
    void simaSimulationIsRunningReturnsFalseIfNoSimaSimulationHasBeenRun() {
        assertFalse(SimaSimulation.simaSimulationIsRunning());
    }
    
    @Test
    void simaSimulationIsRunningReturnsTrueIfSimaSimulationIsRunning() {
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
    void waitEndSimulationWaitUntilTheEndOfSimaSimulation() {
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
    void runSimulationPendingASimulationIsAlreadyRunningThrowsException() {
        schedulerScheduleLongExecutable();
        try {
            SimaSimulation.runSimulation(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS,
                    SimulationSetupTesting.class, SIMA_WATCHER);
            
            verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                    () -> assertThrows(SimaSimulationFailToStartRunningException.class,
                            () -> SimaSimulation.runSimulation(SCHEDULER,
                                    ALL_AGENTS,
                                    ALL_ENVIRONMENTS,
                                    SimulationSetupTesting.class,
                                    SIMA_WATCHER)));
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }
    
    @Test
    void killSimulationKillTheSimulation() {
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
    void getSchedulerThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getScheduler);
    }
    
    @Test
    void getSchedulerNotNullValueIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNotNull(SimaSimulation.getScheduler())
        );
    }
    
    @Test
    void getCurrentTimeThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getCurrentTime);
    }
    
    @Test
    void getCurrentTimeReturnsPositiveValueIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(0 <= SimaSimulation.getCurrentTime()));
    }
    
    @Test
    void addAgentThrowsExceptionIfSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.addAgent(A_0));
    }
    
    @Test
    void addAgentReturnsTrueIfAgentHasNotBeenAlreadyAdded() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.addAgent(A_0)));
    }
    
    @Test
    void addAgentReturnsFalseIfAgentHasNotBeenAlreadyAdded() {
        runSimulationWithLongExecutable();
        SimaSimulation.addAgent(A_0);
        verifyPreConditionAndExecuteTest(() -> SimaSimulation.simaSimulationIsRunning()
                        && SimaSimulation.getAgent(A_0.getAgentIdentifier()) != null,
                () -> assertFalse(SimaSimulation.addAgent(A_0)));
    }
    
    @Test
    void getAgentFromIdentifierThrowsExceptionIfSimaSimulationIsNotRunning() {
        var agentIdentifier = A_0.getAgentIdentifier();
        assertThrows(SimaSimulationIsNotRunningException.class,
                () -> SimaSimulation.getAgent(agentIdentifier));
    }
    
    @Test
    void getAgentFromIdentifierWithNullAgentIdentifierReturnsNull() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(SimaSimulation.getAgent(null)));
    }
    
    @Test
    void getAgentFromIdentifierReturnsNullIfTheAgentIsNotPresent() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(SimaSimulation.getAgent(A_0.getAgentIdentifier())));
    }
    
    @Test
    void getAgentFromIdentifierReturnsTheCorrespondingAgentToTheAgentIdentifierIfItIsPresent() {
        runSimulationWithAgentAddedAndLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> {
                    AbstractAgent a = SimaSimulation.getAgent(A_0.getAgentIdentifier());
                    assertNotNull(a);
                    assertSame(a, A_0);
                });
    }
    
    @Test
    void addEnvironmentThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class,
                () -> SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1));
    }
    
    @Test
    void addEnvironmentReturnsTrueForANotAddedEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1)));
    }
    
    @Test
    void addEnvironmentReturnsFalseWithAlreadyAddedEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertFalse(SimaSimulation.addEnvironment(ENV_0)));
    }
    
    @Test
    void addEnvironmentCanAddSeveralEnvironmentWithSameClassButDifferentNameIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1)
                        && SimaSimulation.addEnvironment(
                        NOT_ADDED_ENVIRONMENT_2)));
    }
    
    @Test
    void getAllEnvironmentsThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getAllEnvironments);
    }
    
    @Test
    void getAllEnvironmentsNeverReturnsNullIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNotNull(SimaSimulation.getAllEnvironments()));
    }
    
    @Test
    void getAllEnvironmentReturnsASetWhichContainsAddedEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertTrue(SimaSimulation.getAllEnvironments().contains(ENV_0)));
    }
    
    @Test
    void getEnvironmentThrowsExceptionIfSimaSimulationIsNotRunning() {
        var envName = ENV_0.getEnvironmentName();
        assertThrows(SimaSimulationIsNotRunningException.class,
                () -> SimaSimulation.getEnvironment(envName));
    }
    
    @Test
    void getEnvironmentReturnsNullWithNullEnvironmentNameIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(SimaSimulation.getEnvironment(null)));
    }
    
    @Test
    void getEnvironmentReturnsNullWithNotAssociatedNameWithEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertNull(
                        SimaSimulation.getEnvironment(EnvironmentTesting.class.getName())));
    }
    
    @Test
    void getEnvironmentReturnsNotNullWithAnAssociatedNameWithEnvironmentIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        SimaSimulation.addEnvironment(NOT_ADDED_ENVIRONMENT_1);
        verifyPreConditionAndExecuteTest(() -> SimaSimulation.simaSimulationIsRunning()
                        && SimaSimulation.getAllEnvironments().contains(NOT_ADDED_ENVIRONMENT_1),
                () -> {
                    Environment env = SimaSimulation.getEnvironment(
                            NOT_ADDED_ENVIRONMENT_1.getEnvironmentName());
                    assertNotNull(env);
                    assertSame(env, NOT_ADDED_ENVIRONMENT_1);
                });
    }
    
    @Test
    void getTimeModeThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getTimeMode);
    }
    
    @Test
    void getTimeModeReturnsTheCorrectTimeModeIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertEquals(SCHEDULER.getTimeMode(), SimaSimulation.getTimeMode()));
    }
    
    @Test
    void getSchedulerTypeThrowsExceptionIfSimaSimulationIsNotRunning() {
        assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getSchedulerType);
    }
    
    @Test
    void getSchedulerTypeReturnsTheCorrectSchedulerTypeIfSimaSimulationIsRunning() {
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(SimaSimulation::simaSimulationIsRunning,
                () -> assertEquals(SCHEDULER.getSchedulerType(),
                        SimaSimulation.getSchedulerType()));
    }
    
    @Test
    void runSimulationWithJsonConfigurationWithNullFileThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(null));
    }
    
    @Test
    void runSimulationWithJsonConfigurationWithEmptyFileThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(""));
    }
    
    @Test
    void runSimulationWithMinimumConfigNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "minimumConfig.json"));
    }
    
    @Test
    void runSimulationWithNegativeEndTimeConfigThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "negativeEndTime.json"));
    }
    
    @Test
    void runSimulationWithNegativeNbThreadsConfigThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "negativeNbThreads.json"));
    }
    
    @Test
    void runSimulationWithUnknownTimeModeConfigThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "unknownTimeMode.json"));
    }
    
    @Test
    void runSimulationWithUnknownSchedulerTypeConfigThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "unknownSchedulerType.json"));
    }
    
    @Test
    void runSimulationWithDiscreteTimeMonoThreadSchedulerThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "discreteTimeMonoThread.json"));
    }
    
    @Test
    void runSimulationWithRealTimeMonoThreadSchedulerThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "realTimeMonoThread.json"));
    }
    
    @Test
    void runSimulationWithRealTimeMultiThreadSchedulerThrowsException() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "realTimeMultiThread.json"));
    }
    
    @Test
    void runSimulationWithWrongFormatArgsThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "wrongFormatArgs.json"));
    }
    
    @Test
    void runSimulationWithControllerScheduledOnceNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "controllerScheduledOnce.json"));
    }
    
    @Test
    void runSimulationWithControllerScheduledRepeatedNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "controllerScheduledRepeated.json"));
    }
    
    @Test
    void runSimulationWithControllerScheduledInfinitelyNotFail() {
        assertDoesNotThrow(
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "controllerScheduledInfinitely.json"));
    }
    
    @Test
    void runSimulationWithBehaviorWhichDoesNotAcceptOneAgentThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "behaviorWhichDoesNotAcceptAgent.json"));
    }
    
    @Test
    void runSimulationWithAgentWhichDoesNotAcceptToAddProtocolThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "agentWhichDoesNotAcceptToAddProtocol"
                        + ".json"));
    }
    
    @Test
    void runSimulationWithProtocolDependenciesNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                "configWithProtocolDependencies.json"));
        AbstractAgent agent = SimaSimulation.getAgent(new AgentIdentifier("SpecificAgentTesting_0", 0, 0));
        SpecificProtocolWithProtocolDependencies protocol =
                (SpecificProtocolWithProtocolDependencies) agent.getProtocol(
                        new ProtocolIdentifier(SpecificProtocolWithProtocolDependencies.class,
                                "SpecificProtocolWithProtocolDependenciesTag"));
        assertNotNull(protocol.getProtocolTesting());
        assertNotNull(protocol.getEventSender());
    }
    
    @Test
    void runSimulationWithNotCorrectFieldNameProtocolDependenciesThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                        "configWithNotCorrectFieldNameProtocolDependencies.json"));
    }
    
    @Test
    void runSimulationWithProtocolDependenciesWithUnknownIdThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                        "configWithProtocolDependenciesWithUnknownId.json"));
    }
    
    @Test
    void runSimulationWithProtocolDependenciesWithUnknownProtocolAttributeThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                        "configWithProtocolDependenciesWithUnknownProtocolAttribute"
                        + ".json"));
    }
    
    @Test
    void runSimulationWithProtocolDependenciesWithWrongTypeOfTheDependenciesInstanceThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                        "configWithProtocolDependenciesWithWrongType.json"));
    }
    
    @Test
    void runSimulationWithUnknownEnvironmentClassConfigThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "unknownEnvironmentClass.json"));
    }
    
    @Test
    void runSimulationWithUnknownEnvironmentIdInAgentThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "unknownEnvironmentIdInAgent.json"));
    }
    
    @Test
    void runSimulationWithTwoEnvironmentsWithHashCodeThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "twoEnvironmentsWithSameHashCode.json"));
    }
    
    @Test
    void runSimulationWithEnvironmentWhichDoesNotAcceptAgentThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "environmentDoesNotAcceptAgent.json"));
    }
    
    @Test
    void runSimulationWithEnvironmentWithoutIdThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "environmentWithoutId.json"));
    }
    
    @Test
    void runSimulationWithoutEnvironmentsInConfigThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "withoutEnvironmentsConfig.json"));
    }
    
    @Test
    void runSimulationWithoutAgentsInConfigNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "withoutAgentsConfig.json"));
    }
    
    @Test
    void runSimulationWithNegativeAgentNumberToCreateThrowsException() {
        assertThrows(SimaSimulationFailToStartRunningException.class,
                () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "negativeAgentNumberToCreate.json"));
    }
    
    @Test
    void runSimulationWithFullConfigNotFail() {
        assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "fullConfig.json"));
        
        SimaSimulation.waitEndSimulation();
        
        assertEquals(1, SpecificControllerTesting.PASS_EXECUTE);
        assertEquals(1, SpecificSimulationSetupTesting.PASS_SETUP_SIMULATION);
    }
    
    // Methods.
    
    private void runSimulationWithLongExecutable() {
        runSimulationWithLongExecutable(SCHEDULER, ALL_ENVIRONMENTS, null);
    }
    
    private void runSimulationWithAgentAddedAndLongExecutable() {
        runSimulationWithLongExecutable(SCHEDULER, ALL_AGENTS, ALL_ENVIRONMENTS, null);
    }
    
    private void schedulerScheduleLongExecutable() {
        SCHEDULER.scheduleExecutableOnce(new LongTimeExecutableTesting(), Scheduler.NOW);
    }
    
    // Inner class.
    
    private static class WrongSimulationSetup implements SimulationSetup {
        
        // Constructor.
        
        WrongSimulationSetup() {
        }
        
        // Methods.
        
        @Override
        public void setupSimulation() {
        
        }
    }
}
