package sima.core.environment;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.event.EventTesting;
import sima.core.exception.NotEvolvingAgentInEnvironmentException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public abstract class GlobalTestEnvironment extends SimaTest {

    // Statics.

    protected static Environment ENVIRONMENT;
    protected static Environment ENVIRONMENT_EQUAL;

    /**
     * An instance of {@link AbstractAgent} which we are sure that the method {@link
     * Environment#agentCanBeAccepted(AgentIdentifier)} returns <strong>TRUE</strong>.
     */
    protected static AgentIdentifier ACCEPTED_AGENT;

    /**
     * An instance of {@link AbstractAgent} which we are sure that the method {@link
     * Environment#agentCanBeAccepted(AgentIdentifier)} returns <strong>FALSE</strong>.
     */
    protected static AgentIdentifier NOT_ACCEPTED_AGENT;

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        assertNotNull(ENVIRONMENT, " ENVIRONMENT cannot be null for tests");
        assertNotNull(ENVIRONMENT_EQUAL, " ENVIRONMENT_EQUAL cannot be null for tests");
        assertNotNull(ACCEPTED_AGENT, " ACCEPTED_AGENT cannot be null for tests");
        assertNotNull(NOT_ACCEPTED_AGENT, " NOT_ACCEPTED_AGENT cannot be null for tests");

        assertNotEquals(ACCEPTED_AGENT, NOT_ACCEPTED_AGENT, "ACCEPTED_AGENT cannot be equals of " +
                "NOT_ACCEPTED_AGENT");
    }

    // Tests.

    @Test
    public void constructEnvironmentWithNullNameThrowsException() {
        assertThrows(NullPointerException.class, () -> new Environment(null, null) {

            @Override
            public void processEvent(Event event) {
            }

            @Override
            protected void processArgument(Map<String, String> args) {
            }

            @Override
            protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
                return false;
            }

            @Override
            protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
            }

            @Override
            protected void broadcastEvent(Event event) {
            }

            @Override
            protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
                return false;
            }

            @Override
            protected void scheduleEventReception(AgentIdentifier receiver, Event event) {
            }
        });
    }

    @Test
    public void constructEnvironmentWithNullArgumentsNotFail() {
        try {
            new Environment("EMPTY", null) {

                @Override
                public void processEvent(Event event) {
                }

                @Override
                protected void processArgument(Map<String, String> args) {
                }

                @Override
                protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
                    return false;
                }

                @Override
                protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
                }

                @Override
                protected void broadcastEvent(Event event) {
                }

                @Override
                protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
                    return false;
                }

                @Override
                protected void scheduleEventReception(AgentIdentifier receiver, Event event) {
                }
            };
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void constructEnvironmentWithNotNullArgumentsNotFail() {
        try {
            new Environment("EMPTY", new HashMap<>()) {

                @Override
                public void processEvent(Event event) {
                }

                @Override
                protected void processArgument(Map<String, String> args) {
                }

                @Override
                protected boolean agentCanBeAccepted(AgentIdentifier abstractAgentIdentifier) {
                    return false;
                }

                @Override
                protected void agentIsLeaving(AgentIdentifier leavingAgentIdentifier) {
                }

                @Override
                protected void broadcastEvent(Event event) {
                }

                @Override
                protected boolean eventCanBeSentTo(AgentIdentifier receiver, Event event) {
                    return false;
                }

                @Override
                protected void scheduleEventReception(AgentIdentifier receiver, Event event) {
                }
            };
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void acceptAgentReturnsFalseForNullAgentIdentifier() {
        assertFalse(ENVIRONMENT.acceptAgent(null));
    }

    @Test
    public void acceptAgentReturnsTrueForAnAcceptedAgent() {
        assertTrue(ENVIRONMENT.acceptAgent(ACCEPTED_AGENT));
    }

    @Test
    public void acceptAgentReturnsFalseForANotAcceptedAgent() {
        assertFalse(ENVIRONMENT.acceptAgent(NOT_ACCEPTED_AGENT));
    }

    @Test
    public void isEvolvingReturnsFalseForANullAgentIdentifier() {
        assertFalse(ENVIRONMENT.isEvolving(null));
    }

    @Test
    public void isEvolvingReturnsTrueForAnAgentWhichHasBeenAccepted() {
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.acceptAgent(ACCEPTED_AGENT),
                                         () -> assertTrue(ENVIRONMENT.isEvolving(ACCEPTED_AGENT)));
    }

    @Test
    public void isEvolvingReturnsFalseForANotEvolvingAgent() {
        assertFalse(ENVIRONMENT.isEvolving(ACCEPTED_AGENT));
    }

    @Test
    public void isEvolvingReturnsFalseForANotAcceptedAgent() {
        verifyPreConditionAndExecuteTest(() -> !ENVIRONMENT.agentCanBeAccepted(NOT_ACCEPTED_AGENT),
                                         () -> assertFalse(ENVIRONMENT.isEvolving(NOT_ACCEPTED_AGENT)));
    }

    @Test
    public void isEvolvingReturnsFalseAfterThatAnEvolvingAgentLeaveTheEnvironment() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT);

        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT),
                                         () -> {
                                             ENVIRONMENT.leave(ACCEPTED_AGENT);
                                             assertFalse(ENVIRONMENT.isEvolving(ACCEPTED_AGENT));
                                         });
    }

    @Test
    public void evolvingAgentListIsEmptyIfNoAgentHasBeenAccepted() {
        assertTrue(ENVIRONMENT.getEvolvingAgentIdentifiers().isEmpty());
    }

    @Test
    public void evolvingAgentListContainsTheAgentIdentifierAfterThatItHasBeenAccepted() {
        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.acceptAgent(ACCEPTED_AGENT),
                                         () -> assertTrue(
                                                 ENVIRONMENT.getEvolvingAgentIdentifiers().contains(ACCEPTED_AGENT)));
    }

    @Test
    public void evolvingAgentListDoesNotContainsTheAgentIdentifierOfAnNotAcceptedAgent() {
        verifyPreConditionAndExecuteTest(() -> !ENVIRONMENT.acceptAgent(NOT_ACCEPTED_AGENT),
                                         () -> assertFalse(ENVIRONMENT.getEvolvingAgentIdentifiers()
                                                                   .contains(NOT_ACCEPTED_AGENT)));
    }

    @Test
    public void evolvingAgentListDoesNotContainsAnAgentWhichHasLeftTheEnvironment() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT);

        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.getEvolvingAgentIdentifiers().contains(ACCEPTED_AGENT),
                                         () -> {
                                             ENVIRONMENT.leave(ACCEPTED_AGENT);
                                             assertFalse(ENVIRONMENT.getEvolvingAgentIdentifiers()
                                                                 .contains(ACCEPTED_AGENT));
                                         });
    }

    @Test
    public void sendEventThrowsExceptionWithANullEvent() {
        assertThrows(NullPointerException.class, () -> ENVIRONMENT.sendEvent(null));
    }

    @Test
    public void sendEventThrowsExceptionIfSenderAgentIsNotEvolvingInTheEnvironment() {
        verifyPreConditionAndExecuteTest(() -> !ENVIRONMENT.isEvolving(ACCEPTED_AGENT),
                                         () -> {
                                             Event event = new EventTesting(ACCEPTED_AGENT, ACCEPTED_AGENT, null);
                                             assertThrows(NotEvolvingAgentInEnvironmentException.class,
                                                          () -> ENVIRONMENT.sendEvent(event));
                                         });
    }

    @Test
    public void sendEventThrowsExceptionIfReceiverAgentIsNotEvolvingInTheEnvironment() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT);

        verifyPreConditionAndExecuteTest(
                () -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT) && !ENVIRONMENT.isEvolving(NOT_ACCEPTED_AGENT),
                () -> {
                    Event event = new EventTesting(ACCEPTED_AGENT, NOT_ACCEPTED_AGENT, null);
                    assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> ENVIRONMENT.sendEvent(event));
                });
    }

    @Test
    public void getEnvironmentNameNeverReturnsNull() {
        assertNotNull(ENVIRONMENT.getEnvironmentName());
    }

    @Test
    public void sendEventNotFailForAnEventWithNoReceiver() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT);

        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT),
                                         () -> {
                                             try {
                                                 Event event = new EventTesting(ACCEPTED_AGENT, null, null);
                                                 ENVIRONMENT.sendEvent(event);
                                             } catch (Exception e) {
                                                 fail(e);
                                             }
                                         });
    }

    @Test
    public void sendEventNotFailForAnEventWithAnEvolvingAgentReceiver() {
        ENVIRONMENT.acceptAgent(ACCEPTED_AGENT);

        verifyPreConditionAndExecuteTest(() -> ENVIRONMENT.isEvolving(ACCEPTED_AGENT),
                                         () -> {
                                             try {
                                                 Event event = new EventTesting(ACCEPTED_AGENT, ACCEPTED_AGENT, null);
                                                 ENVIRONMENT.sendEvent(event);
                                             } catch (Exception e) {
                                                 fail(e);
                                             }
                                         });
    }

    @Test
    public void equalsReturnsTrueWithTwoEqualEnvironment() {
        assertEquals(ENVIRONMENT, ENVIRONMENT_EQUAL);
    }
    
    @Test
    public void equalsReturnsFalseWithAnNotInstanceOfEnvironment() {
        assertNotEquals(new Object(), ENVIRONMENT);
    }
}
