package environment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // Private classes.

    private static class EnvironmentTestImpl extends Environment {

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

        }

        @Override
        protected boolean eventCanBeSentTo(AbstractAgent receiver, Event event) {
            return false;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AbstractAgent receiver, Event event) {

        }

        @Override
        public void processEvent(Event event) {

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
        protected void treatGeneralEvent(Event event) {

        }

        @Override
        protected void treatEventWithNotFindProtocol(Event event) {

        }
    }
}
