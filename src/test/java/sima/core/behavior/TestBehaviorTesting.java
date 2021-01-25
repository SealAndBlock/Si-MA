package sima.core.behavior;

import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;
import sima.core.exception.BehaviorCannotBePlayedByAgentException;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestBehaviorTesting extends GlobalTestBehavior {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        BehaviorTesting.NOT_PLAYABLE_AGENT_LIST.clear();

        AgentTesting agentTesting = new AgentTesting("AGENT_BEHAVIOR_TEST_0", 0, 0, null);
        try {
            BEHAVIOR = new BehaviorTesting(agentTesting, null);

            PLAYABLE_AGENT = agentTesting;
            NOT_PLAYABLE_AGENT = null;

            super.verifyAndSetup();
        } catch (BehaviorCannotBePlayedByAgentException e) {
            fail(e);
        }
    }

    // Tests.

    @Test
    public void constructBehaviorTestingWithNullAgentThrowsException() {
        assertThrows(NullPointerException.class, () -> new BehaviorTesting(null, null));
    }

    @Test
    public void constructBehaviorTestingWithNullArgsNotFail() {
        AbstractAgent agent = new AgentTesting("A_0", 0, 0, null);
        assertDoesNotThrow(() -> {
            try {
                new BehaviorTesting(agent, null);
            } catch (BehaviorCannotBePlayedByAgentException e) {
                fail();
            }
        });
    }

    @Test
    public void constructBehaviorTestingWithNotNullArgsNotFail() {
        AbstractAgent agent = new AgentTesting("A_0", 0, 0, null);
        assertDoesNotThrow(() -> {
            try {
                new BehaviorTesting(agent, new HashMap<>());
            } catch (BehaviorCannotBePlayedByAgentException e) {
                fail();
            }
        });
    }

    @Test
    public void constructBehaviorTestingWithNotPlayableAgentThrowsException() {
        AbstractAgent agent = new AgentTesting("A_0", 0, 0, null);
        BehaviorTesting.NOT_PLAYABLE_AGENT_LIST.add(agent);

        assertThrows(BehaviorCannotBePlayedByAgentException.class, () -> new BehaviorTesting(agent, null));
    }

    @Test
    public void startPlayingCallOnStartPlaying() {
        BEHAVIOR.startPlaying();

        verifyPreConditionAndExecuteTest(BEHAVIOR::isPlaying,
                                         () -> assertEquals(1, ((BehaviorTesting) BEHAVIOR).getPassToOnStartPlaying()));
    }

    @Test
    public void stopPlayingCallOnStopPlaying() {
        BEHAVIOR.startPlaying();

        verifyPreConditionAndExecuteTest(BEHAVIOR::isPlaying,
                                         () -> {
                                             BEHAVIOR.stopPlaying();
                                             assertEquals(1, ((BehaviorTesting) BEHAVIOR).getPassToOnStopPlaying());
                                         });
    }
}
