package sima.standard.environment.message.event;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.environment.event.TestEvent;
import sima.core.protocol.ProtocolIdentifier;
import sima.standard.environment.message.Message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestMessageReceptionEvent extends TestEvent {

    // Variables.

    private MessageReceptionEvent messageReceptionEvent;

    @Mock
    private Message mockMessage;

    @Mock
    private Message mockMessageOther;

    @Mock
    private ProtocolIdentifier mockProtocolIdentifier;

    @Mock
    private ProtocolIdentifier mockProtocolIdentifierOther;

    // Init.

    @BeforeEach
    void setUp() {
        messageReceptionEvent = new MessageReceptionEvent(mockMessage, mockProtocolIdentifier);
        event = messageReceptionEvent;
    }

    // Tests.

    @Nested
    @Tag("MessageReceptionEvent.constructor")
    @DisplayName("MessageReceptionEvent constructor tests")
    class ConstructorTest {

        @Test
        @DisplayName("Test if constructor does not throw exception with null message")
        void testConstructorWithNullMessage() {
            assertDoesNotThrow(() -> new MessageReceptionEvent(null, mockProtocolIdentifier));
        }

        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null intended protocol")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(NullPointerException.class, () -> new MessageReceptionEvent(mockMessage, null));
        }

        @Test
        @DisplayName("Test if constructors does not throw exception with not null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new MessageReceptionEvent(mockMessage, mockProtocolIdentifier));
        }

    }

    @Nested
    @Tag("PhysicalMessageReceptionEvent.hashCode")
    @DisplayName("PhysicalMessageReceptionEvent hashCode tests")
    class HashCodeTest {

        @Test
        @DisplayName("Test if two equals PhysicalMessageReceptionEvents has the same hashCode")
        void testHashCodeWithEqualMessages() {
            // WHEN
            var m1 = new MessageReceptionEvent(mockMessage, mockProtocolIdentifier);
            var m2 = new MessageReceptionEvent(mockMessage, mockProtocolIdentifier);

            // GIVEN
            assertThat(m1.hashCode()).isEqualByComparingTo(m2.hashCode());
        }

    }

    @Nested
    @Tag("PhysicalMessageReceptionEvent.equals")
    @DisplayName("PhysicalMessageReceptionEvent equals tests")
    class EqualsTest {

        @Test
        @DisplayName("Test if equals returns true with two equals PhysicalMessageReceptionEvents")
        void testEqualsWithTwoEqualsMessage() {
            // WHEN
            var m1 = new MessageReceptionEvent(mockMessage, mockProtocolIdentifier);
            var m2 = new MessageReceptionEvent(mockMessage, mockProtocolIdentifier);

            // GIVEN
            assertThat(m1).isEqualTo(m2);
        }

        @Test
        @DisplayName("Test if equals returns false with two not equals PhysicalMessageReceptionEvents")
        void testEqualsWithTwoNotEqualsMessage() {
            // WHEN

            var m1 = new MessageReceptionEvent(mockMessage, mockProtocolIdentifier);
            var m2 = new MessageReceptionEvent(mockMessageOther, mockProtocolIdentifier);
            var m3 = new MessageReceptionEvent(mockMessage, mockProtocolIdentifierOther);

            // GIVEN
            assertThat(m1).isNotEqualTo(m2).isNotEqualTo(m3);
        }

    }

    @Nested
    @Tag("ProtocolEvent.getIntendedProtocol")
    @DisplayName("ProtocolEvent getIntendedProtocol tests")
    class GetIntendedProtocolTest {

        @Test
        @DisplayName("Test if getIntendedProtocol never returns null")
        void testGetIntendedProtocolNeverReturnsNull() {
            assertThat(messageReceptionEvent.getIntendedProtocol()).isNotNull();
        }

    }
}
