package sima.core.environment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.scheduler.Scheduler;
import sima.core.simulation.SimaSimulation;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static sima.core.SimaTest.mockSimaSimulation;

public abstract class TestEnvironment {
    
    // Variables.
    
    protected Environment environment;
    
    protected AgentIdentifier agentIdentifier0;
    
    protected AgentIdentifier agentIdentifier1;
    
    @Mock
    private Event mockEvent;
    
    @Mock
    private Scheduler mockScheduler;
    
    // Tests.
    
    /**
     * Simulate that the method {@link SimaSimulation#getScheduler()} returns {@link #mockScheduler}.
     *
     * @param simaSimulationMockedStatic the mock static to simulate SimaSimulation static method.
     */
    protected void simulateSimaSimulationGetSchedulerReturnsMockScheduler(MockedStatic<SimaSimulation> simaSimulationMockedStatic) {
        simaSimulationMockedStatic.when(SimaSimulation::getScheduler).then(invocation -> mockScheduler);
    }
    
    @Nested
    @Tag("Environment.toString")
    @DisplayName("Environment toString tests")
    public class ToStringTest {
        
        @Test
        @DisplayName("Test if the method toString returns a correct String")
        public void testToString() {
            String expectedToString = "[Environment - " +
                    "class=" + this.getClass().getName() +
                    ", environmentName=" + environment.getEnvironmentName() + "]";
            String toString = environment.toString();
            assertEquals(expectedToString, toString);
        }
        
    }
    
    @Nested
    @Tag("Environment.leave")
    @DisplayName("Environment leave tests")
    class LeaveTest {
        
        @Test
        @DisplayName("Test if leave remove an agent from the environment")
        void testLeave() {
            environment.acceptAgent(agentIdentifier0);
            environment.leave(agentIdentifier0);
            boolean isEvolving = environment.isEvolving(agentIdentifier0);
            assertFalse(isEvolving);
        }
        
    }
    
    @Nested
    @Tag("Environment.isEvolving")
    @DisplayName("Environment isEvolving tests")
    class IsEvolvingTest {
        
        @Test
        @DisplayName("Test if isEvolving method returns false with null agentIdentifier")
        void testIsEvolvingWithNull() {
            boolean isEvolving = environment.isEvolving(null);
            assertFalse(isEvolving);
        }
        
        @Test
        @DisplayName("Test if isEvolving method returns false if the agent is not evolving in the environment")
        void testIsEvolvingWithNotEvolvingAgent() {
            boolean isEvolving = environment.isEvolving(agentIdentifier0);
            assertFalse(isEvolving);
        }
        
        @Test
        @DisplayName("Test if isEvolving method return true if the agent is evolving in the environment")
        void testIsEvolvingWithEvolvingAgent() {
            environment.acceptAgent(agentIdentifier0);
            boolean isEvolving = environment.isEvolving(agentIdentifier0);
            assertTrue(isEvolving);
        }
        
    }
    
    @Nested
    @Tag("Environment.getEvolvingAgentIdentifiers")
    @DisplayName("Environment getEvolvingAgentIdentifiers tests")
    class GetEvolvingAgentIdentifiersTest {
        
        @Test
        @DisplayName("Test if getEvolvingAgentIdentifiers returns empty list if there is no evolving agent")
        void testGetEvolvingAgentIdentifiersWithNoEvolvingAgent() {
            List<AgentIdentifier> list = environment.getEvolvingAgentIdentifiers();
            assertTrue(list.isEmpty());
        }
        
        @Test
        @DisplayName("Test if getEvolvingAgentIdentifiers returns the list which contains all evolving agent")
        void testGetEvolvingAgentIdentifiersWithEvolvingAgent() {
            environment.acceptAgent(agentIdentifier0);
            List<AgentIdentifier> list = environment.getEvolvingAgentIdentifiers();
            assertThat(list).containsExactly(agentIdentifier0);
        }
        
    }
    
    @Nested
    @Tag("Environment.sendEvent")
    @DisplayName("Environment sendEvent tests")
    class SendEventTest {
        
        @Test
        @DisplayName("Test if sendEvent throws a NotEvolvingAgentInEnvironmentException if the event sender is not an evolving agent")
        void testSendEventWithNotEvolvingEventSender() {
            // GIVEN
            when(mockEvent.getSender()).thenReturn(agentIdentifier0);
            
            // WHEN
            assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> environment.sendEvent(mockEvent));
            
            // THEN
            verify(mockEvent, atLeast(1)).getSender();
        }
        
        @Test
        @DisplayName("Test if sendEvent throws an IllegalArgumentException if the event receiver is not an evolving agent")
        void testSendEventWithNullEventReceiver() {
            // GIVEN
            when(mockEvent.getSender()).thenReturn(agentIdentifier0);
            when(mockEvent.getReceiver()).thenReturn(null);
            
            // WHEN
            environment.acceptAgent(agentIdentifier0);
            assertThrows(IllegalArgumentException.class, () -> environment.sendEvent(mockEvent));
            
            // THEN
            verify(mockEvent, atLeast(1)).getSender();
            verify(mockEvent, atLeast(1)).getReceiver();
        }
        
        @Test
        @DisplayName("Test if sendEvent throws an NotEvolvingAgentInEnvironmentException if the event receiver is not in the environment")
        void testSendEventWithNotEvolvingEventReceiver() {
            // GIVEN
            when(mockEvent.getSender()).thenReturn(agentIdentifier0);
            when(mockEvent.getReceiver()).thenReturn(agentIdentifier1);
            
            // WHEN
            environment.acceptAgent(agentIdentifier0);
            assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> environment.sendEvent(mockEvent));
            
            // THEN
            verify(mockEvent, atLeast(1)).getSender();
            verify(mockEvent, atLeast(1)).getReceiver();
        }
        
        @Test
        @DisplayName("Test if sendEvent does not throw exception with sender and receiver evolving agent")
        void testSendEventWithSenderAndReceiverEvolvingAgent() {
            // GIVEN
            when(mockEvent.getSender()).thenReturn(agentIdentifier0);
            when(mockEvent.getReceiver()).thenReturn(agentIdentifier1);
            
            // WHEN
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                simulateSimaSimulationGetSchedulerReturnsMockScheduler(simaSimulationMockedStatic);
                
                environment.acceptAgent(agentIdentifier0);
                environment.acceptAgent(agentIdentifier1);
                assertDoesNotThrow(() -> environment.sendEvent(mockEvent));
            }
            
            // THEN
            verify(mockEvent, atLeast(1)).getSender();
            verify(mockEvent, atLeast(1)).getReceiver();
        }
    }
    
    @Nested
    @Tag("Environment.broadcastEvent")
    @DisplayName("Environment broadcastEvent tests")
    class BroadcastEventTest {
        
        @Test
        @DisplayName("Test if broadcastEvent throws a NotEvolvingAgentInEnvironmentException if the event sender is not evolving")
        void testBroadcastEventWithNotEvolvingEventSender() {
            // GIVEN
            when(mockEvent.getSender()).thenReturn(agentIdentifier0);
            
            // WHEN
            assertThrows(NotEvolvingAgentInEnvironmentException.class, () -> environment.broadcastEvent(mockEvent));
            
            // THEN
            verify(mockEvent, atLeast(1)).getSender();
        }
        
        @Test
        @DisplayName("Test if broadcastEvent does not throw exception if the event sender is evolving")
        void testBroadcastEventWithEvolvingEventSender() {
            // GIVEN
            when(mockEvent.getSender()).thenReturn(agentIdentifier0);
            
            // WHEN
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                simulateSimaSimulationGetSchedulerReturnsMockScheduler(simaSimulationMockedStatic);
                
                environment.acceptAgent(agentIdentifier0);
                environment.acceptAgent(agentIdentifier1);
                assertDoesNotThrow(() -> environment.broadcastEvent(mockEvent));
            }
            
            // THEN
            verify(mockEvent, atLeast(1)).getSender();
        }
        
    }
    
}
