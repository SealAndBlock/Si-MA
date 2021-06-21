package sima.basic.environment.event;

import org.junit.jupiter.api.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransportableString {
    
    // Variables.
    
    protected TransportableString transportableString;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        transportableString = new TransportableString("TEST");
    }
    
    // Tests.
    
    @Nested
    @Tag("TransportableString.constructor")
    @DisplayName("TransportableString constructor test")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor with null content does not throw exception")
        void testConstructorWithNullContent() {
            assertDoesNotThrow(() -> new TransportableString(null));
        }
        
        @Test
        @DisplayName("Test if constructor with not null content does not throw exception")
        void testConstructorWithNotNullContent() {
            assertDoesNotThrow(() -> new TransportableString("TEST_CONTENT"));
        }
        
    }
    
    @Nested
    @Tag("TTransportableString.equals")
    @DisplayName("TransportableString equals test")
    class EqualsTest {
        
        @Test
        @DisplayName("Test if equals returns true with same instance")
        void testEqualsWithSameInstance() {
            assertEquals(transportableString, transportableString);
        }
        
        @Test
        @DisplayName("Test if equals returns false with null")
        void testEqualsWithNull() {
            assertNotEquals(null, transportableString);
        }
        
        @Test
        @DisplayName("Test if equals returns false with different class")
        void testEqualsWithDifferentClass() {
            assertNotEquals(transportableString, new Object());
        }
        
        @Test
        @DisplayName("Test if equals returns false with same class but different content")
        void testEqualsWithDifferentContent() {
            assertNotEquals(new TransportableString("OTHER"), transportableString);
        }
        
        @Test
        @DisplayName("Test if equals returns true with same class and same content")
        void testEqualsWithSameContent() {
            assertEquals(new TransportableString(transportableString.getContent()), transportableString);
        }
    }
    
    @Nested
    @Tag("TransportableString.hashCode")
    @DisplayName("TransportableString hashCode tests")
    class HashCodeTest {
        
        @Test
        @DisplayName("Test if hashCode returns correct value")
        void testHashCode() {
            int expectedHashCode = Objects.hash(transportableString.getContent());
            int hashCode = transportableString.hashCode();
            assertEquals(expectedHashCode, hashCode);
        }
        
    }
    
    @Nested
    @Tag("TransportableString.duplicate")
    @DisplayName("TransportableString duplicate tests")
    class DuplicateTest {
        
        @Test
        @DisplayName("Test if duplicate returns a new instance of TransportableString equals to the base TransportableString")
        void testDuplicate() {
            var duplicate = transportableString.duplicate();
            assertNotSame(transportableString, duplicate);
            assertEquals(transportableString, duplicate);
        }
        
    }
    
}
