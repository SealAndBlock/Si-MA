package sima.core.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.scheduler.Scheduler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimaSimulation {

    // Static.

    private static TestSimulationSchedulerWatcher SCHEDULER_WATCHER;
    private static TestSimaWatcher SIMA_WATCHER;

    // Set up.

    @BeforeEach
    void setUp() {
        SCHEDULER_WATCHER = new TestSimulationSchedulerWatcher();
        SIMA_WATCHER = new TestSimaWatcher();
    }

    // Tests.

    @Test
    public void testSimulationIsRunning() {
        // Simulation not running.
        assertFalse(SimaSimulation.simulationIsRunning());

        // Test if all methods throw NullPointerException.
        assertThrows(NullPointerException.class, SimaSimulation::getScheduler);
        assertThrows(NullPointerException.class, SimaSimulation::currentTime);
        assertThrows(NullPointerException.class, () -> SimaSimulation.getAgentFromIdentifier(null));
        assertThrows(NullPointerException.class, SimaSimulation::getAllEnvironments);
        assertThrows(NullPointerException.class, () -> SimaSimulation.getEnvironmentFromName(null));
        assertThrows(NullPointerException.class, SimaSimulation::timeMode);
    }

    @Test
    public void testRunSimulationMonoThreadRealTimeExceptions() {
        Set<Class<? extends Environment>> envClasses = new HashSet<>();
        envClasses.add(TestEnvironment.class);

        assertThrows(UnsupportedOperationException.class, () ->
                SimaSimulation.runSimulation(SimaSimulation.TimeMode.REAL_TIME,
                        SimaSimulation.SchedulerType.MONO_THREAD, 10_000L, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simulationIsRunning());
    }

    @Test
    public void testRunSimulationMonoThreadDiscreteTimeException() {
        Set<Class<? extends Environment>> envClasses = new HashSet<>();
        envClasses.add(TestEnvironment.class);

        assertThrows(UnsupportedOperationException.class, () ->
                SimaSimulation.runSimulation(SimaSimulation.TimeMode.DISCRETE_TIME,
                        SimaSimulation.SchedulerType.MONO_THREAD, 10_000L, envClasses, null,
                        SCHEDULER_WATCHER, SIMA_WATCHER));

        assertFalse(SimaSimulation.simulationIsRunning());
    }

    // Inner classes.

    private static class TestSimulationSchedulerWatcher implements Scheduler.SchedulerWatcher {

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

    private static class TestSimaWatcher implements SimaSimulation.SimaWatcher {

        // Variables.

        private int isPassStarted = 0;
        private int isPassKilled = 0;

        // Methods.

        public void reset() {
            this.isPassStarted = 0;
            this.isPassKilled = 0;
        }

        @Override
        public void simulationStarted() {
            this.isPassStarted++;
        }

        @Override
        public void simulationKilled() {
            this.isPassKilled++;
        }

        // Getters and Setters.

        public int getIsPassStarted() {
            return isPassStarted;
        }

        public int getIsPassKilled() {
            return isPassKilled;
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
        protected TestEnvironment(String environmentName, Map<String, String> args) {
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
