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
public class TestBasicBroadcast extends TestProtocol {
    
    // Variables.
    
    protected BasicBroadcast BasicBroadcast;
    
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
        BasicBroadcast = new BasicBroadcast("BD_P", mockAgent, correctArgs);
        
        protocol = BasicBroadcast;
    }
    
    // Tests.
    
    @Nested
    @Tag("BasicBroadcast.constructor")
    @DisplayName("BasicBroadcast constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null name")
        void testConstructorWithNullName() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new BasicBroadcast(null, mockAgent, args));
        }
        
        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null agent")
        void testConstructorWithNullAgent() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new BasicBroadcast("BD_P", null, args));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw Exception with null args")
        void testConstructorWithNullArgs() {
            assertDoesNotThrow(() -> new BasicBroadcast("BD_P", mockAgent, null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw Exception with not null args")
        void testConstructorWithNotNullArgs() {
            Map<String, String> args = new HashMap<>();
            assertDoesNotThrow(() -> new BasicBroadcast("BD_P", mockAgent, args));
        }
    }
    
    @Nested
    @Tag("BasicBroadcast.broadcast")
    @DisplayName("BasicBroadcast broadcast tests")
    class BroadcastTest {
        
        @Test
        @DisplayName("Test if broadcast does not throw an Exception with not null Message")
        void testBroadcastWithNotNullMessage() {
            // WHEN
            BasicBroadcast.setEnvironment(mockEnvironment);
            BasicBroadcast.setMessageTransport(mockMessageTransport);
            
            List<AgentIdentifier> evolvingAgent = new ArrayList<>();
            evolvingAgent.add(mockAgentIdentifier);
            
            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(evolvingAgent);
            when(mockAgent.getAgentIdentifier()).thenReturn(mockAgentIdentifier);
            
            // GIVEN
            assertDoesNotThrow(() -> BasicBroadcast.broadcast(mockMessage));
        }
        
        @Test
        @DisplayName("Test if broadcast throws IllegalArgumentException if the message is null")
        void testBroadcastWithNullMessage() {
            assertThrows(IllegalArgumentException.class, () -> BasicBroadcast.broadcast(null));
        }
        
    }
    
    @Nested
    @Tag("BasicBroadcast.receive")
    @DisplayName("BasicBroadcast receive tests")
    class ReceiveTest {
        
        @Test
        @DisplayName("Test if receive throws UnsupportedOperationException if the message is not a BroadcastMessage")
        void testReceiveWithNotBroadcastMessage() {
            assertThrows(UnsupportedOperationException.class, () -> BasicBroadcast.receive(mockMessage));
        }
        
        @Test
        @DisplayName("Test if receive does not throw Exception with a correct BroadcastMessage")
        void testReceiveWithCorrectBroadcastMessage() {
            // WHEN
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            
            // GIVEN
            BasicBroadcast.setEnvironment(mockEnvironment);
            assertDoesNotThrow(() -> BasicBroadcast.receive(new BroadcastMessage(mockAgentIdentifier, mockMessage,
                    BasicBroadcast.getIdentifier())));
        }
        
    }
    
    @Nested
    @Tag("BasicBroadcast.deliver")
    @DisplayName("BasicBroadcast deliver tests")
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
            assertDoesNotThrow(() -> BasicBroadcast.deliver(broadcastMessage));
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
            assertThrows(UnknownProtocolForAgentException.class, () -> BasicBroadcast.deliver(broadcastMessage));
        }
        
        @Test
        @DisplayName("Test if deliver throws an Exception if the message is not a BroadcastMessage")
        void testDeliverWithNotBroadcastMessage() {
            assertThrows(UnsupportedOperationException.class, () -> BasicBroadcast.deliver(mockMessage));
        }
        
    }
    
    @Nested
    @Tag("BasicBroadcast.processEvent")
    @DisplayName("BasicBroadcast processEvent tests")
    class ProcessEventTest {
        
        @Test
        @DisplayName("Test if processEvent does not throw an Exception if the event contains a BroadcastMessage")
        void testProcessEventWithBroadcastMessage() {
            // WHEN
            BroadcastMessage broadcastMessage = new BroadcastMessage(mockAgentIdentifier, mockMessage,
                    mockProtocolIdentifier);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            
            // GIVEN
            assertDoesNotThrow(() -> BasicBroadcast.processEvent(broadcastMessage));
        }
        
        @Test
        @DisplayName("Test if processEvent throw an UnsupportedOperationException if the event does not contains a BroadcastMessage")
        void testProcessEventWithOtherEvent() {
            assertThrows(UnsupportedOperationException.class, () -> BasicBroadcast.processEvent(mockEvent));
        }
    }
}
