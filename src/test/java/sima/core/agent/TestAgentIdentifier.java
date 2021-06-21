package sima.core.agent;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestAgentIdentifier {
    
    // Variables.
    
    protected AgentIdentifier agentIdentifier;
    
    @Mock
    private AgentIdentifier mockAgentIdentifier;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        agentIdentifier = new AgentIdentifier("AGENT", 0, 0);
    }
    
    // Tests.
    
    @Nested
    @Tag("AgentIdentifier.constructor")
    @DisplayName("AgentIdentifier constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructs an AgentIdentifier with null agentName throws a NullPointerException")
        void testConstructorWithNullAgentName() {
            assertThrows(NullPointerException.class, () -> new AgentIdentifier(null, 0, 0));
        }
        
        @Test
        @DisplayName("Test if constructs an AgentIdentifier with empty agentName throws an IllegalArgumentException")
        void testConstructorWithEmptyAgentName() {
            assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier("", 0, 0));
        }
        
        @Test
        @DisplayName(
                "Test if constructs an AgentIdentifier with negative agentSequenceId throws an IllegalArgumentException")
        void testConstructorWithNegativeAgentSequenceId() {
            assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier("AGENT", -1, 0));
        }
        
        @Test
        @DisplayName(
                "Test if constructs an AgentIdentifier with negative agentUniqueId throws an IllegalArgumentException")
        void testConstructorWithNegativeAgentUniqueId() {
            assertThrows(IllegalArgumentException.class, () -> new AgentIdentifier("AGENT", 0, -1));
        }
        
        @Test
        @DisplayName("Test if constructs an agentIdentifier with correct parameters does not throw exception")
        void testConstructorWithCorrectParameters() {
            assertDoesNotThrow(() -> new AgentIdentifier("AGENT", 0, 0));
        }
        
    }
    
    @Nested
    @Tag("AgentIdentifier.toString")
    @DisplayName("AgentIdentifier toString tests")
    class ToStringTest {
        
        @Test
        @DisplayName("Test if the method toString returns the correct format with the correct values")
        void testToString() {
            var expectedToString =
                    "[AgentIdentifier - " + "agentName=" + agentIdentifier.getAgentName() + ", agentSequenceId=" +
                            agentIdentifier.getAgentSequenceId() + ", agentUniqueId=" +
                            agentIdentifier.getAgentUniqueId() +
                            "]";
            var toString = agentIdentifier.toString();
            assertEquals(expectedToString, toString);
        }
        
    }
    
    @Nested
    @Tag("AgentIdentifier.equals")
    @DisplayName("AgentIdentifier equals tests")
    class EqualsTest {
        
        @Test
        @DisplayName("Test if the methods equals returns true with the same instance")
        void testEqualsWithSameInstance() {
            assertEquals(agentIdentifier, agentIdentifier);
        }
        
        @Test
        @DisplayName("Test if the methods equals returns false with null")
        void testEqualsWithNull() {
            assertNotEquals(null, agentIdentifier);
        }
        
        @Test
        @DisplayName("Test if the methods equals returns false with other type than AgentIdentifier")
        void testEqualsWithOtherClassType() {
            assertNotEquals(agentIdentifier, new Object());
        }
        
        @Test
        @DisplayName("Test if the methods equals returns false with an other instance of AgentIdentifier with " +
                             "different values")
        void testEqualsWithOtherInstanceOfAgentIdentifierWithDifferentValues() {
            assertNotEquals(mockAgentIdentifier, agentIdentifier);
        }
        
        @Test
        @DisplayName("Test if the methods equals returns true with an other instance of AgentIdentifier with same " +
                             "values")
        void testEqualsWithOtherInstanceOfAgentIdentifierWithSameValues() {
            var other = new AgentIdentifier(agentIdentifier.getAgentName(), agentIdentifier.getAgentSequenceId(),
                    agentIdentifier.getAgentUniqueId());
            assertEquals(other, agentIdentifier);
        }
    }
    
    @Nested
    @Tag("AgentIdentifier.hashCode")
    @DisplayName("AgentIdentifier hashCode tests")
    class HashCodeTest {
        
        @Test
        @DisplayName("Test if the methods hashCode returns a correct value")
        void testHashCode() {
            int exceptedHashCode = Objects.hash(agentIdentifier.getAgentName(), agentIdentifier.getAgentSequenceId(),
                    agentIdentifier.getAgentUniqueId());
            int hashCode = agentIdentifier.hashCode();
            assertEquals(exceptedHashCode, hashCode);
        }
        
    }
    
}
