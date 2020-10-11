package environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentInfo;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.protocol.ProtocolIdentifier;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestEnvironment {

    // Variables.

    private static AbstractAgent AGENT_0;
    private static AbstractAgent AGENT_1;

    private static Environment ENV;

    // Setup.

    @BeforeEach
    void setUp() {
        AGENT_0 = new AgentTestImpl("AGENT_0");
        AGENT_1 = new AgentTestImpl("AGENT_1");

        ENV = new EnvironmentTestImpl("ENV_TEST");
    }

    // Tests.

    /**
     * Test the method {@link sima.core.environment.Environment#acceptAgent(AbstractAgent)}.
     * <p>
     * Test in first if the null agent and {@link #AGENT_0} and {@link #AGENT_1} are not evolving in the environment.
     * <p>
     * After that, try to accept the null agent, the agent {@link #AGENT_0} and the {@link #AGENT_1}. Only the
     * {@link #AGENT_0} must be accepted.
     * <p>
     * Verifies if only the agent {@link #AGENT_0} is evolving in the environment. The agent {@link #AGENT_1} is not
     * accepted agent in the implementation of {@link EnvironmentTestImpl}.
     */
    @Test
    public void testAcceptingAgent() {
        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0));
        assertFalse(ENV.isEvolving(AGENT_1));

        assertFalse(ENV.acceptAgent(null));
        assertTrue(ENV.acceptAgent(AGENT_0));
        assertFalse(ENV.acceptAgent(AGENT_1));

        assertFalse(ENV.isEvolving(null));
        assertTrue(ENV.isEvolving(AGENT_0));
        assertFalse(ENV.isEvolving(AGENT_1));
    }

    /**
     * Test if after accept some agent, the environment remove the agents which leave it.
     */
    @Test
    public void testLeavingAgent() {
        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0));
        assertFalse(ENV.isEvolving(AGENT_1));

        assertFalse(ENV.acceptAgent(null));
        assertTrue(ENV.acceptAgent(AGENT_0));
        assertFalse(ENV.acceptAgent(AGENT_1));

        ENV.leave(null);
        ENV.leave(AGENT_0);
        ENV.leave(AGENT_1);

        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0));
        assertFalse(ENV.isEvolving(AGENT_1));
    }

    /**
     * Test if the list returns by the method {@link Environment#getEvolvingAgentsInfo()} coincides with the current
     * agents evolving in the environment.
     */
    @Test
    public void testGetEvolvingAgentsInfo() {
        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0));
        assertFalse(ENV.isEvolving(AGENT_1));

        assertFalse(ENV.acceptAgent(null));
        assertTrue(ENV.acceptAgent(AGENT_0));
        assertFalse(ENV.acceptAgent(AGENT_1));

        List<AgentInfo> agentInfos = ENV.getEvolvingAgentsInfo();
        assertEquals(agentInfos.size(), 1);
        assertTrue(agentInfos.contains(AGENT_0.getInfo()));
        assertFalse(agentInfos.contains(AGENT_1.getInfo()));

        ENV.leave(null);
        ENV.leave(AGENT_0);
        ENV.leave(AGENT_1);

        agentInfos = ENV.getEvolvingAgentsInfo();
        assertEquals(agentInfos.size(), 0);
        assertFalse(agentInfos.contains(AGENT_0.getInfo()));
        assertFalse(agentInfos.contains(AGENT_1.getInfo()));
    }

    @Test
    public void testSendEvent() {
        assertFalse(ENV.isEvolving(AGENT_0));

        assertTrue(ENV.acceptAgent(AGENT_0));

        assertThrows(NullPointerException.class, () -> ENV.sendEvent(null));

        EventTestImpl e0 = new EventTestImpl(AGENT_1.getUUID(), AGENT_0.getUUID(), null);
        assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> ENV.sendEvent(e0));

        EventTestImpl e1 = new EventTestImpl(AGENT_0.getUUID(), AGENT_1.getUUID(), null);
        assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> ENV.sendEvent(e1));

        EventTestImpl e2 = new EventTestImpl(AGENT_0.getUUID(), AGENT_0.getUUID(), null);
        try {
            ENV.sendEvent(e2);
            assertTrue(((EnvironmentTestImpl) ENV).isPassToScheduleEventReceptionToOneAgent);
            assertFalse(((EnvironmentTestImpl) ENV).isPassToSendEventWithoutReceiver);
            ((EnvironmentTestImpl) ENV).reset();
        } catch (NotEvolvingAgentInEnvironmentException e) {
            fail();
        }

        EventTestImpl e3 = new EventTestImpl(AGENT_0.getUUID(), null, null);
        try {
            ENV.sendEvent(e3);
            assertFalse(((EnvironmentTestImpl) ENV).isPassToScheduleEventReceptionToOneAgent);
            assertTrue(((EnvironmentTestImpl) ENV).isPassToSendEventWithoutReceiver);
            ((EnvironmentTestImpl) ENV).reset();
        } catch (NotEvolvingAgentInEnvironmentException e) {
            e.printStackTrace();
        }
    }

    // Private classes.

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

    private static class EventTestImpl extends Event {

        // Constructors.

        public EventTestImpl(UUID sender, UUID receiver, ProtocolIdentifier protocolTargeted) {
            super(sender, receiver, protocolTargeted);
        }
    }
}
