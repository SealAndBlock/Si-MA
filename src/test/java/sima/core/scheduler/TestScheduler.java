package sima.core.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that all Classes which implements {@link Scheduler} must pass.
 */
@Disabled
public class TestScheduler {

    // Static.

    protected static Scheduler SCHEDULER;

    /**
     * Define the tolerance when we test when the scheduler execute executable. Example: If a executable must be execute
     * at time 5, in function of the type of the scheduler, it is not possible to it to execute th executable at 5.
     * Therefore the test verify if the execution time is equal to 5 +/- TIME_EXECUTION_TOLERANCE.
     */
    protected static long TIME_EXECUTION_TOLERANCE = 0;

    // Tests.

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

        assertEquals(1, watcher.isPassToNoExecutionToExecute);
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
    public void scheduleExecutableThrowsExceptionWithAllRepeatedScheduleModeIfNbRepetitionsIsLessThanOne() {
        TestExecutable e0 = new TestExecutable();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.INFINITELY, 0, 1));
    }

    @Test
    public void scheduleExecutableThrowsExceptionWithAllRepeatedScheduleModeIfExecutionTimeStepIsLessThanOne() {
        TestExecutable e0 = new TestExecutable();

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.REPEATED, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(e0, 1,
                Scheduler.ScheduleMode.INFINITELY, 1, 0));
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
        SCHEDULER.scheduleExecutable(e2, Scheduler.NOW + 1, Scheduler.ScheduleMode.ONCE, -1, -1);

        assertTrue(SCHEDULER.start());

        blockSchedulerWatcher.waitUntilKilled();

        this.verifyNumber(e0.executedTime, Scheduler.NOW, TIME_EXECUTION_TOLERANCE);
        this.verifyNumber(e1.executedTime, Scheduler.NOW + 1, TIME_EXECUTION_TOLERANCE);
        this.verifyNumber(e2.executedTime, Scheduler.NOW + 2, TIME_EXECUTION_TOLERANCE);
    }

    @Test
    public void schedulerExecuteAtTimeSeveralExecutableAfterTheStart() {
        // TODO
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
        assertTrue((expected - delta) <= valToVerify && valToVerify <= (expected + delta));
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

        public void reset() {
            this.isPassToSchedulerStarted = 0;
            this.isPassToSchedulerKilled = 0;
            this.isPassToSimulationEndTimeReach = 0;
            this.isPassToNoExecutionToExecute = 0;
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Can block the current thread to wait a new notification of the watched Scheduler. Only work for waiting start and
     * kill.
     */
    protected class BlockSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Variables.

        private final Object START_LOCK = new Object();
        private final Object KILL_LOCK = new Object();

        // Methods.

        /**
         * Block until the next call of {@link Scheduler#start()}.
         */
        public void waitUntilStarted() {
            synchronized (START_LOCK) {
                try {
                    START_LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

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
