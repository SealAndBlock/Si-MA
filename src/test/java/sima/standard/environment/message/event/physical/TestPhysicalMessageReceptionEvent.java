package sima.standard.environment.message.event.physical;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.standard.environment.message.Message;
import sima.standard.environment.message.event.MessageReceptionEvent;
import sima.core.environment.physical.TestPhysicalEvent;
import sima.core.protocol.ProtocolIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestPhysicalMessageReceptionEvent extends TestPhysicalEvent {
    
    // Variables.
    
    protected PhysicalMessageReceptionEvent physicalMessageReceptionEvent;
    
    @Mock
    private Message mockMessage;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    // Init.
    
    @BeforeEach
    @Override
    public void setUp() {
        physicalMessageReceptionEvent = new PhysicalMessageReceptionEvent(mockMessage, mockProtocolIdentifier);
        physicalEvent = physicalMessageReceptionEvent;
        super.setUp();
    }
    
    // Tests.
    
    @Nested
    @Tag("PhysicalMessageReceptionEvent.constructor")
    @DisplayName("PhysicalMessageReceptionEvent constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor does not throw exception with null message")
        void testConstructorWithNullMessage() {
            assertDoesNotThrow(() -> new PhysicalMessageReceptionEvent(null, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor throws NullPointerException with null intended protocol")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(NullPointerException.class, () -> new PhysicalMessageReceptionEvent(mockMessage, null));
        }
        
        @Test
        @DisplayName("Test if constructors does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new MessageReceptionEvent(mockMessage, mockProtocolIdentifier));
        }
    }
    
    @Nested
    @Tag("ProtocolEvent.getIntendedProtocol")
    @DisplayName("ProtocolEvent getIntendedProtocol tests")
    class GetIntendedProtocolTest {
        
        @Test
        @DisplayName("Test if getIntendedProtocol never returns null")
        void testGetIntendedProtocolNeverReturnsNull() {
            assertThat(physicalMessageReceptionEvent.getIntendedProtocol()).isNotNull();
        }
        
    }
    
}
