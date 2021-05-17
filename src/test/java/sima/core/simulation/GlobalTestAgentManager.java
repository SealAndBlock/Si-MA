package sima.core.simulation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AbstractAgent;
import sima.core.agent.AgentTesting;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
abstract class GlobalTestAgentManager extends SimaTest {
    
    protected static AgentManager AGENT_MANAGER;
    
    // Initialization.
    
    @Override
    protected void verifyAndSetup() {
        assertNotNull(AGENT_MANAGER, "AGENT_MANAGER cannot be null for tests");
    }
    
    // Tests.
    
    @Test
    void canAddAbstractAgent() {
        AgentTesting agent = new AgentTesting("AGENT1", 0, 0, null);
        assertTrue(AGENT_MANAGER.addAgent(agent));
    }
    
    @Test
    void cannotAddSameAbstractAgent() {
        AgentTesting agent = new AgentTesting("AGENT1", 0, 0, null);
        assertTrue(AGENT_MANAGER.addAgent(agent));
        assertFalse(AGENT_MANAGER.addAgent(agent));
    }
    
    @Test
    void cannotAddNullAgent() {
        assertFalse(AGENT_MANAGER.addAgent(null));
    }
    
    @Test
    void canAddSeveralDifferentAgent() {
        AgentTesting agent0 = new AgentTesting("AGENT1", 0, 0, null);
        AgentTesting agent1 = new AgentTesting("AGENT2", 1, 1, null);
        AgentTesting agent2 = new AgentTesting("AGENT3", 2, 2, null);
        AgentTesting agent3 = new AgentTesting("AGENT4", 3, 3, null);
        AgentTesting agent4 = new AgentTesting("AGENT5", 4, 4, null);
        
        assertTrue(AGENT_MANAGER.addAgent(agent0));
        assertTrue(AGENT_MANAGER.addAgent(agent1));
        assertTrue(AGENT_MANAGER.addAgent(agent2));
        assertTrue(AGENT_MANAGER.addAgent(agent3));
        assertTrue(AGENT_MANAGER.addAgent(agent4));
    }
    
    @Test
    void returnsAllAddedAgents() {
        AgentTesting agent0 = new AgentTesting("AGENT1", 0, 0, null);
        AgentTesting agent1 = new AgentTesting("AGENT2", 1, 1, null);
        AgentTesting agent2 = new AgentTesting("AGENT3", 2, 2, null);
        AgentTesting agent3 = new AgentTesting("AGENT4", 3, 3, null);
        AgentTesting agent4 = new AgentTesting("AGENT5", 4, 4, null);
        AgentTesting notAddedAgent = new AgentTesting("AGENT_NOT_ADDED", 5, 5, null);
        
        List<AbstractAgent> agentList = new ArrayList<>();
        assertTrue(agentList.add(agent0));
        assertTrue(agentList.add(agent1));
        assertTrue(agentList.add(agent2));
        assertTrue(agentList.add(agent3));
        assertTrue(agentList.add(agent4));
        
        assertTrue(AGENT_MANAGER.addAgent(agent0));
        assertTrue(AGENT_MANAGER.addAgent(agent1));
        assertTrue(AGENT_MANAGER.addAgent(agent2));
        assertTrue(AGENT_MANAGER.addAgent(agent3));
        assertTrue(AGENT_MANAGER.addAgent(agent4));
        
        List<AbstractAgent> returnedList = AGENT_MANAGER.getAllAgents();
        assertTrue(returnedList.containsAll(agentList));
        assertFalse(returnedList.contains(notAddedAgent));
    }
    
}
