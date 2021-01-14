package sima.core.protocol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestProtocolIdentifier extends GlobalTestProtocolIdentifier {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {

        PROTOCOL_IDENTIFIER = new ProtocolIdentifier(ProtocolTesting.class, "TAG_0");
        PROTOCOL_IDENTIFIER_EQUAL = new ProtocolIdentifier(ProtocolTesting.class, "TAG_0");
        PROTOCOL_IDENTIFIER_NOT_EQUAL = new ProtocolIdentifier(ProtocolTesting.class, "TAG_1");

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructProtocolIdentifierWithNullProtocolClassThrowsException() {
        assertThrows(NullPointerException.class, () -> new ProtocolIdentifier(null, "TAG"));
    }

    @Test
    public void constructProtocolIdentifierWithNullTagThrowsException() {
        assertThrows(NullPointerException.class, () -> new ProtocolIdentifier(ProtocolTesting.class, null));
    }

    @Test
    public void constructProtocolIdentifierWithNotNullArgumentsNotFail() {
        assertDoesNotThrow(() -> new ProtocolIdentifier(ProtocolTesting.class, "TAG"));
    }
}
