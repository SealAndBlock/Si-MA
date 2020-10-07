package agent;

import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.environment.event.Event;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractAgent {

    // Tests.

    /**
     * Test the constructor of {@link AbstractAgent} and if each instance are always different with different
     * {@link java.util.UUID}.
     */
    @Test
    public void testAgentConstructor() {
        AgentTestImpl a0 = new AgentTestImpl("AGENT_0");
        AgentTestImpl a0_1 = new AgentTestImpl("AGENT_0");
        AgentTestImpl a1 = new AgentTestImpl("AGENT_1");

        assertNotEquals(a1.getUUID(), a0.getUUID());
        assertNotEquals(a0, a0_1);
    }

    /**
     * Test the method {@link AbstractAgent#start()}. Verifies if all exceptions are thrown in function of the state of
     * the agent.
     */
    @Test
    public void testStart() {
        AgentTestImpl a0 = new AgentTestImpl("AGENT_0");

        a0.start();

        assertTrue(a0.isStarted());
        assertThrows(AlreadyStartedAgentException.class, a0::start);
        assertTrue(a0.isStarted());

        a0.kill();
        assertThrows(KilledAgentException.class, a0::start);

    }

    // Inner classes.

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

}
