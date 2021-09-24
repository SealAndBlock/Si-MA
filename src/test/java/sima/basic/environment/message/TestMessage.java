package sima.basic.environment.message;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.protocol.ProtocolIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestMessage {
    
    // Variables
    
    protected Message message;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    protected Message mockContentMessage;
    
    @Mock
    private Message mockContentMessageOther;
    
    // Init.
    
    @BeforeEach
    protected void setUp() {
        message = new Message(mockContentMessage, mockProtocolIdentifier);
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
            assertThrows(NullPointerException.class, () -> new Message(mockContentMessage, null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new Message(mockContentMessage, mockProtocolIdentifier));
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
            when(mockContentMessage.duplicate()).thenReturn(mockContentMessageOther);
            
            // WHEN
            Message duplicateMessage = message.duplicate();
            
            // THEN
            verify(mockContentMessage, times(1)).duplicate();
            assertSame(duplicateMessage.getIntendedProtocol(), message.getIntendedProtocol());
            assertNotSame(duplicateMessage.getContent(), message.getContent());
        }
        
    }
    
    @Nested
    @Tag("Message.getMessage")
    @DisplayName("Message getMessage tests")
    class getMessageTest {
        
        @Test
        @DisplayName("Test if getMessage returns the same instance of getContent")
        void testGetMessageReturnsSameInstanceOfGetContent() {
            var content = message.getContent();
            var contentMessage = message.getMessage();
            assertThat(contentMessage).isSameAs(content);
        }
        
    }
    
}
