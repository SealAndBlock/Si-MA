package sima.core.behavior;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.standard.behavior.PlayableByAllAgentsBehavior;
import sima.core.agent.SimaAgent;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public abstract class TestBehavior {
    
    // Variables.
    
    protected Behavior behavior;
    
    @Mock
    protected SimaAgent mockAgent;
    
    // Tests.
    
    @Nested
    @Tag("Behavior.toString")
    @DisplayName("Behavior toString tests")
    class ToStringTest {
        
        @Test
        @DisplayName("Test if the method toString returns excepted string")
        void testToString() {
            String expectedToString =
                    "[Behavior - " + "class=" + PlayableByAllAgentsBehavior.class.getName() + ", agent=" + behavior.getAgent() + "]";
            String toString = behavior.toString();
            assertEquals(expectedToString, toString);
        }
        
    }
    
    @Nested
    @Tag("Behavior.startPlaying")
    @DisplayName("Behavior startPlaying tests")
    class StartPlayingTest {
        
        @Test
        @DisplayName("Test if startPlaying set to true isPlaying variable")
        void testStartPlaying() {
            boolean beforeStart = behavior.isPlaying();
            behavior.startPlaying();
            boolean afterStart = behavior.isPlaying();
            assertFalse(beforeStart);
            assertTrue(afterStart);
        }
        
    }
    
    @Nested
    @Tag("Behavior.stopPlaying")
    @DisplayName("Behavior stopPlaying tests")
    class StopPlayingTest {
        
        @Test
        @DisplayName("Test if stopPlaying set to false isPlaying variable after that the behavior has been started")
        void testStopPlaying() {
            behavior.startPlaying();
            behavior.stopPlaying();
            boolean isPlaying = behavior.isPlaying();
            assertFalse(isPlaying);
        }
        
    }
}
