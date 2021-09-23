package sima.basic.environment.message;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.TransportableIntendedToProtocol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMessage {
    
    // Variables
    
    protected Message message;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    protected TransportableIntendedToProtocol mockTransportableIntendedForProtocol;
    
    @Mock
    private TransportableIntendedToProtocol mockTransportableIntendedForProtocolOther;
    
    // Init.
    
    @BeforeEach
    protected void setUp() {
        message = new Message(mockTransportableIntendedForProtocol, mockProtocolIdentifier);
    }
    
    // Test.
    
    @Nested
    @Tag("Message.constructor")
    @DisplayName("Message constructors tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor with null content does not throw exception")
        void testConstructorWithNullContent() {
            assertDoesNotThrow(() -> new Message(null, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor throws NullPointerException with null intended protocol")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(NullPointerException.class, () -> new Message(mockTransportableIntendedForProtocol, null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new Message(mockTransportableIntendedForProtocol, mockProtocolIdentifier));
        }
    }
    
    @Nested
    @Tag("Message.duplicate")
    @DisplayName("Message duplicate tests")
    class DuplicateTest {
        
        @Test
        @DisplayName("Test if the method duplicate returns a message which has the same value than the base message except for the content " +
                "which must also be duplicate")
        void testDuplicate() {
            // GIVEN
            when(mockTransportableIntendedForProtocol.duplicate()).thenReturn(mockTransportableIntendedForProtocolOther);
            
            // WHEN
            Message duplicateMessage = message.duplicate();
            
            // THEN
            verify(mockTransportableIntendedForProtocol, times(1)).duplicate();
            assertSame(duplicateMessage.getIntendedProtocol(), message.getIntendedProtocol());
            assertNotSame(duplicateMessage.getContent(), message.getContent());
        }
        
    }
    
}
