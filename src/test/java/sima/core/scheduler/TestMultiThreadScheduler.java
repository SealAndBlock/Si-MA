package sima.core.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMultiThreadScheduler {

    // Variables.

    private static final long END_SIMULATION = 1000;

    private static MultiThreadScheduler SCHEDULER;

    // Setup.

    @BeforeEach
    void setUp() {
        SCHEDULER = new MultiThreadScheduler(END_SIMULATION, 5);
    }

    // Tests.

    @Test
    public void testAddSchedulerWatcher() {
        TestSchedulerWatcher testSchedulerWatcher = new TestSchedulerWatcher();

        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));
        assertFalse(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));
    }

    @Test
    public void testRemoveSchedulerWatcher() {
        TestSchedulerWatcher testSchedulerWatcher = new TestSchedulerWatcher();

        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));
        assertFalse(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        SCHEDULER.removeSchedulerWatcher(testSchedulerWatcher);

        // The watcher can be re adding because it has been removed.
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));
    }

    @Test
    public void testStart() {
        assertFalse(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        assertFalse(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        // Kill to kill the ExecutorService
        assertTrue(SCHEDULER.kill());
    }

    @Test
    public void testKill() {
        assertFalse(SCHEDULER.isStarted());

        assertFalse(SCHEDULER.kill());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isStarted());

        assertFalse(SCHEDULER.kill());
    }

    @Test
    public void testReStart() {
        assertFalse(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        // Kill to kill the ExecutorService
        assertTrue(SCHEDULER.kill());
    }

    @Test
    public void testWatcherReceivedStartedAndKilledNotification() {
        TestSchedulerWatcher testSchedulerWatcher = new TestSchedulerWatcher();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.start());
        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.kill());

        assertEquals(1, testSchedulerWatcher.isPassToSchedulerStarted());
        assertEquals(1, testSchedulerWatcher.isPassToSchedulerKilled());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.kill());

        assertEquals(2, testSchedulerWatcher.isPassToSchedulerStarted());
        assertEquals(2, testSchedulerWatcher.isPassToSchedulerKilled());
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

        // Getters and Setters.

        public int isPassToSchedulerStarted() {
            return isPassToSchedulerStarted;
        }

        public int isPassToSchedulerKilled() {
            return isPassToSchedulerKilled;
        }

        public int isPassToSimulationEndTimeReach() {
            return isPassToSimulationEndTimeReach;
        }

        public int isPassToNoExecutionToExecute() {
            return isPassToNoExecutionToExecute;
        }
    }

}
