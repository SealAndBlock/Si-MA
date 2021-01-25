package sima.core.environment.event;

import sima.core.agent.AgentTesting;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.ProtocolTesting;

public class TestEventTesting extends GlobalTestEvent {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        AgentTesting a = new AgentTesting("A_0", 0, 0,null);

        EVENT = new EventTesting(a.getAgentIdentifier(), null, null);

        PROTOCOL_EVENT = new EventTesting(a.getAgentIdentifier(), null, new ProtocolIdentifier(ProtocolTesting.class, "PROTOCOL_TAG"));
        NO_PROTOCOL_EVENT = new EventTesting(a.getAgentIdentifier(), null, null);

        super.verifyAndSetup();
    }
}
