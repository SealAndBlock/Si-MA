package sima.core.agent;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.behavior.Behavior;
import sima.core.behavior.BehaviorNotPlayableTesting;
import sima.core.behavior.BehaviorTesting;
import sima.core.environment.Environment;
import sima.core.environment.EnvironmentTesting;
import sima.core.environment.event.EventTesting;
import sima.core.exception.AgentNotStartedException;
import sima.core.exception.AlreadyKilledAgentException;
import sima.core.exception.AlreadyStartedAgentException;
import sima.core.exception.KilledAgentException;
import sima.core.protocol.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public abstract class GlobalTestAbstractAgent extends SimaTest {
    
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
    void agentIsEqualToItSelf() {
        assertEquals(AGENT_0, AGENT_0);
    }
    
    @Test
    void agentIsNotEqualToNull() {
        assertNotEquals(null, AGENT_0);
    }
    
    @Test
    void twoDifferentInstanceOfAgentAreNotEqual() {
        assertNotEquals(AGENT_0, AGENT_1);
    }
    
    @Test
    void twoDifferentAgentMustHaveDifferentHashCode() {
        assertNotEquals(AGENT_0.hashCode(), AGENT_1.hashCode());
    }
    
    @Test
    void theFirstStartOfAnAgentNotThrowsExceptionAndTheAgentIsSpecifiedStarted() {
        try {
            AGENT_0.start();
            assertTrue(AGENT_0.isStarted());
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void startAnAgentAlreadyStartedThrowsAnException() {
        try {
            AGENT_0.start();
            assertTrue(AGENT_0.isStarted());
        } catch (Exception e) {
            fail(e);
        }
        
        assertThrows(AlreadyStartedAgentException.class, () -> AGENT_0.start());
    }
    
    @Test
    void startAnAgentKillThrowsAnException() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());
        
        AGENT_0.kill();
        
        assertThrows(KilledAgentException.class, () -> AGENT_0.start());
    }
    
    @Test
    void killAnAgentAlreadyKillThrowsException() {
        AGENT_0.kill();
        assertThrows(AlreadyKilledAgentException.class, () -> AGENT_0.kill());
    }
    
    @Test
    void notStartedAgentCanBeKillWithoutThrowsAnExceptionAndIsSpecifiedKilled() {
        try {
            AGENT_0.kill();
            assertTrue(AGENT_0.isKilled());
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void startedAgentCanBeKillWithoutThrowsAnExceptionAndIsSpecifiedKilled() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());
        
        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());
    }
    
    @Test
    void agentCanJoinAnEnvironmentWhereItIsNotEvolving() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        
        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);
        
        assertTrue(AGENT_0.joinEnvironment(env));
        
        assertTrue(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsEvolving(env);
    }
    
    @Test
    void afterJoinAnEnvironmentGetEnvironmentListContainsTheJoinedEnvironment() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        
        AGENT_0.joinEnvironment(env);
        
        assertTrue(AGENT_0.getEnvironmentList().contains(env));
    }
    
    @Test
    void getEnvironmentListIsEmptyIfTheAgentHasNoJoinAnyEnvironment() {
        assertTrue(AGENT_0.getEnvironmentList().isEmpty());
    }
    
    @Test
    void afterJoiningAnEnvironmentTheAgentCanVerifyWithIsEvolvingEnvironmentThatItIsEvolvingInTheEnvironment() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);
        
        this.verifyAgent0IsEvolving(env);
    }
    
    @Test
    void isEvolvingEnvironmentReturnsFalseIfTheEnvironmentOrTheEnvironmentNameIsNull() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);
        
        assertFalse(AGENT_0.isEvolvingInEnvironment(null));
    }
    
    @Test
    void joinEnvironmentReturnsFalseIfTheAgentIsAlreadyEvolvingInTheEnvironmentHoweverTheAgentKeepEvolvingInTheEnvironment() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);
        
        assertFalse(AGENT_0.joinEnvironment(env));
        
        assertTrue(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsEvolving(env);
    }
    
    @Test
    void agentCannotJoinAnEnvironmentWhichDoesNotAcceptIt() {
        List<AgentIdentifier> notAccepted = new ArrayList<>();
        notAccepted.add(AGENT_0.getAgentIdentifier());
        
        EnvironmentTesting env = new EnvironmentTesting(0, notAccepted);
        
        assertFalse(AGENT_0.joinEnvironment(env));
        
        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);
    }
    
    @Test
    void leaveNullEnvironmentThrowsException() {
        assertThrows(NullPointerException.class, () -> AGENT_0.leaveEnvironment(null));
    }
    
    @Test
    void agentLeaveAnEnvironmentWhichIsDoesNotJoinDoNothing() {
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
    void afterLeaveAnEnvironmentTheAgentIsNotEvolvingInItAnymore() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);
        
        AGENT_0.leaveEnvironment(env);
        
        assertFalse(env.isEvolving(AGENT_0.getAgentIdentifier()));
        this.verifyAgent0IsNotEvolving(env);
    }
    
    @Test
    void agentCanSetAnEnvironmentInItsMapIfItIsEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        env.acceptAgent(AGENT_0.getAgentIdentifier());
        
        assertFalse(AGENT_0.getEnvironmentList().contains(env));
        AGENT_0.setEvolvingInEnvironment(env);
        assertTrue(AGENT_0.getEnvironmentList().contains(env));
    }
    
    @Test
    void agentCannotSetAnEnvironmentInItsMapIfItIsNotEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        
        this.verifyAgent0IsNotEvolving(env);
        
        AGENT_0.setEvolvingInEnvironment(env);
        
        this.verifyAgent0IsNotEvolving(env);
    }
    
    @Test
    void agentCanUnSetAnEnvironmentWhereItIsNotEvolvingAnymore() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);
        
        env.leave(AGENT_0.getAgentIdentifier());
        
        this.verifyAgent0IsNotEvolving(env);
        
        assertTrue(AGENT_0.getEnvironmentList().contains(env));
        AGENT_0.unSetEvolvingEnvironment(env);
        assertFalse(AGENT_0.getEnvironmentList().contains(env));
        
        this.verifyAgent0IsNotEvolving(env);
    }
    
    @Test
    void nothingIsDoneIfAnAgentUnSetAnEnvironmentWhereItIsAlwaysEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        AGENT_0.joinEnvironment(env);
        
        this.verifyAgent0IsEvolving(env);
        
        AGENT_0.unSetEvolvingEnvironment(env);
        
        this.verifyAgent0IsEvolving(env);
    }
    
    @Test
    void nothingIsDoneIfAnAgentUnSetAnEnvironmentThatItDoesNotKnowThatItIsEvolvingIn() {
        EnvironmentTesting env = new EnvironmentTesting(0);
        env.acceptAgent(AGENT_0.getAgentIdentifier());
        
        this.verifyAgent0IsEvolving(env);
        
        AGENT_0.unSetEvolvingEnvironment(env);
        
        this.verifyAgent0IsEvolving(env);
    }
    
    @Test
    void canPlayBehaviorReturnsTrueIfTheAgentCanPlayTheBehavior() {
        assertTrue(AGENT_0.canPlayBehavior(BehaviorTesting.class));
    }
    
    @Test
    void canPlayBehaviorReturnsFalseIfTheAgentCannotPlayTheBehavior() {
        assertFalse(AGENT_0.canPlayBehavior(BehaviorNotPlayableTesting.class));
    }
    
    @Test
    void agentCanAddBehaviorThatItDoesNotAddBefore() {
        assertTrue(AGENT_0.addBehavior(BehaviorTesting.class, null));
        assertNotNull(AGENT_0.getBehavior(BehaviorTesting.class));
    }
    
    @Test
    void agentCannotAddBehaviorThatItAlreadyAddBefore() {
        assertTrue(AGENT_0.addBehavior(BehaviorTesting.class, null));
        assertFalse(AGENT_0.addBehavior(BehaviorTesting.class, null));
    }
    
    @Test
    void agentCannotAddBehaviorThatItCannotPlay() {
        assertFalse(AGENT_0.addBehavior(BehaviorNotPlayableTesting.class, null));
    }
    
    @Test
    void listOfBehaviorContainsAllBehaviorAdd() {
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
    void listOfBehaviorIsEmptyIfNoBehaviorsHasBeenAdded() {
        assertTrue(AGENT_0.getBehaviorList().isEmpty());
    }
    
    @Test
    void getBehaviorReturnsTheBehaviorInstanceIfTheBehaviorHasBeenAddBefore() {
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        assertNotNull(AGENT_0.getBehavior(BehaviorTesting.class));
    }
    
    @Test
    void getBehaviorReturnsNullIfTheBehaviorIsNotAdd() {
        assertNull(AGENT_0.getBehavior(BehaviorTesting.class));
    }
    
    @Test
    void notStartedAgentCannotStartToPlayBehavior() {
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        
        Behavior behavior = AGENT_0.getBehavior(BehaviorTesting.class);
        
        assertFalse(behavior.isPlaying());
        
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        
        assertFalse(behavior.isPlaying());
    }
    
    @Test
    void startedAgentCanStartToPlayBehavior() {
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        
        BehaviorTesting behavior = (BehaviorTesting) AGENT_0.getBehavior(BehaviorTesting.class);
        
        assertFalse(behavior.isPlaying());
        
        AGENT_0.start();
        
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        assertEquals(1, behavior.getPassToOnStartPlaying());
        
        assertTrue(behavior.isPlaying());
    }
    
    @Test
    void tryToStartPlayingANullBehaviorThrowsException() {
        assertThrows(NullPointerException.class, () -> AGENT_0.startPlayingBehavior(null));
    }
    
    @Test
    void nothingIsDoneIfAgentTryToStartPlayingNotAddedBehavior() {
        try {
            AGENT_0.start();
            AGENT_0.stopPlayingBehavior(BehaviorTesting.class);
            
            assertNull(AGENT_0.getBehavior(BehaviorTesting.class));
        } catch (Exception e) {
            fail(e);
        }
    }
    
    @Test
    void isPlayingBehaviorReturnsTrueIfTheAgentIsPlayingTheBehavior() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        assertTrue(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }
    
    @Test
    void isPlayingBehaviorReturnsFalseIfTheAgentIsNotPlayingTheBehavior() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        assertFalse(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }
    
    @Test
    void afterStopPlayingABehaviorIsPlayingBehaviorReturnsFalseForThisBehavior() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        AGENT_0.stopPlayingBehavior(BehaviorTesting.class);
        assertFalse(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }
    
    @Test
    void agentCanStopABehaviorWhichIsPlaying() {
        AGENT_0.start();
        AGENT_0.addBehavior(BehaviorTesting.class, null);
        
        BehaviorTesting behavior = (BehaviorTesting) AGENT_0.getBehavior(BehaviorTesting.class);
        
        AGENT_0.startPlayingBehavior(BehaviorTesting.class);
        
        AGENT_0.stopPlayingBehavior(BehaviorTesting.class);
        
        assertEquals(1, behavior.getPassToOnStopPlaying());
        
        assertFalse(behavior.isPlaying());
    }
    
    @Test
    void agentNotPlayingABehaviorWhichItDoesNotAddAndWhichIsTryToStop() {
        AGENT_0.stopPlayingBehavior(BehaviorTesting.class);
        assertFalse(AGENT_0.isPlayingBehavior(BehaviorTesting.class));
    }
    
    @Test
    void afterStartAndKillAnAgentAllBehaviorsOfTheAgentAreNotPlayed() {
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
    void afterStartAndKillAnAgentTheAgentHasLeaveAllEnvironments() {
        AGENT_0.start();
        assertTrue(AGENT_0.isStarted());
        
        EnvironmentTesting env0 = new EnvironmentTesting(0);
        EnvironmentTesting env1 = new EnvironmentTesting(1);
        
        AGENT_0.joinEnvironment(env0);
        AGENT_0.joinEnvironment(env1);
        
        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());
        
        List<Environment> environments = AGENT_0.getEnvironmentList();
        for (Environment environment : environments) {
            assertFalse(environment.isEvolving(AGENT_0.getAgentIdentifier()));
            
            assertFalse(AGENT_0.isEvolvingInEnvironment(environment));
        }
    }
    
    @Test
    void agentCanAddProtocolThatItHasNotAddBefore() {
        assertTrue(AGENT_0.addProtocol(ProtocolTesting.class, "P_0", null));
    }
    
    @Test
    void getProtocolReturnsNotNullProtocolIfTheProtocolHasBeenAdded() {
        String protocolTag = "P_0";
        AGENT_0.addProtocol(ProtocolTesting.class, protocolTag, null);
        
        assertNotNull(AGENT_0.getProtocol(new ProtocolIdentifier(ProtocolTesting.class, protocolTag)));
    }
    
    @Test
    void getProtocolReturnsNullIfThereIsNoProtocolAssociatedToTheProtocolIdentifier() {
        String protocolTag = "P_0";
        assertNull(AGENT_0.getProtocol(new ProtocolIdentifier(ProtocolTesting.class, protocolTag)));
    }
    
    @Test
    void agentCanAddTwoProtocolsWithSameClassButNotSameTag() {
        String pTag0 = "P_0";
        String pTag1 = "P_1";
        
        assertTrue(AGENT_0.addProtocol(ProtocolTesting.class, pTag0, null));
        assertTrue(AGENT_0.addProtocol(ProtocolTesting.class, pTag1, null));
        
        Protocol p0 = AGENT_0.getProtocol(new ProtocolIdentifier(ProtocolTesting.class, pTag0));
        Protocol p1 = AGENT_0.getProtocol(new ProtocolIdentifier(ProtocolTesting.class, pTag1));
        
        assertNotNull(p0);
        assertNotNull(p1);
        
        assertNotSame(p0, p1);
    }
    
    @Test
    void agentCannotAddProtocolWhichAlreadyAddWithTheSameClassAndTheSameProtocol() {
        String pTag0 = "P_0";
        assertTrue(AGENT_0.addProtocol(ProtocolTesting.class, pTag0, null));
        assertFalse(AGENT_0.addProtocol(ProtocolTesting.class, pTag0, null));
    }
    
    @Test
    void agentCannotAddProtocolWhichDoesNotRespectTheSpecification() {
        assertFalse(AGENT_0.addProtocol(ProtocolWithoutDefaultProtocolManipulatorTesting.class, "P_WRONG_0", null));
        assertFalse(AGENT_0.addProtocol(ProtocolWithWrongConstructorTesting.class, "P_WRONG_1", null));
    }
    
    @Test
    void getProtocolListIsEmptyIfTheAgentHasNotAddProtocol() {
        assertTrue(AGENT_0.getProtocolList().isEmpty());
    }
    
    @Test
    void getProtocolListContainsAllProtocolsAdded() {
        String pTag0 = "P_0";
        String pTag1 = "P_1";
        
        assertTrue(AGENT_0.addProtocol(ProtocolTesting.class, pTag0, null));
        assertTrue(AGENT_0.addProtocol(ProtocolTesting.class, pTag1, null));
        
        Protocol p0 = AGENT_0.getProtocol(new ProtocolIdentifier(ProtocolTesting.class, pTag0));
        Protocol p1 = AGENT_0.getProtocol(new ProtocolIdentifier(ProtocolTesting.class, pTag1));
        
        assertTrue(AGENT_0.getProtocolList().contains(p0));
        assertTrue(AGENT_0.getProtocolList().contains(p1));
    }
    
    @Test
    void processEventThrowsExceptionIfTheAgentIsNotStarted() {
        EventTesting eventTesting = new EventTesting(AGENT_0.getAgentIdentifier(), AGENT_1.getAgentIdentifier(), null);
        assertThrows(AgentNotStartedException.class, () -> AGENT_0.processEvent(eventTesting));
    }
    
    @Test
    void processEventCallProcessEventMethodOfTheProtocolTargeted() {
        String p0 = "P_0";
        AGENT_0.addProtocol(ProtocolTesting.class, p0, null);
        ProtocolIdentifier p0Identifier = new ProtocolIdentifier(ProtocolTesting.class, p0);
        ProtocolTesting p = (ProtocolTesting) AGENT_0.getProtocol(p0Identifier);
        
        AGENT_0.start();
        
        EventTesting e = new EventTesting(AGENT_0.getAgentIdentifier(), AGENT_0.getAgentIdentifier(), p0Identifier);
        AGENT_0.processEvent(e);
        
        assertEquals(1, p.getPassToProcessEvent());
    }
    
    @Test
    void processEventWithEventWithNotAddedProtocolThrowsException() {
        AGENT_0.start();
        
        String p0 = "P_0";
        ProtocolIdentifier p0Identifier = new ProtocolIdentifier(ProtocolTesting.class, p0);
        EventTesting e = new EventTesting(AGENT_0.getAgentIdentifier(), AGENT_0.getAgentIdentifier(), p0Identifier);
        
        assertThrows(IllegalArgumentException.class, () -> AGENT_0.processEvent(e));
    }
    
    @Test
    void getInfoNeverReturnsNull() {
        assertNotNull(AGENT_0.getInfo());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    void allAgentInfoFieldsCorrespondToTheAgentFields() {
        AgentInfo a0Info = AGENT_0.getInfo();
        
        assertEquals(a0Info.getAgentIdentifier(), AGENT_0.getAgentIdentifier());
        
        for (String behaviorClassName : a0Info.getBehaviors()) {
            try {
                assertNotNull(AGENT_0.getBehavior((Class<? extends Behavior>) Class.forName(behaviorClassName)));
            } catch (ClassNotFoundException e) {
                fail(e);
            }
        }
        
        assertEquals(a0Info.getEnvironments().size(), AGENT_0.getEnvironmentList().size());
        
        for (ProtocolIdentifier protocolIdentifier : a0Info.getProtocols()) {
            assertNotNull(AGENT_0.getProtocol(protocolIdentifier));
        }
    }
    
    @Test
    void getAgentNameNeverReturnsNull() {
        assertNotNull(AGENT_0.getAgentName());
    }
    
    @Test
    void getUniqueIdNeverReturnsNegativeNumber() {
        assertTrue(AGENT_0.getUniqueId() >= 0);
    }
    
    @Test
    void getSequenceIdNeverReturnsNegativeNumber() {
        assertTrue(AGENT_0.getSequenceId() >= 0);
    }
}
