package sima.core.protocol;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestProtocolIdentifier {
    
    // Variables.
    
    protected ProtocolIdentifier protocolIdentifier;
    
    @Mock
    private Protocol mockProtocol;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        protocolIdentifier = new ProtocolIdentifier(mockProtocol.getClass(), "TAG");
    }
    
    // Tests.
    
    @Nested
    @Tag("ProtocolIdentifier.constructor")
    @DisplayName("ProtocolIdentifier constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws an NullPointerException with null protocolClass")
        void testConstructorWithNullProtocolClass() {
            assertThrows(NullPointerException.class, () -> new ProtocolIdentifier(null, "TAG"));
        }
        
        @Test
        @DisplayName("Test if constructor throws an NullPointerException with null tag")
        void testConstructorWithNullProtocolTag() {
            Class<? extends Protocol> protocolClass = mockProtocol.getClass();
            assertThrows(NullPointerException.class, () -> new ProtocolIdentifier(protocolClass, null));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with correct parameters")
        void testConstructorWithCorrectParameters() {
            Class<? extends Protocol> protocolClass = mockProtocol.getClass();
            assertDoesNotThrow(() -> new ProtocolIdentifier(protocolClass, "TAG"));
        }
        
    }
    
    @Nested
    @Tag("ProtocolIdentifier.toString")
    @DisplayName("ProtocolIdentifier toString tests")
    class ToStringTest {
        
        @Test
        @DisplayName("Test if toString method returns correct String")
        void testToString() {
            String expectedToString = "ProtocolIdentifier [" +
                    "protocolClass=" + protocolIdentifier.protocolClass() +
                    ", protocolTag=" + protocolIdentifier.protocolTag() + "]";
            String toString = protocolIdentifier.toString();
            assertEquals(expectedToString, toString);
        }
        
    }
    
    @Nested
    @Tag("ProtocolIdentifier.equals")
    @DisplayName("ProtocolIdentifier equals tests")
    class EqualsTest {
        
        @Test
        @DisplayName("Test if equals returns true with same instance")
        void testEqualsWithSameInstance() {
            assertEquals(protocolIdentifier, protocolIdentifier);
        }
        
        @Test
        @DisplayName("Test equals returns false with null")
        void testEqualsWithNull() {
            assertNotEquals(null, protocolIdentifier);
        }
        
        @Test
        @DisplayName("Test if equals returns false with other class")
        void testEqualsWithOtherClass() {
            assertNotEquals(protocolIdentifier, new Object());
        }
        
        @Test
        @DisplayName("Test if equals returns false with same class but not same protocol class")
        void testEqualsWithSameClassButNotSameProtocolClass() {
            var other = new ProtocolIdentifier(Protocol.class, protocolIdentifier.protocolTag());
            assertNotEquals(protocolIdentifier, other);
        }
        
        @Test
        @DisplayName("Test if equals return false with same class and protocol class but not same tag")
        void testEqualsWithSameClassAndProtocolClassButNotSameTage() {
            var other = new ProtocolIdentifier(protocolIdentifier.protocolClass(), "OTHER");
            assertNotEquals(protocolIdentifier, other);
        }
        
        @Test
        @DisplayName("Test if equals  returns true with all sames")
        void testEqualsWithAllSames() {
            var other = new ProtocolIdentifier(protocolIdentifier.protocolClass(), protocolIdentifier.protocolTag());
            assertEquals(protocolIdentifier, other);
        }
        
    }
    
    @Nested
    @Tag("ProtocolIdentifier.hashCode")
    @DisplayName("ProtocolIdentifier hashCode tests")
    class HashCodeTest {
        
        @Test
        @DisplayName("Test if hashCode returns a correct value")
        void testHashCode() {
            int expectedHashCode = Objects.hash(protocolIdentifier.protocolClass(), protocolIdentifier.protocolTag());
            int hashCode = protocolIdentifier.hashCode();
            assertEquals(expectedHashCode, hashCode);
        }
        
    }
    
}
