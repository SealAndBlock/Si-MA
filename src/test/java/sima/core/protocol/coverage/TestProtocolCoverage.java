package sima.core.protocol.coverage;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.SimpleAgent;
import sima.core.environment.event.Event;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;
import sima.core.protocol.TestProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestProtocolCoverage extends TestProtocol {
    
    // Variables.
    
    @Mock
    private SimpleAgent mockSimpleAgent;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        protocol = new ProtocolCoverage("TAG", mockSimpleAgent, null);
    }
    
    // Tests.
    
    @Nested
    @Tag("ProtocolCoverage.constructor")
    @DisplayName("ProtocolCoverage constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throw an NullPointerException with null protocolTag")
        void testConstructorWithNullProtocolTag() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new ProtocolCoverage(null, mockSimpleAgent, args));
        }
        
        @Test
        @DisplayName("Test if constructor throw an NullPointerException with null agentOwner")
        void testConstructorWithNullAgentOwner() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new ProtocolCoverage("TAG", null, args));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with null args map and set the protocolManipulator to the default protocol " +
                             "manipulator")
        void testConstructorWithNullArgsMap() {
            final AtomicReference<Protocol> protocol = new AtomicReference<>();
            assertDoesNotThrow(() -> protocol.set(new ProtocolCoverage("TAG", mockSimpleAgent, null)));
            var defaultProtocolManipulator = protocol.get().getDefaultProtocolManipulator();
            var protocolManipulator = protocol.get().getProtocolManipulator();
            assertSame(defaultProtocolManipulator, protocolManipulator);
        }
    }
    
    // Inner class.
    
    static class ProtocolCoverage extends Protocol {
        
        // Variables.
        
        
        // Constructors.
        
        ProtocolCoverage(String protocolTag, SimpleAgent agentOwner, Map<String, String> args) {
            super(protocolTag, agentOwner, args);
        }
        
        // Methods.
        
        @Override
        public void processEvent(Event event) {
            // NOTHING
        }
        
        @Override
        protected ProtocolManipulator createDefaultProtocolManipulator() {
            return new ProtocolManipulator.DefaultProtocolManipulator(this);
        }
    }
}
