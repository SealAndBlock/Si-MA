package sima.basic.broadcast;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.broadcast.message.BroadcastMessage;
import sima.basic.environment.message.Message;
import sima.basic.environment.message.event.physical.PhysicalMessageReceptionEvent;
import sima.basic.transport.TestMessageTransportProtocol;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.exception.NoPhysicalConnectionLayerFoundException;
import sima.core.exception.UnknownProtocolForAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.TransportableIntendedToProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestSimpleBroadcastProtocol extends TestMessageTransportProtocol {
    
    // Variables.
    
    protected SimpleBroadcastProtocol simpleBroadcastProtocol;
    
    private Map<String, String> correctArgs;
    
    @Mock
    private SimaAgent mockAgent;
    
    private final AgentIdentifier agentIdentifier = new AgentIdentifier("A", 0, 0);
    
    @Mock
    private TransportableIntendedToProtocol mockTransportableIntendedForProtocol;
    
    @Mock
    private Environment mockEnvironment;
    
    @Mock
    private PhysicalConnectionLayer mockPhysicalConnectionLayer;
    
    @Mock
    private Protocol mockProtocol;
    
    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;
    
    @Mock
    private PhysicalMessageReceptionEvent mockPhysicalMessageReceptionEvent;
    
    @Mock
    private Message mockMessage;
    
    // Init.
    
    @BeforeEach
    @Override
    public void setUp() {
        correctArgs = new HashMap<>();
        correctArgs.put(SimpleBroadcastProtocol.ARG_PHYSICAL_CONNECTION_LAYER_NAME, "PCL");
        
        simpleBroadcastProtocol = new SimpleBroadcastProtocol("BD_P", mockAgent, correctArgs);
        messageTransportProtocol = simpleBroadcastProtocol;
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
        @DisplayName("Test if constructor throws IllegalArgumentException with null args")
        void testConstructorWithNullArgs() {
            assertThrows(IllegalArgumentException.class, () -> new SimpleBroadcastProtocol("BD_P", mockAgent, null));
        }
        
        @Test
        @DisplayName("Test if constructor throws IllegalArgumentException with args which has not physicalConnectionLayerName value")
        void testConstructorWithNoCorrectMapArgs() {
            Map<String, String> incorrectArgs = new HashMap<>();
            incorrectArgs.put("TROLL", "WRONG");
            assertThrows(IllegalArgumentException.class, () -> new SimpleBroadcastProtocol("BD_P", mockAgent, incorrectArgs));
        }
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.broadcast")
    @DisplayName("SimpleBroadcastProtocol broadcast tests")
    class BroadcastTest {
        
        @Test
        @DisplayName("Test if broadcast does not throw an Exception with correct TransportableIntendedForProtocol")
        void testBroadcastWithCorrectTransportableIntendedForProtocol() {
            // WHEN
            List<AgentIdentifier> evolvingAgent = new ArrayList<>();
            evolvingAgent.add(agentIdentifier);
            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(evolvingAgent);
            when(mockAgent.getAgentIdentifier()).thenReturn(agentIdentifier);
            when(mockEnvironment.getPhysicalConnectionLayer(simpleBroadcastProtocol.getPhysicalConnectionLayerName())).thenReturn(
                    mockPhysicalConnectionLayer);
            
            // GIVEN
            simpleBroadcastProtocol.setEnvironment(mockEnvironment);
            assertDoesNotThrow(() -> simpleBroadcastProtocol.broadcast(mockTransportableIntendedForProtocol));
        }
        
        @Test
        @DisplayName("Test if broadcast throws an NoPhysicalConnectionLayerFoundException if there is no PhysicalConnectionLayer associate to " +
                "the getPhysicalConnectionLayerName")
        void testBroadcastWithNoPhysicalConnectionLayerFound() {
            // WHEN
            List<AgentIdentifier> evolvingAgent = new ArrayList<>();
            evolvingAgent.add(agentIdentifier);
            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(evolvingAgent);
            when(mockAgent.getAgentIdentifier()).thenReturn(agentIdentifier);
            when(mockEnvironment.getPhysicalConnectionLayer(simpleBroadcastProtocol.getPhysicalConnectionLayerName())).thenReturn(null);
            
            // GIVEN
            simpleBroadcastProtocol.setEnvironment(mockEnvironment);
            assertThrows(NoPhysicalConnectionLayerFoundException.class,
                    () -> simpleBroadcastProtocol.broadcast(mockTransportableIntendedForProtocol));
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
            // GIVEN
            when(mockAgent.getProtocol(any(ProtocolIdentifier.class))).thenReturn(mockProtocol);
            when(mockTransportableIntendedForProtocol.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            
            simpleBroadcastProtocol.setEnvironment(mockEnvironment);
            assertDoesNotThrow(() -> simpleBroadcastProtocol.receive(new BroadcastMessage(agentIdentifier, mockTransportableIntendedForProtocol,
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
            BroadcastMessage broadcastMessage = new BroadcastMessage(agentIdentifier, mockTransportableIntendedForProtocol,
                    mockProtocolIdentifier);
            when(mockTransportableIntendedForProtocol.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            
            assertDoesNotThrow(() -> simpleBroadcastProtocol.deliver(broadcastMessage));
        }
        
        @Test
        @DisplayName("Test if deliver throws an Exception if the owner has not the intended protocol of the message content")
        void testDeliverWithUnKnownIntendedProtocol() {
            // GIVEN
            BroadcastMessage broadcastMessage = new BroadcastMessage(agentIdentifier, mockTransportableIntendedForProtocol,
                    mockProtocolIdentifier);
            when(mockTransportableIntendedForProtocol.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(mockProtocolIdentifier)).thenReturn(null);
            
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
    class ProcessEventTest extends TestMessageTransportProtocol.ProcessEventTest {
        
        @Test
        @DisplayName("Test if processEvent does not throw an Exception if the event contains a BroadcastMessage")
        void testProcessEventWithBroadcastMessage() {
            // GIVEN
            BroadcastMessage broadcastMessage = new BroadcastMessage(agentIdentifier, mockTransportableIntendedForProtocol,
                    mockProtocolIdentifier);
            when(mockPhysicalMessageReceptionEvent.getContent()).thenReturn(broadcastMessage);
            when(mockTransportableIntendedForProtocol.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgent.getProtocol(any(ProtocolIdentifier.class))).thenReturn(mockProtocol);
            
            
            assertDoesNotThrow(() -> simpleBroadcastProtocol.processEvent(mockPhysicalMessageReceptionEvent));
        }
        
        @Test
        @DisplayName("Test if processEvent throw an UnsupportedOperationException if the event does not contains a BroadcastMessage")
        void testProcessEventWithOtherEvent() {
            when(mockPhysicalMessageReceptionEvent.getContent()).thenReturn(mockMessage);
            
            assertThrows(UnsupportedOperationException.class, () -> simpleBroadcastProtocol.processEvent(mockPhysicalMessageReceptionEvent));
        }
    }
    
    @Nested
    @Tag("SimpleBroadcastProtocol.processTransportable")
    @DisplayName("SimpleBroadcastProtocol processTransportable tests")
    class ProcessTransportableInEventTest {
        
        @Test
        @DisplayName("Test if processTransportable throws an UnsupportedOperationException")
        void testProcessTransportable() {
            assertThrows(UnsupportedOperationException.class, () -> simpleBroadcastProtocol.processEventTransportable(
                    mockTransportableIntendedForProtocol));
        }
        
    }
}
