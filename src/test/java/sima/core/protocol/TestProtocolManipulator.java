package sima.core.protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
public abstract class TestProtocolManipulator {
    
    // Variables.
    
    protected ProtocolManipulator protocolManipulator;
    
    @Mock
    private Protocol mockProtocol;
    
    // Tests.
    
    @Nested
    @Tag("ProtocolManipulator.resetState")
    @DisplayName("ProtocolManipulator resetState tests")
    class ResetStateTest {
        
        @Test
        @DisplayName("Test if resetState does not throw exception")
        void testResetStateDoesNotThrowException() {
            assertDoesNotThrow(() -> protocolManipulator.resetState());
        }
        
    }
    
    @Nested
    @Tag("ProtocolManipulator.setManipulatedProtocol")
    @DisplayName("ProtocolManipulator setManipulatedProtocol tests")
    class SetManipulatedProtocolTest {
        
        @Test
        @DisplayName("Test if setManipulatedProtocol set the parameter as new instance of protocol manipulated")
        void testSetProtocolManipulated() {
            protocolManipulator.setManipulatedProtocol(mockProtocol);
            assertSame(mockProtocol, protocolManipulator.getManipulatedProtocol());
        }
        
    }
    
}
