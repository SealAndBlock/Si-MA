package sima.core.simulation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sima.core.agent.AbstractAgent;
import sima.core.environment.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class AgentTestingManager {

    protected static AgentManager AGENT_MANAGER;

    @Test
    public void canAddAbstractAgent() {
        TestAgent agent = new TestAgent("AGENT1");
        assertTrue(AGENT_MANAGER.addAgent(agent));
    }

    @Test
    public void cannotAddSameAbstractAgent() {
        TestAgent agent = new TestAgent("AGENT1");
        assertTrue(AGENT_MANAGER.addAgent(agent));
        assertFalse(AGENT_MANAGER.addAgent(agent));
    }

    @Test
    public void cannotAddNullAgent() {
        assertFalse(AGENT_MANAGER.addAgent(null));
    }

    @Test
    public void canAddSeveralDifferentAgent() {
        TestAgent agent0 = new TestAgent("AGENT1");
        TestAgent agent1 = new TestAgent("AGENT2");
        TestAgent agent2 = new TestAgent("AGENT3");
        TestAgent agent3 = new TestAgent("AGENT4");
        TestAgent agent4 = new TestAgent("AGENT5");

        assertTrue(AGENT_MANAGER.addAgent(agent0));
        assertTrue(AGENT_MANAGER.addAgent(agent1));
        assertTrue(AGENT_MANAGER.addAgent(agent2));
        assertTrue(AGENT_MANAGER.addAgent(agent3));
        assertTrue(AGENT_MANAGER.addAgent(agent4));
    }

    @Test
    public void returnsAllAddedAgents() {
        TestAgent agent0 = new TestAgent("AGENT1");
        TestAgent agent1 = new TestAgent("AGENT2");
        TestAgent agent2 = new TestAgent("AGENT3");
        TestAgent agent3 = new TestAgent("AGENT4");
        TestAgent agent4 = new TestAgent("AGENT5");

        TestAgent notAddedAgent = new TestAgent("AGENT_NOT_ADDED");

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

    // Inner classes.

    private static class TestAgent extends AbstractAgent {

        // Constructors.

        public TestAgent(String agentName) {
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

        }

        @Override
        protected void treatEventWithNotFindProtocol(Event event) {

        }
    }

}
