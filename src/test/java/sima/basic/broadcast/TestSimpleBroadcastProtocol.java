package sima.basic.broadcast;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.Message;
import sima.basic.transport.MessageTransportProtocol;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.TestProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestSimpleBroadcastProtocol extends TestProtocol {
    
    // Variables.
    
    protected SimpleBroadcastProtocol simpleBroadcastProtocol;
    
    @Mock
    private SimaAgent mockAgent;
    
    @Mock
    private AgentIdentifier mockAgentIdentifier;
    
    @Mock
    private MessageTransportProtocol mockMessageTransport;
    
    @Mock
    private Environment mockEnvironment;
    
    @Mock
    private Protocol mockProtocol;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    private Event mockEvent;
    
    @Mock
    private Message mockMessage;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        Map<String, String> correctArgs = new HashMap<>();
        simpleBroadcastProtocol = new SimpleBroadcastProtocol("BD_P", mockAgent, correctArgs);
        
        protocol = simpleBroadcastProtocol;
    }
    
    // Tests.
    
    @Nested
    @Tag("SimpleBroadcastProtocol.constructor")
    @DisplayName("SimpleBroadcastProtocol constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null name")
        void testConstructorWithNullName() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new SimpleBroadcastProtocol(null, mockAgent, args));
        }
        
        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null agent")
        void testConstructorWithNullAgent() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new SimpleBroadcastProtocol("BD_P", null, args));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw Exception with null args")
        void testConstructorWithNullArgs() {
            assertDoesNotThrow(() -> new SimpleBroadcastProtocol("BD_P", mockAgent, null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw Exception with not null args")
        void testConstructorWithNotNullArgs() {
            Map<String, String> args = new HashMap<>();
            assertDoesNotThrow(() -> new SimpleBroadcastProtocol("BD_P", mockAgent, args));
        }
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.broadcast")
    @DisplayName("SimpleBroadcastProtocol broadcast tests")
    class BroadcastTest {
        
        @Test
        @DisplayName("Test if broadcast does not throw an Exception with not null Message")
        void testBroadcastWithNotNullMessage() {
            // WHEN
            simpleBroadcastProtocol.setEnvironment(mockEnvironment);
            simpleBroadcastProtocol.setMessageTransport(mockMessageTransport);
            
            List<AgentIdentifier> evolvingAgent = new ArrayList<>();
            evolvingAgent.add(mockAgentIdentifier);
            
            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(evolvingAgent);
            when(mockAgent.getAgentIdentifier()).thenReturn(mockAgentIdentifier);
            
            // GIVEN
            assertDoesNotThrow(() -> simpleBroadcastProtocol.broadcast(mockMessage));
        }
        
        @Test
        @DisplayName("Test if broadcast throws IllegalArgumentException if the message is null")
        void testBroadcastWithNullMessage() {
            assertThrows(IllegalArgumentException.class, () -> simpleBroadcastProtocol.broadcast(null));
        }
        
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.receive")
    @DisplayName("SimpleBroadcastProtocol receive tests")
    class ReceiveTest {
        
        @Test
        @DisplayName("Test if receive throws UnsupportedOperationException if the message is not a BroadcastMessage")
        void testReceiveWithNotBroadcastMessage() {
            assertThrows(UnsupportedOperationException.class, () -> simpleBroadcastProtocol.receive(mockMessage));
        }
        
        @Test
        @DisplayName("Test if receive does not throw Exception with a correct BroadcastMessage")
        void testReceiveWithCorrectBroadcastMessage() {
            // WHEN
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            
            // GIVEN
            simpleBroadcastProtocol.setEnvironment(mockEnvironment);
            assertDoesNotThrow(() -> simpleBroadcastProtocol.receive(new BroadcastMessage(mockAgentIdentifier, mockMessage,
                    simpleBroadcastProtocol.getIdentifier())));
        }
        
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.deliver")
    @DisplayName("SimpleBroadcastProtocol deliver tests")
    class DeliverTest {
        
        @Test
        @DisplayName("Test if deliver does not throw Exception if the owner has the intended protocol of the message content")
        void testDeliverWithKnownIntendedProtocol() {
            // WHEN
            BroadcastMessage broadcastMessage = new BroadcastMessage(mockAgentIdentifier, mockMessage,
                    mockProtocolIdentifier);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            
            // GIVEN
            assertDoesNotThrow(() -> simpleBroadcastProtocol.deliver(broadcastMessage));
        }
        
        @Test
        @DisplayName("Test if deliver throws UnknownProtocolForAgentException if the owner does not know the intended protocol of the message " +
                "content")
        void testDeliverWithUnKnownIntendedProtocol() {
            // WHEN
            BroadcastMessage broadcastMessage = new BroadcastMessage(mockAgentIdentifier, mockMessage,
                    mockProtocolIdentifier);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(null);
            
            // GIVEN
            assertThrows(UnknownProtocolForAgentException.class, () -> simpleBroadcastProtocol.deliver(broadcastMessage));
        }
        
        @Test
        @DisplayName("Test if deliver throws an Exception if the message is not a BroadcastMessage")
        void testDeliverWithNotBroadcastMessage() {
            assertThrows(UnsupportedOperationException.class, () -> simpleBroadcastProtocol.deliver(mockMessage));
        }
        
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.processEvent")
    @DisplayName("SimpleBroadcastProtocol processEvent tests")
    class ProcessEventTest {
        
        @Test
        @DisplayName("Test if processEvent does not throw an Exception if the event contains a BroadcastMessage")
        void testProcessEventWithBroadcastMessage() {
            // WHEN
            BroadcastMessage broadcastMessage = new BroadcastMessage(mockAgentIdentifier, mockMessage,
                    mockProtocolIdentifier);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            
            List<AgentIdentifier> evolvingAgent = new ArrayList<>();
            evolvingAgent.add(mockAgentIdentifier);
            
            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(evolvingAgent);
            simpleBroadcastProtocol.setEnvironment(mockEnvironment);
            simpleBroadcastProtocol.setMessageTransport(mockMessageTransport);
            
            // GIVEN
            assertDoesNotThrow(() -> simpleBroadcastProtocol.processEvent(broadcastMessage));
        }
        
        @Test
        @DisplayName("Test if processEvent throw an UnsupportedOperationException if the event does not contains a BroadcastMessage")
        void testProcessEventWithOtherEvent() {
            assertThrows(UnsupportedOperationException.class, () -> simpleBroadcastProtocol.processEvent(mockEvent));
        }
    }
}
