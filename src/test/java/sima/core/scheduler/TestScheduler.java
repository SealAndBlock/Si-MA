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

    // Methods.

    protected void scheduleLongTimeExecutable() {
        SCHEDULER.scheduleExecutableOnce(new LongTimeExecutable(), 0);
    }

    // Inner classes.

    private static class TestSchedulerWatcher implements Scheduler.SchedulerWatcher {

        @Override
        public void schedulerStarted() {

        }

        @Override
        public void schedulerKilled() {

        }

        @Override
        public void simulationEndTimeReach() {

        }

        @Override
        public void noExecutableToExecute() {

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

}
