package sima.basic.broadcast.reliable;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import sima.basic.environment.message.TestMessage;
import sima.core.agent.AgentIdentifier;
import sima.core.protocol.ProtocolIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestReliableBroadcastMessage extends TestMessage {

    // Variables.

    protected ReliableBroadcastMessage reliableBroadcastMessage;

    @Mock
    private AgentIdentifier mockAgentSender;

    @Mock
    private AgentIdentifier mockAgentSenderOther;

    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;

    @Mock
    private ProtocolIdentifier mockProtocolIdentifierOther;

    // Inits.

    @BeforeEach
    @Override
    protected void setUp() {
        reliableBroadcastMessage = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifier);
        message = reliableBroadcastMessage;
    }

    // Tests

    @Nested
    @Tag("ReliableBroadcastMessage.constructor")
    @DisplayName("ReliableBroadcastMessage constructor tests")
    class BroadcastMessageTest {

        @Test
        @DisplayName("Test if constructor throws NullPointerException with null sender")
        void testConstructorWithNullSender() {
            assertThrows(NullPointerException.class, () -> new ReliableBroadcastMessage(0, null, mockContentMessage, mockProtocolIdentifier));
        }

        @Test
        @DisplayName("Test if constructor does not throw exception with null content")
        void testConstructorWithNullContent() {
            assertDoesNotThrow(() -> new ReliableBroadcastMessage(0, mockAgentSender, null, mockProtocolIdentifier));
        }

        @Test
        @DisplayName("Test if constructor throws NullPointerException with null intended protocol")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(NullPointerException.class, () -> new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage,
                                                                                        null));
        }

        @Test
        @DisplayName("Test if constructor does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifier));
        }
    }

    @Nested
    @Tag("ReliableBroadcastMessage.hashCode")
    @DisplayName("ReliableBroadcastMessage hashCode tests")
    class HashCodeTest {

        @Test
        @DisplayName("Test if two equals message has the same hashCode")
        void testHashCodeWithEqualMessages() {
            // WHEN
            var m1 = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            var m2 = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifier);

            // GIVEN
            assertThat(m1.hashCode()).isEqualByComparingTo(m2.hashCode());
        }

    }

    @Nested
    @Tag("ReliableBroadcastMessage.equals")
    @DisplayName("ReliableBroadcastMessage equals tests")
    class EqualsTest {

        @Test
        @DisplayName("Test if equals returns true with two equals message")
        void testEqualsWithTwoEqualsMessage() {
            // WHEN
            var m1 = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            var m2 = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifier);

            // GIVEN
            assertThat(m1).isEqualTo(m2);
        }

        @Test
        @DisplayName("Test if equals returns false with two not equals message")
        void testEqualsWithTwoNotEqualsMessage() {
            // WHEN
            var m1 = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifier);
            var m2 = new ReliableBroadcastMessage(0, mockAgentSenderOther, mockContentMessage, mockProtocolIdentifier);
            var m3 = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessageOther, mockProtocolIdentifier);
            var m4 = new ReliableBroadcastMessage(0, mockAgentSender, mockContentMessage, mockProtocolIdentifierOther);
            var m5 = new ReliableBroadcastMessage(1, mockAgentSender, mockContentMessage, mockProtocolIdentifier);


            // GIVEN
            assertThat(m1).isNotEqualTo(m2).isNotEqualTo(m3).isNotEqualTo(m4).isNotEqualTo(m5);
        }

    }

    @Nested
    @Tag("ReliableBroadcastMessage.getSender")
    @DisplayName("ReliableBroadcastMessage getSender tests")
    class GetSenderTest {

        @Test
        @DisplayName("Test if getSender never returns null")
        void testGetSenderNeverReturnsNull() {
            assertThat(reliableBroadcastMessage.getSender()).isNotNull();
        }

    }
}
