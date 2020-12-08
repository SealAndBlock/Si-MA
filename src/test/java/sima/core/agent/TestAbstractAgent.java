package sima.core.agent;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.behavior.Behavior;
import sima.core.behavior.BehaviorNotPlayableTesting;
import sima.core.behavior.BehaviorTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class TestAbstractAgent extends SimaTest {

    // Static.

    protected static AbstractAgent AGENT_0;

    protected static AbstractAgent AGENT_1;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(AGENT_0, "AGENT_0 cannot be null for tests");
        assertNotNull(AGENT_1, "AGENT_1 cannot be null for tests");

        assertNotSame(AGENT_0, AGENT_1, "AGENT_0 cannot be the same instance of AGENT_1 for tests");

        assertEquals(AGENT_1.getClass(), AGENT_0.getClass(), "AGENT_0 must have the same class of AGENT_1 for tests");
    }

    // Methods.

    private void verifyAgent0IsNotEvolving(EnvironmentTesting env) {
        assertFalse(AGENT_0.isEvolvingInEnvironment(env));
    }

    private void verifyAgent0IsEvolving(EnvironmentTesting env) {
        assertTrue(AGENT_0.isEvolvingInEnvironment(env));
    }

    // Tests.

    @Test
    public void agentIsEqualToItSelf() {
        assertEquals(AGENT_0, AGENT_0);
    }

    @Test
    public void agentIsNotEqualToNull() {
        assertNotEquals(AGENT_0, null);
    }

    @Test
    public void twoDifferentInstanceOfAgentAreNotEqual() {
        assertNotEquals(AGENT_0, AGENT_1);
    }

    @Test
    public void twoDifferentAgentMustHaveDifferentHashCode() {
        assertNotEquals(AGENT_0.hashCode(), AGENT_1.hashCode());
    }

    @Test
    public void theFirstStartOfAnAgentNotThrowsExceptionAndTheAgentIsSpecifiedStarted() {
        try {
            AGENT_0.start();
            assertTrue(AGENT_0.isStarted());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void startAnAgentAlreadyStartedThrowsAnException() {
        try {
            AGENT_0.start();
            assertTrue(AGENT_0.isStarted());
        } catch (Exception e) {
            fail(e);
        }

        assertThrows(AlreadyStartedAgentException.class, () -> AGENT_0.start());
    }

    @Test
    public void startAnAgentKillThrowsAnException() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());

        AGENT_0.kill();

        assertThrows(KilledAgentException.class, () -> AGENT_0.start());
    }

    @Test
    public void notStartedAgentCanBeKillWithoutThrowsAnExceptionAndIsSpecifiedKilled() {
        try {
            AGENT_0.kill();
            assertTrue(AGENT_0.isKilled());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void startedAgentCanBeKillWithoutThrowsAnExceptionAndIsSpecifiedKilled() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());

        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());
    }

    @Test
    public void agentCanJoinAnEnvironmentWhereItIsNotEvolving() {
        EnvironmentTesting env = new EnvironmentTesting(0);

        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);

        assertTrue(AGENT_0.joinEnvironment(env));

        assertTrue(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsEvolving(env);
    }

    @Test
    public void joinNullEnvironmentThrowsException() {
        assertThrows(NullPointerException.class, () -> AGENT_0.joinEnvironment(null));
    }

    @Test
    public void afterJoiningAnEnvironmentTheAgentCanVerifyWithIsEvolvingEnvironmentThatItIsEvolvingInTheEnvironment() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);

        this.verifyAgent0IsEvolving(env);
    }

    @Test
    public void isEvolvingEnvironmentReturnsFalseIfTheEnvironmentOrTheEnvironmentNameIsNull() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);

        assertFalse(AGENT_0.isEvolvingInEnvironment(null));
    }

    @Test
    public void joinEnvironmentReturnsFalseIfTheAgentIsAlreadyEvolvingInTheEnvironmentHoweverTheAgentKeepEvolvingInTheEnvironment() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);

        assertFalse(AGENT_0.joinEnvironment(env));

        assertTrue(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsEvolving(env);
    }

    @Test
    public void agentCannotJoinAnEnvironmentWhichDoesNotAcceptIt() {
        List<AgentIdentifier> notAccepted = new ArrayList<>();
        notAccepted.add(AGENT_0.getAgentIdentifier());

        EnvironmentTesting env = new EnvironmentTesting(0, notAccepted);

        assertFalse(AGENT_0.joinEnvironment(env));

        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);
    }

    @Test
    public void leaveNullEnvironmentThrowsException() {
        assertThrows(NullPointerException.class, () -> AGENT_0.leaveEnvironment(null));
    }

    @Test
    public void agentLeaveAnEnvironmentWhichIsDoesNotJoinDoNothing() {
        EnvironmentTesting env = new EnvironmentTesting(0);

        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);

        try {
            AGENT_0.leaveEnvironment(env);
        } catch (Exception e) {
            fail(e);
        }

        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);
    }

    @Test
    public void afterLeaveAnEnvironmentTheAgentIsNotEvolvingInItAnymore() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);

        // With env instance.

        AGENT_0.leaveEnvironment(env);

        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);

        // With env name.

        AGENT_0.joinEnvironment(env);

        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);
    }

    @Test
    public void agentCanSetAnEnvironmentInItsMapIfItIsEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        env.acceptAgent(AGENT_0.getAgentIdentifier());

        this.verifyAgent0IsNotEvolving(env);

        AGENT_0.setEvolvingInEnvironment(env);

        this.verifyAgent0IsEvolving(env);
    }

    @Test
    public void agentCannotSetAnEnvironmentInItsMapIfItIsNotEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);

        this.verifyAgent0IsNotEvolving(env);

        AGENT_0.setEvolvingInEnvironment(env);

        this.verifyAgent0IsNotEvolving(env);
    }

    @Test
    public void agentCanUnSetAnEnvironmentWhereItIsNotEvolvingAnymore() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);

        env.leave(AGENT_0.getAgentIdentifier());

        this.verifyAgent0IsEvolving(env);

        AGENT_0.unSetEvolvingEnvironment(env);

        this.verifyAgent0IsNotEvolving(env);
    }

    @Test
    public void nothingIsDoneIfAnAgentUnSetAnEnvironmentWhereItIsAlwaysEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);

        this.verifyAgent0IsEvolving(env);

        AGENT_0.unSetEvolvingEnvironment(env);

        this.verifyAgent0IsEvolving(env);
    }

    @Test
    public void nothingIsDoneIfAnAgentUnSetAnEnvironmentThatItDoesNotKnowThatItIsEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        env.acceptAgent(AGENT_0.getAgentIdentifier());

        this.verifyAgent0IsNotEvolving(env);

        AGENT_0.unSetEvolvingEnvironment(env);

        this.verifyAgent0IsNotEvolving(env);
    }

    @Test
    public void canPlayBehaviorReturnsTrueIfTheAgentCanPlayTheBehavior() {
        assertTrue(AGENT_0.canPlayBehavior(BehaviorTesting.class));
    }

    @Test
    public void canPlayBehaviorReturnsFalseIfTheAgentCannotPlayTheBehavior() {
        assertFalse(AGENT_0.canPlayBehavior(BehaviorNotPlayableTesting.class));
    }

    @Test
    public void agentCanAddBehaviorThatItDoesNotAddBefore() {
        assertTrue(AGENT_0.addBehavior(BehaviorTesting.class, null));
        assertNotNull(AGENT_0.getBehavior(Behavior.class));
    }

    @Test
    public void agentCannotAddBehaviorThatItAlreadyAddBefore() {
        assertTrue(AGENT_0.addBehavior(BehaviorTesting.class, null));
        assertFalse(AGENT_0.addBehavior(BehaviorTesting.class, null));
    }

    @Test
    public void agentCannotAddBehaviorThatItCannotPlay() {
        assertFalse(AGENT_0.addBehavior(BehaviorNotPlayableTesting.class, null));
    }

    @Test
    public void listOfBehaviorContainsAllBehaviorAdd() {
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        List<Behavior> behaviors = AGENT_0.getBehaviorList();
        boolean contains = false;
        for (Behavior behavior : behaviors) {
            if (behavior instanceof BehaviorTesting) {
                contains = true;
                break;
            }
        }
        assertTrue(contains);
    }

    @Test
    public void listOfBehaviorIsEmptyIfNoBehaviorsHasBeenAdded() {
        assertTrue(AGENT_0.getBehaviorList().isEmpty());
    }

    @Test
    public void getBehaviorReturnsTheBehaviorInstanceIfTheBehaviorHasBeenAddBefore() {
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        assertNotNull(AGENT_0.getBehavior(BehaviorTesting.class));
    }

    @Test
    public void getBehaviorReturnsNullIfTheBehaviorIsNotAdd() {
        assertNull(AGENT_0.getBehavior(BehaviorTesting.class));
    }

    @Test
    public void notStartedAgentCannotStartToPlayBehavior() {
        AGENT_0.addBehavior(BehaviorTesting.class, null);

        Behavior behavior = AGENT_0.getBehavior(BehaviorTesting.class);

        assertFalse(behavior.isPlaying());

        AGENT_0.startPlayingBehavior(BehaviorTesting.class);

        assertFalse(behavior.isPlaying());
    }

    @Test
    public void startedAgentCanStartToPlayBehavior() {
        AGENT_0.addBehavior(BehaviorTesting.class, null);

        BehaviorTesting behavior = (BehaviorTesting) AGENT_0.getBehavior(BehaviorTesting.class);

        assertFalse(behavior.isPlaying());

        AGENT_0.start();

        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        assertEquals(1, behavior.getPassToOnStartPlaying());

        assertTrue(behavior.isPlaying());
    }

    @Test
    public void tryToStartPlayingANullBehaviorThrowsException() {
        assertThrows(NullPointerException.class, () -> AGENT_0.startPlayingBehavior(null));
    }

    @Test
    public void nothingIsDoneIfAgentTryToStartPlayingNotAddedBehavior() {
        try {
            AGENT_0.start();
            AGENT_0.stopPlayingBehavior(BehaviorTesting.class);

            assertNull(AGENT_0.getBehavior(BehaviorTesting.class));
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void isPlayingBehaviorReturnsTrueIfTheAgentIsPlayingTheBehavior() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        assertTrue(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }

    @Test
    public void isPlayingBehaviorReturnsFalseIfTheAgentIsNotPlayingTheBehavior() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        assertFalse(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }

    @Test
    public void afterStopPlayingABehaviorIsPlayingBehaviorReturnsFalseForThisBehavior() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        AGENT_0.stopPlayingBehavior(BehaviorTesting.class);
        assertFalse(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }

    @Test
    public void agentCanStopABehaviorWhichIsPlaying() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);

        BehaviorTesting behavior = (BehaviorTesting) AGENT_0.getBehavior(BehaviorTesting.class);

        AGENT_0.startPlayingBehavior(BehaviorTesting.class);

        AGENT_0.stopPlayingBehavior(BehaviorTesting.class);

        assertEquals(1, behavior.getPassToInStopPlaying());

        assertFalse(behavior.isPlaying());
    }

    @Test
    public void agentNotPlayingABehaviorWhichItDoesNotAddAndWhichIsTryToStop() {
        AGENT_0.stopPlayingBehavior(BehaviorTesting.class);
        assertFalse(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }

    @Test
    public void tryToStopANullBehaviorThrowsException() {
        assertThrows(NullPointerException.class, () -> AGENT_0.stopPlayingBehavior(null));
    }

    @Test
    public void afterStartAndKillAnAgentAllBehaviorsOfTheAgentAreNotPlayed() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());

        AGENT_0.addBehavior(BehaviorTesting.class, null);
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        assertTrue(AGENT_0.isPlayingBehavior(BehaviorTesting.class));

        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());

        List<Behavior> behaviors = AGENT_0.getBehaviorList();
        for (Behavior behavior : behaviors) {
            assertFalse(behavior.isPlaying());
        }
    }

    @Test
    public void afterStartAndKillAnAgentTheAgentHasLeaveAllEnvironments() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());

        EnvironmentTesting env0 = new EnvironmentTesting(0);
        EnvironmentTesting env1 = new EnvironmentTesting(1);

        AGENT_0.joinEnvironment(env0);
        AGENT_0.joinEnvironment(env1);

        List<Environment> environmentList = new ArrayList<>();
        environmentList.add(env0);
        environmentList.add(env1);

        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());

        List<Environment> environments = AGENT_0.getEnvironmentList();
        for (Environment environment : environments) {
            assertFalse(environment.isEvolving(AGENT_0.getAgentIdentifier()));

            assertFalse(AGENT_0.isEvolvingInEnvironment(environment));
        }
    }
}
