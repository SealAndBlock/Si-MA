package sima.core.environment.physical.coverage;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.environment.event.Event;
import sima.core.environment.event.transport.TransportableInEvent;
import sima.core.environment.physical.PhysicalEvent;
import sima.core.environment.physical.TestPhysicalEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class TestPhysicalEventCoverage extends TestPhysicalEvent {
    
    // Variables.
    
    @Mock
    private TransportableInEvent mockEvenTransportable;
    
    // Init.
    
    @BeforeEach
    @Override
    public void setUp() {
        physicalEvent = new PhysicalEventCoverage(mockEvenTransportable);
        super.setUp();
    }
    
    // Tests.
    
    @Nested
    @Tag("PhysicalEventCoverage.constructor")
    @DisplayName("PhysicalEventCoverage constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test constructor does not throw exception with null args")
        void testConstructorWithNullArgs() {
            assertDoesNotThrow(() -> new PhysicalEventCoverage((TransportableInEvent) null));
        }
        
        @Test
        @DisplayName("Test constructor does not throw exception with null args")
        void testConstructorWithNotNullArgs() {
            assertDoesNotThrow(() -> new PhysicalEventCoverage(mockEvenTransportable));
        }
        
    }
    
    // Inner classes.
    
    static class PhysicalEventCoverage extends PhysicalEvent {
        
        // Constructors.
        
        public PhysicalEventCoverage(TransportableInEvent content) {
            super(content);
        }
        
        private PhysicalEventCoverage(PhysicalEventCoverage other) {
            this(other.getContent().duplicate());
        }
        
        // Methods.
        
        @Override
        public @NotNull Event duplicate() {
            return new PhysicalEventCoverage(this);
        }
    }
}
