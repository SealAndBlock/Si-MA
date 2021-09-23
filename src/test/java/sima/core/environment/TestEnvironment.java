package sima.core.environment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.exception.NotEvolvingAgentInEnvironmentException;
import sima.core.scheduler.Scheduler;
import sima.core.simulation.SimaSimulation;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static sima.core.TestSima.mockSimaSimulation;

public abstract class TestEnvironment {
    
    // Variables.
    
    protected Environment environment;
    
    protected AgentIdentifier agentIdentifier0;
    
    protected AgentIdentifier agentIdentifier1;
    
    private final long arbitraryDelay = 10L;
    
    @Mock
    private Event mockEvent;
    
    @Mock
    private Scheduler mockScheduler;
    
    @Mock
    private PhysicalConnectionLayer mockPhysicalConnectionLayer;
    
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
    @Tag("Environment.processEventOn")
    @DisplayName("Environment processEventOn tests")
    class ProcessEventOnTest {
        
        @Test
        @DisplayName("Test if processEventOn throws a NotEvolvingAgentInEnvironmentException if the event sender is not an evolving agent")
        void testProcessEventOnWithNotEvolvingEventSender() {
            assertThrows(NotEvolvingAgentInEnvironmentException.class,
                    () -> environment.processEventOn(agentIdentifier0, mockEvent, arbitraryDelay));
        }
        
        @Test
        @DisplayName("Test if sendEvent throws an NotEvolvingAgentInEnvironmentException if the target agent is not an evolving agent")
        void testProcessEventOnWithNullEventReceiver() {
            environment.acceptAgent(agentIdentifier0);
            assertThrows(NotEvolvingAgentInEnvironmentException.class,
                    () -> environment.processEventOn(agentIdentifier1, mockEvent, arbitraryDelay));
        }
        
        @Test
        @DisplayName("Test if sendEvent does not throw exception with sender and receiver evolving agent")
        void testProcessEventOnWithSenderAndReceiverEvolvingAgent() {
            // WHEN
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                simulateSimaSimulationGetSchedulerReturnsMockScheduler(simaSimulationMockedStatic);
                
                environment.acceptAgent(agentIdentifier0);
                assertDoesNotThrow(() -> environment.processEventOn(agentIdentifier0, mockEvent, arbitraryDelay));
            }
        }
    }
    
    @Nested
    @Tag("Environment.addPhysicalConnectionLayer")
    @DisplayName("Environment addPhysicalConnectionLayer tests")
    class AddPhysicalConnectionLayerTest {
        
        @Test
        @DisplayName("Test if addPhysicalConnectionLayer throws IllegalArgumentException if the name or physicalConnectionLayer is null")
        void testAddPhysicalConnectionLayerWithNullArgs() {
            assertThrows(NullPointerException.class, () -> environment.addPhysicalConnectionLayer(null, mockPhysicalConnectionLayer));
            assertThrows(NullPointerException.class, () -> environment.addPhysicalConnectionLayer("REAL_NAME", null));
        }
        
        @Test
        @DisplayName("Test if addPhysicalConnectionLayer returns true and map the physicalConnectionLayer if there is no already name key")
        void testAddPhysicalConnectionLayerWithNewKey() {
            String name = "PCL_NAME";
            boolean added = environment.addPhysicalConnectionLayer(name, mockPhysicalConnectionLayer);
            PhysicalConnectionLayer physicalConnectionLayer = environment.getPhysicalConnectionLayer(name);
            
            assertThat(added).isTrue();
            assertThat(physicalConnectionLayer).isSameAs(mockPhysicalConnectionLayer);
        }
        
    }
    
    @Nested
    @Tag("Environment.getPhysicalConnectionLayer")
    @DisplayName("Environment getPhysicalConnectionLayer tests")
    class GetPhysicalConnectionLayerTest {
        
        @Test
        @DisplayName("Test if getPhysicalConnectionLayer returns null if there is no mapped PhysicalConnectionLayer with the name")
        void testGetPhysicalConnectionLayerWithNotMappedName() {
            assertThat(environment.getPhysicalConnectionLayer("NOT_MAPPED")).isNull();
        }
        
        @Test
        @DisplayName("Test if getPhysicalConnectionLayer returns the correct mapped PhysicalConnectionLayer if the name is mapped")
        void testGetPhysicalConnectionLayerWithMappedName() {
            String name = "MAPPED_NAME";
            environment.addPhysicalConnectionLayer(name, mockPhysicalConnectionLayer);
            assertThat(environment.getPhysicalConnectionLayer(name)).isSameAs(mockPhysicalConnectionLayer);
        }
        
    }
    
}
