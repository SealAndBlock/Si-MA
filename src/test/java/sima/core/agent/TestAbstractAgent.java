package sima.core.agent;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.behavior.Behavior;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @SuppressWarnings("ConstantConditions")
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

    @SuppressWarnings("ConstantConditions")
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
    public void afterStartAndKillAnAgentAllBehaviorsOfTheAgentAreNotPlayed() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());

        // TODO Add protocols.

        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());

        Map<String, Behavior> mapBehaviors = AGENT_0.getMapBehaviors();
        Set<Map.Entry<String, Behavior>> entrySet = mapBehaviors.entrySet();
        for (Map.Entry<String, Behavior> entry : entrySet) {
            assertFalse(entry.getValue().isPlaying());
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

        Map<String, Environment> mapEnvironments = AGENT_0.getMapEnvironments();
        assertTrue(mapEnvironments.isEmpty());

        for (Environment environment : environmentList) {
            assertFalse(environment.isEvolving(AGENT_0.getAgentIdentifier()));

            assertFalse(AGENT_0.isEvolvingInEnvironment(environment));
        }
    }
}
