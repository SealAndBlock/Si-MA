package sima.basic.broadcast.message;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.environment.message.TestMessage;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.ProtocolIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class TestBroadcastMessage extends TestMessage {
    
    // Variables.
    
    protected BroadcastMessage broadcastMessage;
    
    @Mock
    private AgentIdentifier mockAgentIdentifier;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    // Init
    
    @BeforeEach
    @Override
    protected void setUp() {
        broadcastMessage = new BroadcastMessage(mockAgentIdentifier, mockEventTransportable, mockProtocolIdentifier);
        message = broadcastMessage;
    }
    
    // Tests.
    
    @Nested
    @Tag("BroadcastMessage.constructor")
    @DisplayName("BroadcastMessage constructor tests")
    class BroadcastMessageTest {
        
        @Test
        @DisplayName("Test if constructor throws NullPointerException with null sender")
        void testConstructorWithNullSender() {
            assertThrows(NullPointerException.class, () -> new BroadcastMessage(null, mockEventTransportable, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with null content")
        void testConstructorWithNullContent() {
            assertDoesNotThrow(() -> new BroadcastMessage(mockAgentIdentifier, null, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor throws NullPointerException with null intended protocol")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(NullPointerException.class, () -> new BroadcastMessage(mockAgentIdentifier, mockEventTransportable,
                    null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new BroadcastMessage(mockAgentIdentifier, mockEventTransportable, mockProtocolIdentifier));
        }
    }
    
    @Nested
    @Tag("BroadcastMessage.getSender")
    @DisplayName("BroadcastMessage getSender tests")
    class GetSenderTest {
        
        @Test
        @DisplayName("Test if getSender never returns null")
        void testGetSenderNeverReturnsNull() {
            assertThat(broadcastMessage.getSender()).isNotNull();
        }
        
    }
    
}
