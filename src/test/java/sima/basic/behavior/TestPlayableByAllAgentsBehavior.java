package sima.basic.behavior;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.behavior.TestBehavior;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestPlayableByAllAgentsBehavior extends TestBehavior {
    
    // Init.
    
    @BeforeEach
    void setUp() {
        try {
            behavior = new PlayableByAllAgentsBehavior(mockAgent, null);
        } catch (BehaviorCannotBePlayedByAgentException e) {
            fail(e);
        }
    }
    
    // Tests.
    
    @Nested
    @Tag("PlayableByAllAgentsBehavior.constructor")
    @DisplayName("PlayableByAllAgentsBehavior constructors tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if the constructor throws a NullPointerException if the agent is null")
        void testConstructorWithNullAgent() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new PlayableByAllAgentsBehavior(null, args));
        }
        
        @Test
        @DisplayName("Test if the constructor does not throw an exception with null map args")
        void testConstructorWithNullArgsMap() {
            assertDoesNotThrow(() -> new PlayableByAllAgentsBehavior(mockAgent, null));
        }
    }
    
}
