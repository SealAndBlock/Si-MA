package sima.basic.environment.event;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.environment.message.Message;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.exchange.event.TestEvent;
import sima.core.environment.exchange.transport.Transportable;
import sima.core.protocol.ProtocolIdentifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMessage extends TestEvent {
    
    // Variables
    
    protected Message message;
    
    @Mock
    private AgentIdentifier mockSender;
    
    @Mock
    private AgentIdentifier mockReceiver;
    
    @Mock
    private AgentIdentifier mockReceiverOther;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    private Transportable mockTransportable;
    
    @Mock
    private Transportable mockTransportableOther;
    
    // Init.
    
    @BeforeEach
    protected void setUp() {
        message = new Message(mockSender, mockReceiver, mockTransportable, mockProtocolIdentifier);
        event = message;
    }
    
    // Test.
    
    @Nested
    @Tag("Message.constructor")
    @DisplayName("Message constructors tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor with null sender throws a NullPointerException")
        void testConstructorWithNullSender() {
            assertThrows(NullPointerException.class, () -> new Message(null, mockReceiver, mockTransportable, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor with null receiver does not throws exception")
        void testConstructorWithNullReceiver() {
            assertDoesNotThrow(() -> new Message(mockSender, null, mockTransportable, mockProtocolIdentifier));
        }
        
        @Test
        @DisplayName("Test if constructor with null protocolIdentifier does not throw exception")
        void testConstructorWithNullProtocolIdentifier() {
            assertDoesNotThrow(() -> new Message(mockSender, mockReceiver, mockTransportable, null));
        }
        
        @Test
        @DisplayName("Test if constructor with null protocolIdentifier does not throw exception")
        void testConstructorWithNullContent() {
            assertDoesNotThrow(() -> new Message(mockSender, mockReceiver, null, mockProtocolIdentifier));
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
            when(mockTransportable.duplicate()).thenReturn(mockTransportableOther);
            
            // WHEN
            Message duplicateMessage = message.duplicate();
            
            // THEN
            verify(mockTransportable, times(1)).duplicate();
            assertSame(duplicateMessage.getSender(), message.getSender());
            assertSame(duplicateMessage.getReceiver(), message.getReceiver());
            assertSame(duplicateMessage.getProtocolIntended(), message.getProtocolIntended());
            assertNotSame(duplicateMessage.getContent(), message.getContent());
        }
        
    }
    
    @Nested
    @Tag("Message.duplicateWithNewReceiver")
    @DisplayName("Message duplicateWithNewReceiver tests")
    class DuplicateWithNewReceiverTest extends TestEvent.DuplicateWithNewReceiverTest {
        
        @Test
        @DisplayName("Test if the method duplicateWithNewReceiver return a message with same value than the base message except for the " +
                "receiver and also the content which must be duplicate")
        @Override
        public void testDuplicateWithNewReceiver() {
            // GIVEN
            when(mockTransportable.duplicate()).thenReturn(mockTransportableOther);
            
            // WHEN
            Message duplicatedMessage = message.duplicateWithNewReceiver(mockReceiverOther);
            
            // THEN
            verify(mockTransportable, times(1)).duplicate();
            assertSame(duplicatedMessage.getSender(), message.getSender());
            assertNotSame(duplicatedMessage.getReceiver(), message.getReceiver());
            assertEquals(mockReceiverOther, duplicatedMessage.getReceiver());
            assertSame(duplicatedMessage.getProtocolIntended(), message.getProtocolIntended());
            assertNotSame(duplicatedMessage.getContent(), message.getContent());
        }
        
    }
    
    @Nested
    @Tag("Message.hasIntendedProtocol")
    @DisplayName("Message hasIntendedProtocol tests")
    class IsProtocolEventTest extends TestEvent.HasIntendedProtocol {
        
        @Test
        @DisplayName("Test if hasIntendedProtocol methods returns false if the protocolTargeted is null")
        void testHasIntendedProtocolWithNullProtocolTargeted() {
            Message messageWithNotProtocolTargeted = new Message(mockSender, mockReceiver, mockTransportable, null);
            assertFalse(messageWithNotProtocolTargeted.hasIntendedProtocol());
        }
        
        @Test
        @DisplayName("Test if hasIntendedProtocol methods returns true if the protocolTargeted is not null")
        void testHasIntendedProtocolWithNotNullProtocolTargeted() {
            Message messageWithNotProtocolTargeted = new Message(mockSender, mockReceiver, mockTransportable, mockProtocolIdentifier);
            assertTrue(messageWithNotProtocolTargeted.hasIntendedProtocol());
        }
        
    }
    
}
