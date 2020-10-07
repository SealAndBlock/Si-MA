package behavior;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.behavior.Behavior;
import sima.core.behavior.exception.BehaviorCannotBePlayedByAgentException;
import sima.core.environment.event.Event;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class TestBehavior {

    // Variables.

    private static AbstractAgent AGENT_0;
    private static AbstractAgent AGENT_1;

    // Setup.

    @BeforeEach
    void setUp() {
        AGENT_0 = new AgentTestImpl("AGENT_0");
        AGENT_1 = new AgentTestImpl("AGENT_1");
    }

    // Tests.

    /**
     * Test if the {@link Behavior} constructors works correctly and throws an exception if the agent in parameter
     * cannot play the {@code Behavior}.
     */
    @Test
    public void testBehaviorConstructor() {
        assertThrows(BehaviorCannotBePlayedByAgentException.class, () -> new Behavior(AGENT_1, null) {
            @Override
            protected void processArgument(String[] args) {
            }

            @Override
            public boolean canBePlayedBy(AbstractAgent agent) {
                return agent.equals(AGENT_0);
            }

            @Override
            public void onStartPlaying() {
            }

            @Override
            public void onStopPlaying() {
            }
        });

        try {
            new Behavior(AGENT_0, null) {
                @Override
                protected void processArgument(String[] args) {
                }

                @Override
                public boolean canBePlayedBy(AbstractAgent agent) {
                    return agent.equals(AGENT_0);
                }

                @Override
                public void onStartPlaying() {
                }

                @Override
                public void onStopPlaying() {
                }
            };
        } catch (BehaviorCannotBePlayedByAgentException e) {
            fail();
        }
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
