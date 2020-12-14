package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AgentTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;
import sima.core.environment.event.EventTesting;
import sima.core.exception.NotSchedulableTimeException;
import sima.core.simulation.AgentManager;
import sima.core.simulation.LocalAgentManager;
import sima.core.simulation.SimaSimulation;
import sima.core.simulation.SimaSimulationTesting;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that all Classes which implements {@link Scheduler} must pass.
 * <p>
 * For that the tests works, you need to initialized the fields {@link #SCHEDULER}, {@link #END_SIMULATION} and
 * {@link #TIME_EXECUTION_TOLERANCE}.
 */
@Disabled
public abstract class GlobalTestScheduler extends SimaTest {

    // Static.

    private AgentTesting A0;
    private AgentTesting A1;

    protected static Scheduler SCHEDULER;

    /**
     * To specify and must be the same which is specify when {@link #SCHEDULER} is instantiate.
     * <p>
     * In that way we can verify if the Scheduler returns the right value with the method
     * {@link Scheduler#getEndSimulation()}.
     */
    protected static long END_SIMULATION = 100;

    /**
     * Define the tolerance when we test when the scheduler execute executable. Example: If a executable must be execute
     * at time 5, in function of the type of the scheduler, it is not possible to it to execute th executable at 5.
     * Therefore the test verify if the execution time is equal to 5 +/- TIME_EXECUTION_TOLERANCE.
     */
    protected static long TIME_EXECUTION_TOLERANCE = 0;

    // Setup.

    @Override
    protected void verifyAndSetup() {
        A0 = new AgentTesting("A0", 0, null);
        A0.start();
        A1 = new AgentTesting("A1", 1, null);
        A1.start();

        assertTrue(END_SIMULATION >= 100, "END_SIMULATION must be greater or equal to 100 for tests");
        assertNotNull(SCHEDULER, "NULL SCHEDULER -> Tests cannot be realize");
        assertTrue(TIME_EXECUTION_TOLERANCE >= 0, "TIME_EXECUTION TOLERANCE cannot be less than 0");
    }

    // Tests.

    @Test
    public void endSimulationIsCorrect() {
        assertEquals(END_SIMULATION, SCHEDULER.getEndSimulation());

        assertTrue(END_SIMULATION >= 100);
    }

    @Test
    public void canAddOneSchedulerWatcher() {
        TestSchedulerWatcher watcher0 = new TestSchedulerWatcher();

        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));
    }

    @Test
    public void cannotAddTwoTimesTheSameSchedulerWatcher() {
        TestSchedulerWatcher watcher0 = new TestSchedulerWatcher();

        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));
        assertFalse(SCHEDULER.addSchedulerWatcher(watcher0));
    }

    @Test
    public void cannotAddNullSchedulerWatcher() {
        assertFalse(SCHEDULER.addSchedulerWatcher(null));
    }

    @Test
    public void canRemoveSchedulerWatcherAdded() {
        TestSchedulerWatcher watcher0 = new TestSchedulerWatcher();

        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));

        SCHEDULER.removeSchedulerWatcher(watcher0);

        // We can re add the watcher because it has been removed.
        assertTrue(SCHEDULER.addSchedulerWatcher(watcher0));
    }

    @Test
    public void nothingIsDoneWhenRemoveNull() {
        try {
            SCHEDULER.removeSchedulerWatcher(null);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void nothingIsDoneWhenRemoveNonAddedSchedulerWatcher() {
        TestSchedulerWatcher watcher0 = new TestSchedulerWatcher();

        try {
            SCHEDULER.removeSchedulerWatcher(watcher0);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void startReturnsTrueIfSchedulerIsNotStarted() {
        assertTrue(SCHEDULER.start());
    }

    @Test
    public void startReturnsFalseIfSchedulerIsAlreadyStarted() {
        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.start());
    }

    @Test
    public void killReturnsFalseIfSchedulerIsNotStarted() {
        assertFalse(SCHEDULER.kill());
    }

    @Test
    public void killReturnsTrueIfSchedulerIsStarted() {
        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());

        assertTrue(SCHEDULER.kill());
    }

    @Test
    public void isRunningReturnsFalseWhenSchedulerIsNotStarted() {
        assertFalse(SCHEDULER.isRunning());
    }

    @Test
    public void isRunningReturnsTrueWhenSchedulerIsRunning() {
        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());

        assertTrue(SCHEDULER.isRunning());
    }

    @Test
    public void isRunningReturnsFalseAfterKillingScheduler() {
        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());

        assertTrue(SCHEDULER.kill());

        assertFalse(SCHEDULER.isRunning());
    }

    @Test
    public void schedulerCanBeRestarted() {
        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());

        assertTrue(SCHEDULER.kill());

        assertTrue(SCHEDULER.start());
    }

    @Test
    public void schedulerCanBeKillAfterRestart() {
        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());

        assertTrue(SCHEDULER.kill());

        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());

        assertTrue(SCHEDULER.kill());
    }

    @Test
    public void watcherNotifyOneTimeOnSchedulerStart() {
        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        assertTrue(SCHEDULER.start());

        assertEquals(1, watcher.isPassToSchedulerStarted);
    }

    @Test
    public void watcherNotifyOneTimeOnSchedulerKill() {
        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        this.scheduleLongTimeExecutable();

        assertTrue(SCHEDULER.start());

        assertTrue(SCHEDULER.kill());

        assertEquals(1, watcher.isPassToSchedulerKilled);
    }

    @Test
    public void schedulerKillDirectlyAfterStartIfNoExecutableToExecute() {
        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());
    }

    @Test
    public void watcherNotifyOneTimeOnStartEvenSchedulerKillDirectlyForNoExecutableToExecute() {
        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());

        assertEquals(1, watcher.isPassToSchedulerStarted);
    }

    @Test
    public void watcherNotifyOneTimeOnKillSchedulerAfterStartWithNoExecutableToExecute() {
        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());

        assertEquals(1, watcher.isPassToSchedulerKilled);
    }

    @Test
    public void watcherNotifyOneTimeOnSchedulerStopAfterNoExecutableToExecuteAtTheStart() {
        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.isRunning());

        assertEquals(1, watcher.isPassToSchedulerKilled);
        assertEquals(1, watcher.isPassToNoExecutionToExecute);
        assertEquals(0, watcher.isPassToSimulationEndTimeReach);
    }

    @Test
    public void currentTimeIsEqualToZeroAfterSchedulerStart() {
        assertEquals(0, SCHEDULER.getCurrentTime());
    }

    @Test
    public void scheduleExecutableThrowsExceptionWithAllScheduleModeIfWaitingTimeLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.ONCE, -1, -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.REPEATED, -1, -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.INFINITELY, -1, -1));
    }

    @Test
    public void scheduleExecutableThrowsExceptionWithRepeatedScheduleModeIfNbRepetitionsIsLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED, 0, 1));
    }

    @Test
    public void scheduleExecutableThrowsExceptionWithAllRepeatedScheduleModeIfExecutionTimeStepIsLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.INFINITELY, -1, 0));
    }

    @Test
    public void scheduleExecutableIgnoreNbRepetitionsAndExecutionTimeStepInScheduleModeOnce() {
        ExecutableTesting e0 = new ExecutableTesting();

        try {
            SCHEDULER.scheduleExecutable(e0, 1, Scheduler.ScheduleMode.ONCE, -1, -1);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void schedulerExecuteAtTimeOneExecutableScheduledOnceTime() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        ExecutableTesting e0 = new ExecutableTesting();

        SCHEDULER.scheduleExecutable(e0, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        blockSchedulerWatcher.waitUntilKilled();

        this.verifyNumber(e0.executedTime, Scheduler.NOW, TIME_EXECUTION_TOLERANCE);
    }

    @Test
    public void schedulerExecuteAtTimeSeveralExecutablesScheduledOnceTime() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        ExecutableTesting e0 = new ExecutableTesting();
        ExecutableTesting e1 = new ExecutableTesting();
        ExecutableTesting e2 = new ExecutableTesting();

        SCHEDULER.scheduleExecutable(e0, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
        SCHEDULER.scheduleExecutable(e1, Scheduler.NOW + 1, Scheduler.ScheduleMode.ONCE, -1, -1);
        SCHEDULER.scheduleExecutable(e2, Scheduler.NOW + 2, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        blockSchedulerWatcher.waitUntilKilled();

        this.verifyNumber(e0.executedTime, Scheduler.NOW, TIME_EXECUTION_TOLERANCE);
        this.verifyNumber(e1.executedTime, Scheduler.NOW + 1, TIME_EXECUTION_TOLERANCE);
        this.verifyNumber(e2.executedTime, Scheduler.NOW + 2, TIME_EXECUTION_TOLERANCE);
    }

    @Test
    public void schedulerExecuteAtTimeSeveralExecutableAfterTheStart() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        List<Executable> executables = new Vector<>();

        ExecutableTestingFeederV1 eF0 = new ExecutableTestingFeederV1(Scheduler.NOW, executables);

        SCHEDULER.scheduleExecutable(eF0, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        for (Executable executable : executables) {
            ExecutableTestingFeederV1 executableFeeder = (ExecutableTestingFeederV1) executable;
            if (executableFeeder.executedTime != -1)
                // Executable executed
                this.verifyNumber(executableFeeder.executedTime, executableFeeder.timeToBeExecuted,
                        TIME_EXECUTION_TOLERANCE);
            else
                // Executable normally out of the end of the simulation
                assertTrue(executableFeeder.timeToBeExecuted > SCHEDULER.getEndSimulation());
        }
    }

    @Test
    public void watcherNotifyOneTimeWhenSchedulerFinishByReachingEndSimulation() {

        // Order very important
        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        // Useless.
        List<Executable> executables = new Vector<>();

        ExecutableTestingFeederV1 eF0 = new ExecutableTestingFeederV1(Scheduler.NOW, executables);

        SCHEDULER.scheduleExecutable(eF0, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        blockSchedulerWatcher.waitUntilKilled();

        assertEquals(1, watcher.isPassToSchedulerKilled);
        assertEquals(0, watcher.isPassToNoExecutionToExecute);
        assertEquals(1, watcher.isPassToSimulationEndTimeReach);
    }

    @Test
    public void scheduleAtSpecificTimeThrowsExceptionIfSpecifiedTimeIsLessThanOne() {
        ExecutableTesting e0 = new ExecutableTesting();

        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(e0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(e0, -1));
    }

    @Test
    public void scheduleAtSpecificTimeThrowsExceptionIfSpecifiedTimeIsAlreadyPass() {

        // Order very important
        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        long timeToBeExecuted = (long) (0.5 * SCHEDULER.getEndSimulation());

        final AtomicBoolean isPassed = new AtomicBoolean(false);

        Executable e = () -> {
            isPassed.set(true);
            long currentTime = SCHEDULER.getCurrentTime();
            this.verifyNumber(currentTime, timeToBeExecuted, TIME_EXECUTION_TOLERANCE);

            // Try to schedule an Executable at a passed timed.
            assertThrows(NotSchedulableTimeException.class,
                    () -> SCHEDULER.scheduleExecutableAtSpecificTime(new ExecutableTesting(),
                            currentTime));
        };

        try {
            SCHEDULER.scheduleExecutable(e, timeToBeExecuted, Scheduler.ScheduleMode.ONCE, -1, -1);
        } catch (Exception exc) {
            fail();
        }

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        assertTrue(isPassed.get());

        assertEquals(1, watcher.isPassToSchedulerKilled);
        assertEquals(1, watcher.isPassToNoExecutionToExecute);
        assertEquals(0, watcher.isPassToSimulationEndTimeReach);
    }

    @Test
    public void scheduleAtSpecificTimeScheduleAndExecuteAnExecutableAtTheSpecifiedTime() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        long timeToBeExecuted = (long) (0.75 * SCHEDULER.getEndSimulation());

        final AtomicBoolean isPassed = new AtomicBoolean(false);
        Executable e = () -> {
            isPassed.set(true);
            this.verifyNumber(SCHEDULER.getCurrentTime(), timeToBeExecuted, TIME_EXECUTION_TOLERANCE);
        };

        try {
            SCHEDULER.scheduleExecutableAtSpecificTime(e, timeToBeExecuted);
        } catch (Exception exc) {
            fail(exc);
        }

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        assertTrue(isPassed.get());
    }

    @Test
    public void scheduleAtSpecificTimeCanScheduleWhenTheSchedulerIsStarted() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        long specificTime = (long) (END_SIMULATION * 0.5);

        AtomicLong executionTime = new AtomicLong(-1);

        SCHEDULER.scheduleExecutableOnce(
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(() -> executionTime.set(SCHEDULER.getCurrentTime()), specificTime), Scheduler.NOW);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        this.verifyNumber(executionTime.get(), specificTime, TIME_EXECUTION_TOLERANCE);
    }

    @Test
    public void scheduleOnceThrowsExceptionIfWaitingIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableOnce(new ExecutableTesting(), 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableOnce(new ExecutableTesting(), -1));
    }

    @Test
    public void scheduleOnceScheduleAndExecuteOneTimeAnExecutableAtTime() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        List<Executable> executables = new Vector<>();

        ExecutableTestingFeederV2 eF0 = new ExecutableTestingFeederV2(Scheduler.NOW, executables);

        SCHEDULER.scheduleExecutableOnce(eF0, Scheduler.NOW);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        for (Executable executable : executables) {
            ExecutableTestingFeederV2 executableFeeder = (ExecutableTestingFeederV2) executable;
            if (executableFeeder.executedTime != -1)
                // Executable executed
                this.verifyNumber(executableFeeder.executedTime, executableFeeder.timeToBeExecuted,
                        TIME_EXECUTION_TOLERANCE);
            else
                // Executable normally out of the end of the simulation
                assertTrue(executableFeeder.timeToBeExecuted > SCHEDULER.getEndSimulation());
        }
    }

    @Test
    public void scheduleRepeatedThrowsExceptionIfWaitingTimeLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(new ExecutableTesting(), 0, 1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(new ExecutableTesting(), -1, 1, 1));
    }

    @Test
    public void scheduleRepeatedThrowsExceptionIfNbRepetitionsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(new ExecutableTesting(), 1, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(new ExecutableTesting(), 1, -1, 1));
    }

    @Test
    public void scheduleRepeatedThrowsExceptionIfExecutionTimeStepLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(new ExecutableTesting(), 1, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableRepeated(new ExecutableTesting(), 1, 1, -1));
    }

    @Test
    public void scheduleRepeatedSchedulesAndExecuteAnExecutableAtTimeAndRepetitivelyAsDefine() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        long nbRepetitions = 5;
        long stepBetweenRepetition = 10;
        long repetitionBegin = Scheduler.NOW;

        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new HashMap<>();

        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());

        SCHEDULER.scheduleExecutableRepeated(executable, repetitionBegin, nbRepetitions, stepBetweenRepetition);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = entry.getKey() * stepBetweenRepetition + repetitionBegin;
            this.verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE);
        }

        long timeToExecuteAllRepetitions = nbRepetitions * stepBetweenRepetition;

        long endExecutionOfRepetition = repetitionBegin + timeToExecuteAllRepetitions;
        long base;
        long expectedNbExecutions;
        // Supposes that repetitionBegin always less or equal to END_SIMULATION
        if (END_SIMULATION < endExecutionOfRepetition) {
            base = END_SIMULATION - repetitionBegin;
            expectedNbExecutions = (base / stepBetweenRepetition) + 1;
        } else {
            expectedNbExecutions = nbRepetitions;
        }

        assertEquals(expectedNbExecutions, nbExecutions.get());
    }

    @Test
    public void scheduleRepeatedWorksEvenIfRepetitionsPassTheEndSimulation() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        long stepBetweenRepetition = 10;
        long repetitionBegin = Scheduler.NOW;
        long timeBetweenBeginAndEnd = END_SIMULATION - repetitionBegin;
        long nbRepetitions = (timeBetweenBeginAndEnd / stepBetweenRepetition) + 1;

        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new HashMap<>();

        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());

        SCHEDULER.scheduleExecutableRepeated(executable, repetitionBegin, nbRepetitions, stepBetweenRepetition);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = entry.getKey() * stepBetweenRepetition + repetitionBegin;
            this.verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE);
        }

        long timeToExecuteAllRepetitions = nbRepetitions * stepBetweenRepetition;

        long endExecutionOfRepetition = repetitionBegin + timeToExecuteAllRepetitions;
        long base;
        long expectedNbExecutions;
        // Supposes that repetitionBegin always less or equal to END_SIMULATION
        if (END_SIMULATION < endExecutionOfRepetition) {
            base = END_SIMULATION - repetitionBegin;
            expectedNbExecutions = (base / stepBetweenRepetition) + 1;
        } else {
            expectedNbExecutions = nbRepetitions;
        }

        assertEquals(expectedNbExecutions, nbExecutions.get());
    }

    @Test
    public void scheduleWithRepeatedModeThrowsExceptionIfWaitingTimeLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(new ExecutableTesting(), 0, Scheduler.ScheduleMode.REPEATED,
                        1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(new ExecutableTesting(), -1, Scheduler.ScheduleMode.REPEATED,
                        1, 1));
    }

    @Test
    public void scheduleWithRepeatedModeThrowsExceptionIfNbRepetitionsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(new ExecutableTesting(), 1, Scheduler.ScheduleMode.REPEATED,
                        0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(new ExecutableTesting(), 1, Scheduler.ScheduleMode.REPEATED,
                        -1, 1));
    }

    @Test
    public void scheduleWithRepeatedModeThrowsExceptionIfExecutionTimeStepLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(new ExecutableTesting(), 1, Scheduler.ScheduleMode.REPEATED,
                        1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutable(new ExecutableTesting(), 1, Scheduler.ScheduleMode.REPEATED,
                        1, -1));
    }

    @Test
    public void scheduleWithRepeatedModeSchedulesAndExecuteAnExecutableAtTimeAndRepetitivelyAsDefine() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        long nbRepetitions = 5;
        long stepBetweenRepetition = 10;
        long repetitionBegin = Scheduler.NOW;

        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new HashMap<>();

        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());

        SCHEDULER.scheduleExecutable(executable, repetitionBegin, Scheduler.ScheduleMode.REPEATED, nbRepetitions,
                stepBetweenRepetition);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = entry.getKey() * stepBetweenRepetition + repetitionBegin;
            this.verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE);
        }

        long timeToExecuteAllRepetitions = nbRepetitions * stepBetweenRepetition;

        long endExecutionOfRepetition = repetitionBegin + timeToExecuteAllRepetitions;
        long base;
        long expectedNbExecutions;
        // Supposes that repetitionBegin always less or equal to END_SIMULATION
        if (END_SIMULATION < endExecutionOfRepetition) {
            base = END_SIMULATION - repetitionBegin;
            expectedNbExecutions = (base / stepBetweenRepetition) + 1;
        } else {
            expectedNbExecutions = nbRepetitions;
        }

        assertEquals(expectedNbExecutions, nbExecutions.get());
    }

    @Test
    public void scheduleInfinitelyThrowsExceptionIfWaitingTimeLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(new ExecutableTesting(), 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(new ExecutableTesting(), -1, 1));
    }

    @Test
    public void scheduleInfinitelyThrowsExceptionIfExecutionTimeStepLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(new ExecutableTesting(), 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableInfinitely(new ExecutableTesting(), 1, -1));
    }

    @Test
    public void scheduleInfinitelySchedulesAndExecuteAnExecutableAtTimeAndRepetitivelyAsDefine() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        long stepBetweenRepetition = 10;
        long repetitionBegin = Scheduler.NOW;

        final AtomicLong nbExecutions = new AtomicLong(0);
        final Map<Long, Long> mapExecutionAndTimeExecution = new HashMap<>();

        final Executable executable = () -> mapExecutionAndTimeExecution.put(nbExecutions.getAndIncrement(),
                SCHEDULER.getCurrentTime());

        SCHEDULER.scheduleExecutableInfinitely(executable, repetitionBegin, stepBetweenRepetition);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        Set<Map.Entry<Long, Long>> setExecutionAndTimeExecution = mapExecutionAndTimeExecution.entrySet();
        for (Map.Entry<Long, Long> entry : setExecutionAndTimeExecution) {
            long executionTimeExpected = entry.getKey() * stepBetweenRepetition + repetitionBegin;
            this.verifyNumber(entry.getValue(), executionTimeExpected, TIME_EXECUTION_TOLERANCE);
        }

        long timeToExecuteAllRepetitions = END_SIMULATION - repetitionBegin;
        long expectedNbExecutions = (timeToExecuteAllRepetitions / stepBetweenRepetition) + 1;

        assertEquals(expectedNbExecutions, nbExecutions.get());
    }

    @Test
    public void scheduleEventThrowsExceptionIfWaitingTimeIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleEvent(new EventTesting(A0.getAgentIdentifier(), A1.getAgentIdentifier(),
                        null), 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleEvent(new EventTesting(A0.getAgentIdentifier(), A1.getAgentIdentifier(),
                        null), -1));
    }

    @Test
    public void scheduleEventThrowsExceptionIfAgentReceiverIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleEvent(new EventTesting(A0.getAgentIdentifier(), null,
                        null), Scheduler.NOW));
    }

    @Test
    public void scheduleEventScheduleAndExecuteEventAtTime() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        // Schedule Event.

        long expectedEventExecutionTime = Scheduler.NOW;

        EventTesting testing = new EventTesting(A0.getAgentIdentifier(), A1.getAgentIdentifier(), null);
        SCHEDULER.scheduleEvent(testing, expectedEventExecutionTime);

        // Prepare the simulation.

        AgentManager agentManager = new LocalAgentManager();
        agentManager.addAgent(A0);
        agentManager.addAgent(A1);

        EnvironmentTesting environmentTesting = new EnvironmentTesting(0);
        Map<String, Environment> environmentMap = new HashMap<>();
        environmentMap.put(environmentTesting.getEnvironmentName(), environmentTesting);

        SimaSimulationTesting.runTestingSimulation(agentManager, SCHEDULER, SimaSimulation.TimeMode.UNSPECIFIED,
                environmentMap, null);

        blockSchedulerWatcher.waitUntilKilled();

        assertEquals(1, A1.getPassToProcessEvent());

        SimaSimulation.killSimulation();
    }

    // Methods.

    /**
     * Method use to schedule a long task to allow test method when the scheduler is running.
     * <p>
     * Tests are probabilist but the task take very long time compare to the speed of processor, in that way we can be
     * very sure that all tests will pass and are correct.
     */
    protected void scheduleLongTimeExecutable() {
        SCHEDULER.scheduleExecutableOnce(new LongTimeExecutable(), Scheduler.NOW);
    }

    protected void verifyNumber(long valToVerify, long expected, long delta) {
        assertTrue((expected - delta) <= valToVerify && valToVerify <= (expected + delta),
                "valToVerify = " + valToVerify + " expected = " + expected + " delta = " + delta + " min = "
                        + (expected - delta) + " max = " + (expected + delta));
    }

    // Inner classes.

    protected static class TestSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Variables.

        private int isPassToSchedulerStarted = 0;
        private int isPassToSchedulerKilled = 0;
        private int isPassToSimulationEndTimeReach = 0;
        private int isPassToNoExecutionToExecute = 0;

        // Methods.

        @Override
        public void schedulerStarted() {
            this.isPassToSchedulerStarted++;
        }

        @Override
        public void schedulerKilled() {
            this.isPassToSchedulerKilled++;
        }

        @Override
        public void simulationEndTimeReach() {
            this.isPassToSimulationEndTimeReach++;
        }

        @Override
        public void noExecutableToExecute() {
            this.isPassToNoExecutionToExecute++;
        }
    }

    protected static class ExecutableTesting implements Executable {

        // Variables.

        private long executedTime = -1;

        // Methods.

        @Override
        public void execute() {
            this.executedTime = SCHEDULER.getCurrentTime();
        }
    }

    protected static class LongTimeExecutable implements Executable {

        // Static.

        public static final long WAITING_TIME = 10_000L;

        // Methods.

        @Override
        public void execute() {
            try {
                Thread.sleep(WAITING_TIME);
            } catch (InterruptedException ignored) {
            }
        }
    }

    protected static class ExecutableTestingFeederV1 implements Executable {

        // Variables.

        private final long timeToBeExecuted;
        private final List<Executable> executableList;
        private long executedTime = -1;

        // Constructors.

        public ExecutableTestingFeederV1(long timeToBeExecuted, List<Executable> executableList) {
            this.executableList = executableList;
            this.executableList.add(this);

            this.timeToBeExecuted = timeToBeExecuted;
        }

        // Methods.

        @Override
        public void execute() {
            ExecutableTestingFeederV1 executableFeeder =
                    new ExecutableTestingFeederV1(SCHEDULER.getCurrentTime() + 10, this.executableList);
            SCHEDULER.scheduleExecutable(executableFeeder, 10, Scheduler.ScheduleMode.ONCE, -1, -1);
            this.executedTime = SCHEDULER.getCurrentTime();
        }
    }

    protected static class ExecutableTestingFeederV2 implements Executable {

        // Variables.

        private final long timeToBeExecuted;
        private final List<Executable> executableList;
        private long executedTime = -1;

        // Constructors.

        public ExecutableTestingFeederV2(long timeToBeExecuted, List<Executable> executableList) {
            this.executableList = executableList;
            this.executableList.add(this);

            this.timeToBeExecuted = timeToBeExecuted;
        }

        // Methods.

        @Override
        public void execute() {
            ExecutableTestingFeederV2 executableFeederV2 =
                    new ExecutableTestingFeederV2(SCHEDULER.getCurrentTime() + 10, this.executableList);
            SCHEDULER.scheduleExecutableOnce(executableFeederV2, 10);
            this.executedTime = SCHEDULER.getCurrentTime();
        }
    }

    /**
     * Can block the current thread to wait a new notification of the watched Scheduler. Only work for waiting start and
     * kill.
     */
    protected static class BlockSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Variables.

        private final Object START_LOCK = new Object();
        private final Object KILL_LOCK = new Object();

        private int nbKill = 0;
        private int nbBlockKill = 0;

        // Methods.

        /**
         * Block until the next call of {@link Scheduler#kill()}.
         */
        public void waitUntilKilled() {
            synchronized (this.KILL_LOCK) {
                if (this.nbBlockKill == this.nbKill)
                    try {
                        this.KILL_LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                this.nbBlockKill = this.nbKill;
            }
        }

        @Override
        public void schedulerStarted() {
            synchronized (this.START_LOCK) {
                this.START_LOCK.notifyAll();
            }
        }

        @Override
        public void schedulerKilled() {
            synchronized (this.KILL_LOCK) {
                this.KILL_LOCK.notifyAll();
                this.nbKill++;
            }
        }

        @Override
        public void simulationEndTimeReach() {

        }

        @Override
        public void noExecutableToExecute() {

        }
    }

}
