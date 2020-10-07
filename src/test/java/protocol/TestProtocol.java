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

    @Test
    public void testSetProtocolManipulator() {
        ProtocolTestImpl p = new ProtocolTestImpl("P0", null);
        assertEquals(p.getDefaultProtocolManipulator(), p.getProtocolManipulator());

        assertThrows(NullPointerException.class, () -> p.setProtocolManipulator(null));

        ProtocolManipulator pm = new ProtocolManipulator(p) {
        };
        p.setProtocolManipulator(pm);
        assertEquals(pm, p.getProtocolManipulator());

        p.resetDefaultProtocolManipulator();
        assertEquals(p.getDefaultProtocolManipulator(), p.getProtocolManipulator());
    }

    // Inner classes

    private static class ProtocolTestImpl extends Protocol {

        // Variables.

        public ProtocolManipulator defaultProtocolManipulator;

        // Constructors.

        public ProtocolTestImpl(String protocolTag, String[] args) {
            super(protocolTag, args);
        }

        // Methods.

        @Override
        protected void processArgument(String[] args) {
        }

        @Override
        protected ProtocolManipulator getDefaultProtocolManipulator() {
            if (this.defaultProtocolManipulator == null) {
                this.defaultProtocolManipulator = new ProtocolManipulator(this) {
                };
            }
            return this.defaultProtocolManipulator;
        }

        @Override
        public void processEvent(Event event) {
        }
    }

}
