package sima.basic.message_sender;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.environment.message.Message;
import sima.basic.transport.MessageTransportProtocol;
import sima.basic.transport.TestMessageTransportProtocol;
import sima.core.agent.SimaAgent;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestSimpleMessageSenderProtocol extends TestMessageTransportProtocol {
    
    // Variables.
    
    private SimpleMessageSenderProtocol simpleMessageSenderProtocol;
    
    private final String protocolTag = "MESSAGE_SENDER_TAG";
    
    @Mock
    private SimaAgent mockAgentOwner;
    
    private Map<String, String> correctArgs;
    
    @Mock
    private Message mockMessage;
    
    @Mock
    private ProtocolIdentifier mockIntendedProtocolId;
    
    @Mock
    private Protocol mockIntendedProtocol;
    
    // Init.
    
    @BeforeEach
    @Override
    public void setUp() {
        correctArgs = new HashMap<>();
        correctArgs.put(MessageTransportProtocol.ARG_PHYSICAL_CONNECTION_LAYER_NAME, "PC_LAYER_NAME");
        
        simpleMessageSenderProtocol = new SimpleMessageSenderProtocol(protocolTag, mockAgentOwner, correctArgs);
        messageTransportProtocol = simpleMessageSenderProtocol;
        super.setUp();
    }
    
    // Tests.
    
    @Nested
    @Tag("SimpleMessageSenderProtocol.constructor")
    @DisplayName("SimpleMessageSenderProtocol constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws IllegalArgumentException with null args")
        void testConstructorsWithNullArgs() {
            assertThrows(IllegalArgumentException.class, () -> new SimpleMessageSenderProtocol(null, mockAgentOwner, correctArgs));
            assertThrows(IllegalArgumentException.class, () -> new SimpleMessageSenderProtocol(protocolTag, null, correctArgs));
            assertThrows(IllegalArgumentException.class, () -> new SimpleMessageSenderProtocol(protocolTag, mockAgentOwner, null));
        }
        
        @Test
        @DisplayName("Test if constructors throws IllegalArgumentException if there is no the argument ARG_PHYSICAL_CONNECTION_LAYER_NAME in " +
                "the args map")
        void testConstructorWithNoCorrectArgumentInArgs() {
            Map<String, String> wrongArgs = new HashMap<>();
            wrongArgs.put("WRONG_ARG", "WRONG");
            
            assertThrows(IllegalArgumentException.class, () -> new SimpleMessageSenderProtocol(protocolTag, mockAgentOwner, wrongArgs));
            assertThrows(IllegalArgumentException.class, () -> new SimpleMessageSenderProtocol(protocolTag, mockAgentOwner, null));
        }
        
    }
    
    @Nested
    @Tag("SimpleMessageSenderProtocol.receive")
    @DisplayName("SimpleMessageSenderProtocol receive tests")
    class ReceiveTest {
        
        @Test
        @DisplayName("Test if receive throws NullPointerException with null Message")
        void testReceiveWithNullMessage() {
            assertThrows(NullPointerException.class, () -> simpleMessageSenderProtocol.receive(null));
        }
        
        @Test
        @DisplayName("Test if receive does not throw Exception")
        void testReceive() {
            // WHEN
            when(mockMessage.getMessage()).thenReturn(mockMessage);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockIntendedProtocolId);
            when(mockAgentOwner.getProtocol(mockIntendedProtocolId)).thenReturn(mockIntendedProtocol);
            
            // GIVEN
            assertDoesNotThrow(() -> simpleMessageSenderProtocol.receive(mockMessage));
        }
        
    }
    
    @Nested
    @Tag("SimpleMessageSenderProtocol.deliver")
    @DisplayName("SimpleMessageSenderProtocol deliver tests")
    class DeliverTest {
        
        @Test
        @DisplayName("Test if deliver throws UnknownProtocolForAgentException if the protocol is not in the agentOwner")
        void testDeliverWithUnknownProtocol() {
            // WHEN
            when(mockMessage.getMessage()).thenReturn(mockMessage);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockIntendedProtocolId);
            when(mockAgentOwner.getProtocol(mockIntendedProtocolId)).thenReturn(null);
            
            // GIVEN
            assertThrows(UnknownProtocolForAgentException.class, () -> simpleMessageSenderProtocol.deliver(mockMessage));
        }
        
        @Test
        @DisplayName("Test if deliver throws UnsupportedOperationException if the content of the message is null")
        void testDeliverWithNullMessageContent() {
            // WHEN
            when(mockMessage.getMessage()).thenReturn(null);
            
            // GIVEN
            assertThrows(UnsupportedOperationException.class, () -> simpleMessageSenderProtocol.deliver(mockMessage));
        }
        
    }
}
