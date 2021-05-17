package sima.core.environment;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventTesting;
import sima.core.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.protocol.ProtocolTesting;
import sima.core.simulation.SimaSimulation;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public abstract class GlobalTestEnvironment extends SimaTest {
    
    // Statics.
    
    protected static final String PROTOCOL_TESTING_TAG = "P_TEST";
    
    protected static Environment ENVIRONMENT;
    protected static Environment ENVIRONMENT_EQUAL;
    
    /**
     * An instance of {@link AbstractAgent} which we are sure that the method {@link
     * Environment#agentCanBeAccepted(AgentIdentifier)} returns <strong>TRUE</strong>.
     */
    protected static AbstractAgent ACCEPTED_AGENT;
    
    /**
     * The identifier of {@link #ACCEPTED_AGENT}.
     */
    protected static AgentIdentifier ACCEPTED_AGENT_IDENTIFIER;
    
    /**
     * An instance of {@link AbstractAgent} which we are sure that the method {@link
     * Environment#agentCanBeAccepted(AgentIdentifier)} returns <strong>FALSE</strong>.
     */
    protected static AgentIdentifier NOT_ACCEPTED_AGENT_IDENTIFIER;
    
    /**
     * An agent which is not evolving in the tested environment.
     */
    protected static AgentIdentifier NOT_EVOLVING_AGENT_IDENTIFIER;
    
    
    protected static ProtocolIdentifier PROTOCOL_TESTING_IDENTIFIER;
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(ENVIRONMENT, " ENVIRONMENT cannot be null for tests");
        assertNotNull(ENVIRONMENT_EQUAL, " ENVIRONMENT_EQUAL cannot be null for tests");
        assertNotNull(ACCEPTED_AGENT, " ACCEPTED_AGENT cannot be null for tests");
        assertEquals(ACCEPTED_AGENT.getAgentIdentifier(), ACCEPTED_AGENT_IDENTIFIER, "ACCEPTED_AGENT identifier must "
                + "be equal to ACCEPTED_AGENT_IDENTIFIER");
        assertNotNull(ACCEPTED_AGENT_IDENTIFIER, " ACCEPTED_AGENT_IDENTIFIER cannot be null for tests");
        assertNotNull(NOT_EVOLVING_AGENT_IDENTIFIER, " NOT_EVOLVING_AGENT_IDENTIFIER cannot be null for tests");
        assertNotEquals(ACCEPTED_AGENT_IDENTIFIER, NOT_ACCEPTED_AGENT_IDENTIFIER,
                "ACCEPTED_AGENT_IDENTIFIER cannot be equals of " +
                        "NOT_ACCEPTED_AGENT_IDENTIFIER");
        assertNotEquals(ACCEPTED_AGENT_IDENTIFIER, NOT_EVOLVING_AGENT_IDENTIFIER,
                "ACCEPTED_AGENT_IDENTIFIER cannot be equals of " +
                        "NOT_EVOLVING_AGENT_IDENTIFIER");
        
        assertTrue(ACCEPTED_AGENT.addProtocol(ProtocolTesting.class, PROTOCOL_TESTING_TAG, null), "The ACCEPTED_AGENT"
                + " must be able to add ProtocolTesting.class");
        
        PROTOCOL_TESTING_IDENTIFIER = new ProtocolIdentifier(ProtocolTesting.class, PROTOCOL_TESTING_TAG);
        
        SimaSimulation.waitEndSimulation();
    }
    
    // Tests.
    
    @Test
    void acceptAgentReturnsFalseForNullAgentIdentifier() {
        assertFalse(ENVIRONMENT.acceptAgent(null));
    }
    
    @Test
    void acceptAgentReturnsTrueForAnAcceptedAgent() {
        assertTrue(ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER));
    }
    
    @Test
    void acceptAgentReturnsFalseForANotAcceptedAgent() {
        if (NOT_ACCEPTED_AGENT_IDENTIFIER != null)
            assertFalse(ENVIRONMENT.acceptAgent(NOT_ACCEPTED_AGENT_IDENTIFIER));
    }
    
    @Test
    void isEvolvingReturnsFalseForANullAgentIdentifier() {
        assertFalse(ENVIRONMENT.isEvolving(null));
    }
    
    @Test
    void isEvolvingReturnsTrueForAnAgentWhichHasBeenAccepted() {
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER),
                () -> assertTrue(ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER)));
    }
    
    @Test
    void isEvolvingReturnsFalseForANotEvolvingAgent() {
        assertFalse(ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER));
    }
    
    @Test
    void isEvolvingReturnsFalseForANotAcceptedAgent() {
        if (NOT_ACCEPTED_AGENT_IDENTIFIER != null)
            verifyPreConditionAndExecuteTest(() -> !ENVIRONMENT.agentCanBeAccepted(NOT_ACCEPTED_AGENT_IDENTIFIER),
                    () -> assertFalse(ENVIRONMENT.isEvolving(NOT_ACCEPTED_AGENT_IDENTIFIER)));
    }
    
    @Test
    void isEvolvingReturnsFalseAfterThatAnEvolvingAgentLeaveTheEnvironment() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER);
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER),
                () -> {
                    ENVIRONMENT.leave(ACCEPTED_AGENT_IDENTIFIER);
                    assertFalse(ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER));
                });
    }
    
    @Test
    void evolvingAgentListIsEmptyIfNoAgentHasBeenAccepted() {
        assertTrue(ENVIRONMENT.getEvolvingAgentIdentifiers().isEmpty());
    }
    
    @Test
    void evolvingAgentListContainsTheAgentIdentifierAfterThatItHasBeenAccepted() {
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER),
                () -> assertTrue(
                        ENVIRONMENT.getEvolvingAgentIdentifiers().contains(
                                ACCEPTED_AGENT_IDENTIFIER)));
    }
    
    @Test
    void evolvingAgentListDoesNotContainsTheAgentIdentifierOfAnNotAcceptedAgent() {
        if (NOT_ACCEPTED_AGENT_IDENTIFIER != null)
            verifyPreConditionAndExecuteTest(() -> !ENVIRONMENT.acceptAgent(NOT_ACCEPTED_AGENT_IDENTIFIER),
                    () -> assertFalse(ENVIRONMENT.getEvolvingAgentIdentifiers()
                            .contains(NOT_ACCEPTED_AGENT_IDENTIFIER)));
    }
    
    @Test
    void evolvingAgentListDoesNotContainsAnAgentWhichHasLeftTheEnvironment() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER);
        
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.getEvolvingAgentIdentifiers().contains(
                ACCEPTED_AGENT_IDENTIFIER),
                () -> {
                    ENVIRONMENT.leave(ACCEPTED_AGENT_IDENTIFIER);
                    assertFalse(ENVIRONMENT.getEvolvingAgentIdentifiers()
                            .contains(ACCEPTED_AGENT_IDENTIFIER));
                });
    }
    
    @SuppressWarnings("ConstantConditions")
    @Test
    void sendEventThrowsExceptionWithANullEvent() {
        assertThrows(NullPointerException.class, () -> ENVIRONMENT.sendEvent(null));
    }
    
    @Test
    void sendEventThrowsExceptionIfSenderAgentIsNotEvolvingInTheEnvironment() {
        verifyPreConditionAndExecuteTest(() -> !ENVIRONMENT.isEvolving(NOT_EVOLVING_AGENT_IDENTIFIER),
                () -> {
                    Event event = new EventTesting(NOT_EVOLVING_AGENT_IDENTIFIER,
                            NOT_EVOLVING_AGENT_IDENTIFIER,
                            PROTOCOL_TESTING_IDENTIFIER);
                    assertThrows(NotEvolvingAgentInEnvironmentException.class,
                            () -> ENVIRONMENT.sendEvent(event));
                });
    }
    
    @Test
    void sendEventThrowsExceptionIfReceiverAgentIsNotEvolvingInTheEnvironment() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER);
        verifyPreConditionAndExecuteTest(
                () -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER) && !ENVIRONMENT.isEvolving(
                        NOT_EVOLVING_AGENT_IDENTIFIER), () -> {
                    Event event = new EventTesting(ACCEPTED_AGENT_IDENTIFIER, NOT_EVOLVING_AGENT_IDENTIFIER,
                            PROTOCOL_TESTING_IDENTIFIER);
                    assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> ENVIRONMENT.sendEvent(event));
                });
    }
    
    @Test
    void getEnvironmentNameNeverReturnsNull() {
        assertNotNull(ENVIRONMENT.getEnvironmentName());
    }
    
    @Test
    void sendEventForAnEventWithNoReceiverThrowsException() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER);
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER), () -> {
            Event event = new EventTesting(ACCEPTED_AGENT_IDENTIFIER, null, null);
            assertThrows(IllegalArgumentException.class,
                    assertDoesNotThrow(() -> () -> ENVIRONMENT.sendEvent(event)));
        });
    }
    
    @Test
    void sendEventNotFailForAnEventWithAnEvolvingAgentReceiver() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER);
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER), () -> {
            Event event = new EventTesting(ACCEPTED_AGENT_IDENTIFIER,
                    ACCEPTED_AGENT_IDENTIFIER, PROTOCOL_TESTING_IDENTIFIER);
            assertDoesNotThrow(() -> ENVIRONMENT.sendEvent(event));
        });
    }
    
    @SuppressWarnings("ConstantConditions")
    @Test
    void broadcastEventWithNullEventThrowsException() {
        assertThrows(NullPointerException.class, () -> ENVIRONMENT.broadcastEvent(null));
    }
    
    @Test
    void sprayEventWithNotEvolvingSenderThrowsException() {
        verifyPreConditionAndExecuteTest(() -> !ENVIRONMENT.isEvolving(NOT_EVOLVING_AGENT_IDENTIFIER), () -> {
            Event event = new EventTesting(NOT_EVOLVING_AGENT_IDENTIFIER, NOT_EVOLVING_AGENT_IDENTIFIER,
                    PROTOCOL_TESTING_IDENTIFIER);
            assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> ENVIRONMENT.broadcastEvent(event));
        });
    }
    
    @Test
    void broadcastEventWithCorrectEventNotFail() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT_IDENTIFIER);
        runSimulationWithLongExecutable();
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT_IDENTIFIER), () -> {
            Event event = new EventTesting(ACCEPTED_AGENT_IDENTIFIER,
                    ACCEPTED_AGENT_IDENTIFIER, PROTOCOL_TESTING_IDENTIFIER);
            assertDoesNotThrow(() -> ENVIRONMENT.broadcastEvent(event));
        });
    }
    
    @Test
    void equalsReturnsTrueWithTwoEqualEnvironment() {
        assertEquals(ENVIRONMENT, ENVIRONMENT_EQUAL);
    }
    
    @Test
    void equalsReturnsFalseWithAnNotInstanceOfEnvironment() {
        assertNotEquals(new Object(), ENVIRONMENT);
    }
    
    // Methods.
    
    private void runSimulationWithLongExecutable() {
        Set<Environment> allEnvironments = new HashSet<>();
        allEnvironments.add(ENVIRONMENT);
        
        Set<AbstractAgent> allAgents = new HashSet<>();
        allAgents.add(ACCEPTED_AGENT);
        
        runSimulationWithLongExecutable(allAgents, allEnvironments, null);
    }
}
