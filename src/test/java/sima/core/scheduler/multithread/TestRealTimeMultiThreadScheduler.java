package sima.core.scheduler.multithread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.scheduler.Action;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
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
        assertFalse(SCHEDULER.isRunning());

        assertTrue(SCHEDULER.start());
        /*assertTrue(SCHEDULER.isRunning());

        assertFalse(SCHEDULER.start());
        assertTrue(SCHEDULER.isRunning());*/

        // Kill to kill the ExecutorService
        SCHEDULER.kill();
        assertFalse(SCHEDULER.isRunning());
    }

    @Test
    public void testKill() {
        assertFalse(SCHEDULER.isRunning());

        assertFalse(SCHEDULER.kill());

        assertTrue(SCHEDULER.start());
        /*assertTrue(SCHEDULER.isRunning());*/

        /*assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isRunning());*/

        SCHEDULER.kill();
    }

    @Test
    public void testReStart() {
        assertFalse(SCHEDULER.isRunning());

        assertTrue(SCHEDULER.start());

        // Restart because already kill by no execution of executable
        assertTrue(SCHEDULER.start());
        /*assertTrue(SCHEDULER.isRunning());

        assertTrue(SCHEDULER.kill());
        assertFalse(SCHEDULER.isRunning());

        assertTrue(SCHEDULER.start());
        assertTrue(SCHEDULER.isRunning());*/

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

    @Test
    public void testScheduleAgentActionRepeated() {
        TestAction a0 = new TestAction(AGENT_0.getAgentIdentifier());
        TestAction a1 = new TestAction(AGENT_1.getAgentIdentifier());

        long time = 5;
        long nbRepetitions = 3;
        long executionTimeStep = 5;

        SCHEDULER.scheduleExecutable(a0, time, Scheduler.ScheduleMode.REPEATED, nbRepetitions, executionTimeStep);
        SCHEDULER.scheduleExecutable(a1, time, Scheduler.ScheduleMode.REPEATED, nbRepetitions, executionTimeStep);

        List<MultiThreadScheduler.ExecutorThread> executorThreads = SCHEDULER.getExecutorThreadList();
        List<Executable> executables = executorThreads.stream().collect(ArrayList::new,
                (list, executorThread) ->
                        list.add(((RealTimeMultiThreadScheduler.RealTimeExecutorThread) executorThread).getExecutable())
                , ArrayList::addAll);

        assertEquals(nbRepetitions * 2, executables.size());

        Map<Long, Executable> map = new HashMap<>();
        executorThreads.forEach(executorThread ->
                map.put(((RealTimeMultiThreadScheduler.RealTimeExecutorThread) executorThread).getDelay(),
                        ((RealTimeMultiThreadScheduler.RealTimeExecutorThread) executorThread).getExecutable()));

        for (int i = (int) time; i < (nbRepetitions + executionTimeStep) + time; i += executionTimeStep) {
            assertNotNull(map.get((long) i));
        }
    }

    @Test
    public void testScheduleAgentActionInfinitely() {
        TestAction a0 = new TestAction(AGENT_0.getAgentIdentifier());
        TestAction a1 = new TestAction(AGENT_1.getAgentIdentifier());

        long time = 5;
        long executionTimeStep = 150;

        SCHEDULER.scheduleExecutable(a0, time, Scheduler.ScheduleMode.INFINITELY, -1, executionTimeStep);
        SCHEDULER.scheduleExecutable(a1, time, Scheduler.ScheduleMode.INFINITELY, -1, executionTimeStep);

        List<MultiThreadScheduler.ExecutorThread> executorThreads = SCHEDULER.getExecutorThreadList();

        executorThreads.forEach(executor -> assertTrue(
                ((RealTimeMultiThreadScheduler.RealTimeExecutorThread) executor).getDelay()
                        <= END_SIMULATION));

        Map<Long, Executable> map = new HashMap<>();
        executorThreads.forEach(executorThread ->
                map.put(((RealTimeMultiThreadScheduler.RealTimeExecutorThread) executorThread).getDelay(),
                        ((RealTimeMultiThreadScheduler.RealTimeExecutorThread) executorThread).getExecutable()));

        for (int i = (int) time; i <= END_SIMULATION; i += executionTimeStep) {
            assertNotNull(map.get((long) i), "Time = " + i);
        }
    }

    @Test
    public void testScheduleAgentActionWithoutAgent() {
        TestAction a0 = new TestAction(null);
        TestAction a1 = new TestAction(null);

        long time = 5;

        SCHEDULER.scheduleExecutableOnce(a0, time);
        SCHEDULER.scheduleExecutableOnce(a1, time);

        List<MultiThreadScheduler.ExecutorThread> executorThreads = SCHEDULER.getExecutorThreadList();
        assertEquals(2, executorThreads.size());
    }

    @Test
    public void testWatcherNoExecutableToExecute() {
        // Kill directly after the start because no executable to execute

        TestSchedulerWatcher testSchedulerWatcher = new TestSchedulerWatcher();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        assertTrue(SCHEDULER.start());

        assertEquals(1, testSchedulerWatcher.isPassToSchedulerStarted());
        assertEquals(1, testSchedulerWatcher.isPassToNoExecutableToExecute());

        // Kill to kill the ExecutorService
        SCHEDULER.kill();
    }

    @Test
    public void testExecutionOfScheduledAction() {
        TestAction a1 = new TestAction(AGENT_0.getAgentIdentifier());

        TestSchedulerWatcher testSchedulerWatcher = new TestSchedulerWatcher();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        long time = 500;

        SCHEDULER.scheduleExecutableOnce(a1, time);

        assertTrue(SCHEDULER.start());

        synchronized (a1) {
            while (a1.isExecuted == 0) {
                try {
                    a1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, a1.isExecuted);
        }

        assertTrue(time <= SCHEDULER.getCurrentTime());
    }

    @Test
    public void testExecutionOfSeveralScheduledActions() {
        TestAction a1 = new TestAction(AGENT_0.getAgentIdentifier());
        TestAction a2 = new TestAction(AGENT_1.getAgentIdentifier());

        TestSchedulerWatcher testSchedulerWatcher = new TestSchedulerWatcher();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        long t1 = 500;
        long t2 = 750;

        SCHEDULER.scheduleExecutableOnce(a1, t1);
        SCHEDULER.scheduleExecutableOnce(a2, t2);

        assertTrue(SCHEDULER.start());

        synchronized (a1) {
            while (a1.isExecuted == 0) {
                try {
                    a1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, a1.isExecuted);
        }

        synchronized (a2) {
            while (a2.isExecuted == 0) {
                try {
                    a2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, a2.isExecuted);
        }

        assertTrue(t2 <= SCHEDULER.getCurrentTime());
    }

    @Test
    public void TestReachEndOfSimulationWithAgentAction() {
        TestAction a1 = new TestAction(AGENT_0.getAgentIdentifier());
        TestAction a2 = new TestAction(AGENT_1.getAgentIdentifier());
        TestAction a3 = new TestAction(AGENT_0.getAgentIdentifier());

        TestSchedulerWatcherBlocking testSchedulerWatcher = new TestSchedulerWatcherBlocking();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        long t1 = 500;
        long t2 = 750;
        long t3 = END_SIMULATION + 50;

        SCHEDULER.scheduleExecutableOnce(a1, t1);
        SCHEDULER.scheduleExecutableOnce(a2, t2);
        SCHEDULER.scheduleExecutableOnce(a3, t3);

        assertTrue(SCHEDULER.start());

        synchronized (a1) {
            while (a1.isExecuted == 0) {
                try {
                    a1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, a1.isExecuted);
        }

        synchronized (a2) {
            while (a2.isExecuted == 0) {
                try {
                    a2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, a2.isExecuted);
        }

        synchronized (testSchedulerWatcher.LOCK_NO_EXECUTABLE) {
            while (testSchedulerWatcher.isPassToNoExecutableToExecute() == 0) {
                try {
                    testSchedulerWatcher.LOCK_NO_EXECUTABLE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, testSchedulerWatcher.isPassToNoExecutableToExecute());
        }

        assertEquals(0, a3.isExecuted);
        assertTrue(t2 <= SCHEDULER.getCurrentTime());
    }

    @Test
    public void TestReachEndOfSimulationWithExecutable() {
        TestExecutable e1 = new TestExecutable();
        TestExecutable e2 = new TestExecutable();
        TestExecutable e3 = new TestExecutable();

        TestSchedulerWatcherBlocking testSchedulerWatcher = new TestSchedulerWatcherBlocking();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        long t1 = 500;
        long t2 = 750;
        long t3 = END_SIMULATION + 50;

        SCHEDULER.scheduleExecutableOnce(e1, t1);
        SCHEDULER.scheduleExecutableOnce(e2, t2);
        SCHEDULER.scheduleExecutableOnce(e3, t3);

        assertTrue(SCHEDULER.start());

        synchronized (e1) {
            while (e1.isExecuted == 0) {
                try {
                    e1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, e1.isExecuted);
        }

        synchronized (e2) {
            while (e2.isExecuted == 0) {
                try {
                    e2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, e2.isExecuted);
        }

        synchronized (testSchedulerWatcher.LOCK_NO_EXECUTABLE) {
            while (testSchedulerWatcher.isPassToNoExecutableToExecute() == 0) {
                try {
                    testSchedulerWatcher.LOCK_NO_EXECUTABLE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, testSchedulerWatcher.isPassToNoExecutableToExecute());
        }

        assertEquals(0, e3.isExecuted);
        assertTrue(t2 <= SCHEDULER.getCurrentTime());
    }

    @Test
    public void testExecutionCombineAgentActionAndExecutable() {
        TestAction a1 = new TestAction(AGENT_0.getAgentIdentifier());
        TestAction a2 = new TestAction(AGENT_1.getAgentIdentifier());
        TestAction a3 = new TestAction(AGENT_0.getAgentIdentifier());

        TestExecutable e1 = new TestExecutable();
        TestExecutable e2 = new TestExecutable();
        TestExecutable e3 = new TestExecutable();

        TestSchedulerWatcherBlocking testSchedulerWatcher = new TestSchedulerWatcherBlocking();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        long t1 = 500;
        long t2 = 750;
        long t3 = END_SIMULATION + 50;

        SCHEDULER.scheduleExecutableOnce(a1, t1);
        SCHEDULER.scheduleExecutableOnce(a2, t2);
        SCHEDULER.scheduleExecutableOnce(a3, t3);

        SCHEDULER.scheduleExecutableOnce(e1, t1);
        SCHEDULER.scheduleExecutableOnce(e2, t2);
        SCHEDULER.scheduleExecutableOnce(e3, t3);

        assertTrue(SCHEDULER.start());

        synchronized (e1) {
            while (e1.isExecuted == 0) {
                try {
                    e1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, e1.isExecuted);
        }

        synchronized (e2) {
            while (e2.isExecuted == 0) {
                try {
                    e2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, e2.isExecuted);
        }

        synchronized (a1) {
            while (a1.isExecuted == 0) {
                try {
                    a1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, a1.isExecuted);
        }

        synchronized (a2) {
            while (a2.isExecuted == 0) {
                try {
                    a2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, a2.isExecuted);
        }

        synchronized (testSchedulerWatcher.LOCK_NO_EXECUTABLE) {
            while (testSchedulerWatcher.isPassToNoExecutableToExecute() == 0) {
                try {
                    testSchedulerWatcher.LOCK_NO_EXECUTABLE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, testSchedulerWatcher.isPassToNoExecutableToExecute());
        }

        assertEquals(0, a3.getIsExecuted());
        assertEquals(0, e3.getIsExecuted());
        assertTrue(t2 <= SCHEDULER.getCurrentTime());
    }

    @Test
    public void testExecutionFeedSchedule() {
        TestExecutableFeeder e1 = new TestExecutableFeeder(SCHEDULER, 100);

        TestSchedulerWatcherBlocking testSchedulerWatcher = new TestSchedulerWatcherBlocking();
        assertTrue(SCHEDULER.addSchedulerWatcher(testSchedulerWatcher));

        SCHEDULER.scheduleExecutableOnce(e1, 100);

        assertTrue(SCHEDULER.start());

        synchronized (testSchedulerWatcher.LOCK_NO_EXECUTABLE) {
            while (testSchedulerWatcher.isPassToNoExecutableToExecute() == 0) {
                try {
                    testSchedulerWatcher.LOCK_NO_EXECUTABLE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertEquals(1, testSchedulerWatcher.isPassToNoExecutableToExecute());
        }
    }

    @Test
    public void testScheduleEventThrows() {
        TestEvent e1 = new TestEvent(AGENT_0.getAgentIdentifier(), AGENT_1.getAgentIdentifier(), null);
        TestEvent e2 = new TestEvent(AGENT_0.getAgentIdentifier(), null, null);

        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleEvent(e1, -1));
        assertThrows(IllegalArgumentException.class, () -> SCHEDULER.scheduleEvent(e2, 1));
    }

    // Inner classes.

    private static class TestEvent extends Event {

        // Constructors.

        public TestEvent(AgentIdentifier sender, AgentIdentifier receiver, ProtocolIdentifier protocolTargeted) {
            super(sender, receiver, protocolTargeted);
        }
    }

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

        public int isPassToNoExecutableToExecute() {
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

    private static class TestExecutable implements Executable {

        // Variables.

        private int isExecuted = 0;

        // Methods.

        @Override
        public void execute() {
            synchronized (this) {
                this.isExecuted++;
                this.notifyAll();
            }
        }

        // Getters and Setters.

        public int getIsExecuted() {
            return isExecuted;
        }
    }

    private static class TestExecutableFeeder implements Executable {

        // Variables.

        private final Scheduler scheduler;
        private final long mustBeExecutedAt;

        // Constructors.

        public TestExecutableFeeder(Scheduler scheduler, long mustBeExecutedAt) {
            this.scheduler = scheduler;
            this.mustBeExecutedAt = mustBeExecutedAt;
        }

        // Methods.

        @Override
        public void execute() {
            /*assertEquals(this.mustBeExecutedAt, this.scheduler.getCurrentTime());*/
            this.scheduler.scheduleExecutableOnce(new TestExecutableFeeder(this.scheduler,
                    this.scheduler.getCurrentTime() + 100), 100);
        }
    }

}
