package sima.core.behavior;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
abstract class GlobalTestBehavior extends SimaTest {
    
    // Static.
    
    protected Behavior BEHAVIOR;
    
    /**
     * A playable agent for {@link #BEHAVIOR}. Cannot be null.
     */
    protected AbstractAgent PLAYABLE_AGENT;
    
    /**
     * A not playable agent for {@link #BEHAVIOR}. Can be null.
     */
    protected AbstractAgent NOT_PLAYABLE_AGENT;
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(BEHAVIOR, "BEHAVIOR cannot be null for tests");
        assertNotNull(PLAYABLE_AGENT, "PLAYABLE_AGENT cannot be null for tests");
        
        assertNotEquals(PLAYABLE_AGENT, NOT_PLAYABLE_AGENT,
                "PLAYABLE_AGENT and NOT_PLAYABLE_AGENT cannot be equal for tests");
    }
    
    // Tests.
    
    @Test
    void canBePlayedByReturnsTrueForPlayableAgent() {
        assertTrue(BEHAVIOR.canBePlayedBy(PLAYABLE_AGENT));
    }
    
    @Test
    void canBePlayedByReturnsFalseForNotPlayableAgent() {
        assertFalse(BEHAVIOR.canBePlayedBy(NOT_PLAYABLE_AGENT));
    }
    
    @Test
    void canBePlayedByReturnsFalseForNullAgent() {
        assertFalse(BEHAVIOR.canBePlayedBy(null));
    }
    
    @Test
    void isPlayingReturnsFalseIfBehaviorIsNotPlaying() {
        assertFalse(BEHAVIOR.isPlaying());
    }
    
    @Test
    void IsPlayingReturnsTrueAfterStartPlaying() {
        BEHAVIOR.startPlaying();
        assertTrue(BEHAVIOR.isPlaying());
    }
    
    @Test
    void IsPlayingReturnsFalseAfterStopPlaying() {
        BEHAVIOR.startPlaying();
        BEHAVIOR.stopPlaying();
        assertFalse(BEHAVIOR.isPlaying());
    }
    
    @Test
    void getAgentNeverReturnsNull() {
        assertNotNull(BEHAVIOR.getAgent());
    }
    
}
