package sima.core.scheduler.multithread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.environment.event.Event;
import sima.core.scheduler.Scheduler;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRealTimeMultiThreadScheduler {

    // Variables.

    private static final long END_SIMULATION = 1000;

    private static RealTimeMultiThreadScheduler SCHEDULER;

    private static AgentTestImpl AGENT_0;
    private static AgentTestImpl AGENT_1;

    // Setup.

    @BeforeEach
    void setUp() {
        SCHEDULER = new RealTimeMultiThreadScheduler(END_SIMULATION, 5);

        AGENT_0 = new AgentTestImpl("AGENT_0");
        AGENT_1 = new AgentTestImpl("AGENT_1");
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
        SCHEDULER.kill();
        assertFalse(SCHEDULER.isStarted());
    }

    @Test
    public void testKill() {
        assertFalse(SCHEDULER.isStarted());

        assertFalse(SCHEDULER.kill());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isStarted());

        SCHEDULER.kill();
    }

    @Test
    public void testReStart() {
        assertFalse(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.start());
        assertFalse(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());

        // Kill to kill the ExecutorService
        SCHEDULER.kill();
    }

    // Inner classes.

    private static class AgentTestImpl extends AbstractAgent {

        // Constructors.

        public AgentTestImpl(String agentName) {
            super(agentName, 0, null);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onKill() {

        }

        @Override
        protected void treatNoProtocolEvent(Event event) {

        }

        @Override
        protected void treatEventWithNotFindProtocol(Event event) {

        }
    }

    private static class TestSchedulerWatcherBlocking extends TestSchedulerWatcher {

        // Variables.

        final Object LOCK_START = new Object();
        final Object LOCK_KILLED = new Object();
        final Object LOCK_END = new Object();
        final Object LOCK_NO_EXECUTABLE = new Object();


        // Methods.

        @Override
        public void schedulerStarted() {
            synchronized (LOCK_START) {
                super.schedulerStarted();
                LOCK_START.notifyAll();
            }
        }

        @Override
        public void schedulerKilled() {
            synchronized (LOCK_KILLED) {
                super.schedulerKilled();
                LOCK_KILLED.notifyAll();
            }
        }

        @Override
        public void simulationEndTimeReach() {
            synchronized (LOCK_END) {
                super.simulationEndTimeReach();
                LOCK_END.notifyAll();
            }
        }

        @Override
        public void noExecutableToExecute() {
            synchronized (LOCK_NO_EXECUTABLE) {
                super.noExecutableToExecute();
                LOCK_NO_EXECUTABLE.notifyAll();
            }
        }

    }

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
