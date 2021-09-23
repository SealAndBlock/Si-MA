package sima.core.environment.physical;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import sima.core.agent.AgentIdentifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class TestPhysicalConnectionLayer {
    
    // Variables.
    
    protected PhysicalConnectionLayer physicalConnectionLayer;
    
    @Mock
    private AgentIdentifier mockAgentInitiator;
    
    @Mock
    private AgentIdentifier mockAgentTarget;
    
    @Mock
    private PhysicalEvent mockPhysicalEvent;
    
    @Mock
    private PhysicalConnectionLayer mockNext;
    
    // Tests.
    
    @Nested
    @Tag("PhysicalConnectionLayer.send")
    @DisplayName("PhysicalConnectionLayer send tests")
    class SendTest {
        
        @Test
        @DisplayName("Test if the method send throws NullPointerException if the initiator, the target or the physicalEvent is null")
        void testSendWithNullArgs() {
            assertThrows(NullPointerException.class, () -> physicalConnectionLayer.send(null, mockAgentTarget, mockPhysicalEvent));
            assertThrows(NullPointerException.class, () -> physicalConnectionLayer.send(mockAgentInitiator, null, mockPhysicalEvent));
            assertThrows(NullPointerException.class, () -> physicalConnectionLayer.send(mockAgentInitiator, mockAgentTarget, null));
        }
        
        @Test
        @DisplayName("Test if the method send does not throw exception with not null args")
        void testSendWithNotNullArgs() {
            assertDoesNotThrow(() -> physicalConnectionLayer.send(mockAgentInitiator, mockAgentTarget, mockPhysicalEvent));
        }
        
    }
    
    @Nested
    @Tag("PhysicalConnectionLayer.hasPhysicalConnection")
    @DisplayName("PhysicalConnectionLayer hasPhysicalConnection tests")
    class HasPhysicalConnectionTest {
        
        @Test
        @DisplayName("Test if hasPhysicalConnection throws NullPointerException if a1 or a2 is null")
        void testHasPhysicalConnectionWithNullArgs() {
            assertThrows(NullPointerException.class, () -> physicalConnectionLayer.hasPhysicalConnection(null, mockAgentTarget));
            assertThrows(NullPointerException.class, () -> physicalConnectionLayer.hasPhysicalConnection(mockAgentInitiator, null));
        }
        
    }
    
    @Nested
    @Tag("PhysicalConnectionLayer.getEnvironment")
    @DisplayName("PhysicalConnectionLayer getEnvironment tests")
    class GetEnvironmentTest {
        
        @Test
        @DisplayName("Test if getEnvironment never returns null")
        void testGetEnvironmentNeverReturnsNull() {
            assertThat(physicalConnectionLayer.getEnvironment()).isNotNull();
        }
        
    }
    
    @Nested
    @Tag("PhysicalConnectionLayer.setNext")
    @DisplayName("PhysicalConnectionLayer setNext tests")
    class SetNextTest {
        
        @Test
        @DisplayName("Test if setNext set the new next")
        void testSetTest() {
            physicalConnectionLayer.setNext(mockNext);
            assertThat(physicalConnectionLayer.getNext()).isSameAs(mockNext);
        }
        
    }
}