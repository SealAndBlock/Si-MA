package protocol;

import org.junit.jupiter.api.Test;
import sima.core.environment.event.Event;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import static org.junit.jupiter.api.Assertions.*;

public class TestProtocol {

    @Test
    public void testProtocolDefaultProtocolManipulatorConstructor() {
        assertThrows(NullPointerException.class, () -> new Protocol("P0", null) {
            @Override
            public void processEvent(Event event) {
            }

            @Override
            protected void processArgument(String[] args) {
            }

            @Override
            protected ProtocolManipulator getDefaultProtocolManipulator() {
                return null;
            }
        });

        try {
            Protocol p = new Protocol("P0", null) {
                @Override
                public void processEvent(Event event) {
                }

                @Override
                protected void processArgument(String[] args) {
                }

                @Override
                protected ProtocolManipulator getDefaultProtocolManipulator() {
                    return new ProtocolManipulator(this) {
                    };
                }
            };
        } catch (NullPointerException e) {
            fail();
        }
    }

}
