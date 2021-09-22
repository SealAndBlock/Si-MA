package sima.basic.broadcast;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.event.MessageReceptionEvent;
import sima.basic.transport.TestTransportProtocol;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.EventTransportable;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestSimpleBroadcastProtocol extends TestTransportProtocol {
    
    // Variables.
    
    protected SimpleBroadcastProtocol simpleBroadcastProtocol;
    
    @Mock
    private SimaAgent mockAgent;
    
    private final AgentIdentifier agentIdentifier = new AgentIdentifier("A", 0, 0);
    
    @Mock
    private EventTransportable mockEventTransportable;
    
    @Mock
    private Environment mockEnvironment;
    
    @Mock
    private Protocol mockProtocol;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    private Event mockEvent;
    
    // Init.
    
    @BeforeEach
    @Override
    public void setUp() {
        simpleBroadcastProtocol = new SimpleBroadcastProtocol("BD_P", mockAgent, null);
        transportProtocol = simpleBroadcastProtocol;
        super.setUp();
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
            assertThrows(NullPointerException.class, () -> new SimpleBroadcastProtocol(null, mockAgent, args));
        }
        
        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null agent")
        void testConstructorWithNullAgent() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new SimpleBroadcastProtocol("BD_P", null, args));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw an Exception with null args")
        void testConstructorWithNullArgs() {
            assertDoesNotThrow(() -> new SimpleBroadcastProtocol("BD_P", mockAgent, null));
        }
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.broadcast")
    @DisplayName("SimpleBroadcastProtocol broadcast tests")
    class BroadcastTest {
        
        @Test
        @DisplayName("Test if broadcast does not throw an Exception an call environment.broadcastEvent")
        void testBroadcast() {
            // WHEN
            List<AgentIdentifier> evolvingAgent = new ArrayList<>();
            evolvingAgent.add(agentIdentifier);
            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(evolvingAgent);
            when(mockAgent.getAgentIdentifier()).thenReturn(agentIdentifier);
            
            // GIVEN
            simpleBroadcastProtocol.setEnvironment(mockEnvironment);
            assertDoesNotThrow(() -> simpleBroadcastProtocol.broadcast(mockEventTransportable));
        }
        
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.receive")
    @DisplayName("SimpleBroadcastProtocol receive tests")
    class ReceiveTest {
        
        @Test
        @DisplayName("Test if receive does not throw an Exception")
        void testReceive() {
            // GIVEN
            when(mockAgent.getProtocol(any(ProtocolIdentifier.class))).thenReturn(mockProtocol);
            
            assertDoesNotThrow(() -> simpleBroadcastProtocol.receive(new BroadcastMessage(agentIdentifier, mockEventTransportable,
                    simpleBroadcastProtocol.getIdentifier())));
        }
        
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.deliver")
    @DisplayName("SimpleBroadcastProtocol deliver tests")
    class DeliverTest {
        
        @Test
        @DisplayName("Test if deliver does not throw an Exception if the owner has the intended protocol of the message content")
        void testDeliverWithKnownIntendedProtocol() {
            // GIVEN
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            
            BroadcastMessage broadcastMessage = new BroadcastMessage(agentIdentifier, mockEventTransportable, mockProtocolIdentifier);
            
            assertDoesNotThrow(() -> simpleBroadcastProtocol.deliver(broadcastMessage));
        }
        
        @Test
        @DisplayName("Test if deliver throws an Exception if the owner has not the intended protocol of the message content")
        void testDeliverWithUnKnownIntendedProtocol() {
            // GIVEN
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(null);
            
            BroadcastMessage broadcastMessage = new BroadcastMessage(agentIdentifier, mockEventTransportable, mockProtocolIdentifier);
            
            assertThrows(UnknownProtocolForAgentException.class, () -> simpleBroadcastProtocol.deliver(broadcastMessage));
        }
        
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.processEvent")
    @DisplayName("SimpleBroadcastProtocol processEvent tests")
    class ProcessEventTest {
        
        @Test
        @DisplayName("Test if processEvent does not throw an Exception if the event contains a BroadcastMessage")
        void testProcessEventWithBroadcastMessage() {
            // GIVEN
            when(mockAgent.getProtocol(any(ProtocolIdentifier.class))).thenReturn(mockProtocol);
            
            BroadcastMessage broadcastMessage = new BroadcastMessage(agentIdentifier, mockEventTransportable, mockProtocolIdentifier);
            assertDoesNotThrow(() -> simpleBroadcastProtocol.processEvent(new MessageReceptionEvent(broadcastMessage, mockProtocolIdentifier)));
        }
        
        @Test
        @DisplayName("Test if processEvent throw an UnsupportedOperationException if the event does not contains a BroadcastMessage")
        void testProcessEventWithOtherEvent() {
            assertThrows(UnsupportedOperationException.class, () -> simpleBroadcastProtocol.processEvent(mockEvent));
        }
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.processTransportable")
    @DisplayName("SimpleBroadcastProtocol processTransportable tests")
    class ProcessEventTransportableTest {
        
        @Test
        @DisplayName("Test if processTransportable throws an UnsupportedOperationException")
        void testProcessTransportable() {
            assertThrows(UnsupportedOperationException.class, () -> simpleBroadcastProtocol.processEventTransportable(mockEventTransportable));
        }
        
    }
}
