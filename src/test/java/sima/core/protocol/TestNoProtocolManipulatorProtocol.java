package sima.core.protocol;

import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public abstract class TestNoProtocolManipulatorProtocol extends TestProtocol {
    
    // Variables.
    
    protected NoProtocolManipulatorProtocol noProtocolManipulatorProtocol;
    
    @Mock
    private ProtocolManipulator mockProtocolManipulator;
    
    // Init.
    
    @BeforeEach
    protected void setUp() {
        protocol = noProtocolManipulatorProtocol;
    }
    
    // Tests.
    
    @Nested
    @Tag("NoProtocolManipulatorProtocol.setProtocolManipulator")
    @DisplayName("NoProtocolManipulatorProtocol setProtocolManipulator test")
    class SetProtocolManipulatorTest extends TestProtocol.SetProtocolManipulatorTest {
        
        @Ignore("No ProtocolManipulator can be set")
        @Override
        void testSetProtocolManipulatorWithNull() {
            super.testSetProtocolManipulatorWithNull();
        }
        
        @Ignore("No ProtocolManipulator can be set")
        @Override
        void testSetProtocolManipulatorSetTheNewProtocolManipulator() {
            super.testSetProtocolManipulatorSetTheNewProtocolManipulator();
        }
        
        @Test
        @DisplayName("Test if setProtocolManipulator throw an UnsupportedOperationException")
        void testSetProtocolManipulatorThrowUnsupportedOperationException() {
            assertThrows(UnsupportedOperationException.class, () -> noProtocolManipulatorProtocol.setProtocolManipulator(mockProtocolManipulator));
        }
    }
    
}
