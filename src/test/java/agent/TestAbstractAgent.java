package agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.exception.AlreadyKilledAgentException;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.behavior.Behavior;
import sima.core.behavior.exception.BehaviorCannotBePlayedByAgentException;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.exception.NotEvolvingAgentInEnvironmentException;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractAgent {

    // Variables.

    private static AgentTestImpl AGENT_0;
    private static AgentTestImpl AGENT_1;

    private static EnvironmentTestImpl ENV;

    // Setup.

    @BeforeEach
    void setUp() {
        AGENT_0 = new AgentTestImpl("AGENT_0");
        AGENT_1 = new AgentTestImpl("AGENT_1");

        ENV = new EnvironmentTestImpl("ENV");
    }

    // Tests.

    /**
     * Test the constructor of {@link AbstractAgent} and if each instance are always different with different
     * {@link java.util.UUID}.
     */
    @Test
    public void testAgentConstructor() {
        AgentTestImpl a0_1 = new AgentTestImpl("AGENT_0");

        assertNotEquals(AGENT_1.getUUID(), AGENT_0.getUUID());
        assertNotEquals(AGENT_0, a0_1);
    }

    /**
     * Test the method {@link AbstractAgent#start()}. Verifies if all exceptions are thrown in function of the state of
     * the agent.
     */
    @Test
    public void testStart() {
        AGENT_0.start();

        assertTrue(AGENT_0.isStarted());
        assertThrows(AlreadyStartedAgentException.class, AGENT_0::start);
        assertTrue(AGENT_0.isStarted());

        AGENT_0.kill();
        assertThrows(KilledAgentException.class, AGENT_0::start);

    }

    @Test
    public void testKill() {
        assertFalse(AGENT_0.isKilled());
        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());
        assertThrows(AlreadyKilledAgentException.class, AGENT_0::kill);
        assertTrue(AGENT_0.isKilled());
        assertThrows(KilledAgentException.class, AGENT_0::start);
        assertTrue(AGENT_0.isKilled());
    }

    @Test
    public void testJoinEnvironment() {
        assertFalse(AGENT_1.joinEnvironment(ENV));
        assertFalse(ENV.isEvolving(AGENT_1));

        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0));
        assertTrue(AGENT_0.isEvolvingInEnvironment(ENV.getEnvironmentName()));
        assertTrue(AGENT_0.isEvolvingInEnvironment(ENV));

        try {
            assertEquals(AGENT_0, ENV.getAgent(AGENT_0.getUUID()));
        } catch (NotEvolvingAgentInEnvironmentException e) {
            fail();
        }
    }

    @Test
    public void testLeaveEnvironment() {
        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0));

        AGENT_0.leaveEnvironment(ENV);
        assertFalse(ENV.isEvolving(AGENT_0));

        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0));

        AGENT_0.leaveEnvironment(ENV.getEnvironmentName());
        assertFalse(ENV.isEvolving(AGENT_0));
    }

    @Test
    public void testAddBehavior() {

    }

    // Inner classes.

    private static class EnvironmentTestImpl extends Environment {

        public boolean isPassToSendEventWithoutReceiver = false;
        public boolean isPassToScheduleEventReceptionToOneAgent = false;

        // Constructors.

        public EnvironmentTestImpl(String environmentName) {
            super(environmentName);
        }

        // Methods.

        /**
         * @param abstractAgent the agent to verify
         * @return true if the specified agent is to {@link #AGENT_0}, else false.
         */
        @Override
        protected boolean agentCanBeAccepted(AbstractAgent abstractAgent) {
            return abstractAgent.equals(AGENT_0);
        }

        @Override
        protected void agentIsLeaving(AbstractAgent leavingAgent) {

        }

        @Override
        protected void sendEventWithoutReceiver(Event event) {
            this.isPassToSendEventWithoutReceiver = true;
        }

        @Override
        protected boolean eventCanBeSentTo(AbstractAgent receiver, Event event) {
            return true;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AbstractAgent receiver, Event event) {
            this.isPassToScheduleEventReceptionToOneAgent = true;
        }

        @Override
        public void processEvent(Event event) {
        }

        public void reset() {
            this.isPassToSendEventWithoutReceiver = false;
            this.isPassToScheduleEventReceptionToOneAgent = false;
        }
    }

    private static class AgentTestImpl extends AbstractAgent {

        // Constructors.

        public AgentTestImpl(String agentName) {
            super(agentName);
        }

        // Methods.

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

    private static class BehaviorTestImpl extends Behavior {

        // Constructors.

        public BehaviorTestImpl(AbstractAgent agent, String[] args) throws BehaviorCannotBePlayedByAgentException {
            super(agent, args);
        }

        // Methods.

        @Override
        protected void processArgument(String[] args) {

        }

        @Override
        public boolean canBePlayedBy(AbstractAgent agent) {
            return true;
        }

        @Override
        public void onStartPlaying() {

        }

        @Override
        public void onStopPlaying() {

        }
    }

}
