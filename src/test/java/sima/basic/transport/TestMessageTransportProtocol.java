package sima.basic.transport;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.protocol.TestProtocol;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

public abstract class TestMessageTransportProtocol extends TestProtocol {
    
    // Variables.
    
    protected MessageTransportProtocol messageTransportProtocol;
    
    @Mock
    private Environment mockEnvironment;
    
    @Mock
    private Environment mockEnvironmentOther;
    
    @Mock
    private Event mockEvent;
    
    @Mock
    private AgentIdentifier mockAgent;
    
    // Inits.
    
    @BeforeEach
    public void setUp() {
        protocol = messageTransportProtocol;
    }
    
    // Tests.
    
    @Nested
    @Tag("MessageTransportProtocol.processEvent")
    @DisplayName("MessageTransportProtocol processEvent tests")
    public class ProcessEventTest {
        
        @Test
        @DisplayName("Test if processEvent throw an UnsupportedOperationException if the event is not a PhysicalMessageReceptionEvent")
        void testProcessEventWithOtherEvent() {
            assertThrows(UnsupportedOperationException.class, () -> messageTransportProtocol.processEvent(mockEvent));
        }
    }
    
    @Nested
    @Tag("MessageTransportProtocol.getEnvironment")
    @DisplayName("MessageTransportProtocol getEnvironment tests")
    class GetEnvironmentTest {
        
        @Test
        @DisplayName("Test if the method getEnvironment returns null if the environment field has not been set")
        void testGetEnvironmentWithNotSetEnvironment() {
            assertThat(messageTransportProtocol.getEnvironment()).isNull();
        }
        
        
        @Test
        @DisplayName("Test if the method getEnvironment return not null if the environment field has been set")
        void testGetEnvironmentWithSetEnvironment() {
            messageTransportProtocol.setEnvironment(mockEnvironment);
            assertThat(messageTransportProtocol.getEnvironment()).isNotNull();
        }
    }
    
    @Nested
    @Tag("MessageTransportProtocol.setEnvironment")
    @DisplayName("MessageTransportProtocol setEnvironment tests")
    class SetEnvironmentTest {
        
        @Test
        @DisplayName("Test if the method setEnvironment set the environment if it is the first time set")
        void testSetEnvironmentFirstTimeSet() {
            messageTransportProtocol.setEnvironment(mockEnvironment);
            assertThat(messageTransportProtocol.getEnvironment()).isEqualTo(mockEnvironment);
        }
        
        @Test
        @DisplayName("Test if the method setEnvironment does not set any value until the first set is a make with a null value")
        void testSetEnvironmentWithFirstTimeSetNull() {
            messageTransportProtocol.setEnvironment(null);
            messageTransportProtocol.setEnvironment(mockEnvironment);
            assertThat(messageTransportProtocol.getEnvironment()).isEqualTo(mockEnvironment);
        }
        
        @Test
        @DisplayName("Test if the method setEnvironment does not set an other value after that the environment has alreay been set")
        void testSetEnvironmentWithSecondSet() {
            messageTransportProtocol.setEnvironment(mockEnvironment);
            messageTransportProtocol.setEnvironment(mockEnvironmentOther);
            assertThat(messageTransportProtocol.getEnvironment()).isEqualTo(mockEnvironment);
        }
        
    }
}
