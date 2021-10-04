package sima.standard.environment;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.TestEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestSimpleEnvironment extends TestEnvironment {
    
    // Variables.
    
    protected SimpleEnvironment simpleEnvironment;
    
    // Inits.
    
    @BeforeEach
    void setUp() {
        simpleEnvironment = new SimpleEnvironment("TEST", null);
        environment = simpleEnvironment;
        agentInitiator = new AgentIdentifier("AGENT_TEST_0", 0, 0);
        agentTarget = new AgentIdentifier("AGENT_TEST_1", 1, 1);
    }
    
    // Tests
    
    @Nested
    @Tag("SimpleEnvironment.constructor")
    @DisplayName("SimpleEnvironment constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws IllegalArgumentException if the environment name is null")
        void testConstructorWithNullEnvironmentName() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new SimpleEnvironment(null, args));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw Exception if args map is null")
        void testConstructorWithNullArgsMap() {
            assertDoesNotThrow(() -> new SimpleEnvironment("TEST", null));
        }
        
    }
    
    @Nested
    @Tag("SimpleEnvironment.equals")
    @DisplayName("SimpleEnvironment equals tests")
    class EqualsTest {
        
        @Test
        @DisplayName("Test equals returns true with two equals Environment")
        void testEqualsWithTwoEqualsEnvironment() {
            String name = "TEST";
            var e1 = new SimpleEnvironment(name, null);
            var e2 = new SimpleEnvironment(name, null);
            var e3 = new SimpleEnvironment(name, new HashMap<>());
            
            assertThat(e1).isEqualTo(e1).isEqualTo(e2).isEqualTo(e3);
        }
        
        @Test
        @DisplayName("Test equals returns false with two not equals Environment")
        void testEqualsWithTwoNotEqualsEnvironment() {
            var e1 = new SimpleEnvironment("dk8SY3", null);
            var e2 = new SimpleEnvironment("64DN", null);
            
            assertThat(e1).isNotEqualTo(e2);
        }
        
    }
    
    @Nested
    @Tag("SimpleEnvironment.hashCode")
    @DisplayName("SimpleEnvironment hashCode tests")
    class HashCodeTest {
        
        @Test
        @DisplayName("Test if hashcode returns the same value for two equals Environment")
        void testHashCodeWithTwoEqualsEnvironment() {
            String name = "TEST";
            var e1 = new SimpleEnvironment(name, null);
            var e2 = new SimpleEnvironment(name, null);
            
            assertThat(e1.hashCode()).isEqualByComparingTo(e2.hashCode());
        }
        
    }
    
    
}
