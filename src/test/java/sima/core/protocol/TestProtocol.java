package sima.core.protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public abstract class TestProtocol {
    
    // Variables.
    
    protected Protocol protocol;
    
    @Mock
    private ProtocolManipulator mockProtocolManipulator;
    
    // Tests.
    
    @Nested
    @Tag("Protocol.toString")
    @DisplayName("Protocol toString tests")
    class ToStringTest {
        
        @Test
        @DisplayName("Test if the toString method returns a correct String")
        void testToString() {
            String expectedToString = "Protocol [" +
                    "protocolIdentifier=" + protocol.getIdentifier() +
                    ", protocolTag=" + protocol.getProtocolTag() +
                    ", agentOwner=" + protocol.getAgentOwner() +
                    ", protocolManipulator=" + protocol.getProtocolManipulator() +
                    ']';
            String toString = protocol.toString();
            assertEquals(expectedToString, toString);
        }
        
    }
    
    @Nested
    @Tag("Protocol.getIdentifier")
    @DisplayName("Protocol getIdentifier tests")
    class GetIdentifierTest {
        
        @Test
        @DisplayName("Test if getIdentifier returns always the same instance (question of performance) and that instance contains correct " +
                             "values")
        void testGetIdentifierTest() {
            var p0 = protocol.getIdentifier();
            var p1 = protocol.getIdentifier();
            assertSame(p0, p1);
            
            assertEquals(protocol.getClass(), p0.getProtocolClass());
            assertEquals(protocol.getProtocolTag(), p0.getProtocolTag());
        }
        
    }
    
    @Nested
    @Tag("Protocol.resetDefaultProtocolManipulator")
    @DisplayName("Protocol resetDefaultProtocolManipulator tests")
    class ResetDefaultProtocolManipulatorTest {
        
        @Test
        @DisplayName("Test if after resetDefaultProtocolManipulator call, the protocol manipulator of the protocol is the defautl protocol " +
                             "manipulator of the protocol")
        void testResetDefaultProtocolManipulator() {
            protocol.resetDefaultProtocolManipulator();
            var defaultProtocolManipulator = protocol.getDefaultProtocolManipulator();
            var currentProtocolManipulator = protocol.getProtocolManipulator();
            assertSame(defaultProtocolManipulator, currentProtocolManipulator);
        }
        
    }
    
    @Nested
    @Tag("Protocol.setProtocolManipulator")
    @DisplayName("Protocol setProtocolManipulator tests")
    class SetProtocolManipulatorTest {
        
        @Test
        @DisplayName("Test if setProtocolManipulator throw a NullPointerException if the protocolManipulator is null")
        void testSetProtocolManipulatorWithNull() {
            assertThrows(NullPointerException.class, () -> protocol.setProtocolManipulator(null));
        }
        
        @Test
        @DisplayName("Test if setProtocolManipulator set the new protocol manipulator to the protocol")
        void testSetProtocolManipulatorSetTheNewProtocolManipulator() {
            protocol.setProtocolManipulator(mockProtocolManipulator);
            var protocolManipulator = protocol.getProtocolManipulator();
            assertSame(mockProtocolManipulator, protocolManipulator);
        }
        
    }
    
}
