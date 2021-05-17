package sima.core.protocol;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
abstract class GlobalTestProtocolIdentifier extends SimaTest {
    
    // Static.
    
    protected ProtocolIdentifier PROTOCOL_IDENTIFIER;
    
    /**
     * Must be equal to {@link #PROTOCOL_IDENTIFIER} with the method {@link ProtocolIdentifier#equals(Object)}.
     */
    protected ProtocolIdentifier PROTOCOL_IDENTIFIER_EQUAL;
    
    /**
     * Must not be equal to {@link #PROTOCOL_IDENTIFIER} with the method {@link ProtocolIdentifier#equals(Object)}.
     */
    protected ProtocolIdentifier PROTOCOL_IDENTIFIER_NOT_EQUAL;
    
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(PROTOCOL_IDENTIFIER, "PROTOCOL_IDENTIFIER cannot be null for tests");
        assertNotNull(PROTOCOL_IDENTIFIER_EQUAL, "PROTOCOL_IDENTIFIER cannot be null for tests");
        assertNotNull(PROTOCOL_IDENTIFIER_NOT_EQUAL, "PROTOCOL_IDENTIFIER_NOT_EQUAL cannot be null for tests");
        
        assertNotSame(PROTOCOL_IDENTIFIER, PROTOCOL_IDENTIFIER_EQUAL, "PROTOCOL_IDENTIFIER and " +
                "PROTOCOL_IDENTIFIER_EQUAL must not be the same instance for tests");
        assertNotSame(PROTOCOL_IDENTIFIER_EQUAL, PROTOCOL_IDENTIFIER_NOT_EQUAL, "PROTOCOL_IDENTIFIER_EQUAL and " +
                "PROTOCOL_IDENTIFIER_NOT_EQUAL must not be the same instance for tests");
    }
    
    // Tests.
    
    @Test
    void equalsReturnsFalseForNullObject() {
        assertNotEquals(null,PROTOCOL_IDENTIFIER);
    }
    
    @Test
    void equalsReturnsTrueWithEqualProtocolIdentifier() {
        assertEquals(PROTOCOL_IDENTIFIER, PROTOCOL_IDENTIFIER_EQUAL);
    }
    
    @Test
    void equalsReturnsFalseWithNotEqualProtocolIdentifier() {
        assertNotEquals(PROTOCOL_IDENTIFIER, PROTOCOL_IDENTIFIER_NOT_EQUAL);
    }
    
    @Test
    void equalsReturnsFalseWithNotInstanceOfProtocolIdentifier() {
        assertNotEquals(PROTOCOL_IDENTIFIER, new Object());
    }
    
    @Test
    void hashCodeIsEqualForTwoEqualsProtocolIdentifier() {
        assertEquals(PROTOCOL_IDENTIFIER.hashCode(), PROTOCOL_IDENTIFIER_EQUAL.hashCode());
    }
    
    @Test
    void hashCodeIsDifferentForTwoNotEqualsProtocolIdentifier() {
        assertNotEquals(PROTOCOL_IDENTIFIER.hashCode(), PROTOCOL_IDENTIFIER_NOT_EQUAL.hashCode());
    }
    
    @Test
    void getProtocolClassNeverReturnsNull() {
        assertNotNull(PROTOCOL_IDENTIFIER.getProtocolClass());
    }
    
    @Test
    void getProtocolTagNeverReturnsNull() {
        assertNotNull(PROTOCOL_IDENTIFIER.getProtocolTag());
    }
}
