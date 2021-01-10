package sima.core.simulation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.exception.EnvironmentConstructionException;
import sima.core.exception.SimaSimulationAlreadyRunningException;
import sima.core.exception.SimaSimulationSetupConstructionException;
import sima.core.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public abstract class WrongGlobalTestSimaSimulation extends SimaTest {

    // Static.

    private static TestSimulationSchedulerWatcher SCHEDULER_WATCHER;
    private static TestSimaWatcher SIMA_WATCHER;

    private static final int NB_THREAD_THREAD_EXECUTOR = 8;
    private static final long END_SIMULATION = 10_000L;

    private static final String SAME_NAME_ENVIRONMENT = "ENV_NAME";

    // Set up.

    @Override
    public void verifyAndSetup() {
        SCHEDULER_WATCHER = new TestSimulationSchedulerWatcher();
        SIMA_WATCHER = new TestSimaWatcher();
    }

    // Tests.

    @Test
    public void throwsNullPointerExceptionWhenSimulationNotRunning() {
        // Simulation not running.
        assertFalse(SimaSimulation.simaSimulationIsRunning());

        // Test if all methods throw NullPointerException.
        assertThrows(NullPointerException.class, SimaSimulation::getScheduler);
        assertThrows(NullPointerException.class, SimaSimulation::currentTime);
        assertThrows(NullPointerException.class, () -> SimaSimulation.getAgentFromIdentifier(null));
        assertThrows(NullPointerException.class, SimaSimulation::getAllEnvironments);
        assertThrows(NullPointerException.class, () -> SimaSimulation.getEnvironmentFromName(null));
        assertThrows(NullPointerException.class, SimaSimulation::timeMode);
    }

    @Test
    public void throwsExceptionWhenSimulationAlreadyRunning() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        envClasses.add(TestEnvironment.class);

        Scheduler.SchedulerWatcher schedulerWatcher = new Scheduler.SchedulerWatcher() {
            @Override
            public void schedulerStarted() {
                // Nothing
            }

            @Override
            public void schedulerKilled() {
                assertThrows(SimaSimulationAlreadyRunningException.class, () ->
                        // Run which launches no exception if the simulation is not already running.
                        SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                                Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses,
                                null, SCHEDULER_WATCHER, SIMA_WATCHER));
            }

            @Override
            public void simulationEndTimeReach() {
                // Nothing
            }

            @Override
            public void noExecutableToExecute() {
                // Nothing
            }
        };

        try {
            // Run which launches no exception.
            SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                    Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                    schedulerWatcher, SIMA_WATCHER);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void throwsExceptionWithMonoThreadRealTime() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        envClasses.add(TestEnvironment.class);

        assertThrows(UnsupportedOperationException.class, () ->
                SimaSimulation.runSimulation(Scheduler.TimeMode.REAL_TIME,
                        Scheduler.SchedulerType.MONO_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void throwsExceptionWithMonoThreadDiscreteTime() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        envClasses.add(TestEnvironment.class);

        assertThrows(UnsupportedOperationException.class, () ->
                SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                        Scheduler.SchedulerType.MONO_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void notThrowsExceptionWithMultiThreadRealTime() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        envClasses.add(TestEnvironment.class);

        try {
            SimaSimulation.runSimulation(Scheduler.TimeMode.REAL_TIME,
                    Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                    SCHEDULER_WATCHER, SIMA_WATCHER);
        } catch (Exception e) {
            fail(e);
        }

        SimaSimulation.waitKillSimulation();

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(1, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SCHEDULER_WATCHER.isPassToNoExecutionToExecute);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void notThrowsExceptionWithMultiThreadDiscreteTime() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        envClasses.add(TestEnvironment.class);

        try {
            SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                    Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                    SCHEDULER_WATCHER, SIMA_WATCHER);
        } catch (Exception e) {
            fail(e);
        }

        SimaSimulation.waitKillSimulation();

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(1, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SCHEDULER_WATCHER.isPassToNoExecutionToExecute);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void throwsExceptionWithNullEnvironmentList() {
        assertThrows(IllegalArgumentException.class, () ->
                SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                        Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, null, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void throwsExceptionWithEmptyEnvironmentList() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () ->
                SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                        Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void throwsExceptionIfTwoEnvironmentsWithSameName() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        assertTrue(envClasses.add(SameNameEnvironment1.class));
        assertTrue(envClasses.add(SameNameEnvironment2.class));

        assertThrows(IllegalArgumentException.class,
                () -> SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                        Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SIMA_WATCHER.isPassKilled);

        envClasses.clear();
        assertTrue(envClasses.add(TestEnvironment.class));
        assertTrue(envClasses.add(TestEnvironment.class));
        assertThrows(IllegalArgumentException.class,
                () -> SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                        Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(2, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void throwsExceptionIfWrongConstructorEnvironment() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        envClasses.add(WrongConstructorEnvironment.class);

        assertThrows(EnvironmentConstructionException.class,
                () -> SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                        Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void notThrowsExceptionIfSeveralCorrectEnvironment() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        assertTrue(envClasses.add(TestEnvironment.class));
        assertTrue(envClasses.add(TestEnvironmentOther.class));

        try {
            SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                    Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                    SCHEDULER_WATCHER, SIMA_WATCHER);
        } catch (Exception e) {
            fail(e);
        }

        SimaSimulation.waitKillSimulation();

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(1, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SCHEDULER_WATCHER.isPassToNoExecutionToExecute);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void notThrowsExceptionWithNullSimulationSetup() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        assertTrue(envClasses.add(TestEnvironment.class));

        try {
            SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                    Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, null,
                    SCHEDULER_WATCHER, SIMA_WATCHER);
        } catch (Exception e) {
            fail(e);
        }

        SimaSimulation.waitKillSimulation();

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(1, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SCHEDULER_WATCHER.isPassToNoExecutionToExecute);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void throwsExceptionWithWrongConstructorSimulationSetup() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        assertTrue(envClasses.add(TestEnvironment.class));

        assertThrows(SimaSimulationSetupConstructionException.class,
                () -> SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                        Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses,
                        WrongConstructorSimulationSetup.class, SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(0, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void notThrowsExceptionWithCorrectSimulationSetup() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        assertTrue(envClasses.add(TestEnvironment.class));

        try {
            SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                    Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, TestSimulationSetup.class,
                    SCHEDULER_WATCHER, SIMA_WATCHER);
        } catch (Exception e) {
            fail(e);
        }

        SimaSimulation.waitKillSimulation();

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(1, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SCHEDULER_WATCHER.isPassToNoExecutionToExecute);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    @Test
    public void simulationSetupMethodIsCalled() {
        List<Class<? extends Environment>> envClasses = new ArrayList<>();
        assertTrue(envClasses.add(TestEnvironment.class));

        try {
            SimaSimulation.runSimulation(Scheduler.TimeMode.DISCRETE_TIME,
                    Scheduler.SchedulerType.MULTI_THREAD, NB_THREAD_THREAD_EXECUTOR, END_SIMULATION, envClasses, TestSimulationSetup.class,
                    SCHEDULER_WATCHER, SIMA_WATCHER);
        } catch (Exception e) {
            fail(e);
        }

        SimaSimulation.waitKillSimulation();

        assertEquals(1, TestSimulationSetup.isPassSetupSimulation);
        TestSimulationSetup.reset();

        assertFalse(SimaSimulation.simaSimulationIsRunning());
        assertEquals(1, SIMA_WATCHER.isPassStarted);
        assertEquals(1, SCHEDULER_WATCHER.isPassToNoExecutionToExecute);
        assertEquals(1, SIMA_WATCHER.isPassKilled);
    }

    // Inner classes.

    private static class WrongConstructorSimulationSetup implements SimulationSetup {

        // Methods.

        @Override
        public void setupSimulation() {

        }
    }

    private static class TestSimulationSetup implements SimulationSetup {

        // static.

        private static int isPassSetupSimulation = 0;

        // Variables.

        // Constructors.

        public TestSimulationSetup(Map<String, String> args) {
        }

        // Methods.

        private static void reset() {
            isPassSetupSimulation = 0;
        }

        @Override
        public void setupSimulation() {
            isPassSetupSimulation++;
        }
    }

    private static class TestSimulationSchedulerWatcher implements Scheduler.SchedulerWatcher {

        // Variables.

        private int isPassToNoExecutionToExecute = 0;

        // Methods.

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
            this.isPassToNoExecutionToExecute++;
        }

        // Getters and Setters.
    }

    private static class TestSimaWatcher implements SimaSimulation.SimaWatcher {

        // Variables.

        private int isPassStarted = 0;
        private int isPassKilled = 0;

        // Methods.

        @Override
        public void simulationStarted() {
            this.isPassStarted++;
        }

        @Override
        public void simulationKilled() {
            this.isPassKilled++;
        }

        // Getters and Setters.
    }

    private static class SameNameEnvironment1 extends Environment {

        // Constructors

        public SameNameEnvironment1(String environmentName, Map<String, String> args) {
            super(SAME_NAME_ENVIRONMENT, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
            return false;
        }

        @Override
        protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {

        }

        @Override
        protected void sendEventWithNullReceiver(Event event) {

        }

        @Override
        protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
            return false;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {

        }

        @Override
        public void processEvent(Event event) {

        }
    }

    private static class SameNameEnvironment2 extends Environment {

        // Constructors.

        public SameNameEnvironment2(String environmentName, Map<String, String> args) {
            super(SAME_NAME_ENVIRONMENT, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
            return false;
        }

        @Override
        protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {

        }

        @Override
        protected void sendEventWithNullReceiver(Event event) {

        }

        @Override
        protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
            return false;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {

        }

        @Override
        public void processEvent(Event event) {

        }
    }

    private static class WrongConstructorEnvironment extends Environment {

        // Constructors.

        public WrongConstructorEnvironment() {
            super("", null);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
            return false;
        }

        @Override
        protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {

        }

        @Override
        protected void sendEventWithNullReceiver(Event event) {

        }

        @Override
        protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
            return false;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {

        }

        @Override
        public void processEvent(Event event) {

        }
    }

    private static class TestEnvironment extends Environment {

        // Constants.

        private static final long NETWORK_DELAY = 10L;

        // Constructors.

        /**
         * Constructs an {@link Environment} with an unique name and an map of arguments.
         * <p>
         * All inherited classes must have this constructor to allow the use of the java reflexivity.
         *
         * @param environmentName the sima.core.environment name
         * @param args            arguments map (map argument name with the argument)
         */
        public TestEnvironment(String environmentName, Map<String, String> args) {
            super(environmentName, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
            return true;
        }

        @Override
        protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
            // Nothing to do
        }

        @Override
        protected void sendEventWithNullReceiver(Event event) {
            // Nothing to do
        }

        @Override
        protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
            return true;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {
            SimaSimulation.getScheduler().scheduleEvent(event, NETWORK_DELAY);
        }

        @Override
        public void processEvent(Event event) {
            // Nothing to do
        }
    }

    private static class TestEnvironmentOther extends Environment {

        // Constants.

        private static final long NETWORK_DELAY = 10L;

        // Constructors.

        /**
         * Constructs an {@link Environment} with an unique name and an map of arguments.
         * <p>
         * All inherited classes must have this constructor to allow the use of the java reflexivity.
         *
         * @param environmentName the sima.core.environment name
         * @param args            arguments map (map argument name with the argument)
         */
        public TestEnvironmentOther(String environmentName, Map<String, String> args) {
            super(environmentName, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
            return true;
        }

        @Override
        protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
            // Nothing to do
        }

        @Override
        protected void sendEventWithNullReceiver(Event event) {
            // Nothing to do
        }

        @Override
        protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
            return true;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {
            SimaSimulation.getScheduler().scheduleEvent(event, NETWORK_DELAY);
        }

        @Override
        public void processEvent(Event event) {
            // Nothing to do
        }
    }

}
