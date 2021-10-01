package sima.standard.environment.message.event;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.standard.environment.message.Message;
import sima.core.environment.event.TestEvent;
import sima.core.protocol.ProtocolIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestMessageReceptionEvent extends TestEvent {
    
    // Variables.
    
    private MessageReceptionEvent messageReceptionEvent;
    
    @Mock
    private Message mockMessage;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        messageReceptionEvent = new MessageReceptionEvent(mockMessage, mockProtocolIdentifier);
        event = messageReceptionEvent;
    }
    
    // Tests.
    
    @Nested
    @Tag("MessageReceptionEvent.constructor")
    @DisplayName("MessageReceptionEvent constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor does not throw exception with null message")
        void testConstructorWithNullMessage() {
            assertDoesNotThrow(() -> new MessageReceptionEvent(null, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null intended protocol")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(NullPointerException.class, () -> new MessageReceptionEvent(mockMessage, null));
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
            assertThat(messageReceptionEvent.getIntendedProtocol()).isNotNull();
        }
        
    }
}
