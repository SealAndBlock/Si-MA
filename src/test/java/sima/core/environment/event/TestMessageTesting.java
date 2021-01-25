package sima.core.environment.event;

import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.ProtocolTesting;

public class TestMessageTesting extends GlobalTestMessage {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {

        AbstractAgent a = new AgentTesting("A_0", 0, 0,null);

        MESSAGE_WITH_NOT_NULL_CONTENT =
                new Message(a.getAgentIdentifier(), null, null, new TransportableString("TransportableString"));
        MESSAGE_WITH_NULL_CONTENT = new Message(a.getAgentIdentifier(), null, null, null);

        MESSAGE_PROTOCOL_EVENT =
                new Message(a.getAgentIdentifier(), null, new ProtocolIdentifier(ProtocolTesting.class, "TAG"), null);
        MESSAGE_NO_PROTOCOL_EVENT = new Message(a.getAgentIdentifier(), null, null, null);

        super.verifyAndSetup();
    }
}
