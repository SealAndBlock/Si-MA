package sima.core.environment.event;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.AgentIdentifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public abstract class TestEvent {
    
    // Variables.
    
    protected Event event;
    
    @Mock
    private AgentIdentifier mockReceiver;
    
    @Mock
    private Transportable mockTransportable;
    
    // Init.
    
    // Tests.
    
    @Nested
    @Tag("Event.duplicate")
    @DisplayName("Event duplicate tests")
    class DuplicateTest {
        
        @Test
        @DisplayName("Test if duplicate returns a new instance of Event equals to the base Event")
        void testDuplicate() {
            var duplicate = event.duplicate();
            assertNotSame(event, duplicate);
            assertEquals(event, duplicate);
        }
        
    }
    
    @Nested
    @Tag("Event.duplicateWithNewReceiver")
    @DisplayName("Event duplicateWithNewReceiver tests")
    public class DuplicateWithNewReceiverTest {
        
        @Test
        @DisplayName("Test if the method duplicateWithNewReceiver returns an event with same value than the base event except for the receiver")
        public void testDuplicateWithNewReceiver() {
            var duplicatedEvent = event.duplicateWithNewReceiver(mockReceiver);
            assertSame(duplicatedEvent.getSender(), event.getSender());
            assertNotSame(duplicatedEvent.getReceiver(), event.getReceiver());
            assertEquals(mockReceiver, duplicatedEvent.getReceiver());
            assertSame(duplicatedEvent.getProtocolTargeted(), event.getProtocolTargeted());
        }
        
    }
    
    @Nested
    @Tag("Event.isProtocolEvent")
    @DisplayName("Event isProtocolEvent tests")
    public class IsProtocolEventTest {
        
        @Test
        @DisplayName("Test if isProtocolEvent methods returns the correct value in function of if the protocolTargeted is null or not")
        void testIsProtocolEvent() {
            boolean expectedIsProtocolEvent = event.getProtocolTargeted() != null;
            boolean isProtocolEvent = event.isProtocolEvent();
            assertEquals(expectedIsProtocolEvent, isProtocolEvent);
        }
        
    }
}
