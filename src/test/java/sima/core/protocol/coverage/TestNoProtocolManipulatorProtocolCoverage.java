package sima.core.protocol.coverage;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.SimaAgent;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.TransportableInEvent;
import sima.core.protocol.NoProtocolManipulatorProtocol;
import sima.core.protocol.Protocol;
import sima.core.protocol.TestNoProtocolManipulatorProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestNoProtocolManipulatorProtocolCoverage extends TestNoProtocolManipulatorProtocol {
    
    // Variables.
    
    @Mock
    private SimaAgent mockSimaAgent;
    
    // Init.
    
    @BeforeEach
    @Override
    protected void setUp() {
        noProtocolManipulatorProtocol = new NoProtocolManipulatorProtocolCoverage("TAG", mockSimaAgent, null);
        super.setUp();
    }
    
    // Tests.
    
    @Nested
    @Tag("NoProtocolManipulatorProtocolCoverage.constructor")
    @DisplayName("NoProtocolManipulatorProtocolCoverage constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throw an NullPointerException with null protocolTag")
        void testConstructorWithNullProtocolTag() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new NoProtocolManipulatorProtocolCoverage(null, mockSimaAgent, args));
        }
        
        @Test
        @DisplayName("Test if constructor throw an NullPointerException with null agentOwner")
        void testConstructorWithNullAgentOwner() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new NoProtocolManipulatorProtocolCoverage("TAG", null, args));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with null args map and set the protocolManipulator to the default protocol " +
                "manipulator")
        void testConstructorWithNullArgsMap() {
            final AtomicReference<Protocol> protocol = new AtomicReference<>();
            assertDoesNotThrow(() -> protocol.set(new NoProtocolManipulatorProtocolCoverage("TAG", mockSimaAgent, null)));
            var defaultProtocolManipulator = protocol.get().getDefaultProtocolManipulator();
            var protocolManipulator = protocol.get().getProtocolManipulator();
            assertSame(defaultProtocolManipulator, protocolManipulator);
        }
    }
    
    // Inner class.
    
    static class NoProtocolManipulatorProtocolCoverage extends NoProtocolManipulatorProtocol {
        
        NoProtocolManipulatorProtocolCoverage(String protocolTag, SimaAgent agentOwner, Map<String, String> args) {
            super(protocolTag, agentOwner, args);
        }
        
        // Methods.
        
        @Override
        public void processEvent(Event event) {
            // Nothing
        }
        
        @Override
        public void processEventTransportable(TransportableInEvent transportableInEvent) {
            // Nothing
        }
    }
}
