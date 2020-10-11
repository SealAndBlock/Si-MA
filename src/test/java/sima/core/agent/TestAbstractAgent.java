package sima.core.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.agent.exception.AlreadyKilledAgentException;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.behavior.Behavior;
import sima.core.behavior.exception.BehaviorCannotBePlayedByAgentException;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TestAbstractAgent {

    // Variables.

    private static AgentTestImpl AGENT_0;
    private static AgentTestImpl AGENT_1;

    private static EnvironmentTestImpl ENV;

    // Setup.

    @BeforeEach
    void setUp() {
        AGENT_0 = new AgentTestImpl("AGENT_0");
        AGENT_1 = new AgentTestImpl("AGENT_1");

        ENV = new EnvironmentTestImpl("ENV", null);
    }

    // Tests.

    /**
     * Test the constructor of {@link AbstractAgent} and if each instance are always different with different
     * {@link java.util.UUID}.
     */
    @Test
    public void testAgentConstructor() {
        AgentTestImpl a0_1 = new AgentTestImpl("AGENT_0");

        assertNotEquals(AGENT_1.getUUID(), AGENT_0.getUUID());
        assertNotEquals(AGENT_0, a0_1);
    }

    /**
     * Test the method {@link AbstractAgent#start()}. Verifies if all exceptions are thrown in function of the state of
     * the sima.core.agent.
     */
    @Test
    public void testStart() {
        AGENT_0.start();

        assertTrue(AGENT_0.isStarted());
        assertThrows(AlreadyStartedAgentException.class, AGENT_0::start);
        assertTrue(AGENT_0.isStarted());

        AGENT_0.kill();
        assertThrows(KilledAgentException.class, AGENT_0::start);

    }

    @Test
    public void testKill() {
        assertFalse(AGENT_0.isKilled());
        AGENT_0.kill();
        assertTrue(AGENT_0.isKilled());
        assertThrows(AlreadyKilledAgentException.class, AGENT_0::kill);
        assertTrue(AGENT_0.isKilled());
        assertThrows(KilledAgentException.class, AGENT_0::start);
        assertTrue(AGENT_0.isKilled());
    }

    @Test
    public void testJoinEnvironment() {
        assertFalse(AGENT_1.joinEnvironment(ENV));
        assertFalse(ENV.isEvolving(AGENT_1));

        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0));
        assertTrue(AGENT_0.isEvolvingInEnvironment(ENV.getEnvironmentName()));
        assertTrue(AGENT_0.isEvolvingInEnvironment(ENV));
    }

    @Test
    public void testLeaveEnvironment() {
        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0));

        AGENT_0.leaveEnvironment(ENV);
        assertFalse(ENV.isEvolving(AGENT_0));

        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0));

        AGENT_0.leaveEnvironment(ENV.getEnvironmentName());
        assertFalse(ENV.isEvolving(AGENT_0));
    }

    @Test
    public void testAddBehavior() {
        assertFalse(AGENT_1.addBehavior(BehaviorTestImpl.class, null));
        assertTrue(AGENT_0.addBehavior(BehaviorTestImpl.class, null));

        AGENT_0.startPlayingBehavior(BehaviorTestImpl.class);
        assertFalse(AGENT_0.isPlayingBehavior(BehaviorTestImpl.class));

        AGENT_0.start();
        AGENT_0.startPlayingBehavior(BehaviorTestImpl.class);
        assertTrue(AGENT_0.isPlayingBehavior(BehaviorTestImpl.class));

        Map<String, Behavior> behaviorMap = AGENT_0.getMapBehaviors();
        BehaviorTestImpl b = (BehaviorTestImpl) behaviorMap.get(BehaviorTestImpl.class.getName());
        assertNotNull(b);
        assertTrue(b.isPassToStartPlaying);
        assertFalse(b.isPassToStopPlaying);

        AGENT_0.stopPlayingBehavior(BehaviorTestImpl.class);
        assertTrue(b.isPassToStopPlaying);
        assertNotNull(behaviorMap.get(BehaviorTestImpl.class.getName()));
    }

    @Test
    public void testAddProtocol() {
        ProtocolTestImpl p0 = new ProtocolTestImpl("TAG_0", null);
        ProtocolTestImpl p1 = new ProtocolTestImpl("TAG_1", null);

        AGENT_0.addProtocol(p0);
        AGENT_0.addProtocol(p1);

        assertNotNull(AGENT_0.getProtocol(p0.getIdentifier()));
        assertNotNull(AGENT_0.getProtocol(p1.getIdentifier()));

        assertNotEquals(AGENT_0.getProtocol(p0.getIdentifier()), AGENT_0.getProtocol(p1.getIdentifier()));
    }

    // Inner classes.

    private static class EnvironmentTestImpl extends Environment {

        public boolean isPassToSendEventWithoutReceiver = false;
        public boolean isPassToScheduleEventReceptionToOneAgent = false;

        // Constructors.

        public EnvironmentTestImpl(String environmentName, Map<String, String> args) {
            super(environmentName, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {
        }

        /**
         * @param abstractAgent the sima.core.agent to verify
         * @return true if the specified sima.core.agent is to {@link #AGENT_0}, else false.
         */
        @Override
        protected boolean agentCanBeAccepted(AbstractAgent abstractAgent) {
            return abstractAgent.equals(AGENT_0);
        }

        @Override
        protected void agentIsLeaving(AbstractAgent leavingAgent) {

        }

        @Override
        protected void sendEventWithoutReceiver(Event event) {
            this.isPassToSendEventWithoutReceiver = true;
        }

        @Override
        protected boolean eventCanBeSentTo(AbstractAgent receiver, Event event) {
            return true;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AbstractAgent receiver, Event event) {
            this.isPassToScheduleEventReceptionToOneAgent = true;
        }

        @Override
        public void processEvent(Event event) {
        }

        public void reset() {
            this.isPassToSendEventWithoutReceiver = false;
            this.isPassToScheduleEventReceptionToOneAgent = false;
        }
    }

    private static class AgentTestImpl extends AbstractAgent {

        // Constructors.

        public AgentTestImpl(String agentName) {
            super(agentName);
        }

        // Methods.

        @Override
        public void onStart() {

        }

        @Override
        public void onKill() {

        }

        @Override
        protected void treatNoProtocolEvent(Event event) {

        }

        @Override
        protected void treatEventWithNotFindProtocol(Event event) {

        }
    }

    public static class BehaviorTestImpl extends Behavior {

        // Variables.

        public boolean isPassToStartPlaying = false;
        public boolean isPassToStopPlaying = false;

        // Constructors.

        public BehaviorTestImpl(AbstractAgent agent, Map<String, String> args) throws BehaviorCannotBePlayedByAgentException {
            super(agent, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        public boolean canBePlayedBy(AbstractAgent agent) {
            return Objects.equals(agent, AGENT_0);
        }

        @Override
        public void onStartPlaying() {
            this.isPassToStartPlaying = true;
        }

        @Override
        public void onStopPlaying() {
            this.isPassToStopPlaying = true;
        }

        public void reset() {
            this.isPassToStartPlaying = true;
            this.isPassToStopPlaying = true;
        }
    }

    private static class ProtocolTestImpl extends Protocol {

        // Constructors.

        public ProtocolTestImpl(String protocolTag, Map<String, String> args) {
            super(protocolTag, args);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {

        }

        @Override
        protected ProtocolManipulator getDefaultProtocolManipulator() {
            return new ProtocolManipulator(this) {
            };
        }

        @Override
        public void processEvent(Event event) {

        }
    }

}