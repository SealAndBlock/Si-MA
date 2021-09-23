package sima.core.simulation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.exception.ConfigurationException;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.exception.SimaSimulationIsNotRunningException;
import sima.core.scheduler.Executable;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.multithread.DiscreteTimeMultiThreadScheduler;
import sima.testing.simulation.NotCorrectSimulationSetup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestSimaSimulation {
    
    //Constants.
    
    static final String PREFIX_CONFIG_PATH = "src/test/resources/config/";
    
    // Variables.
    
    @Mock
    private static Scheduler mockScheduler;
    
    @Mock
    private static Environment mockEnvironment;
    
    private static SimaAgent simaAgent;
    
    // Setup.
    
    @BeforeEach
    void setUp() {
        simaAgent = new SimaAgent("SimpleAgentTest", 0, 0, null);
    }
    
    // Tests.
    
    @Nested
    @Tag("SimaSimulation.runSimulation(String)")
    @DisplayName("SimaSimulation runSimulation(String) tests")
    class RunSimulationStringTest {
        
        @Test
        @DisplayName("Test runSimulation with json syntax error")
        void testRunSimulationWithJsonSyntaxError() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configJsonSyntaxError"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with correct config file")
        void testRunSimaSimulationWithCorrectConfigFile() {
            assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "correctConfig.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation without SimaWatcher configuration")
        void testRunSimaSimulationWithoutSimaWatcher() {
            assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "configWithoutSimaWatcher.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation without SchedulerWatcher configuration")
        void testRunSimaSimulationWithoutSchedulerWatcher() {
            assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH + "configWithoutSchedulerWatcher.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation without agent configuration")
        void testRunSimaSimulationWithAgentWhichMustBeCreatedZerothTime() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithAgentWhichMustBeCreatedZerothTime.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with not playable behavior by all agents")
        void testRunSimaSimulationWithNotPlayableBehavior() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithNotPlayableBehavior.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with protocol with dependencies which has not setter to set its dependencies")
        void testRunSimaSimulationWithProtocolWithNoSetterForItsDependencies() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithProtocolDependWithoutDependenciesSetter.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with not correctly implemented protocol")
        void testRunSimaSimulationWithNotCorrectlyImplementedProtocol() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithNotCorrectlyImplementedProtocol.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with add twice a same protocol")
        void testRunSimaSimulationWithAddingTwiceSameProtocol() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configAddTwiceSameProtocol.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with environment which does not accept all agents")
        void testRunSimaSimulationWithEnvironmentWhichDoesNotAcceptAllAgents() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithEnvironmentWhichDoesNotAcceptAllAgents.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with agent name without pattern")
        void testRunSimaSimulationWithAgentNameWithoutPattern() {
            assertDoesNotThrow(() -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithAgentNameWithoutPattern.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with environment without id")
        void testRunSimaSimulationWithEnvironmentWithoutId() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithEnvironmentWithoutId.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation without environment")
        void testRunSimaSimulationWithoutEnvironment() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithoutEnvironment.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with environments with same hashCode")
        void testRunSimaSimulationWithEnvironmentWithSameHashCode() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithEnvironmentWithSameHashCode.json"));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with wrong arg format")
        void testRunSimaSimulationWithWrongArgFormat() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configWithWrongArgFormat.json"));
            waitEndSimulation();
        }
        
        @ParameterizedTest
        @DisplayName("Test runSimulation with wrong arg for Scheduler")
        @ValueSource(strings = {"configWithSchedulerNullTimeMode.json", "configWithSchedulerNullSchedulerType.json",
                "configWithRealTimeScheduler.json", "configWithDiscreteTimeSchedulerMonoThread.json"})
        void testRunSimulationWithWrongArgForScheduler(String configFile) {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    configFile));
            waitEndSimulation();
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.runSimulation(Scheduler, Set<SimpleAgent>, Set<Environment>, Class<SimulationSetup>, SimaWatcher)")
    @DisplayName("SimaSimulation runSimulation(Scheduler, Set<SimpleAgent>, Set<Environment>, Class<SimulationSetup>, SimaWatcher) test")
    class RunSimulationTest {
        
        @Test
        @DisplayName("Test runSimulation with empty set of Environments")
        void testRunSimulationWithEmptySetOfEnvironments() {
            Set<SimaAgent> agents = new HashSet<>();
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(mockScheduler, agents, null, null
                    , null));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation with not correctly implemented SimulationSetup")
        void testRunSimulationWithNotCorrectlyImplementedSimulationSetup() {
            Set<SimaAgent> agents = new HashSet<>();
            Set<Environment> environments = new HashSet<>();
            environments.add(mockEnvironment);
            assertThrows(SimaSimulationFailToStartRunningException.class,
                    () -> SimaSimulation.runSimulation(mockScheduler, agents, environments, NotCorrectSimulationSetup.class
                            , null));
            waitEndSimulation();
        }
        
        @Test
        @DisplayName("Test runSimulation throws a SimaSimulationFailToStartRunningException if a simulation is already running")
        void testRunSimulationWithAlreadyRunningSimulation() {
            createScheduledSimulationTest(() -> {
                Set<Environment> environments = new HashSet<>();
                environments.add(mockEnvironment);
                assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(mockScheduler, null,
                        environments, null,
                        null));
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getScheduler")
    @DisplayName("SimaSimulation getScheduler tests")
    class GetSchedulerTest {
        
        @Test
        @DisplayName("Test if getScheduler throws a SimaSimulationIsNotRunningException if the simulation is not running")
        void testGetSchedulerInNotRunningSimulation() {
            assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getScheduler);
        }
        
        @Test
        @DisplayName("Test getScheduler in running Simulation")
        void testGetSchedulerInRunningSimulation() {
            createScheduledSimulationTest(() -> {
                Scheduler scheduler = SimaSimulation.getScheduler();
                assertNotNull(scheduler);
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getCurrentTime")
    @DisplayName("SimaSimulation getCurrentTime test")
    class GetCurrentTimeTest {
        
        @Test
        @DisplayName("Test if getCurrentTime throws a SimaSimulationIsNotRunningException if the simulation is not running")
        void testGetCurrentTimeInNotRunningSimulation() {
            assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getCurrentTime);
        }
        
        @Test
        @DisplayName("Test if getCurrentTime returns a correct time in running Simulation")
        void testGetCurrentTimeInRunningSimulation() {
            createScheduledSimulationTest(() -> {
                long currentTime = SimaSimulation.getCurrentTime();
                assertThat(currentTime).isPositive();
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getAgent")
    @DisplayName("SimaSimulation getAgent test")
    class GetAgentTest {
        
        @Test
        @DisplayName("Test if getAgent throws a SimaSimulationIsNotRunningException if the simulation is not running")
        void testGetAgentInNotRunningSimulation() {
            AgentIdentifier agentIdentifier = simaAgent.getAgentIdentifier();
            assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.getAgent(agentIdentifier));
        }
        
        @Test
        @DisplayName("Test if getAgent returns the correct agent in running simulation")
        void testGetAgentInRunningSimulation() {
            Set<SimaAgent> agents = new HashSet<>();
            agents.add(simaAgent);
            createScheduledSimulationTest(agents, () -> {
                SimaAgent agent = SimaSimulation.getAgent(simaAgent.getAgentIdentifier());
                assertThat(agent).isSameAs(simaAgent);
            });
        }
        
        @Test
        @DisplayName("Test if getAgent returns null if the agentIdentifier is null in running simulation")
        void testGetAgentWithNullAgentIdentifierInRunningSimulation() {
            Set<SimaAgent> agents = new HashSet<>();
            agents.add(simaAgent);
            createScheduledSimulationTest(agents, () -> {
                SimaAgent agent = SimaSimulation.getAgent(null);
                assertThat(agent).isNull();
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getAllEnvironments")
    @DisplayName("SimaSimulation getAllEnvironments tests")
    class GetAllEnvironmentsTest {
        
        @Test
        @DisplayName("Test if getAllEnvironments throws a SimaSimulationIsNotRunningException if the simulation is not running")
        void testGetAllEnvironmentsInNotRunningSimulation() {
            assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getAllEnvironments);
        }
        
        @Test
        @DisplayName("Test if getAllEnvironments returns all environments in running simulation")
        void testGetAllEnvironmentsInRunningSimulation() {
            createScheduledSimulationTest(() -> {
                Set<Environment> environments = SimaSimulation.getAllEnvironments();
                assertThat(environments).contains(mockEnvironment);
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getEnvironment")
    @DisplayName("SimaSimulation getEnvironment tests")
    class GetEnvironmentTest {
        
        @Test
        @DisplayName("Test if getEnvironment throws a SimaSimulationIsNotRunningException if the simulation is not running")
        void testGetEnvironmentInNotRunningSimulation() {
            assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.getEnvironment("Env"));
        }
        
        @Test
        @DisplayName("Test if getEnvironment returns correct environment in running simulation")
        void testGetEnvironmentInRunningSimulation() {
            String environmentName = "Env";
            when(mockEnvironment.getEnvironmentName()).thenReturn(environmentName);
            
            createScheduledSimulationTest(() -> {
                Environment environment = SimaSimulation.getEnvironment(environmentName);
                assertThat(environment).isSameAs(mockEnvironment);
                
                verify(mockEnvironment, atLeast(1)).getEnvironmentName();
            });
        }
        
        @Test
        @DisplayName("Test if getEnvironment returns null with null environment name in running simulation")
        void testGetEnvironmentWithNullEnvironmentNameInRunningSimulation() {
            createScheduledSimulationTest(() -> {
                Environment environment = SimaSimulation.getEnvironment(null);
                assertThat(environment).isNull();
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getAgentEnvironment")
    @DisplayName("SimaSimulation getAgentEnvironment tests")
    class GetAgentEnvironmentTest {
        
        @Test
        @DisplayName("Test if getAgentEnvironment throws a SimaSimulationIsNotRunningException if the simulation is not running")
        void testGetAgentEnvironmentInNotRunningSimulation() {
            AgentIdentifier agentIdentifier = simaAgent.getAgentIdentifier();
            assertThrows(SimaSimulationIsNotRunningException.class, () -> SimaSimulation.getAgentEnvironment(agentIdentifier));
        }
        
        @Test
        @DisplayName("Test if getAgentEnvironment returns correct environment list in running simulation")
        void testGetAgentEnvironmentInRunningSimulation() {
            AgentIdentifier agentIdentifier = simaAgent.getAgentIdentifier();
            when(mockEnvironment.isEvolving(agentIdentifier)).thenReturn(true);
            
            createScheduledSimulationTest(() -> {
                List<Environment> agentEnvironments = SimaSimulation.getAgentEnvironment(agentIdentifier);
                assertThat(agentEnvironments).containsExactly(mockEnvironment);
                
                verify(mockEnvironment, atLeast(1)).isEvolving(agentIdentifier);
            });
        }
        
        @Test
        @DisplayName("Test if getAgentEnvironment return empty list with null agentIdentifier in running simulation")
        void testGetAgentEnvironmentWithNullAgentIdentifierInRunningSimulation() {
            createScheduledSimulationTest(() -> {
                List<Environment> agentEnvironments = SimaSimulation.getAgentEnvironment(null);
                assertThat(agentEnvironments).isEmpty();
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getTimeMode")
    @DisplayName("SimaSimulation getTimeMode tests")
    class GetTimeModeTest {
        
        @Test
        @DisplayName("Test if getTimeMode throws a SimaSimulationIsNotRunningException in not running simulation")
        void testGetTimeModeInNotRunningSimulation() {
            assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getTimeMode);
        }
        
        @Test
        @DisplayName("Test if getTimeMode returns the correct time mode in running simulation")
        void testGetTimeModeInRunningSimulation() {
            createScheduledSimulationTest(() -> {
                Scheduler.TimeMode timeMode = SimaSimulation.getTimeMode();
                assertThat(timeMode).isEqualTo(Scheduler.TimeMode.DISCRETE_TIME);
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.getSchedulerType")
    @DisplayName("SimaSimulation getSchedulerType tests")
    class GetSchedulerTypeTest {
        
        @Test
        @DisplayName("Test if getSchedulerType throws a SimaSimulationIsNotRunningException in not running simulation")
        void testGetSchedulerTypeInNotRunningSimulation() {
            assertThrows(SimaSimulationIsNotRunningException.class, SimaSimulation::getSchedulerType);
        }
        
        @Test
        @DisplayName("Test if getTimeMode returns the correct time mode in running simulation")
        void testGetSchedulerTypeInRunningSimulation() {
            createScheduledSimulationTest(() -> {
                Scheduler.SchedulerType schedulerType = SimaSimulation.getSchedulerType();
                assertThat(schedulerType).isEqualTo(Scheduler.SchedulerType.MULTI_THREAD);
            });
        }
        
    }
    
    @Nested
    @Tag("SimaSimulation.physicalConnectionLayerConfig")
    @DisplayName("SimaSimulation physicalConnectionLayerConfig tests")
    class PhysicalConnectionLayerConfigTest {
        
        @Test
        @DisplayName("Test if runSimulation throws a ConfigurationException if two chain in an Environment have the same name")
        void testRunSimulationWithTwoEnvironmentPhysicalConnectionLayerChainHaveTheSameName() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaSimulation.runSimulation(PREFIX_CONFIG_PATH +
                    "configSamePhysicalConnectionLayerChainName.json"));
        }
        
    }
    
    
    // Methods.
    
    private static void waitEndSimulation() {
        SimaSimulation.waitEndSimulation();
    }
    
    private static void createScheduledSimulationTest(Executable executable) {
        createScheduledSimulationTest(null, executable);
    }
    
    private static void createScheduledSimulationTest(Set<SimaAgent> agentToAdd, Executable executable) {
        Scheduler scheduler = new DiscreteTimeMultiThreadScheduler(1000L, 2);
        scheduler.scheduleExecutableOnce(executable, Scheduler.NOW);
        
        Set<SimaAgent> agents = null;
        if (agentToAdd != null)
            agents = new HashSet<>(agentToAdd);
        
        Set<Environment> environments = new HashSet<>();
        environments.add(mockEnvironment);
        
        try {
            SimaSimulation.runSimulation(scheduler, agents, environments, null, null);
        } catch (SimaSimulationFailToStartRunningException e) {
            fail(e);
        } finally {
            SimaSimulation.waitEndSimulation();
        }
    }
    
}
