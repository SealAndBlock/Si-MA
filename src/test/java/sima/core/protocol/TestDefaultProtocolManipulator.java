package sima.core.protocol;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestDefaultProtocolManipulator extends TestProtocolManipulator {
    
    // Variables.
    
    @Mock
    private Protocol mockProtocol;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        protocolManipulator = new ProtocolManipulator.DefaultProtocolManipulator(mockProtocol);
    }
    
    
    // Tests.
    
    @Nested
    @Tag("DefaultProtocolManipulator.constructor")
    @DisplayName("DefaultProtocolManipulator constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws NUllPointerException with null Protocol")
        void testConstructorWithNullProtocol() {
            assertThrows(NullPointerException.class, () -> new ProtocolManipulator.DefaultProtocolManipulator(null));
        }
        
        @Test
        @DisplayName("Test if constructor set the parameter protocol as protocolManipulated")
        void testConstructorWithCorrectParameters() {
            ProtocolManipulator.DefaultProtocolManipulator defaultProtocolManipulator =
                    new ProtocolManipulator.DefaultProtocolManipulator(mockProtocol);
            assertSame(mockProtocol, defaultProtocolManipulator.getManipulatedProtocol());
        }
        
    }
}
