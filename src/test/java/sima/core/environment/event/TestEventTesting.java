package sima.core.environment.event;

import sima.core.agent.AgentTesting;

public class TestEventTesting extends GlobalTestEvent {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        AgentTesting a = new AgentTesting("A_0", 0, null);
        EVENT = new EventTesting(a.getAgentIdentifier(), null, null);

        super.verifyAndSetup();
    }
}
