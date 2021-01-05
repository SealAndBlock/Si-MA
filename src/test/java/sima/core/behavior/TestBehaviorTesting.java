package sima.core.behavior;

import sima.core.agent.AgentTesting;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import static org.junit.jupiter.api.Assertions.fail;

public class TestBehaviorTesting extends GlobalTestBehavior {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        AgentTesting agentTesting = new AgentTesting("AGENT_BEHAVIOR_TEST_0", 0, null);
        try {
            BEHAVIOR = new BehaviorTesting(agentTesting, null);

            PLAYABLE_AGENT = agentTesting;
            NOT_PLAYABLE_AGENT = null;

            super.verifyAndSetup();
        } catch (BehaviorCannotBePlayedByAgentException e) {
            fail(e);
        }
    }
}
