package sima.core.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.agent.exception.AgentNotStartedException;
import sima.core.agent.exception.AlreadyKilledAgentException;
import sima.core.agent.exception.AlreadyStartedAgentException;
import sima.core.agent.exception.KilledAgentException;
import sima.core.behavior.Behavior;
import sima.core.behavior.exception.BehaviorCannotBePlayedByAgentException;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.ProtocolManipulator;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class WrongTestAbstractAgent {

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
    public void testNotProcessEventWhenNotStarted() {
        assertThrows(AgentNotStartedException.class, () -> AGENT_0.processEvent(null)); // No need to add real event
        AGENT_0.start();
        assertThrows(NullPointerException.class, () -> AGENT_0.processEvent(null));

        try {
            AGENT_0.processEvent(new Event(AGENT_0.getAgentIdentifier(), AGENT_1.getAgentIdentifier(), null) {
            });
        } catch (AgentNotStartedException e) {
            fail();
        }

        AGENT_0.kill();
        assertThrows(AgentNotStartedException.class, () -> AGENT_0.processEvent(null)); // No need to add real event
    }

    @Test
    public void testProcessEventOfTargetedProtocol() {
        AGENT_0.start();

        AGENT_0.addProtocol(ProtocolTestImpl.class, "PT", null);
        ProtocolTestImpl protocolTest = (ProtocolTestImpl) AGENT_0.getProtocol(
                new ProtocolIdentifier(ProtocolTestImpl.class.getName(), "PT"));

        AGENT_0.processEvent(new Event(AGENT_0.getAgentIdentifier(), AGENT_1.getAgentIdentifier(),
                protocolTest.getIdentifier()) {
        });

        assertEquals(1, protocolTest.passInProcessEvent);
    }

    @Test
    public void testProcessEventWithNullProtocol() {
        AGENT_0.start();

        AGENT_0.addProtocol(ProtocolTestImpl.class, "PT", null);
        ProtocolTestImpl protocolTest = (ProtocolTestImpl) AGENT_0.getProtocol(
                new ProtocolIdentifier(ProtocolTestImpl.class.getName(), "PT"));

        AGENT_0.processEvent(new Event(AGENT_0.getAgentIdentifier(), AGENT_1.getAgentIdentifier(),
                null) {
        });

        assertEquals(0, protocolTest.passInProcessEvent);
        assertEquals(1, AGENT_0.passInTreatNoProtocolEvent);
        assertEquals(0, AGENT_0.passInTreatEventWithNotFindProtocol);
    }

    @Test
    public void testProcessEventWithNotFindProtocol() {
        AGENT_0.start();

        AGENT_0.processEvent(new Event(AGENT_0.getAgentIdentifier(), AGENT_1.getAgentIdentifier(),
                new ProtocolIdentifier(ProtocolTestImpl.class.getName(), "PT")) {
        });

        assertEquals(0, AGENT_0.passInTreatNoProtocolEvent);
        assertEquals(1, AGENT_0.passInTreatEventWithNotFindProtocol);
    }

    @Test
    public void testJoinEnvironment() {
        assertFalse(AGENT_1.joinEnvironment(ENV));
        assertFalse(ENV.isEvolving(AGENT_1.getAgentIdentifier()));

        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0.getAgentIdentifier()));
        assertTrue(AGENT_0.isEvolvingInEnvironment(ENV));
    }

    @Test
    public void testLeaveEnvironment() {
        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0.getAgentIdentifier()));

        AGENT_0.leaveEnvironment(ENV);
        assertFalse(ENV.isEvolving(AGENT_0.getAgentIdentifier()));

        assertTrue(AGENT_0.joinEnvironment(ENV));
        assertTrue(ENV.isEvolving(AGENT_0.getAgentIdentifier()));
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

        /*Map<String, Behavior> behaviorMap = AGENT_0.getMapBehaviors();
        BehaviorTestImpl b = (BehaviorTestImpl) behaviorMap.get(BehaviorTestImpl.class.getName());
        assertNotNull(b);
        assertTrue(b.isPassToStartPlaying);
        assertFalse(b.isPassToStopPlaying);

        AGENT_0.stopPlayingBehavior(BehaviorTestImpl.class);
        assertTrue(b.isPassToStopPlaying);
        assertNotNull(behaviorMap.get(BehaviorTestImpl.class.getName()));*/
    }

    @Test
    public void testAddProtocol() {
        ProtocolTestImpl protocolTest = new ProtocolTestImpl("P0", AGENT_0, null);

        assertTrue(AGENT_0.addProtocol(ProtocolTestImpl.class, "P0", null));
        assertFalse(AGENT_0.addProtocol(ProtocolTestImpl.class, "P0", null));

        assertTrue(AGENT_1.addProtocol(ProtocolTestImpl.class, "P0", null));

        Protocol protocol0 = AGENT_0.getProtocol(protocolTest.getIdentifier());
        Protocol protocol1 = AGENT_1.getProtocol(protocolTest.getIdentifier());

        assertNotNull(protocol0);
        assertNotNull(protocol1);

        assertEquals(AGENT_0, protocol0.getAgentOwner());
        assertEquals(AGENT_1, protocol1.getAgentOwner());

        assertEquals(protocol0.getIdentifier(), protocol1.getIdentifier());
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
         * @param abstractAgentIdentifier the sima.core.agent to verify
         * @return true if the specified sima.core.agent is to {@link #AGENT_0}, else false.
         */
        @Override
        protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
            return abstractAgentIdentifier.equals(AGENT_0.getAgentIdentifier());
        }

        @Override
        protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {

        }

        @Override
        protected void sendEventWithNullReceiver(Event event) {
            this.isPassToSendEventWithoutReceiver = true;
        }

        @Override
        protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
            return true;
        }

        @Override
        protected void scheduleEventReceptionToOneAgent(AgentIdentifier receiver, Event event) {
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

        // Variables.

        private int passInTreatNoProtocolEvent = 0;

        private int passInTreatEventWithNotFindProtocol = 0;

        // Constructors.

        public AgentTestImpl(String agentName) {
            super(agentName, 0, null);
        }

        // Methods.

        @Override
        protected void processArgument(Map<String, String> args) {
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onKill() {

        }

        @Override
        protected void treatNoProtocolEvent(Event event) {
            this.passInTreatNoProtocolEvent++;
        }

        @Override
        protected void treatEventWithNotFindProtocol(Event event) {
            this.passInTreatEventWithNotFindProtocol++;
        }

        public void reset() {
            this.passInTreatNoProtocolEvent = 0;
            this.passInTreatEventWithNotFindProtocol = 0;
        }

        // Getters and Setters.

        public int getPassInTreatNoProtocolEvent() {
            return passInTreatNoProtocolEvent;
        }

        public int getPassInTreatEventWithNotFindProtocol() {
            return passInTreatEventWithNotFindProtocol;
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

        // Variables.

        private int passInProcessEvent = 0;

        // Constructors.

        public ProtocolTestImpl(String protocolTag, AbstractAgent agentOwner, Map<String, String> args) {
            super(protocolTag, agentOwner, args);
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
            this.passInProcessEvent++;
        }

        public void reset() {
            this.passInProcessEvent = 0;
        }

        // Getters and Setters.

        public int getPassInProcessEvent() {
            return passInProcessEvent;
        }
    }

}
