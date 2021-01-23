package sima.core;

import org.junit.jupiter.api.BeforeEach;
import sima.core.agent.AbstractAgent;
import sima.core.environment.Environment;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.core.simulation.SimaSimulation;
import sima.core.simulation.SimulationSetupWithLongExecutable;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class SimaTest {

    // Setup.

    @BeforeEach
    public void setup() {
        this.verifyAndSetup();
    }

    // Initialization.

    /**
     * Initialize all fields that the tests need.
     * <p>
     * This method is called before each unit tests.
     */
    protected abstract void verifyAndSetup();

    // Methods.

    /**
     * Verify the precondition of the test and if the precondition is true, execute the test.
     *
     * @param preConditionTest the pre condition to verify before execute the test
     * @param testRunnable     the test to run after verify the pre condition
     */
    public static void verifyPreConditionAndExecuteTest(PreConditionTest preConditionTest, TestRunnable testRunnable) {
        if (preConditionTest.verifyPreConditionTest()) {
            try {
                testRunnable.runTest();
            } catch (Throwable throwable) {
                fail(throwable);
            }
        } else {
            fail("Pre condition tests not verified");
        }
    }

    public static void verifyNumber(long valToVerify, long expected, long delta) {
        assertTrue((expected - delta) <= valToVerify && valToVerify <= (expected + delta),
                   "valToVerify = " + valToVerify + " expected = " + expected + " delta = " + delta + " min = "
                           + (expected - delta) + " max = " + (expected + delta));
    }

    public static void runSimulationWithLongExecutable(Set<AbstractAgent> allAgents, Set<Environment> allEnvironments,
                                                       SimaSimulation.SimaWatcher simaWatcher) {
        runSimulationWithLongExecutable(new DiscreteTimeMultiThreadScheduler(1000, 8), allAgents, allEnvironments,
                                        simaWatcher);
    }

    public static void runSimulationWithLongExecutable(Scheduler scheduler, Set<Environment> allEnvironments,
                                                       SimaSimulation.SimaWatcher simaWatcher) {
        runSimulationWithLongExecutable(scheduler, null, allEnvironments, simaWatcher);
    }

    public static void runSimulationWithLongExecutable(Scheduler scheduler, Set<AbstractAgent> allAgents,
                                                       Set<Environment> allEnvironments,
                                                       SimaSimulation.SimaWatcher simaWatcher) {
        try {
            SimaSimulation.runSimulation(scheduler, allAgents, allEnvironments, SimulationSetupWithLongExecutable.class,
                                         simaWatcher);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
    }

}
