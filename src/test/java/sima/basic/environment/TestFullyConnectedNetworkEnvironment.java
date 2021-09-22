package sima.basic.environment;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.TestEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestFullyConnectedNetworkEnvironment extends TestEnvironment {
    
    // Variables.
    
    protected FullyConnectedNetworkEnvironment fullyConnectedNetworkEnvironment;
    
    @Mock
    private FullyConnectedNetworkEnvironment mockFullyConnectedNetworkEnvironment;
    
    // Inits.
    
    @BeforeEach
    void setUp() {
        fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST", null);
        environment = fullyConnectedNetworkEnvironment;
        agentIdentifier0 = new AgentIdentifier("AGENT_TEST_0", 0, 0);
        agentIdentifier1 = new AgentIdentifier("AGENT_TEST_1", 1, 1);
    }
    
    // Tests.
    
    @Nested
    @Tag("FullyConnectedNetworkEnvironment.constructor")
    @DisplayName("FullyConnectedNetworkEnvironment constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws a NullPointerException with null name")
        void testConstructorWithNullName() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new FullyConnectedNetworkEnvironment(null, args));
        }
        
        @Test
        @DisplayName("Test if constructor with null args map set the min and max delays to default values")
        void testConstructorWithNullArgsMap() {
            FullyConnectedNetworkEnvironment fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST", null);
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY, fullyConnectedNetworkEnvironment.getMinSendDelay());
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY, fullyConnectedNetworkEnvironment.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with miss args delay set the missing delay to default value")
        void testConstructorWithMissingDelayArgs() {
            FullyConnectedNetworkEnvironment fullyConnectedNetworkEnvironment;
            
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST", createMapArgs(null,
                    String.valueOf(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY)));
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY, fullyConnectedNetworkEnvironment.getMinSendDelay());
            
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST",
                    createMapArgs(String.valueOf(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY), null));
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY, fullyConnectedNetworkEnvironment.getMaxSendDelay());
            
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST", createMapArgs(null, null));
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY, fullyConnectedNetworkEnvironment.getMinSendDelay());
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY, fullyConnectedNetworkEnvironment.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with min or max delay string args has not number format then set the wrong args to default value")
        void testConstructorWithArgsWithNotNumberFormat() {
            FullyConnectedNetworkEnvironment fullyConnectedNetworkEnvironment;
            
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST", createMapArgs("WRONG",
                    String.valueOf(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY)));
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY, fullyConnectedNetworkEnvironment.getMinSendDelay());
            
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST",
                    createMapArgs(String.valueOf(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY), "WRONG"));
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY, fullyConnectedNetworkEnvironment.getMaxSendDelay());
            
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST", createMapArgs("WRONG", "WRONG"));
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY, fullyConnectedNetworkEnvironment.getMinSendDelay());
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY, fullyConnectedNetworkEnvironment.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with negative min and max delay set them to default values")
        void testConstructorWithLessThanOneDelay() {
            var fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST", createMapArgs("0", "0"));
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY, fullyConnectedNetworkEnvironment.getMinSendDelay());
            assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY, fullyConnectedNetworkEnvironment.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor set a min delay always less or equals to max delay in any case")
        void testConstructorAlwaysSetMinDelayLessOrEqualToMaxDelay() {
            FullyConnectedNetworkEnvironment fullyConnectedNetworkEnvironment;
            long minSendDelay;
            long maxSendDelay;
            
            // WRONG min delay and max delay less than default min delay
            maxSendDelay = FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY - 1;
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST",
                    createMapArgs("WRONG", String.valueOf(maxSendDelay)));
            assertTrue(fullyConnectedNetworkEnvironment.getMinSendDelay() <= fullyConnectedNetworkEnvironment.getMaxSendDelay());
            
            // WRONG max delay and min delay greater than default max delay
            minSendDelay = FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY + 1;
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST",
                    createMapArgs(String.valueOf(minSendDelay), "WRONG"));
            assertTrue(fullyConnectedNetworkEnvironment.getMinSendDelay() <= fullyConnectedNetworkEnvironment.getMaxSendDelay());
            
            // Set correct format but min > max
            minSendDelay = FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY;
            maxSendDelay = FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY;
            fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST",
                    createMapArgs(String.valueOf(maxSendDelay), String.valueOf(minSendDelay)));
            assertTrue(fullyConnectedNetworkEnvironment.getMinSendDelay() <= fullyConnectedNetworkEnvironment.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with correct min and max args set correct values")
        void testConstructorWithCorrectMapArgs() {
            long minSendDelay = 5;
            long maxSendDelay = 10;
            var fullyConnectedNetworkEnvironment = new FullyConnectedNetworkEnvironment("TEST",
                    createMapArgs(String.valueOf(minSendDelay), String.valueOf(maxSendDelay)));
            assertEquals(minSendDelay, fullyConnectedNetworkEnvironment.getMinSendDelay());
            assertEquals(maxSendDelay, fullyConnectedNetworkEnvironment.getMaxSendDelay());
        }
        
        private Map<String, String> createMapArgs(String minSendDelay, String maxSendDelay) {
            Map<String, String> args = new HashMap<>();
            if (minSendDelay != null)
                args.put(FullyConnectedNetworkEnvironment.MIN_SEND_DELAY_ARGS, minSendDelay);
            
            if (maxSendDelay != null)
                args.put(FullyConnectedNetworkEnvironment.MAX_SEND_DELAY_ARGS, maxSendDelay);
            
            return args;
        }
        
    }
    
    @Nested
    @Tag("FullyConnectedNetworkEnvironment.toString")
    @DisplayName("FullyConnectedNetworkEnvironment toString tests")
    class ToStringTest extends TestEnvironment.ToStringTest {
        
        @Test
        @DisplayName("Test if the method toString returns a correct String")
        @Override
        public void testToString() {
            String expectedToString =
                    "[Environment - " + "class=" + fullyConnectedNetworkEnvironment.getClass().getName() + ", environmentName=" +
                            fullyConnectedNetworkEnvironment
                                    .getEnvironmentName() +
                            "]";
            String toString = fullyConnectedNetworkEnvironment.toString();
            assertEquals(expectedToString, toString);
        }
        
    }
    
    @Nested
    @Tag("FullyConnectedNetworkEnvironment.equals")
    @DisplayName("FullyConnectedNetworkEnvironment equals tests")
    class EqualsTest {
        
        @Test
        @DisplayName("Test if the method equals returns true with the same instance")
        void testEqualsWithSameInstance() {
            assertEquals(fullyConnectedNetworkEnvironment, fullyConnectedNetworkEnvironment);
        }
        
        @Test
        @DisplayName("Test if the method equals returns false with null")
        void testEqualsWithNull() {
            assertNotEquals(null, fullyConnectedNetworkEnvironment);
        }
        
        @Test
        @DisplayName("Test if the method equals returns false with different class")
        void testEqualsWithDifferentClass() {
            assertNotEquals(fullyConnectedNetworkEnvironment, new Object());
        }
        
        @Test
        @DisplayName("Test if the method equals returns false with same class but not same name")
        void testEqualsWithSameClassWithDifferentName() {
            // GIVEN
            when(mockFullyConnectedNetworkEnvironment.getEnvironmentName()).thenReturn("MOCK_ENVIRONMENT");
            
            // WHEN
            assertNotEquals(fullyConnectedNetworkEnvironment, mockFullyConnectedNetworkEnvironment);
            
            // THEN
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getEnvironmentName();
        }
        
        @Test
        @DisplayName("Test if the method equals returns false with same class and same name but not same minDelay")
        void testEqualsWithSameClassAndSameNameButDifferentMinDelay() {
            // GIVEN
            when(mockFullyConnectedNetworkEnvironment.getEnvironmentName()).thenReturn(fullyConnectedNetworkEnvironment.getEnvironmentName());
            when(mockFullyConnectedNetworkEnvironment.getMinSendDelay()).thenReturn(fullyConnectedNetworkEnvironment.getMinSendDelay() + 10);
            
            // WHEN
            assertNotEquals(fullyConnectedNetworkEnvironment, mockFullyConnectedNetworkEnvironment);
            
            // THEN
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getEnvironmentName();
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getMinSendDelay();
        }
        
        @Test
        @DisplayName("Test if the method equals returns false with same class, name and minDelay but not same maxDelay")
        void testEqualsWithSameClassAndSameNameAndSameMinDelayButNotSameMaxDelay() {
            // GIVEN
            when(mockFullyConnectedNetworkEnvironment.getEnvironmentName()).thenReturn(fullyConnectedNetworkEnvironment.getEnvironmentName());
            when(mockFullyConnectedNetworkEnvironment.getMinSendDelay()).thenReturn(fullyConnectedNetworkEnvironment.getMinSendDelay());
            when(mockFullyConnectedNetworkEnvironment.getMaxSendDelay()).thenReturn(fullyConnectedNetworkEnvironment.getMaxSendDelay() + 10);
            
            // WHEN
            assertNotEquals(fullyConnectedNetworkEnvironment, mockFullyConnectedNetworkEnvironment);
            
            // THEN
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getEnvironmentName();
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getMinSendDelay();
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getMaxSendDelay();
        }
        
        @Test
        @DisplayName("Test if the method equals returns true with all same")
        void testEqualsWithAllSame() {
            // GIVEN
            when(mockFullyConnectedNetworkEnvironment.getEnvironmentName()).thenReturn(fullyConnectedNetworkEnvironment.getEnvironmentName());
            when(mockFullyConnectedNetworkEnvironment.getMinSendDelay()).thenReturn(fullyConnectedNetworkEnvironment.getMinSendDelay());
            when(mockFullyConnectedNetworkEnvironment.getMaxSendDelay()).thenReturn(fullyConnectedNetworkEnvironment.getMaxSendDelay());
            
            // WHEN
            assertEquals(fullyConnectedNetworkEnvironment, mockFullyConnectedNetworkEnvironment);
            
            // THEN
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getEnvironmentName();
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getMinSendDelay();
            verify(mockFullyConnectedNetworkEnvironment, times(1)).getMaxSendDelay();
        }
        
    }
    
    @Nested
    @Tag("FullyConnectedNetworkEnvironment.hashCode")
    @DisplayName("FullyConnectedNetworkEnvironment hashCode tests")
    class HashCodeTest {
        
        @Test
        @DisplayName("Test if hashCode returns a correct values")
        void testHashCode() {
            int expectedHashCode = Objects.hash(Objects.hash(fullyConnectedNetworkEnvironment.getEnvironmentName()),
                    fullyConnectedNetworkEnvironment.getMinSendDelay(),
                    fullyConnectedNetworkEnvironment.getMaxSendDelay());
            int hashCode = fullyConnectedNetworkEnvironment.hashCode();
            assertEquals(expectedHashCode, hashCode);
        }
    }
    
    @Nested
    @Tag("FullyConnectedNetworkEnvironment.acceptAgent")
    @DisplayName("FullyConnectedNetworkEnvironment acceptAgent tests")
    class AcceptAgentTest {
        
        @Test
        @DisplayName("Test if acceptAgent returns false if agentIdentifier is null")
        void testAcceptAgentWithNullAgentIdentifier() {
            boolean accepted = fullyConnectedNetworkEnvironment.acceptAgent(null);
            assertFalse(accepted);
        }
        
        @Test
        @DisplayName("Test if acceptAgent returns true for an agentIdentifier not already accepted and it is considered has evolving in the " +
                "environment")
        void testAcceptAgentWithNotAlreadyAcceptedAgent() {
            boolean accepted = fullyConnectedNetworkEnvironment.acceptAgent(agentIdentifier0);
            assertTrue(accepted);
        }
        
        @Test
        @DisplayName("Test if acceptAgent returns false with an already accepted agent but the agent stay evolving")
        void testAcceptAgentWithAlreadyAcceptedAgent() {
            fullyConnectedNetworkEnvironment.acceptAgent(agentIdentifier0);
            boolean acceptedSecond = fullyConnectedNetworkEnvironment.acceptAgent(agentIdentifier0);
            boolean isEvolving = fullyConnectedNetworkEnvironment.isEvolving(agentIdentifier0);
            assertFalse(acceptedSecond);
            assertTrue(isEvolving);
        }
        
    }
    
    
}
