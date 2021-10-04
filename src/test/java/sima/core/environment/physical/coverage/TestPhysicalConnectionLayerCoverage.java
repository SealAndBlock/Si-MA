package sima.core.environment.physical.coverage;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.Environment;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.environment.physical.PhysicalEvent;
import sima.core.environment.physical.TestPhysicalConnectionLayer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestPhysicalConnectionLayerCoverage extends TestPhysicalConnectionLayer {

    // Variables.

    @Mock
    private Environment mockEnvironment;

    // Inits.

    @BeforeEach
    void setUp() {
        physicalConnectionLayer = new PhysicalConnectionLayerCoverage(mockEnvironment);
    }

    // Tests.

    @Nested
    @Tag("PhysicalConnectionLayerCoverage.constructor")
    @DisplayName("PhysicalConnectionLayerCoverage constructor tests")
    class ConstructorTest {

        @Test
        @DisplayName("Test if constructor throws NullPointerException with null environment")
        void testConstructorWithNullEnvironment() {
            assertThrows(IllegalArgumentException.class, () -> new PhysicalConnectionLayerCoverage(null));
        }

        @Test
        @DisplayName("Test if constructor does not throw exception with not null environment")
        void testConstructorWithNotNullEnvironment() {
            assertDoesNotThrow(() -> new PhysicalConnectionLayerCoverage(mockEnvironment));
        }

    }

    // Inner classes.

    static class PhysicalConnectionLayerCoverage extends PhysicalConnectionLayer {

        // Constructors.

        protected PhysicalConnectionLayerCoverage(Environment environment) {
            super(environment, null);
        }

        // Methods.

        @Override
        protected @NotNull PhysicalEvent decoratePhysicalEvent(PhysicalEvent physicalEvent) {
            return physicalEvent;
        }

        @Override
        public boolean hasPhysicalConnection(AgentIdentifier a1, AgentIdentifier a2) {
            if (a1 == null || a2 == null)
                throw new IllegalArgumentException();
            return true;
        }

        @Override
        protected boolean canBeSent(AgentIdentifier initiator, AgentIdentifier target, PhysicalEvent physicalEvent) {
            if (initiator == null || target == null || physicalEvent == null)
                throw new NullPointerException();
            return true;
        }

        @Override
        protected void scheduleInEnvironment(AgentIdentifier initiator, AgentIdentifier target, PhysicalEvent physicalEvent) {
            if (initiator == null || target == null || physicalEvent == null)
                throw new NullPointerException();
        }
    }

}
