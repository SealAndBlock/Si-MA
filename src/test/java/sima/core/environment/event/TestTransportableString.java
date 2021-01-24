package sima.core.environment.event;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransportableString extends SimaTest {

    // Static.

    protected static TransportableString TRANSPORTABLE_STRING;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        TRANSPORTABLE_STRING = new TransportableString("TEST");
    }

    // Tests.

    @Test
    public void cloneNotFail() {
        AtomicReference<TransportableString> clone = new AtomicReference<>();
        assertDoesNotThrow(() -> clone.set(TRANSPORTABLE_STRING.clone()));
        assertNotSame(clone.get(), TRANSPORTABLE_STRING);
        assertSame(clone.get().getContent(), TRANSPORTABLE_STRING.getContent());
    }
}
