package sima.basic.broadcast.reliable;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.basic.broadcast.basic.BroadcastMessage;
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
public class TestReliableBroadcast extends TestProtocol {

    // Variables.

    protected ReliableBroadcast reliableBroadcast;

    @Mock
    private SimaAgent mockAgentOwner;

    @Mock
    private AgentIdentifier mockOwnerIdentifier;

    @Mock
    private AgentIdentifier mockOtherIdentifier;

    @Mock
    private MessageTransportProtocol mockMessageTransport;

    @Mock
    private Environment mockEnvironment;

    @Mock
    private Protocol mockProtocol;

    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;

    @Mock
    private Message mockMessage;

    @Mock
    private Event mockEvent;

    // Init.

    @BeforeEach
    public void setUp() {
        Map<String, String> correctArgs = new HashMap<>();
        reliableBroadcast = new ReliableBroadcast("RB_P", mockAgentOwner, correctArgs);
        protocol = reliableBroadcast;
    }

    // Tests.

    @Nested
    @Tag("ReliableBroadcast.constructor")
    @DisplayName("ReliableBroadcast constructor tests")
    class ConstructorTest {

        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null name")
        void testConstructorWithNullName() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new ReliableBroadcast(null, mockAgentOwner, args));
        }

        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null agent")
        void testConstructorWithNullAgent() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new ReliableBroadcast("BD_P", null, args));
        }

        @Test
        @DisplayName("Test if constructor does not throw Exception with null args")
        void testConstructorWithNullArgs() {
            assertDoesNotThrow(() -> new ReliableBroadcast("BD_P", mockAgentOwner, null));
        }

        @Test
        @DisplayName("Test if constructor does not throw Exception with not null args")
        void testConstructorWithNotNullArgs() {
            Map<String, String> args = new HashMap<>();
            assertDoesNotThrow(() -> new ReliableBroadcast("BD_P", mockAgentOwner, args));
        }
    }

    @Nested
    @Tag("ReliableBroadcast.broadcast")
    @DisplayName("ReliableBroadcast broadcast tests")
    class BroadcastTest {

        @Test
        @DisplayName("Test if broadcast does not throw an Exception with not null Message")
        void testBroadcastWithNotNullMessage() {
            // WHEN
            reliableBroadcast.setEnvironment(mockEnvironment);
            reliableBroadcast.setMessageTransport(mockMessageTransport);

            List<AgentIdentifier> evolvingAgent = new ArrayList<>();
            evolvingAgent.add(mockOwnerIdentifier);

            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(evolvingAgent);
            when(mockAgentOwner.getAgentIdentifier()).thenReturn(mockOwnerIdentifier);

            // GIVEN
            assertDoesNotThrow(() -> reliableBroadcast.broadcast(mockMessage));
        }

        @Test
        @DisplayName("Test if broadcast throws IllegalArgumentException if the message is null")
        void testBroadcastWithNullMessage() {
            assertThrows(IllegalArgumentException.class, () -> reliableBroadcast.broadcast(null));
        }

    }


    @Nested
    @Tag("ReliableBroadcast.receive")
    @DisplayName("ReliableBroadcast receive tests")
    class ReceiveTest {

        @Test
        @DisplayName("Test if receive throws UnsupportedOperationException if the message is not a ReliableBroadcastMessage")
        void testReceiveWithNotReliableBroadcastMessage() {
            assertThrows(UnsupportedOperationException.class, () -> reliableBroadcast.receive(mockMessage));
        }

        @Test
        @DisplayName("Test if receive does not throw Exception with a correct ReliableBroadcastMessage")
        void testReceiveWithCorrectReliableBroadcastMessage() {
            // WHEN
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgentOwner.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);

            // GIVEN
            reliableBroadcast.setEnvironment(mockEnvironment);
            assertDoesNotThrow(() -> reliableBroadcast.receive(new ReliableBroadcastMessage(0, mockOwnerIdentifier, mockMessage,
                                                                                            reliableBroadcast.getIdentifier())));
        }

    }

    @Nested
    @Tag("ReliableBroadcast.deliver")
    @DisplayName("ReliableBroadcast deliver tests")
    class DeliverTest {

        @Test
        @DisplayName("Test if deliver does not throw Exception if the owner has the intended protocol of the message content")
        void testDeliverWithKnownIntendedProtocol() {
            // WHEN
            ReliableBroadcastMessage reliableBroadcastMessage = new ReliableBroadcastMessage(0, mockOwnerIdentifier, mockMessage,
                                                                                             mockProtocolIdentifier);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgentOwner.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);

            // GIVEN
            assertDoesNotThrow(() -> reliableBroadcast.deliver(reliableBroadcastMessage));
        }

        @Test
        @DisplayName("Test if deliver throws UnknownProtocolForAgentException if the owner does not know the intended protocol of the message " +
                "content")
        void testDeliverWithUnKnownIntendedProtocol() {
            // WHEN
            ReliableBroadcastMessage reliableBroadcastMessage = new ReliableBroadcastMessage(0, mockOwnerIdentifier, mockMessage,
                                                                                             mockProtocolIdentifier);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgentOwner.getProtocol(mockProtocolIdentifier)).thenReturn(null);

            // GIVEN
            assertThrows(UnknownProtocolForAgentException.class, () -> reliableBroadcast.deliver(reliableBroadcastMessage));
        }

        @Test
        @DisplayName("Test if deliver throws an Exception if the message is not a ReliableBroadcastMessage")
        void testDeliverWithNotReliableBroadcastMessage() {
            assertThrows(UnsupportedOperationException.class, () -> reliableBroadcast.deliver(mockMessage));
        }

    }

    @Nested
    @Tag("BasicBroadcast.processEvent")
    @DisplayName("BasicBroadcast processEvent tests")
    class ProcessEventTest {

        @Test
        @DisplayName("Test if processEvent does not throw an Exception if the event contains a ReliableBroadcastMessage")
        void testProcessEventWithBroadcastMessage() {
            // WHEN
            BroadcastMessage broadcastMessage = new ReliableBroadcastMessage(0, mockOtherIdentifier, mockMessage,
                                                                             mockProtocolIdentifier);
            when(mockMessage.getIntendedProtocol()).thenReturn(mockProtocolIdentifier);
            when(mockAgentOwner.getProtocol(mockProtocolIdentifier)).thenReturn(mockProtocol);
            when(mockAgentOwner.getAgentIdentifier()).thenReturn(mockOwnerIdentifier);

            List<AgentIdentifier> agents = new ArrayList<>();
            agents.add(mockOwnerIdentifier);
            agents.add(mockOtherIdentifier);
            when(mockEnvironment.getEvolvingAgentIdentifiers()).thenReturn(agents);

            reliableBroadcast.setEnvironment(mockEnvironment);
            reliableBroadcast.setMessageTransport(mockMessageTransport);

            // GIVEN
            assertDoesNotThrow(() -> reliableBroadcast.processEvent(broadcastMessage));
        }

        @Test
        @DisplayName("Test if processEvent throw an UnsupportedOperationException if the event does not contains a BroadcastMessage")
        void testProcessEventWithOtherEvent() {
            assertThrows(UnsupportedOperationException.class, () -> reliableBroadcast.processEvent(mockEvent));
        }
    }
}
