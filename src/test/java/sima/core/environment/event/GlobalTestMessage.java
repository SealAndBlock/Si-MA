package sima.core.environment.event;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
public abstract class GlobalTestMessage extends GlobalTestEvent {

    // Static.

    protected static Message MESSAGE;

    protected Message MESSAGE_PROTOCOL_EVENT;
    protected Message MESSAGE_NO_PROTOCOL_EVENT;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(MESSAGE, "MESSAGE cannot be null for tests");
        assertNotNull(MESSAGE_PROTOCOL_EVENT, "MESSAGE_PROTOCOL_EVENT cannot be null for tests");
        assertNotNull(MESSAGE_NO_PROTOCOL_EVENT, "MESSAGE_NO_PROTOCOL_EVENT cannot be null for tests");

        EVENT = MESSAGE;
        PROTOCOL_EVENT = MESSAGE_PROTOCOL_EVENT;
        NO_PROTOCOL_EVENT = MESSAGE_NO_PROTOCOL_EVENT;

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructMessageWithNullSenderThrowsException() {
        assertThrows(NullPointerException.class,
                () -> new Message(null, null, null, null) {
                });
    }

    @Test
    public void constructMessageWithNullContentNotFail() {
        AbstractAgent a = new AgentTesting("A_0", 0, null);
        this.testNotFail(() -> new Message(a.getAgentIdentifier(), null, null, null) {
        });
    }

    @Test
    public void getContentNotFail() {
        this.testNotFail(MESSAGE::getContent);
    }
}
