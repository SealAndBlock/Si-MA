package sima.basic.environment.message;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.environment.message.Message;
import sima.core.environment.event.transport.EventTransportable;
import sima.core.protocol.ProtocolIdentifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMessage {
    
    // Variables
    
    protected Message message;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    protected EventTransportable mockEventTransportable;
    
    @Mock
    private EventTransportable mockEventTransportableOther;
    
    // Init.
    
    @BeforeEach
    protected void setUp() {
        message = new Message(mockEventTransportable, mockProtocolIdentifier);
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
            assertThrows(NullPointerException.class, () -> new Message(mockEventTransportable, null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new Message(mockEventTransportable, mockProtocolIdentifier));
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
            when(mockEventTransportable.duplicate()).thenReturn(mockEventTransportableOther);
            
            // WHEN
            Message duplicateMessage = message.duplicate();
            
            // THEN
            verify(mockEventTransportable, times(1)).duplicate();
            assertSame(duplicateMessage.getIntendedProtocol(), message.getIntendedProtocol());
            assertNotSame(duplicateMessage.getContent(), message.getContent());
        }
        
    }
    
}
