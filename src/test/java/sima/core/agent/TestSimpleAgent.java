package sima.core.agent;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.behavior.Behavior;
import sima.core.environment.Environment;
import sima.core.environment.exchange.event.Event;
import sima.core.exception.AgentNotStartedException;
import sima.core.exception.AlreadyKilledAgentException;
import sima.core.exception.AlreadyStartedAgentException;
import sima.core.exception.KilledAgentException;
import sima.core.protocol.Protocol;
import sima.core.protocol.ProtocolIdentifier;
import sima.core.simulation.SimaSimulation;
import sima.testing.behavior.NotPlayableBehavior;
import sima.testing.behavior.PlayableBehavior;
import sima.testing.behavior.WrongConstructorBehavior;
import sima.testing.protocol.CorrectProtocol0;
import sima.testing.protocol.CorrectProtocol1;
import sima.testing.protocol.NoDefaultProtocolManipulatorProtocol;
import sima.testing.protocol.WrongConstructorProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static sima.core.TestSima.mockSimaSimulation;

@ExtendWith(MockitoExtension.class)
public class TestSimpleAgent {
    
    // Variables.
    
    protected SimpleAgent simpleAgent;
    
    @Mock
    private SimpleAgent mockSimpleAgent;
    
    @Mock
    private SimpleAgent mockAgent;
    
    @Mock
    private Environment mockEnvironment;
    
    @Mock
    private Behavior mockBehavior;
    
    @Mock
    private Event mockEvent;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        simpleAgent = new SimpleAgent("SimpleAgent", 0, 0, null);
    }
    
    // Tests.
    
    /**
     * Simulate that when the method {@link SimaSimulation#getAgentEnvironment(AgentIdentifier)} is called, then an empty list is returns.
     *
     * @param simaSimulationMockedStatic the mock static to simulate SimaSimulation static method.
     * @param agentIdentifier            the agent identifier
     */
    protected void simulateSimaSimulationGetAgentEnvironmentReturnsEmptyList(
            MockedStatic<SimaSimulation> simaSimulationMockedStatic, AgentIdentifier agentIdentifier) {
        simaSimulationMockedStatic.when(() -> SimaSimulation.getAgentEnvironment(agentIdentifier))
                .then(invocation -> new ArrayList<>());
    }
    
    /**
     * Simulate that when the method {@link SimaSimulation#getAgentEnvironment(AgentIdentifier)} is called, then an list which contains {@link
     * #mockEnvironment} is returns.
     *
     * @param simaSimulationMockedStatic the mock static to simulate SimaSimulation static method.
     * @param agentIdentifier            the agent identifier
     */
    protected void simulateSimaSimulationGetAgentEnvironmentReturnsListWithMockEnvironment(
            MockedStatic<SimaSimulation> simaSimulationMockedStatic, AgentIdentifier agentIdentifier) {
        simaSimulationMockedStatic.when(() -> SimaSimulation.getAgentEnvironment(agentIdentifier))
                .then(invocation -> {
                    List<Environment> environmentList = new ArrayList<>();
                    environmentList.add(mockEnvironment);
                    return environmentList;
                });
        
    }
    
    @Nested
    @Tag("SimpleAgent.constructor")
    @DisplayName("SimpleAgent constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if the constructor throws an NullPointerException if the AgentName is null")
        void testWithNullAgentName() {
            Map<String, String> args = new HashMap<>();
            assertThrows(NullPointerException.class, () -> new SimpleAgent(null, 0, 0, args));
        }
        
        @Test
        @DisplayName("Test if the constructor throws an IllegalArgumentException if the sequenceId is less than 0")
        void testWithNegativeSequenceId() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new SimpleAgent("TEST_AGENT", -1, 0, args));
        }
        
        @Test
        @DisplayName("Test if the constructor throws an IllegalArgumentException if the uniqueId is less than 0")
        void testWithNegativeUniqueId() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new SimpleAgent("TEST_AGENT", 0, -1, args));
        }
        
        @Test
        @DisplayName("Test if the constructor does not throw exception with acceptable arguments and not null args " +
                             "map")
        void testWithAllCorrectArguments() {
            Map<String, String> args = new HashMap<>();
            assertDoesNotThrow(() -> new SimpleAgent("TEST_AGENT", 0, 0, args));
        }
        
        @Test
        @DisplayName("Test if the constructors does not throw exception with acceptable arguments and null args map")
        void testWithAllCorrectArgumentsAndNullArgsMap() {
            assertDoesNotThrow(() -> new SimpleAgent("TEST_AGENT", 0, 0, null));
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.toString")
    @DisplayName("SimpleAgent toString tests")
    class ToStringTest {
        
        @Test
        @DisplayName("Test if the toString method returns the correct format with the correct values")
        void testToStringReturnsFormatAndResult() {
            String expectedString = "[AGENT - " +
                    "class=" + simpleAgent.getClass().getName() +
                    ", name=" + simpleAgent.getAgentName() +
                    ", sequenceId=" + simpleAgent.getSequenceId() +
                    ", uniqueId=" + simpleAgent.getUniqueId() + "]";
            
            assertEquals(expectedString, simpleAgent.toString());
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.equals")
    @DisplayName("SimpleAgent equals tests")
    class EqualsTest {
        
        @Test
        @DisplayName("Test if the methods equals returns true if the argument is the agent itself")
        void testEqualsWithAgentItSelf() {
            assertEquals(simpleAgent, simpleAgent);
        }
        
        @Test
        @DisplayName("Test if the methods equals returns false if the argument is null")
        void testEqualsWithNull() {
            assertNotEquals(null, simpleAgent);
        }
        
        @Test
        @DisplayName("Test if the methods equals returns false if the argument is an another agent")
        void testEqualsWithNotEqualAgent() {
            assertNotEquals(mockAgent, simpleAgent);
        }
        
        @Test
        @DisplayName("Test if the method equals returns false if the argument is a different agent")
        void testEqualsWithDifferentAgent() {
            assertNotEquals(simpleAgent, mockSimpleAgent);
        }
        
        @Test
        @DisplayName("Test if the method equals returns false if the argument is not an agent")
        void testEqualsWithNotAbstractAgentObject() {
            assertNotEquals(simpleAgent, new Object());
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.hashCode")
    @DisplayName("SimpleAgent hashCode tests")
    class HashCodeTest {
        
        @Test
        @DisplayName("Test if the method hashCode returns the value of the AgentIdentifier hashCode")
        void testHashCodeResult() {
            int hashCodeExcepted = simpleAgent.getAgentIdentifier().hashCode();
            int hashCode = simpleAgent.hashCode();
            assertEquals(hashCodeExcepted, hashCode);
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.start")
    @DisplayName("SimpleAgent start tests")
    class StartTest {
        
        @Test
        @DisplayName("Test if after have been started, the agent is started")
        void testStartCorrectly() {
            simpleAgent.start();
            boolean isStarted = simpleAgent.isStarted();
            assertTrue(isStarted);
        }
        
        @Test
        @DisplayName("Test if the method start throws an KilledAgentException if the SimpleAgent is killed")
        void testStartAfterKill() {
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                // GIVEN
                simulateSimaSimulationGetAgentEnvironmentReturnsEmptyList(simaSimulationMockedStatic,
                        simpleAgent.getAgentIdentifier());
                
                // WHEN
                simpleAgent.kill();
                
                // THEN
                assertThrows(KilledAgentException.class, () -> simpleAgent.start());
            }
        }
        
        @Test
        @DisplayName("test if the method start throws an AlreadyStartedAgentException if the SimpleAgent is already " +
                             "started")
        void testStartAfterAlreadyStarted() {
            simpleAgent.start();
            assertThrows(AlreadyStartedAgentException.class, () -> simpleAgent.start());
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.kill")
    @DisplayName("SimpleAgent kill tests")
    class KillTest {
        
        @Test
        @DisplayName("Test if the method kill kills the SimpleAgent even if it has not been started")
        void testKillBeforeStart() {
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                // GIVEN
                simulateSimaSimulationGetAgentEnvironmentReturnsEmptyList(simaSimulationMockedStatic,
                        simpleAgent.getAgentIdentifier());
                
                // WHEN
                simpleAgent.kill();
                boolean isStarted = simpleAgent.isStarted();
                boolean isKilled = simpleAgent.isKilled();
                
                // THEN
                assertFalse(isStarted);
                assertTrue(isKilled);
            }
        }
        
        @Test
        @DisplayName("Test if the method kill kills the SimpleAgent after that the SimpleAgent has been started")
        void testKillAfterStart() {
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                // GIVEN
                simulateSimaSimulationGetAgentEnvironmentReturnsEmptyList(simaSimulationMockedStatic,
                        simpleAgent.getAgentIdentifier());
                
                // WHEN
                simpleAgent.start();
                simpleAgent.kill();
                boolean isStarted = simpleAgent.isStarted();
                boolean isKilled = simpleAgent.isKilled();
                
                // THEN
                assertFalse(isStarted);
                assertTrue(isKilled);
            }
        }
        
        @Test
        @DisplayName("Test if the method kill throws and AlreadyKilledAgentException if the SimpleAgent is already " +
                             "killed")
        void testKillAfterAlreadyKill() {
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                // GIVEN
                simulateSimaSimulationGetAgentEnvironmentReturnsEmptyList(simaSimulationMockedStatic,
                        simpleAgent.getAgentIdentifier());
                
                // WHEN
                simpleAgent.kill();
                
                // THEN
                assertThrows(AlreadyKilledAgentException.class, () -> simpleAgent.kill());
            }
        }
        
        @Test
        @DisplayName("Test if after kill the agent, all behaviors added are stopped")
        void testKillWithAddedBehaviors() {
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                // GIVEN
                simulateSimaSimulationGetAgentEnvironmentReturnsEmptyList(simaSimulationMockedStatic,
                        simpleAgent.getAgentIdentifier());
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                simpleAgent.start();
                simpleAgent.startPlayingBehavior(PlayableBehavior.class);
                
                // WHEN
                simpleAgent.kill();
                
                // THEN
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertFalse(isPlayingBehavior);
            }
        }
        
        @Test
        @DisplayName("Test if after kill the agent, the agent has leave all environment and does not throw exception")
        void testKillWithOneEnvironment() {
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                // GIVEN
                simulateSimaSimulationGetAgentEnvironmentReturnsListWithMockEnvironment(simaSimulationMockedStatic,
                        simpleAgent.getAgentIdentifier());
                
                // WHEN
                assertDoesNotThrow(() -> simpleAgent.kill());
            }
        }
    }
    
    @Nested
    @Tag("SimpleAgent.Environment")
    @DisplayName("SimpleAgent environment methods tests")
    class EnvironmentTest {
        
        @Nested
        @Tag("SimpleAgent.isEvolvingInEnvironment")
        @DisplayName("SimpleAgent isEvolvingInEnvironment methods tests")
        class IsEvolvingInEnvironmentTest {
            
            @Test
            @DisplayName(
                    "Test if isEvolvingInEnvironment method returns false when the agent is not evolving in the environment")
            void testIsEvolvingInEnvironmentWithNotEvolvingAgent() {
                // GIVEN
                when(mockEnvironment.isEvolving(simpleAgent.getAgentIdentifier())).thenReturn(false);
                
                // WHEN
                boolean isEvolving = simpleAgent.isEvolvingInEnvironment(mockEnvironment);
                
                // THEN
                verify(mockEnvironment, times(1)).isEvolving(simpleAgent.getAgentIdentifier());
                assertFalse(isEvolving);
            }
            
            @Test
            @DisplayName("Test if isEvolvingInEnvironment method returns true when the agent is evolving in the " +
                                 "environment")
            void testIsEvolvingInEnvironmentWithEvolvingAgent() {
                // GIVEN
                when(mockEnvironment.isEvolving(simpleAgent.getAgentIdentifier())).thenReturn(true);
                
                // WHEN
                boolean isEvolving = simpleAgent.isEvolvingInEnvironment(mockEnvironment);
                
                // THEN
                verify(mockEnvironment, times(1)).isEvolving(simpleAgent.getAgentIdentifier());
                assertTrue(isEvolving);
            }
            
            @Test
            @DisplayName("Test if isEvolvingInEnvironment method returns false when the environment parameter is null")
            void testIsEvolvingInEnvironmentWithNullEnvironment() {
                boolean isEvolving = simpleAgent.isEvolvingInEnvironment(null);
                assertFalse(isEvolving);
            }
            
        }
        
        @Nested
        @Tag("SimpleAgent.joinEnvironment")
        @DisplayName("SimpleAgent joinEnvironment tests")
        class JoinEnvironmentTest {
            
            @Test
            @DisplayName("Test if joinEnvironment returns true for an agent which join the environment where it is not evolving")
            void testJoinEnvironmentWithAcceptedAgent() {
                // GIVEN
                when(mockEnvironment.acceptAgent(simpleAgent.getAgentIdentifier())).thenReturn(true);
                
                // WHEN
                boolean join = simpleAgent.joinEnvironment(mockEnvironment);
                
                // THEN
                verify(mockEnvironment, times(1)).acceptAgent(simpleAgent.getAgentIdentifier());
                assertTrue(join);
            }
            
            @Test
            @DisplayName("Test if joinEnvironment returns false for an agent which join the environment where it is already evolving")
            void testJoinEnvironmentWithNonAcceptedAgent() {
                // GIVEN
                when(mockEnvironment.acceptAgent(simpleAgent.getAgentIdentifier())).thenReturn(false);
                
                // WHEN
                boolean join = simpleAgent.joinEnvironment(mockEnvironment);
                
                // THEN
                verify(mockEnvironment, times(1)).acceptAgent(simpleAgent.getAgentIdentifier());
                assertFalse(join);
            }
        }
        
        @Nested
        @Tag("SimpleAgent.leaveEnvironment")
        @DisplayName("SimpleAgent leaveEnvironment tests")
        class LeaveEnvironmentTest {
            
            @Test
            @DisplayName("Test if leaveEnvironment does not throws exception")
            void testLeaveEnvironmentDoesNotThrowsException() {
                assertDoesNotThrow(() -> simpleAgent.leaveEnvironment(mockEnvironment));
            }
            
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.Behavior")
    @DisplayName("SimpleAgent behavior methods tests")
    class BehaviorTest {
        
        @Nested
        @Tag("SimpleAgent.addBehavior")
        @DisplayName("SimpleAgent addBehavior tests")
        class AddBehaviorTest {
            
            @Test
            @DisplayName("Test if addBehavior returns false if the behavior class does not have a correct constructor")
            void testAddBehaviorWithBehaviorClassWithWrongConstructor() {
                Map<String, String> args = new HashMap<>();
                boolean behaviorAdded = simpleAgent.addBehavior(WrongConstructorBehavior.class, args);
                assertFalse(behaviorAdded);
            }
            
            @Test
            @DisplayName("Test if addBehavior returns true if the behavior class has a correct constructor")
            void testAddBehaviorWithBehaviorClassWithCorrectConstructor() {
                Map<String, String> args = new HashMap<>();
                boolean behaviorAdded = simpleAgent.addBehavior(PlayableBehavior.class, args);
                assertTrue(behaviorAdded);
            }
            
            @Test
            @DisplayName("Test if addBehavior returns ture if the behavior class is correct and the map args is null")
            void testAddBehaviorWithNullArgs() {
                boolean behaviorAdded = simpleAgent.addBehavior(PlayableBehavior.class, null);
                assertTrue(behaviorAdded);
            }
            
            @Test
            @DisplayName("Test if addBehavior returns false if the behavior class has already been added")
            void testAddBehaviorWithAlreadyAddBehaviorClass() {
                Map<String, String> args = new HashMap<>();
                boolean firstAdd = simpleAgent.addBehavior(PlayableBehavior.class, args);
                boolean secondAdd = simpleAgent.addBehavior(PlayableBehavior.class, args);
                assertTrue(firstAdd);
                assertFalse(secondAdd);
            }
            
            @Test
            @DisplayName("Test if addBehavior returns false if the behavior is not playable by the agent")
            void testAddBehaviorWithNotPlayableBehaviorByTheAgent() {
                Map<String, String> args = new HashMap<>();
                boolean behaviorAdded = simpleAgent.addBehavior(NotPlayableBehavior.class, args);
                assertFalse(behaviorAdded);
            }
            
        }
        
        @Nested
        @Tag("SimpleAgent.startPlayingBehavior")
        @DisplayName("SimpleAgent startPlayingBehavior tests")
        class StartPlayingBehaviorTest {
            
            @Test
            @DisplayName("Test if startPlayingBehavior throws an NullPointerException if the behaviorClass is null")
            void testStartPlayingBehaviorWithNullBehaviorClass() {
                assertThrows(NullPointerException.class, () -> simpleAgent.startPlayingBehavior(null));
            }
            
            @Test
            @DisplayName(
                    "Test if for a started agent and an added behavior to the agent, after call the method startPlayingBehavior, the agent is not playing the behavior")
            void testStartPlayingBehaviorWithNotStartedAgentAndAddedBehavior() {
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                simpleAgent.startPlayingBehavior(PlayableBehavior.class);
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertFalse(isPlayingBehavior);
            }
            
            @Test
            @DisplayName("Test if for a started agent and an added behavior to the agent, after call the method " +
                                 "startPlayingBehavior, the agent is playing the behavior")
            void testStartPlayingBehaviorWithStartedAgentAndAddedBehavior() {
                simpleAgent.start();
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                simpleAgent.startPlayingBehavior(PlayableBehavior.class);
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertTrue(isPlayingBehavior);
            }
            
            @Test
            @DisplayName(
                    "Test if for a started agent and a non added behavior to the agent, after call the method startPlayingBehavior, the agent is not playing the behavior")
            void testStartPlayingBehaviorWithStartedAgentAndNotAddedBehavior() {
                simpleAgent.start();
                simpleAgent.startPlayingBehavior(PlayableBehavior.class);
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertFalse(isPlayingBehavior);
            }
        }
        
        @Nested
        @Tag("SimpleAgent.stopPlayingBehavior")
        @DisplayName("SimpleAgent stopPlayingBehavior tests")
        class StopPlayingBehaviorTest {
            
            @Test
            @DisplayName("Test if for a added but non playing behavior, the method stopPlayingBehavior do nothing")
            void testStopPlayingBehaviorWithNotPlayingBehavior() {
                simpleAgent.start();
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                boolean isPlayingBehaviorBeforeStop = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                simpleAgent.stopPlayingBehavior(PlayableBehavior.class);
                boolean isPlayingBehaviorAfterStop = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertFalse(isPlayingBehaviorBeforeStop);
                assertFalse(isPlayingBehaviorAfterStop);
            }
            
            @Test
            @DisplayName("Test if for added and playing behavior, the after the call of the method " +
                                 "stopPlayingBehavior, the agent does not play anymore the behavior")
            void testStopPlayingBehaviorWithPlayingBehavior() {
                simpleAgent.start();
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                simpleAgent.startPlayingBehavior(PlayableBehavior.class);
                simpleAgent.stopPlayingBehavior(PlayableBehavior.class);
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertFalse(isPlayingBehavior);
            }
            
        }
        
        @Nested
        @Tag("SimpleAgent.canPlayBehavior")
        @DisplayName("SimpleAgent canPlayBehavior tests")
        class CanPlayBehaviorTest {
            
            @Test
            @DisplayName("Test if canPlayBehavior returns true if the behavior is playable by the agent")
            void testCanPlayBehaviorWithPlayableBehavior() {
                boolean isPlayableBehavior = simpleAgent.canPlayBehavior(PlayableBehavior.class);
                assertTrue(isPlayableBehavior);
            }
            
            @Test
            @DisplayName("Test if canPlayBehavior returns false if the behavior is not playable by the agent")
            void testCanPlayBehaviorWithNotPlayableBehavior() {
                boolean isPlayableBehavior = simpleAgent.canPlayBehavior(NotPlayableBehavior.class);
                assertFalse(isPlayableBehavior);
            }
            
        }
        
        @Nested
        @Tag("SimpleAgent.isPlayingBehavior")
        @DisplayName("SimpleAgent isPlayingBehavior tests")
        class IsPlayingBehaviorTest {
            
            @Test
            @DisplayName("Test if isPlayingBehavior returns false if the behaviors is not added in the agent")
            void testIsPlayingBehaviorWithNotAddedBehavior() {
                simpleAgent.start();
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertFalse(isPlayingBehavior);
            }
            
            @Test
            @DisplayName("Test if isPlayingBehavior returns false if the behavior is added but not played")
            void testIsPlayingBehaviorWithAddedButNotPlayedBehavior() {
                simpleAgent.start();
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertFalse(isPlayingBehavior);
            }
            
            @Test
            @DisplayName("Test if isPlayingBehavior returns true if the behavior is added and played by the agent")
            void testIsPlayingBehaviorWithAddedAndPlayedBehavior() {
                simpleAgent.start();
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                simpleAgent.startPlayingBehavior(PlayableBehavior.class);
                boolean isPlayingBehavior = simpleAgent.isPlayingBehavior(PlayableBehavior.class);
                assertTrue(isPlayingBehavior);
            }
            
        }
        
        @Nested
        @Tag("SimpleAgent.getBehavior")
        @DisplayName("SimpleAgent getBehavior tests")
        class GetBehaviorTest {
            
            @Test
            @DisplayName("Test if getBehavior returns null if the behavior is not added")
            void testGetBehaviorWithNotAddedBehavior() {
                var behavior = simpleAgent.getBehavior(PlayableBehavior.class);
                assertNull(behavior);
            }
            
            @Test
            @DisplayName("Test if getBehavior returns a instance of behavior if the behavior has been added")
            void testGetBehaviorWithAddedBehavior() {
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                var behavior = simpleAgent.getBehavior(PlayableBehavior.class);
                assertNotNull(behavior);
            }
            
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.Protocol")
    @DisplayName("SimpleAgent protocol methods tests")
    class ProtocolTest {
        
        @Nested
        @Tag("SimpleAgent.addProtocol")
        @DisplayName("SimpleAgent addProtocol tests")
        class AddProtocolTest {
            
            @Test
            @DisplayName("Test if addProtocol returns false if the protocol class has not a correct constructor")
            void testAddProtocolWithProtocolClassWithNotCorrectConstructor() {
                Map<String, String> args = new HashMap<>();
                String protocolTag = "TAG";
                boolean protocolAdded = simpleAgent.addProtocol(WrongConstructorProtocol.class, protocolTag, args);
                assertFalse(protocolAdded);
            }
            
            @Test
            @DisplayName("Test if addProtocol returns true if the protocol class has a correct constructor")
            void testAddProtocolWithProtocolClassWithCorrectConstructor() {
                Map<String, String> args = new HashMap<>();
                String protocolTag = "TAG";
                boolean protocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, args);
                assertTrue(protocolAdded);
            }
            
            @Test
            @DisplayName("Test if addProtocol returns false if the protocol class has not default protocol manipulator")
            void testAddProtocolWithProtocolClassWithNoDefaultProtocolManipulator() {
                Map<String, String> args = new HashMap<>();
                String protocolTag = "TAG";
                boolean protocolAdded = simpleAgent
                        .addProtocol(NoDefaultProtocolManipulatorProtocol.class, protocolTag, args);
                assertFalse(protocolAdded);
            }
            
            @Test
            @DisplayName("Test if addProtocol returns false if the method is called with null protocolTag")
            void testAddProtocolWithNullProtocolTag() {
                Map<String, String> args = new HashMap<>();
                boolean protocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, null, args);
                assertFalse(protocolAdded);
            }
            
            @Test
            @DisplayName("Test if addProtocol returns true if the methods is called with null args map")
            void testAddProtocolWithNullArgsMap() {
                String protocolTag = "TAG";
                boolean protocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
                assertTrue(protocolAdded);
            }
            
            @Test
            @DisplayName("Test if addProtocol returns true if the protocol class added is the same than an already " +
                                 "added protocol class but the tag is different")
            void testAddProtocolWithSameProtocolClassButDifferentTag() {
                String protocolTag0 = "TAG_0";
                String protocolTag1 = "TAG_1";
                boolean firstProtocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag0, null);
                boolean secondProtocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag1, null);
                assertTrue(firstProtocolAdded);
                assertTrue(secondProtocolAdded);
            }
            
            @Test
            @DisplayName("Test if addProtocol returns false if the protocol tag is the same than an already added " +
                                 "protocol")
            void testAddProtocolWithSameProtocolTag() {
                String protocolTag = "TAG_0";
                boolean firstProtocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
                boolean secondProtocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
                assertTrue(firstProtocolAdded);
                assertFalse(secondProtocolAdded);
            }
            
            @Test
            @DisplayName("Test if addProtocol returns true if the protocol tag is the same than an already added " +
                                 "protocol but the class is different")
            void testAddProtocolWithDifferentProtocolClassButSameTag() {
                String protocolTag = "TAG_0";
                boolean firstProtocolAdded = simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
                boolean secondProtocolAdded = simpleAgent.addProtocol(CorrectProtocol1.class, protocolTag, null);
                assertTrue(firstProtocolAdded);
                assertTrue(secondProtocolAdded);
            }
            
        }
        
        @Nested
        @Tag("SimpleAgent.getProtocol")
        @DisplayName("SimpleAgent getProtocol tests")
        class GetProtocolTest {
            
            @Test
            @DisplayName("Test if getProtocol returns null if the protocol is not added")
            void testGetProtocolWithNotAddedProtocol() {
                String protocolTag = "TAG";
                var p0 = new ProtocolIdentifier(CorrectProtocol0.class, protocolTag);
                var protocol = simpleAgent.getProtocol(p0);
                assertNull(protocol);
            }
            
            @Test
            @DisplayName("Test if getProtocol returns an instance of protocol if the protocol is added")
            void testGetProtocolWithAddedProtocol() {
                String protocolTag = "TAG";
                simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
                var p0 = new ProtocolIdentifier(CorrectProtocol0.class, protocolTag);
                var protocol = simpleAgent.getProtocol(p0);
                assertNotNull(protocol);
            }
            
            @Test
            @DisplayName(
                    "Test if getProtocol returns two different instances of protocol if protocols added have the" +
                            " same class but not the same tag")
            void testGetProtocolWithSameProtocolClassButDifferentTag() {
                String protocolTag0 = "TAG_0";
                String protocolTag1 = "TAG_1";
                simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag0, null);
                simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag1, null);
                var p0 = new ProtocolIdentifier(CorrectProtocol0.class, protocolTag0);
                var p1 = new ProtocolIdentifier(CorrectProtocol0.class, protocolTag1);
                var protocol0 = simpleAgent.getProtocol(p0);
                var protocol1 = simpleAgent.getProtocol(p1);
                assertNotSame(protocol0, protocol1);
            }
            
            @Test
            @DisplayName("Test if getProtocol returns two different instances of protocol if protocols added have " +
                                 "different class but same tag")
            void testGetProtocolWithDifferentProtocolClassButSameTag() {
                String protocolTag = "TAG";
                simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
                simpleAgent.addProtocol(CorrectProtocol1.class, protocolTag, null);
                var p0 = new ProtocolIdentifier(CorrectProtocol0.class, protocolTag);
                var p1 = new ProtocolIdentifier(CorrectProtocol1.class, protocolTag);
                var protocol0 = simpleAgent.getProtocol(p0);
                var protocol1 = simpleAgent.getProtocol(p1);
                assertNotSame(protocol0, protocol1);
            }
        }
    }
    
    // Utils.
    
    @Nested
    @Tag("SimpleAgent.processEvent")
    @DisplayName("SimpleAgent processEvent tests")
    class ProcessEventTest {
        
        @Test
        @DisplayName("Test if processEvent throws an AgentNotStartedException if the agent is not started")
        void testProcessEventWithNotStartedAgent() {
            assertThrows(AgentNotStartedException.class, () -> simpleAgent.processEvent(mockEvent));
        }
        
        @Test
        @DisplayName("Test if processEvent throws an UnsupportedOperationException if the event does not have " +
                             "protocolTargeted")
        void testProcessEventWithNoIntendedProtocolEvent() {
            // GIVEN
            when(mockEvent.hasIntendedProtocol()).thenReturn(false);
            
            // WHEN
            simpleAgent.start();
            assertThrows(UnsupportedOperationException.class, () -> simpleAgent.processEvent(mockEvent));
            
            // THEN
            verify(mockEvent, times(1)).hasIntendedProtocol();
        }
        
        @Test
        @DisplayName("Test if processEvent throws an IllegalArgumentException if the event has as Intended protocol a " +
                             "protocol not added in the agent")
        void testProcessEventWithEventWithNotAddedIntendedProtocolInTheAgent() {
            String protocolTag = "TAG";
            var protocolIdentifier = new ProtocolIdentifier(CorrectProtocol0.class, protocolTag);
            
            // GIVEN
            when(mockEvent.hasIntendedProtocol()).thenReturn(true);
            when(mockEvent.getProtocolIntended()).thenReturn(protocolIdentifier);
            
            // WHEN
            simpleAgent.start();
            assertThrows(IllegalArgumentException.class, () -> simpleAgent.processEvent(mockEvent));
            
            // THEN
            verify(mockEvent, times(1)).hasIntendedProtocol();
            verify(mockEvent, times(1)).getProtocolIntended();
        }
        
        @Test
        @DisplayName("Test if processEvent does not throw an exception if the event has a protocolTargeted added in " +
                             "the agent")
        void testProcessEventWithEventWithIntendedProtocolAddedInTheAgent() {
            String protocolTag = "TAG";
            var protocolIdentifier = new ProtocolIdentifier(CorrectProtocol0.class, protocolTag);
            
            // GIVEN
            when(mockEvent.hasIntendedProtocol()).thenReturn(true);
            when(mockEvent.getProtocolIntended()).thenReturn(protocolIdentifier);
            
            // WHEN
            simpleAgent.start();
            simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
            assertDoesNotThrow(() -> simpleAgent.processEvent(mockEvent));
            
            // THEN
            verify(mockEvent, times(1)).hasIntendedProtocol();
            verify(mockEvent, times(1)).getProtocolIntended();
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.getAgentIdentifier")
    @DisplayName("SimpleAgent getAgentIdentifier tests")
    class GetAgentIdentifierTest {
        
        @Test
        @DisplayName("Test if getAgentIdentifier returns a correct AgentIdentifier")
        void testGetAgentIdentifier() {
            var expectedAgentIdentifier = new AgentIdentifier(simpleAgent.getAgentName(), simpleAgent.getSequenceId()
                    , simpleAgent.getUniqueId());
            var agentIdentifier = simpleAgent.getAgentIdentifier();
            assertEquals(expectedAgentIdentifier, agentIdentifier);
        }
        
    }
    
    @Nested
    @Tag("SimpleAgent.getInfo")
    @DisplayName("SimpleAgent getInfo tests")
    class GetInfoTest {
        
        @Test
        @DisplayName("Test if getInfo returns a correct AgentInfo")
        void testGetInfo() {
            try (MockedStatic<SimaSimulation> simaSimulationMockedStatic = mockSimaSimulation()) {
                // GIVEN
                simulateSimaSimulationGetAgentEnvironmentReturnsEmptyList(simaSimulationMockedStatic,
                        simpleAgent.getAgentIdentifier());
                
                // WHEN
                String protocolTag = "TAG";
                simpleAgent.addBehavior(PlayableBehavior.class, null);
                simpleAgent.addProtocol(CorrectProtocol0.class, protocolTag, null);
                simpleAgent.addProtocol(CorrectProtocol1.class, protocolTag, null);
                
                List<String> behaviorNames = simpleAgent.getBehaviorList().stream().map(b -> b.getClass().getName())
                        .collect(Collectors.toList());
                List<ProtocolIdentifier> protocolIdentifiers =
                        simpleAgent.getProtocolList().stream().map(Protocol::getIdentifier)
                                .collect(Collectors.toList());
                List<String> environmentNames =
                        simpleAgent.getEnvironmentList().stream().map(Environment::getEnvironmentName)
                                .collect(Collectors.toList());
                var expectedAgentInfo = new AgentInfo(simpleAgent.getAgentIdentifier(), behaviorNames,
                        protocolIdentifiers, environmentNames);
                var agentInfo = simpleAgent.getInfo();
                
                // THEN
                assertEquals(expectedAgentInfo.getAgentIdentifier(), agentInfo.getAgentIdentifier());
                assertThat(expectedAgentInfo.getBehaviors())
                        .containsExactly(agentInfo.getBehaviors().toArray(new String[0]));
                assertThat(expectedAgentInfo.getProtocols())
                        .containsExactly(agentInfo.getProtocols().toArray(new ProtocolIdentifier[0]));
                assertThat(expectedAgentInfo.getEnvironments())
                        .containsExactly(agentInfo.getEnvironments().toArray(new String[0]));
            }
        }
        
    }
}

