package sima.core.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.scheduler.exception.NotSchedulableTimeException;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that all Classes which implements {@link Scheduler} must pass.
 */
@Disabled
public abstract class TestScheduler {

    // Static.

    protected static Scheduler SCHEDULER;

    /**
     * To specify and must be the same which is specify when {@link #SCHEDULER} is instantiate.
     * <p>
     * In that way we can verify if the Scheduler returns the right value with the method
     * {@link Scheduler#getEndSimulation()}.
     */
    protected static long END_SIMULATION;

    /**
     * Define the tolerance when we test when the scheduler execute executable. Example: If a executable must be execute
     * at time 5, in function of the type of the scheduler, it is not possible to it to execute th executable at 5.
     * Therefore the test verify if the execution time is equal to 5 +/- TIME_EXECUTION_TOLERANCE.
     */
    protected static long TIME_EXECUTION_TOLERANCE = 0;

    // Setup.

    @BeforeEach
    public void setup() {
        this.initialize();
    }

    /**
     * Here in this function you initialize all need variable for tests.
     * <p>
     * Needed variables are: <strong>{@link #END_SIMULATION}, {@link #SCHEDULER},
     * {@link #TIME_EXECUTION_TOLERANCE}</strong>
     */
    protected abstract void initialize();

    // Tests.

    @Test
    public void endSimulationIsCorrect() {
        assertEquals(END_SIMULATION, SCHEDULER.getEndSimulation());

        assertTrue(END_SIMULATION >= 2);
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
        TestExecutable e0 = new TestExecutable();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.ONCE, -1, -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.REPEATED, -1, -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 0,
                Scheduler.ScheduleMode.INFINITELY, -1, -1));
    }

    @Test
    public void scheduleExecutableThrowsExceptionWithRepeatedScheduleModeIfNbRepetitionsIsLessThanOne() {
        TestExecutable e0 = new TestExecutable();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED, 0, 1));
    }

    @Test
    public void scheduleExecutableThrowsExceptionWithAllRepeatedScheduleModeIfExecutionTimeStepIsLessThanOne() {
        TestExecutable e0 = new TestExecutable();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.INFINITELY, -1, 0));
    }

    @Test
    public void scheduleExecutableIgnoreNbRepetitionsAndExecutionTimeStepInScheduleModeOnce() {
        TestExecutable e0 = new TestExecutable();

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

        TestExecutable e0 = new TestExecutable();

        SCHEDULER.scheduleExecutable(e0, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        blockSchedulerWatcher.waitUntilKilled();

        this.verifyNumber(e0.executedTime, Scheduler.NOW, TIME_EXECUTION_TOLERANCE);
    }

    @Test
    public void schedulerExecuteAtTimeSeveralExecutablesScheduledOnceTime() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        TestExecutable e0 = new TestExecutable();
        TestExecutable e1 = new TestExecutable();
        TestExecutable e2 = new TestExecutable();

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

        TestExecutableFeederV1 eF0 = new TestExecutableFeederV1(Scheduler.NOW, executables);

        SCHEDULER.scheduleExecutable(eF0, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        for (Executable executable : executables) {
            TestExecutableFeederV1 executableFeeder = (TestExecutableFeederV1) executable;
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
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        // Useless.
        List<Executable> executables = new Vector<>();

        TestExecutableFeederV1 eF0 = new TestExecutableFeederV1(Scheduler.NOW, executables);

        SCHEDULER.scheduleExecutable(eF0, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        blockSchedulerWatcher.waitUntilKilled();

        assertEquals(1, watcher.isPassToSchedulerKilled);
        assertEquals(0, watcher.isPassToNoExecutionToExecute);
        assertEquals(1, watcher.isPassToSimulationEndTimeReach);
    }

    @Test
    public void scheduleAtSpecificTimeThrowsExceptionIfSpecifiedTimeIsLessThanOne() {
        TestExecutable e0 = new TestExecutable();

        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(e0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableAtSpecificTime(e0, -1));
    }

    @Test
    public void scheduleAtSpecificTimeThrowsExceptionIfSpecifiedTimeIsAlreadyPass() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        TestSchedulerWatcher watcher = new TestSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(watcher);

        long timeToBeExecuted = (long) (0.5 * SCHEDULER.getEndSimulation());

        final AtomicBoolean isPassed = new AtomicBoolean(false);

        Executable e = () -> {
            isPassed.set(true);
            this.verifyNumber(SCHEDULER.getCurrentTime(), timeToBeExecuted, TIME_EXECUTION_TOLERANCE);

            // Try to schedule an Executable at a passed timed.
            assertThrows(NotSchedulableTimeException.class,
                    () -> SCHEDULER.scheduleExecutableAtSpecificTime(new TestExecutable(),
                            SCHEDULER.getCurrentTime()));
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
    public void scheduleOnceThrowsExceptionIfWaitingIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableOnce(new TestExecutable(), 0));
        assertThrows(IllegalArgumentException.class,
                () -> SCHEDULER.scheduleExecutableOnce(new TestExecutable(), -1));
    }

    @Test
    public void scheduleOnceScheduleAndExecuteOneTimeAnExecutableAtTime() {
        BlockSchedulerWatcher blockSchedulerWatcher = new BlockSchedulerWatcher();
        SCHEDULER.addSchedulerWatcher(blockSchedulerWatcher);

        List<Executable> executables = new Vector<>();

        TestExecutableFeederV2 eF0 = new TestExecutableFeederV2(Scheduler.NOW, executables);

        SCHEDULER.scheduleExecutableOnce(eF0, Scheduler.NOW);

        assertTrue(SCHEDULER.start());

        // Finish by not reaching time.
        blockSchedulerWatcher.waitUntilKilled();

        for (Executable executable : executables) {
            TestExecutableFeederV2 executableFeeder = (TestExecutableFeederV2) executable;
            if (executableFeeder.executedTime != -1)
                // Executable executed
                this.verifyNumber(executableFeeder.executedTime, executableFeeder.timeToBeExecuted,
                        TIME_EXECUTION_TOLERANCE);
            else
                // Executable normally out of the end of the simulation
                assertTrue(executableFeeder.timeToBeExecuted > SCHEDULER.getEndSimulation());
        }
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

    private static class TestSchedulerWatcher implements Scheduler.SchedulerWatcher {

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

    protected static class TestExecutable implements Executable {

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

    protected static class TestExecutableFeederV1 implements Executable {

        // Variables.

        private final long timeToBeExecuted;
        private final List<Executable> executableList;
        private long executedTime = -1;

        // Constructors.

        public TestExecutableFeederV1(long timeToBeExecuted, List<Executable> executableList) {
            this.executableList = executableList;
            this.executableList.add(this);

            this.timeToBeExecuted = timeToBeExecuted;
        }

        // Methods.

        @Override
        public void execute() {
            TestExecutableFeederV1 executableFeeder =
                    new TestExecutableFeederV1(SCHEDULER.getCurrentTime() + 10, this.executableList);
            SCHEDULER.scheduleExecutable(executableFeeder, 10, Scheduler.ScheduleMode.ONCE, -1, -1);
            this.executedTime = SCHEDULER.getCurrentTime();
        }
    }

    protected static class TestExecutableFeederV2 implements Executable {

        // Variables.

        private final long timeToBeExecuted;
        private final List<Executable> executableList;
        private long executedTime = -1;

        // Constructors.

        public TestExecutableFeederV2(long timeToBeExecuted, List<Executable> executableList) {
            this.executableList = executableList;
            this.executableList.add(this);

            this.timeToBeExecuted = timeToBeExecuted;
        }

        // Methods.

        @Override
        public void execute() {
            TestExecutableFeederV2 executableFeederV2 =
                    new TestExecutableFeederV2(SCHEDULER.getCurrentTime() + 10, this.executableList);
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

        // Methods.

        /*/**
         * Block until the next call of {@link Scheduler#start()}.
         */
        /*public void waitUntilStarted() {
            synchronized (START_LOCK) {
                try {
                    START_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/

        /**
         * Block until the next call of {@link Scheduler#kill()}.
         */
        public void waitUntilKilled() {
            synchronized (KILL_LOCK) {
                try {
                    KILL_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void schedulerStarted() {
            synchronized (START_LOCK) {
                START_LOCK.notifyAll();
            }
        }

        @Override
        public void schedulerKilled() {
            synchronized (KILL_LOCK) {
                KILL_LOCK.notifyAll();
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
