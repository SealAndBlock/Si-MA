package sima.core.agent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sima.core.protocol.ProtocolIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class TestAgentInfo {
    
    // Variables.
    
    // Init.
    
    // Tests.
    
    @Nested
    @Tag("AgentInfo.constructor")
    @DisplayName("AgentInfo constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructs an AgentInfo with null agentIdentifier throws a NullPointerException")
        void testConstructorWithNullAgentIdentifier() {
            List<String> behaviors = new ArrayList<>();
            List<ProtocolIdentifier> protocols = new ArrayList<>();
            List<String> environments = new ArrayList<>();
            assertThrows(NullPointerException.class, () -> new AgentInfo(null, behaviors, protocols, environments));
        }
        
        @Test
        @DisplayName("Test if constructs an AgentInfo with null behaviors does not throws exception and the " +
                "behavior list is empty")
        void testConstructorWithNullBehaviors() {
            AgentIdentifier agentIdentifier = new AgentIdentifier("AGENT", 0, 0);
            List<ProtocolIdentifier> protocols = new ArrayList<>();
            List<String> environments = new ArrayList<>();
            AtomicReference<AgentInfo> info = new AtomicReference<>();
            assertDoesNotThrow(() -> info.set(new AgentInfo(agentIdentifier, null, protocols, environments)));
            assertTrue(info.get().behaviors().isEmpty());
        }
        
        @Test
        @DisplayName("Test if constructs an AgentInfo with null protocols does not throws exception and the protocol " +
                "list is empty")
        void testConstructorWithNullProtocols() {
            AgentIdentifier agentIdentifier = new AgentIdentifier("AGENT", 0, 0);
            List<String> behaviors = new ArrayList<>();
            List<String> environments = new ArrayList<>();
            AtomicReference<AgentInfo> info = new AtomicReference<>();
            assertDoesNotThrow(() -> info.set(new AgentInfo(agentIdentifier, behaviors, null, environments)));
            assertTrue(info.get().protocols().isEmpty());
        }
        
        @Test
        @DisplayName("Test if constructs an AgentInfo with null environments does not throws exception and the " +
                "environments list is empty")
        void testConstructorWithNullEnvironments() {
            AgentIdentifier agentIdentifier = new AgentIdentifier("AGENT", 0, 0);
            List<String> behaviors = new ArrayList<>();
            List<ProtocolIdentifier> protocols = new ArrayList<>();
            AtomicReference<AgentInfo> info = new AtomicReference<>();
            assertDoesNotThrow(() -> info.set(new AgentInfo(agentIdentifier, behaviors, protocols, null)));
            assertTrue(info.get().environments().isEmpty());
        }
        
        @Test
        @DisplayName("Test if constructs an AgentInfo with not null parameters does not throws exception")
        void testConstructorWithNotNullParameters() {
            AgentIdentifier agentIdentifier = new AgentIdentifier("AGENT", 0, 0);
            List<String> behaviors = new ArrayList<>();
            List<ProtocolIdentifier> protocols = new ArrayList<>();
            List<String> environments = new ArrayList<>();
            AtomicReference<AgentInfo> info = new AtomicReference<>();
            assertDoesNotThrow(() -> info.set(new AgentInfo(agentIdentifier, behaviors, protocols, environments)));
            assertEquals(agentIdentifier, info.get().agentIdentifier());
        }
        
    }
}
