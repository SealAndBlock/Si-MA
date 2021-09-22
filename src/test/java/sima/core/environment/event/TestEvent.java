package sima.core.environment.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotSame;

public abstract class TestEvent {
    
    // Variables.
    
    protected Event event;
    
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
        }
        
    }
}
