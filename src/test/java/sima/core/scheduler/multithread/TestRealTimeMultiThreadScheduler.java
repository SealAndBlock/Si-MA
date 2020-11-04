package sima.core.scheduler.multithread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.scheduler.Action;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        /*assertTrue(SCHEDULER.isStarted());

        assertFalse(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());*/

        // Kill to kill the ExecutorService
        SCHEDULER.kill();
        assertFalse(SCHEDULER.isStarted());
    }

    @Test
    public void testKill() {
        assertFalse(SCHEDULER.isStarted());

        assertFalse(SCHEDULER.kill());

        assertTrue(SCHEDULER.start());
        /*assertTrue(SCHEDULER.isStarted());*/

        /*assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isStarted());*/

        SCHEDULER.kill();
    }

    @Test
    public void testReStart() {
        assertFalse(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.start());

        // Restart because already kill by no execution of executable
        assertTrue(SCHEDULER.start());
        /*assertTrue(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isStarted());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isStarted());*/

        // Kill to kill the ExecutorService
        SCHEDULER.kill();
    }

    @Test
    public void testWatcherReceivedStartedAndKilledNotification() {
        // Kill directly after the start because no executable to execute

        TestSchedulerWatcher testSchedulerWatcher = new TestSchedulerWatcher();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        assertTrue(SCHEDULER.start());
        /*assertFalse(SCHEDULER.start());
        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.kill());*/

        assertEquals(1, testSchedulerWatcher.isPassToSchedulerStarted());
        assertEquals(1, testSchedulerWatcher.isPassToSchedulerKilled());

        assertTrue(SCHEDULER.start());
        /*assertTrue(SCHEDULER.kill());*/

        assertEquals(2, testSchedulerWatcher.isPassToSchedulerStarted());
        assertEquals(2, testSchedulerWatcher.isPassToSchedulerKilled());
    }

    @Test
    public void testScheduleAgentActionException() {
        TestAction a1 = new TestAction(AGENT_0.getAgentIdentifier());

        // Test of exception

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(a1, 0,
                Scheduler.ScheduleMode.ONCE, -1, -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(a1, 1,
                Scheduler.ScheduleMode.REPEATED, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(a1, 1,
                Scheduler.ScheduleMode.REPEATED, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleExecutable(a1, 1,
                Scheduler.ScheduleMode.INFINITELY, 1, 0));
    }

    @Test
    public void testScheduleAgentActionOnce() {
        TestAction a0 = new TestAction(AGENT_0.getAgentIdentifier());
        TestAction a1 = new TestAction(AGENT_1.getAgentIdentifier());

        long time = 5;

        SCHEDULER.scheduleExecutable(a0, time, Scheduler.ScheduleMode.ONCE, -1, -1);
        SCHEDULER.scheduleExecutable(a0, time, Scheduler.ScheduleMode.ONCE, -1, -1);

        SCHEDULER.scheduleExecutable(a1, time, Scheduler.ScheduleMode.ONCE, -1, -1);

        List<MultiThreadScheduler.ExecutorThread> executorThreads = SCHEDULER.getExecutorThreadList();
        List<Executable> executables = executorThreads.stream().collect(ArrayList::new,
                (list, executorThread) ->
                        list.add(((RealTimeMultiThreadScheduler.RealTimeExecutorThread) executorThread).getExecutable())
                , ArrayList::addAll);
        assertTrue(executables.contains(a0));
        assertTrue(executables.contains(a1));
        assertEquals(3, executables.size());
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

    private static class TestAction extends Action {

        // Variables.

        private int isExecuted = 0;

        // Constructors.

        public TestAction(AgentIdentifier executorAgent) {
            super(executorAgent);
        }

        // Methods.

        @Override
        public void execute() {
            synchronized (this) {
                this.isExecuted++;
                this.notify();
            }
        }

        // Getters and Setters.

        public int getIsExecuted() {
            return isExecuted;
        }
    }

}
