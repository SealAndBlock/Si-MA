package sima.core.agent;

import sima.core.agent.AgentTesting;
import sima.core.agent.TestAbstractAgent;

public class TestAgentTesting extends TestAbstractAgent {

    // Initialisation.

    @Override
    public void verifyAndSetup() {
        AGENT_0 = new AgentTesting("AGENT_0", 0, null);
        AGENT_1 = new AgentTesting("AGENT_1", 1, null);
    }

}
