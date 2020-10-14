package sima.core.environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.protocol.ProtocolIdentifier;

import java.util.List;
import java.util.Map;

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

        ENV = new EnvironmentTestImpl("ENV_TEST", null);
    }

    // Tests.

    /**
     * Test the method {@link sima.core.environment.Environment#acceptAgent(AgentIdentifier)}.
     * <p>
     * Test in first if the null sima.core.agent and {@link #AGENT_0} and {@link #AGENT_1} are not evolving in the sima.core.environment.
     * <p>
     * After that, try to accept the null sima.core.agent, the sima.core.agent {@link #AGENT_0} and the {@link #AGENT_1}. Only the
     * {@link #AGENT_0} must be accepted.
     * <p>
     * Verifies if only the sima.core.agent {@link #AGENT_0} is evolving in the sima.core.environment. The sima.core.agent {@link #AGENT_1} is not
     * accepted sima.core.agent in the implementation of {@link EnvironmentTestImpl}.
     */
    @Test
    public void testAcceptingAgent() {
        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.isEvolving(AGENT_1.getAgentIdentifier()));

        assertFalse(ENV.acceptAgent(null));
        assertTrue(ENV.acceptAgent(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.acceptAgent(AGENT_1.getAgentIdentifier()));

        assertFalse(ENV.isEvolving(null));
        assertTrue(ENV.isEvolving(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.isEvolving(AGENT_1.getAgentIdentifier()));
    }

    /**
     * Test if after accept some sima.core.agent, the sima.core.environment remove the agents which leave it.
     */
    @Test
    public void testLeavingAgent() {
        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.isEvolving(AGENT_1.getAgentIdentifier()));

        assertFalse(ENV.acceptAgent(null));
        assertTrue(ENV.acceptAgent(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.acceptAgent(AGENT_1.getAgentIdentifier()));

        ENV.leave(null);
        ENV.leave(AGENT_0.getAgentIdentifier());
        ENV.leave(AGENT_1.getAgentIdentifier());

        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.isEvolving(AGENT_1.getAgentIdentifier()));
    }

    /**
     * Test if the list returns by the method {@link Environment#getEvolvingAgentIdentifiers()} coincides with the current
     * agents evolving in the sima.core.environment.
     */
    @Test
    public void testGetEvolvingAgentsInfo() {
        assertFalse(ENV.isEvolving(null));
        assertFalse(ENV.isEvolving(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.isEvolving(AGENT_1.getAgentIdentifier()));

        assertFalse(ENV.acceptAgent(null));
        assertTrue(ENV.acceptAgent(AGENT_0.getAgentIdentifier()));
        assertFalse(ENV.acceptAgent(AGENT_1.getAgentIdentifier()));

        List<AgentIdentifier> agentIdentifiers = ENV.getEvolvingAgentIdentifiers();
        assertEquals(agentIdentifiers.size(), 1);
        assertTrue(agentIdentifiers.contains(AGENT_0.getAgentIdentifier()));
        assertFalse(agentIdentifiers.contains(AGENT_1.getAgentIdentifier()));

        ENV.leave(null);
        ENV.leave(AGENT_0.getAgentIdentifier());
        ENV.leave(AGENT_1.getAgentIdentifier());

        agentIdentifiers = ENV.getEvolvingAgentIdentifiers();
        assertEquals(agentIdentifiers.size(), 0);
        assertFalse(agentIdentifiers.contains(AGENT_0.getAgentIdentifier()));
        assertFalse(agentIdentifiers.contains(AGENT_1.getAgentIdentifier()));
    }

    @Test
    public void testSendEvent() {
        assertFalse(ENV.isEvolving(AGENT_0.getAgentIdentifier()));

        assertTrue(ENV.acceptAgent(AGENT_0.getAgentIdentifier()));

        assertThrows(NullPointerException.class, () -> ENV.sendEvent(null));

        EventTestImpl e0 = new EventTestImpl(AGENT_1.getAgentIdentifier(), AGENT_0.getAgentIdentifier(), null);
        assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> ENV.sendEvent(e0));

        // AGENT_1 not evolving in environment
        EventTestImpl e1 = new EventTestImpl(AGENT_0.getAgentIdentifier(), AGENT_1.getAgentIdentifier(), null);
        assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> ENV.sendEvent(e1));

        EventTestImpl e2 = new EventTestImpl(AGENT_0.getAgentIdentifier(), AGENT_0.getAgentIdentifier(), null);
        try {
            ENV.sendEvent(e2);
            assertTrue(((EnvironmentTestImpl) ENV).isPassToScheduleEventReceptionToOneAgent);
            assertFalse(((EnvironmentTestImpl) ENV).isPassToSendEventWithoutReceiver);
            ((EnvironmentTestImpl) ENV).reset();
        } catch (NotEvolvingAgentInEnvironmentException e) {
            fail();
        }

        EventTestImpl e3 = new EventTestImpl(AGENT_0.getAgentIdentifier(), null, null);
        try {
            ENV.sendEvent(e3);
            assertFalse(((EnvironmentTestImpl) ENV).isPassToScheduleEventReceptionToOneAgent);
            assertTrue(((EnvironmentTestImpl) ENV).isPassToSendEventWithoutReceiver);
            ((EnvironmentTestImpl) ENV).reset();
        } catch (NotEvolvingAgentInEnvironmentException e) {
            fail();
        }
    }

    // Private classes.

    private static class EnvironmentTestImpl extends Environment {

        public boolean isPassToSendEventWithoutReceiver = false;
        public boolean isPassToScheduleEventReceptionToOneAgent = false;

        // Constructors.

        public EnvironmentTestImpl(String environmentName, Map<String, String> args) {
            super(environmentName, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {
        }

        /**
         * @param abstractAgentIdentifier the sima.core.agent to verify
         * @return true if the specified sima.core.agent is to {@link #AGENT_0}, else false.
         */
        @Override
        protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
            return abstractAgentIdentifier.equals(AGENT_0.getAgentIdentifier());
        }

        @Override
        protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
        }

        @Override
        protected void sendEventWithoutReceiver(Event event) {
            this.isPassToSendEventWithoutReceiver = true;
        }

        @Override
        protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
            return true;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {
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

    private static class EventTestImpl extends Event {

        // Constructors.

        public EventTestImpl(AgentIdentifier sender, AgentIdentifier receiver, ProtocolIdentifier protocolTargeted) {
            super(sender, receiver, protocolTargeted);
        }
    }
}
