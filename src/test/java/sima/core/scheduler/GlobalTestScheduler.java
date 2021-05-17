package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventTesting;
import sima.core.exception.NotSchedulableTimeException;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.ProtocolTesting;
import sima.core.simulation.SimaSimulation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static sima.core.scheduler.Scheduler.NOW;

/**
 * Test that all Classes which implements {@link Scheduler} must pass.
 * <p>
 * For that the tests works, you need to initialized the fields {@link #SCHEDULER}, {@link #END_SIMULATION} and {@link
 * #TIME_EXECUTION_TOLERANCE}.
 */
@Disabled
public abstract class GlobalTestScheduler extends SimaTest {
    
    // Static.
    
    protected static final String PROTOCOL_TESTING_TAG = "P_TEST";
    
    private AgentTesting A0;
    private AgentTesting A1;
    
    protected static Scheduler SCHEDULER;
    
    /**
     * To specify and must be the same which is specify when {@link #SCHEDULER} is instantiate.
     * <p>
     * In that way we can verify if the Scheduler returns the right value with the method {@link
     * Scheduler#getEndSimulation()}.
     */
    protected static long END_SIMULATION;
    
    /**
     * Define the tolerance when we test when the scheduler execute executable. Example: If a executable must be execute
     * at time 5, in function of the type of the scheduler, it is not possible to it to execute th executable at 5.
     * Therefore the test verify if the execution time is equal to 5 +/- TIME_EXECUTION_TOLERANCE.
     */
    protected static long TIME_EXECUTION_TOLERANCE;
    
    /**
     * Define the tolerance when a test count the number of execution done by the scheduler. This variable is mainly
     * util for scheduler which works in real time mode.
     */
    protected static long NB_EXECUTION_TOLERANCE;
    
    protected static long REPETITION_STEP;
    
    protected static ProtocolIdentifier PROTOCOL_TESTING_IDENTIFIER;
    
    // Setup.
    
    @Override
    protected void verifyAndSetup() {
        A0 = new AgentTesting("A0", 0, 0, null);
        A0.start();
        A1 = new AgentTesting("A1", 1, 1, null);
        A1.start();
        
        assertTrue(END_SIMULATION >= 100, "END_SIMULATION must be greater or equal to 100 for tests");
        assertNotNull(SCHEDULER, "NULL SCHEDULER -> Tests cannot be realize");
        assertTrue(TIME_EXECUTION_TOLERANCE >= 0, "TIME_EXECUTION_TOLERANCE cannot be less than 0");
        assertTrue(NB_EXECUTION_TOLERANCE >= 0, "NB_EXECUTION_TOLERANCE cannot be less than 0");
        assertTrue(REPETITION_STEP > 0, "REPETITION_STEP must be greater or equal to 1");
        
        assertTrue(A0.addProtocol(ProtocolTesting.class, PROTOCOL_TESTING_TAG, null), "The A0"
                + " must be able to add ProtocolTesting.class");
        assertTrue(A1.addProtocol(ProtocolTesting.class, PROTOCOL_TESTING_TAG, null), "The A1"
                + " must be able to add ProtocolTesting.class");
        
        
        PROTOCOL_TESTING_IDENTIFIER = new ProtocolIdentifier(ProtocolTesting.class, PROTOCOL_TESTING_TAG);
    }
    
    // Tests.
    
    @Test
    void endSimulationIsCorrect() {
        assertEquals(END_SIMULATION, SCHEDULER.getEndSimulation());
        
        assertTrue(END_SIMULATION >= 100);
    }
    
    @Test
    void canAddOneSchedulerWatcher() {
        SchedulerWatcherTesting watcher0 = new SchedulerWatcherTesting();
        
        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));
    }
    
    @Test
    void cannotAddTwoTimesTheSameSchedulerWatcher() {
        SchedulerWatcherTesting watcher0 = new SchedulerWatcherTesting();
        
        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));
        assertFalse(SCHEDULER.addSchedulerWatcher(watcher0));
    }
    
    @Test
    void cannotAddNullSchedulerWatcher() {
        assertFalse(SCHEDULER.addSchedulerWatcher(null));
    }
    
    @Test
    void canRemoveSchedulerWatcherAdded() {
        SchedulerWatcherTesting watcher0 = new SchedulerWatcherTesting();
        
        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));
        
        SCHEDULER.removeSchedulerWatcher(watcher0);
        
        // We can re add the watcher because it has been removed.
        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));
    }
    
    @Test
    void nothingIsDoneWhenRemoveNull() {
        try {
            SCHEDULER.removeSchedulerWatcher(null);
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void nothingIsDoneWhenRemoveNonAddedSchedulerWatcher() {
        SchedulerWatcherTesting watcher0 = new SchedulerWatcherTesting();
        
        try {
            SCHEDULER.removeSchedulerWatcher(watcher0);
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void startReturnsTrueIfSchedulerIsNotStarted() {
        assertTrue(SCHEDULER.start());
    }
    
    @Test
    void startReturnsFalseIfSchedulerIsAlreadyStarted() {
        scheduleLongTimeExecutable();
        
        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.start());
    }
    
    @Test
    void killReturnsTrueIfSchedulerIsNotStarted() {
        assertTrue(SCHEDULER.kill());
    }
    
    @Test
    void killReturnsTrueIfSchedulerIsStarted() {
        scheduleLongTimeExecutable();
        
        assertTrue(SCHEDULER.start());
        
        assertTrue(SCHEDULER.kill());
    }
    
    @Test
    void killReturnsFalseIfSchedulerHasBeenAlreadyKilled() {
        SCHEDULER.kill();
        verifyPreConditionAndExecuteTest(() -> SCHEDULER.isKilled(), () -> assertFalse(SCHEDULER.kill()));
    }
    
    @Test
    void isRunningReturnsFalseWhenSchedulerIsNotStarted() {
        assertFalse(SCHEDULER.isRunning());
    }
    
    @Test
    void isRunningReturnsTrueWhenSchedulerIsRunning() {
        scheduleLongTimeExecutable();
        
        assertTrue(SCHEDULER.start());
        
        assertTrue(SCHEDULER.isRunning());
    }
    
    @Test
    void isRunningReturnsFalseAfterKillingScheduler() {
        scheduleLongTimeExecutable();
        
        assertTrue(SCHEDULER.start());
        
        assertTrue(SCHEDULER.kill());
        
        assertFalse(SCHEDULER.isRunning());
    }
    
    @Test
    void schedulerCannotBeRestarted() {
        scheduleLongTimeExecutable();
        
        assertTrue(SCHEDULER.start());
        
        assertTrue(SCHEDULER.kill());
        
        assertFalse(SCHEDULER.start());
    }
    
    @Test
    void watcherNotifyOneTimeOnSchedulerStart() {
        SchedulerWatcherTesting watcher = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(watcher);
        
        assertTrue(SCHEDULER.start());
        
        assertEquals(1, watcher.isPassToSchedulerStarted);
    }
    
    @Test
    void watcherNotifyOneTimeOnSchedulerKill() {
        SchedulerWatcherTesting watcher = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(watcher);
        
        scheduleLongTimeExecutable();
        
        assertTrue(SCHEDULER.start());
        
        assertTrue(SCHEDULER.kill());
        
        assertEquals(1, watcher.isPassToSchedulerKilled);
    }
    
    @Test
    void schedulerKillDirectlyAfterStartIfNoExecutableToExecute() {
        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());
    }
    
    @Test
    void watcherNotifyOneTimeOnStartEvenSchedulerKillDirectlyForNoExecutableToExecute() {
        SchedulerWatcherTesting watcher = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(watcher);
        
        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());
        
        assertEquals(1, watcher.isPassToSchedulerStarted);
    }
    
    @Test
    void watcherNotifyOneTimeOnKillSchedulerAfterStartWithNoExecutableToExecute() {
        SchedulerWatcherTesting watcher = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(watcher);
        
        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());
        
        assertEquals(1, watcher.isPassToSchedulerKilled);
    }
    
    @Test
    void watcherNotifyOneTimeOnSchedulerStopAfterNoExecutableToExecuteAtTheStart() {
        SchedulerWatcherTesting watcher = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(watcher);
        
        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());
        
        assertEquals(1, watcher.isPassToSchedulerKilled);
        assertEquals(1, watcher.isPassToNoExecutionToExecute);
        assertEquals(0, watcher.isPassToSimulationEndTimeReach);
    }
    
    @Test
    void getCurrentTimeReturnsZeroBeforeSchedulerStart() {
        assertEquals(0, SCHEDULER.getCurrentTime());
    }
    
    @Test
    void getCurrentTimeReturnsZeroIfSchedulerIsKilled() {
        SCHEDULER.kill();
        
        verifyPreConditionAndExecuteTest(() -> SCHEDULER.isKilled(),
                () -> assertEquals(-1, SCHEDULER.getCurrentTime()));
    }
    
    @Test
    void scheduleExecutableThrowsExceptionWithAllScheduleModeIfWaitingTimeLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.ONCE, -1,
                -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.REPEATED,
                -1, -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.INFINITE,
                -1, -1));
    }
    
    @Test
    void scheduleExecutableThrowsExceptionWithRepeatedScheduleModeIfNbRepetitionsIsLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED,
                0, 1));
    }
    
    @Test
    void scheduleExecutableThrowsExceptionWithAllRepeatedScheduleModeIfExecutionTimeStepIsLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED,
                1, 0));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.INFINITE,
                -1, 0));
    }
    
    @Test
    void scheduleExecutableIgnoresNbRepetitionsAndExecutionTimeStepInScheduleModeOnce() {
        ExecutableTesting e0 = new ExecutableTesting();
        
        try {
            SCHEDULER.scheduleExecutable(e0, 1, Scheduler.ScheduleMode.ONCE, -1, -1);
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void schedulerExecutesAtTimeOneExecutableScheduledOnceTime() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        ExecutableTesting e0 = new ExecutableTesting();
        
        SCHEDULER.scheduleExecutable(e0, NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
        
        assertTrue(SCHEDULER.start());
        
        waitSchedulerWatcher.waitUntilKilled();
        
        verifyNumber(e0.executedTime, NOW, TIME_EXECUTION_TOLERANCE);
    }
    
    @Test
    void schedulerExecutesAtTimeSeveralExecutablesScheduledOnceTime() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        ExecutableTesting e0 = new ExecutableTesting();
        ExecutableTesting e1 = new ExecutableTesting();
        ExecutableTesting e2 = new ExecutableTesting();
        
        SCHEDULER.scheduleExecutable(e0, NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
        SCHEDULER.scheduleExecutable(e1, NOW + 1, Scheduler.ScheduleMode.ONCE, -1, -1);
        SCHEDULER.scheduleExecutable(e2, NOW + 2, Scheduler.ScheduleMode.ONCE, -1, -1);
        
        assertTrue(SCHEDULER.start());
        
        waitSchedulerWatcher.waitUntilKilled();
        
        verifyNumber(e0.executedTime, NOW, TIME_EXECUTION_TOLERANCE);
        verifyNumber(e1.executedTime, NOW + 1, TIME_EXECUTION_TOLERANCE);
        verifyNumber(e2.executedTime, NOW + 2, TIME_EXECUTION_TOLERANCE);
    }
    
    @Test
    void schedulerExecutesAtTimeSeveralExecutableAfterTheStart() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        List<Executable> executables = new Vector<>();
        
        ExecutableTestingFeederV1 eF0 = new ExecutableTestingFeederV1(NOW, executables);
        
        SCHEDULER.scheduleExecutable(eF0, NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        for (Executable executable : executables) {
            ExecutableTestingFeederV1 executableFeeder = (ExecutableTestingFeederV1) executable;
            if (executableFeeder.executedTime != -1)
                // Executable executed
                verifyNumber(executableFeeder.executedTime, executableFeeder.timeToBeExecuted,
                        TIME_EXECUTION_TOLERANCE);
            /*else not executed executable*/
        }
    }
    
    @Test
    void watcherNotifyOneTimeWhenSchedulerFinishByReachingEndSimulation() {
        
        // Order very important
        SchedulerWatcherTesting watcher = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(watcher);
        
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        // Useless.
        List<Executable> executables = new Vector<>();
        
        ExecutableTestingFeederV1 eF0 = new ExecutableTestingFeederV1(NOW, executables);
        
        SCHEDULER.scheduleExecutable(eF0, NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
        
        assertTrue(SCHEDULER.start());
        
        waitSchedulerWatcher.waitUntilKilled();
        
        assertEquals(1, watcher.isPassToSchedulerKilled);
        assertEquals(0, watcher.isPassToNoExecutionToExecute);
        assertEquals(1, watcher.isPassToSimulationEndTimeReach);
    }
    
    @Test
    void scheduleAtSpecificTimeThrowsExceptionIfSpecifiedTimeIsLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(e0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(e0, -1));
    }
    
    @Test
    void scheduleAtSpecificTimeThrowsExceptionIfSpecifiedTimeIsAlreadyPass() {
        
        // Order very important
        SchedulerWatcherTesting watcher = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(watcher);
        
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        long timeToBeExecuted = (long) (0.5 * SCHEDULER.getEndSimulation());
        
        final AtomicBoolean isPassed = new AtomicBoolean(false);
        
        Executable e = () -> {
            isPassed.set(true);
            long currentTime = SCHEDULER.getCurrentTime();
            verifyNumber(currentTime, timeToBeExecuted, TIME_EXECUTION_TOLERANCE);
            
            Executable executable = new ExecutableTesting();
            // Try to schedule an Executable at a passed timed.
            assertThrows(NotSchedulableTimeException.class,
                    () -> SCHEDULER.scheduleExecutableAtSpecificTime(executable,
                            currentTime));
        };
        
        assertDoesNotThrow(
                () -> SCHEDULER.scheduleExecutable(e, timeToBeExecuted, Scheduler.ScheduleMode.ONCE, -1, -1));
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        verifyPreConditionAndExecuteTest(isPassed::get,
                () -> {
                    assertEquals(1, watcher.isPassToSchedulerKilled);
                    assertEquals(1, watcher.isPassToNoExecutionToExecute);
                    assertEquals(0, watcher.isPassToSimulationEndTimeReach);
                });
    }
    
    @Test
    void scheduleAtSpecificTimeScheduleAndExecuteAnExecutableAtTheSpecifiedTime() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        long timeToBeExecuted = (long) (0.75 * SCHEDULER.getEndSimulation());
        
        final AtomicBoolean isPassed = new AtomicBoolean(false);
        Executable e = () -> {
            isPassed.set(true);
            verifyNumber(SCHEDULER.getCurrentTime(), timeToBeExecuted, TIME_EXECUTION_TOLERANCE);
        };
        
        try {
            SCHEDULER.scheduleExecutableAtSpecificTime(e, timeToBeExecuted);
        } catch (Exception exc) {
            fail(exc);
        }
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        assertTrue(isPassed.get());
    }
    
    @Test
    void scheduleAtSpecificTimeCanScheduleWhenTheSchedulerIsStarted() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        long specificTime = (long) (END_SIMULATION * 0.5);
        
        AtomicLong executionTime = new AtomicLong(-1);
        
        SCHEDULER.scheduleExecutableOnce(
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(() -> executionTime.set(SCHEDULER.getCurrentTime()),
                        specificTime), NOW);
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        verifyNumber(executionTime.get(), specificTime, TIME_EXECUTION_TOLERANCE);
    }
    
    @Test
    void scheduleOnceThrowsExceptionIfWaitingIsLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableOnce(e0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableOnce(e1, -1));
    }
    
    @Test
    void scheduleOnceSchedulesAndExecuteOneTimeAnExecutableAtTime() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        List<Executable> executables = new Vector<>();
        
        ExecutableTestingFeederV2 eF0 = new ExecutableTestingFeederV2(NOW, executables);
        
        SCHEDULER.scheduleExecutableOnce(eF0, NOW);
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        for (Executable executable : executables) {
            ExecutableTestingFeederV2 executableFeeder = (ExecutableTestingFeederV2) executable;
            if (executableFeeder.executedTime != -1)
                // Executable executed
                verifyNumber(executableFeeder.executedTime, executableFeeder.timeToBeExecuted,
                        TIME_EXECUTION_TOLERANCE);
            /*else not executed executable*/
        }
    }
    
    @Test
    void scheduleRepeatedThrowsExceptionIfWaitingTimeLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(e0, 0, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(e1, -1, 1, 1));
    }
    
    @Test
    void scheduleRepeatedThrowsExceptionIfNbRepetitionsLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(e0, 1, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(e1, 1, -1, 1));
    }
    
    @Test
    void scheduleRepeatedThrowsExceptionIfExecutionTimeStepLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(e0, 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(e1, 1, 1, -1));
    }
    
    @Test
    void scheduleRepeatedSchedulesAndExecuteAnExecutableAtTimeAndRepetitivelyAsDefine() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        long nbRepetitions = 5;
        long repetitionBegin = NOW;
        
        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new HashMap<>();
        
        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());
        
        SCHEDULER.scheduleExecutableRepeated(executable, repetitionBegin, nbRepetitions, REPETITION_STEP);
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = entry.getKey() * REPETITION_STEP + repetitionBegin;
            
            if (entry.getValue() != -1)
                verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE);
        }
        
        long timeToExecuteAllRepetitions = nbRepetitions * REPETITION_STEP;
        
        long endExecutionOfRepetition = repetitionBegin + timeToExecuteAllRepetitions;
        long base;
        long expectedNbExecutions;
        // Supposes that repetitionBegin always less or equal to END_SIMULATION
        if (END_SIMULATION < endExecutionOfRepetition) {
            base = END_SIMULATION - repetitionBegin;
            expectedNbExecutions = (base / REPETITION_STEP) + 1;
        } else {
            expectedNbExecutions = nbRepetitions;
        }
        
        verifyNumber(nbExecutions.get(), expectedNbExecutions, NB_EXECUTION_TOLERANCE);
    }
    
    @Test
    void scheduleRepeatedWorksEvenIfRepetitionsPassTheEndSimulation() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        long repetitionBegin = NOW;
        long timeBetweenBeginAndEnd = END_SIMULATION - repetitionBegin;
        long nbRepetitions = (timeBetweenBeginAndEnd / REPETITION_STEP) + 1;
        
        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new ConcurrentHashMap<>();
        
        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());
        
        SCHEDULER.scheduleExecutableRepeated(executable, repetitionBegin, nbRepetitions, REPETITION_STEP);
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        long timeToExecuteAllRepetitions = nbRepetitions * REPETITION_STEP;
        
        long endExecutionOfRepetition = repetitionBegin + timeToExecuteAllRepetitions;
        long base;
        long expectedNbExecutions;
        // Supposes that repetitionBegin always less or equal to END_SIMULATION
        if (END_SIMULATION < endExecutionOfRepetition) {
            base = END_SIMULATION - repetitionBegin;
            expectedNbExecutions = (base / REPETITION_STEP) + 1;
        } else {
            expectedNbExecutions = nbRepetitions;
        }
        
        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = (entry.getKey() * REPETITION_STEP) + repetitionBegin;
            if (entry.getValue() != -1)
                verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE,
                        "Execution-th = " + entry.getKey() + " ExpectedNbExecution = " + expectedNbExecutions);
        }
        
        verifyNumber(nbExecutions.get(), expectedNbExecutions, NB_EXECUTION_TOLERANCE);
    }
    
    @Test
    void scheduleWithRepeatedModeThrowsExceptionIfWaitingTimeLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(e0, 0, Scheduler.ScheduleMode.REPEATED,
                        1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(e1, -1, Scheduler.ScheduleMode.REPEATED,
                        1, 1));
    }
    
    @Test
    void scheduleWithRepeatedModeThrowsExceptionIfNbRepetitionsLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(e0, 1, Scheduler.ScheduleMode.REPEATED,
                        0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(e1, 1, Scheduler.ScheduleMode.REPEATED,
                        -1, 1));
    }
    
    @Test
    void scheduleWithRepeatedModeThrowsExceptionIfExecutionTimeStepLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(e0, 1, Scheduler.ScheduleMode.REPEATED,
                        1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(e1, 1, Scheduler.ScheduleMode.REPEATED,
                        1, -1));
    }
    
    @Test
    void scheduleWithRepeatedModeSchedulesAndExecuteAnExecutableAtTimeAndRepetitivelyAsDefine() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        long nbRepetitions = 5;
        long repetitionBegin = NOW;
        
        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new HashMap<>();
        
        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());
        
        SCHEDULER.scheduleExecutable(executable, repetitionBegin, Scheduler.ScheduleMode.REPEATED, nbRepetitions,
                REPETITION_STEP);
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = entry.getKey() * REPETITION_STEP + repetitionBegin;
            if (entry.getValue() != -1)
                verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE);
        }
        
        long timeToExecuteAllRepetitions = nbRepetitions * REPETITION_STEP;
        
        long endExecutionOfRepetition = repetitionBegin + timeToExecuteAllRepetitions;
        long base;
        long expectedNbExecutions;
        // Supposes that repetitionBegin always less or equal to END_SIMULATION
        if (END_SIMULATION < endExecutionOfRepetition) {
            base = END_SIMULATION - repetitionBegin;
            expectedNbExecutions = (base / REPETITION_STEP) + 1;
        } else {
            expectedNbExecutions = nbRepetitions;
        }
        
        verifyNumber(nbExecutions.get(), expectedNbExecutions, NB_EXECUTION_TOLERANCE);
    }
    
    @Test
    void scheduleInfinitelyThrowsExceptionIfWaitingTimeLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(e0, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(e1, -1, 1));
    }
    
    @Test
    void scheduleInfinitelyThrowsExceptionIfExecutionTimeStepLessOrEqualToZero() {
        Executable e0 = new ExecutableTesting();
        Executable e1 = new ExecutableTesting();
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(e0, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(e1, 1, -1));
    }
    
    @Test
    void scheduleInfinitelySchedulesAndExecuteAnExecutableAtTimeAndRepetitivelyAsDefine() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        long repetitionBegin = NOW;
        
        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new HashMap<>();
        
        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());
        
        SCHEDULER.scheduleExecutableInfinitely(executable, repetitionBegin, REPETITION_STEP);
        
        assertTrue(SCHEDULER.start());
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        long timeToExecuteAllRepetitions = END_SIMULATION - repetitionBegin;
        long expectedNbExecutions = (timeToExecuteAllRepetitions / REPETITION_STEP) + 1;
        
        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = entry.getKey() * REPETITION_STEP + repetitionBegin;
            
            if (entry.getValue() != -1)
                verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE,
                        "Execution-th = " + entry.getKey() + " ExpectedNbExecution = " + expectedNbExecutions);
        }
        
        verifyNumber(nbExecutions.get(), expectedNbExecutions, NB_EXECUTION_TOLERANCE);
    }
    
    @Test
    void scheduleEventThrowsExceptionIfWaitingTimeIsLessOrEqualToZero() {
        Event event0 = new EventTesting(A0.getAgentIdentifier(), A1.getAgentIdentifier(),
                PROTOCOL_TESTING_IDENTIFIER);
        Event event1 = new EventTesting(A0.getAgentIdentifier(), A1.getAgentIdentifier(),
                PROTOCOL_TESTING_IDENTIFIER);
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleEvent(event0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleEvent(event1, -1));
    }
    
    @Test
    void scheduleEventThrowsExceptionIfAgentReceiverIsNull() {
        Event event = new EventTesting(A0.getAgentIdentifier(), null,
                PROTOCOL_TESTING_IDENTIFIER);
        
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleEvent(event, NOW));
    }
    
    @Test
    void scheduleEventScheduleAndExecuteEventAtTime() {
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        // Schedule Event.
        
        EventTesting testing =
                new EventTesting(A0.getAgentIdentifier(), A1.getAgentIdentifier(), PROTOCOL_TESTING_IDENTIFIER);
        SCHEDULER.scheduleEvent(testing, NOW);
        
        // Prepare the simulation.
        
        Set<AbstractAgent> agents = new HashSet<>();
        agents.add(A0);
        agents.add(A1);
        
        Set<Environment> environments = new HashSet<>();
        environments.add(new EnvironmentTesting(0));
        
        try {
            SimaSimulation.runSimulation(SCHEDULER, agents, environments, null, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        }
        
        SimaSimulation.waitEndSimulation(); // Two to wait
        /*blockSchedulerWatcher.waitUntilKilled();*/
        
        assertEquals(1, A1.getPassToProcessEvent());
        
        SimaSimulation.killSimulation();
    }
    
    @Test
    void scheduleAExecutableWhichCrashDuringTheExecutionNotCrashTheSimulation() {
        SchedulerWatcherTesting schedulerWatcherTesting = new SchedulerWatcherTesting();
        SCHEDULER.addSchedulerWatcher(schedulerWatcherTesting);
        
        WaitSchedulerWatcher waitSchedulerWatcher = new WaitSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(waitSchedulerWatcher);
        
        List<Executable> executables = new ArrayList<>();
        SCHEDULER.scheduleExecutableOnce(new ExecutableTestingFeederV2(NOW, executables), NOW);
        SCHEDULER.scheduleExecutableOnce(() -> {
            throw new RuntimeException();
        }, (SCHEDULER.getEndSimulation() / 2));
        
        SCHEDULER.start();
        
        // Finish by not reaching time.
        waitSchedulerWatcher.waitUntilKilled();
        
        assertEquals(1, schedulerWatcherTesting.isPassToSimulationEndTimeReach);
    }
    
    @Test
    void getTimeModeNeverReturnsNull() {
        assertNotNull(SCHEDULER.getTimeMode());
    }
    
    @Test
    void getSchedulerTypeNeverReturnsNull() {
        assertNotNull(SCHEDULER.getSchedulerType());
    }
    
    // Methods.
    
    /**
     * Method use to schedule a long task to allow test method when the scheduler is running.
     * <p>
     * Tests are probabilist but the task take very long time compare to the speed of processor, in that way we can be
     * very sure that all tests will pass and are correct.
     */
    protected void scheduleLongTimeExecutable() {
        SCHEDULER.scheduleExecutableOnce(new LongTimeExecutableTesting(), NOW);
    }
    
    // Inner classes.
    
    protected static class ExecutableTesting implements Executable {
        
        // Variables.
        
        private long executedTime = -1;
        
        // Methods.
        
        @Override
        public void execute() {
            executedTime = SCHEDULER.getCurrentTime();
        }
    }
    
    protected static class ExecutableTestingFeederV1 implements Executable {
        
        // Variables.
        
        private final long timeToBeExecuted;
        private final List<Executable> executableList;
        private long executedTime = -1;
        
        // Constructors.
        
        ExecutableTestingFeederV1(long timeToBeExecuted, List<Executable> executableList) {
            this.executableList = executableList;
            executableList.add(this);
            
            this.timeToBeExecuted = timeToBeExecuted;
        }
        
        // Methods.
        
        @Override
        public void execute() {
            ExecutableTestingFeederV1 executableFeeder =
                    new ExecutableTestingFeederV1(SCHEDULER.getCurrentTime() + 10, executableList);
            SCHEDULER.scheduleExecutable(executableFeeder, 10, Scheduler.ScheduleMode.ONCE, -1, -1);
            executedTime = SCHEDULER.getCurrentTime();
        }
    }
    
    protected static class ExecutableTestingFeederV2 implements Executable {
        
        // Variables.
        
        private final long timeToBeExecuted;
        private final List<Executable> executableList;
        private long executedTime = -1;
        
        // Constructors.
        
        ExecutableTestingFeederV2(long timeToBeExecuted, List<Executable> executableList) {
            this.executableList = executableList;
            executableList.add(this);
            
            this.timeToBeExecuted = timeToBeExecuted;
        }
        
        // Methods.
        
        @Override
        public void execute() {
            ExecutableTestingFeederV2 executableFeederV2 =
                    new ExecutableTestingFeederV2(SCHEDULER.getCurrentTime() + 10, executableList);
            SCHEDULER.scheduleExecutableOnce(executableFeederV2, 10);
            executedTime = SCHEDULER.getCurrentTime();
        }
    }
    
}
