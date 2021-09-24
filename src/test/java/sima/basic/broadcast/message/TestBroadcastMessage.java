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
    private AgentIdentifier mockAgentSender;
    
    @Mock
    private AgentIdentifier mockAgentSenderOther;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifierOther;
    
    // Init
    
    @BeforeEach
    @Override
    protected void setUp() {
        broadcastMessage = new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifier);
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
            assertThrows(NullPointerException.class, () -> new BroadcastMessage(null, mockContentMessage, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with null content")
        void testConstructorWithNullContent() {
            assertDoesNotThrow(() -> new BroadcastMessage(mockAgentSender, null, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor throws NullPointerException with null intended protocol")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(NullPointerException.class, () -> new BroadcastMessage(mockAgentSender, mockContentMessage,
                    null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifier));
        }
    }
    
    @Nested
    @Tag("BroadcastMessage.hashCode")
    @DisplayName("BroadcastMessage hashCode tests")
    class HashCodeTest {
        
        @Test
        @DisplayName("Test if two equals message has the same hashCode")
        void testHashCodeWithEqualMessages() {
            // WHEN
            var m1 = new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            var m2 = new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            
            // GIVEN
            assertThat(m1.hashCode()).isEqualByComparingTo(m2.hashCode());
        }
        
    }
    
    @Nested
    @Tag("BroadcastMessage.equals")
    @DisplayName("BroadcastMessage equals tests")
    class EqualsTest {
        
        @Test
        @DisplayName("Test if equals returns true with two equals message")
        void testEqualsWithTwoEqualsMessage() {
            // WHEN
            var m1 = new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            var m2 = new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            
            // GIVEN
            assertThat(m1).isEqualTo(m2);
        }
        
        @Test
        @DisplayName("Test if equals returns false with two not equals message")
        void testEqualsWithTwoNotEqualsMessage() {
            // WHEN
            var m1 = new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            var m2 = new BroadcastMessage(mockAgentSender, mockContentMessageOther, mockProtocolIdentifier);
            var m3 = new BroadcastMessage(mockAgentSenderOther, mockContentMessage, mockProtocolIdentifier);
            var m4 = new BroadcastMessage(mockAgentSender, mockContentMessage, mockProtocolIdentifierOther);
            
            // GIVEN
            assertThat(m1).isNotEqualTo(m2).isNotEqualTo(m3).isNotEqualTo(m4);
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
