package sima.core.protocol.event;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.environment.event.TestEvent;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public abstract class TestProtocolEvent extends TestEvent {
    
    // Variables.
    
    protected ProtocolEvent protocolEvent;
    
    // Init.
    
    @BeforeEach
    public void setUp() {
        event = protocolEvent;
    }
    
    // Tests.
    
    @Nested
    @Tag("ProtocolEvent.getIntendedProtocol")
    @DisplayName("ProtocolEvent getIntendedProtocol tests")
    class GetIntendedProtocolTest {
        
        @Test
        @DisplayName("Test if getIntendedProtocol never returns null")
        void testGetIntendedProtocolNeverReturnsNull() {
            assertThat(protocolEvent.getIntendedProtocol()).isNotNull();
        }
        
    }
}
