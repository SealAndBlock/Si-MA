package sima.standard.environment.message;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.protocol.ProtocolIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestStringMessage extends TestMessage {

    // Variables.

    protected StringMessage stringMessage;

    private final String stringContent = "stringContent";

    @Mock
    private ProtocolIdentifier mockIntendedProtocol;

    @Mock
    private ProtocolIdentifier mockOtherIntendedProtocol;

    // Init.

    @BeforeEach
    @Override
    protected void setUp() {
        stringMessage = new StringMessage(stringContent, mockIntendedProtocol);
        message = stringMessage;
    }

    // Tests.

    @Nested
    @Tag("StringMessage.constructor")
    @DisplayName("StringMessage constructor tests")
    class ConstructorTest {

        @Test
        @DisplayName("Test if constructor does not throw Exception with null string content")
        void testConstructorWithNullStringContent() {
            assertDoesNotThrow(() -> new StringMessage(null, mockIntendedProtocol));
        }

        @Test
        @DisplayName("Test if constructor throws IllegalArgumentException if the intended protocol is null")
        void testConstructorWithNullIntendedProtocol() {
            assertThrows(IllegalArgumentException.class, () -> new StringMessage(stringContent, null));
        }

        @Test
        @DisplayName("Test if constructor does not throw Exception with correct arguments")
        void testConstructorWithCorrectArgs() {
            assertDoesNotThrow(() -> new StringMessage(stringContent, mockIntendedProtocol));
        }

    }

    @Nested
    @Tag("StringMessage.hashCode")
    @DisplayName("StringMessage hashCode tests")
    class HashCodeTest {

        @Test
        @DisplayName("Test if two equals StringMessage has the same hashCode")
        void testHashCodeWithEqualMessages() {
            // WHEN
            var m1 = new StringMessage("stringContent", mockIntendedProtocol);
            var m2 = new StringMessage("stringContent", mockIntendedProtocol);

            // GIVEN
            assertThat(m1.hashCode()).isEqualByComparingTo(m2.hashCode());
        }

    }

    @Nested
    @Tag("StringMessage.equals")
    @DisplayName("StringMessage equals tests")
    class EqualsTest {

        @Test
        @DisplayName("Test if equals returns true with two equals message")
        void testEqualsWithTwoEqualsMessage() {
            // WHEN
            var m1 = new StringMessage(stringContent, mockIntendedProtocol);
            var m2 = new StringMessage(stringContent, mockIntendedProtocol);

            // GIVEN
            assertThat(m1).isEqualTo(m2);
        }

        @Test
        @DisplayName("Test if equals returns false with two not equals message")
        void testEqualsWithTwoNotEqualsMessage() {
            // WHEN
            var m1 = new StringMessage(stringContent, mockIntendedProtocol);
            var m2 = new StringMessage("other", mockIntendedProtocol);
            var m3 = new StringMessage(stringContent, mockOtherIntendedProtocol);

            // GIVEN
            assertThat(m1).isNotEqualTo(m2).isNotEqualTo(m3);
        }

    }

    @Nested
    @Tag("StringMessage.getStringContent")
    @DisplayName("StringMessage getStringContent tests")
    class GetStringContentTest {

        @Test
        @DisplayName("Test if getStringContent returns the correct value")
        void testGetStringContent() {
            assertThat(stringContent).isEqualTo(stringMessage.getStringContent());
        }

    }

}
