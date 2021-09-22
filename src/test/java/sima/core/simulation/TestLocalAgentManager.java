package sima.core.simulation;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.SimaAgent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TestLocalAgentManager {
    
    // Variables.
    
    protected LocalAgentManager localAgentManager;
    
    @Mock
    private SimaAgent mockSimaAgent;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        localAgentManager = new LocalAgentManager();
    }
    
    // Tests.
    
    @Nested
    @Tag("LocalAgentManager.constructor")
    @DisplayName("LocalAgentManager constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor constructs a LocalAgentManager with an empty list of agents")
        void testConstructor() {
            LocalAgentManager localAgentManager = new LocalAgentManager();
            List<SimaAgent> addedAgents = localAgentManager.getAllAgents();
            assertThat(addedAgents).isEmpty();
        }
        
    }
    
    @Nested
    @Tag("LocalAgentManager.addAgent")
    @DisplayName("LocalAgentManager addAgent tests")
    class AddAgentTest {
        
        @Test
        @DisplayName("Test if addAgent returns false if the agent is null")
        void testAddAgentWithNullAgent() {
            boolean added = localAgentManager.addAgent(null);
            List<SimaAgent> addedAgents = localAgentManager.getAllAgents();
            assertThat(added).isFalse();
            assertThat(addedAgents).isEmpty();
        }
        
        @Test
        @DisplayName("Test if addAgent returns true and add the agent with a not null agent")
        void testAddAgentWithNotAlreadyAddedAgent() {
            boolean added = localAgentManager.addAgent(mockSimaAgent);
            List<SimaAgent> addedAgents = localAgentManager.getAllAgents();
            assertThat(added).isTrue();
            assertThat(addedAgents).containsExactly(mockSimaAgent);
        }
        
        @Test
        @DisplayName("Test if addAgent returns false ")
        void testAddAgentWithAlreadyAddedAgent() {
            localAgentManager.addAgent(mockSimaAgent);
            boolean secondAdd = localAgentManager.addAgent(mockSimaAgent);
            List<SimaAgent> addedAgents = localAgentManager.getAllAgents();
            assertThat(secondAdd).isFalse();
            assertThat(addedAgents).containsExactly(mockSimaAgent);
        }
    }
    
}
