package sima.standard.transport;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import sima.standard.environment.message.Message;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.event.Event;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.exception.NoPhysicalConnectionLayerFoundException;
import sima.core.protocol.TestProtocol;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    private AgentIdentifier mockTarget;
    
    @Mock
    private Message mockMessage;
    
    @Mock
    private PhysicalConnectionLayer mockPhysicalLayer;
    
    // Inits.
    
    @BeforeEach
    public void setUp() {
        protocol = messageTransportProtocol;
    }
    
    // Tests.
    
    @Nested
    @Tag("MessageTransportProtocol.send")
    @DisplayName("MessageTransportProtocol send tests")
    class SendTest {
        
        @Test
        @DisplayName("Test if send throws IllegalArgumentException if the target is null")
        void testSendWithNullTarget() {
            assertThrows(IllegalArgumentException.class, () -> messageTransportProtocol.send(null, mockMessage));
        }
        
        @Test
        @DisplayName("Test if send does not throw Exception with null message")
        void testSendWithNullMessage() {
            // WHEN
            sendMockConfig(mockPhysicalLayer);
    
            // GIVEN
            assertDoesNotThrow(() -> messageTransportProtocol.send(mockTarget, null));
        }
        
        @Test
        @DisplayName("Test if send throws NoPhysicalConnectionLayerFoundException if the Environment does not have the physicalLayer")
        void testSendWithPhysicalLayerNotFound() {
            // WHEN
            sendMockConfig(null);
    
            // GIVEN
            assertThrows(NoPhysicalConnectionLayerFoundException.class, () -> messageTransportProtocol.send(mockTarget, mockMessage));
        }
        
        @Test
        @DisplayName("Test if send does not throw Exception if the Environment have the physicalLayer")
        void testSendWithPhysicalLayerFound() {
            // WHEN
            sendMockConfig(mockPhysicalLayer);
    
            // GIVEN
            assertDoesNotThrow(() -> messageTransportProtocol.send(mockTarget, mockMessage));
        }
    
        private void sendMockConfig(PhysicalConnectionLayer mockPhysicalLayer) {
            messageTransportProtocol.setEnvironment(mockEnvironment);
            when(mockEnvironment.getPhysicalConnectionLayer(any(String.class))).thenReturn(mockPhysicalLayer);
        }
    }
    
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
        @DisplayName("Test if the method setEnvironment does not set an other value after that the environment has already been set")
        void testSetEnvironmentWithSecondSet() {
            messageTransportProtocol.setEnvironment(mockEnvironment);
            messageTransportProtocol.setEnvironment(mockEnvironmentOther);
            assertThat(messageTransportProtocol.getEnvironment()).isEqualTo(mockEnvironment);
        }
        
    }
}
