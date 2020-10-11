package protocol;

import org.junit.jupiter.api.Test;
import sima.core.environment.event.Event;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestProtocol {

    /**
     * Test if for a {@link Protocol} which returns null with the method which give the default
     * {@link ProtocolManipulator}, a {@link NullPointerException} is thrown.
     */
    @Test
    public void testProtocolDefaultProtocolManipulatorConstructor() {
        assertThrows(NullPointerException.class, () -> new Protocol("P0", null) {
            @Override
            public void processEvent(Event event) {
            }

            @Override
            protected void processArgument(Map<String, String> args) {
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
                protected void processArgument(Map<String, String> args) {
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

    /**
     * Test if a protocol can set a other {@link ProtocolManipulator} and can reset its default
     * {@code ProtocolManipulator} after.
     */
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

        assertEquals(p, p.getProtocolManipulator().getManipulatedProtocol());
    }

    /**
     * Test if a {@link Protocol} can reset its default {@link ProtocolManipulator}.
     */
    @Test
    public void testResetDefaultProtocolManipulator() {
        Protocol p = new Protocol("P0", null) {
            private int i;

            @Override
            protected void processArgument(Map<String, String> args) {
            }

            @Override
            protected ProtocolManipulator getDefaultProtocolManipulator() {
                if (this.i == 0) {
                    this.i = this.i + 1;
                    return new ProtocolManipulator(this) {
                    };
                } else
                    return null;
            }

            @Override
            public void processEvent(Event event) {
            }
        };

        assertThrows(NullPointerException.class, p::resetDefaultProtocolManipulator);

        ProtocolTestImpl p1 = new ProtocolTestImpl("P0", null);
        assertEquals(p1.getDefaultProtocolManipulator(), p1.getProtocolManipulator());

        ProtocolManipulator pm = new ProtocolManipulator(p1) {
        };
        p1.setProtocolManipulator(pm);
        assertEquals(pm, p1.getProtocolManipulator());

        p1.resetDefaultProtocolManipulator();
        assertEquals(p1.getDefaultProtocolManipulator(), p1.getProtocolManipulator());
    }

    /**
     * Test if two instances of a same class of {@link Protocol} with different tag have two not equal
     * {@link ProtocolIdentifier} and if two instances of a same class of {@code Protocol} with the same tag have two
     * equal {@code ProtocolIdentifier}.
     */
    @Test
    public void testProtocolIdentifier() {
        Protocol p0 = new ProtocolTestImpl("P0", null);
        Protocol p1 = new ProtocolTestImpl("P1", null);
        Protocol p2 = new ProtocolTestImpl("P0", null);

        ProtocolIdentifier pI = p0.getIdentifier();

        assertEquals(pI, p0.getIdentifier());
        assertNotEquals(pI, p1.getIdentifier());
        assertEquals(pI, p2.getIdentifier());
    }

    // Inner classes

    private static class ProtocolTestImpl extends Protocol {

        // Variables.

        public ProtocolManipulator defaultProtocolManipulator;

        // Constructors.

        public ProtocolTestImpl(String protocolTag, Map<String, String> args) {
            super(protocolTag, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {
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