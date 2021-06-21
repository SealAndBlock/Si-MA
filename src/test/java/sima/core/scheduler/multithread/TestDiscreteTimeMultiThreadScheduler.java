package sima.core.scheduler.multithread;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.scheduler.Scheduler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestDiscreteTimeMultiThreadScheduler extends TestMultiThreadScheduler {
    
    // Variables.
    
    protected DiscreteTimeMultiThreadScheduler discreteTimeMultiThreadScheduler;
    
    // Init.
    
    @BeforeEach
    @Override
    protected void setUp() {
        discreteTimeMultiThreadScheduler = new DiscreteTimeMultiThreadScheduler(1492L, 8);
        multiThreadScheduler = discreteTimeMultiThreadScheduler;
        super.setUp();
    }
    
    // Tests.
    
    @Nested
    @Tag("DiscreteTimeMultiThreadScheduler.constructor")
    @DisplayName("DiscreteTimeMultiThreadScheduler constructors tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws an IllegalArgumentException if the endSimulation is less than 1")
        void testConstructorWithEndSimulationLessThanOne() {
            assertThrows(IllegalArgumentException.class, () -> new DiscreteTimeMultiThreadScheduler(0, 1));
        }
        
        @Test
        @DisplayName("Test if constructor throws an IllegalArgumentException if the nbExecutorThread is less than 1")
        void testConstructorWithNbExecutorThreadLessThanOne() {
            assertThrows(IllegalArgumentException.class, () -> new DiscreteTimeMultiThreadScheduler(1, 0));
        }
        
        @Test
        @DisplayName("Test if constructor does not throw exception with correct arguments")
        void testConstructorWithCorrectArguments() {
            assertDoesNotThrow(() -> new DiscreteTimeMultiThreadScheduler(1, 1));
        }
    }
    
    @Nested
    @Tag("DiscreteTimeMultiThreadScheduler.getTimeMode")
    @DisplayName("DiscreteTimeMultiThreadScheduler getTimeMode tests")
    class GetTimeModeTest {
        
        @Test
        @DisplayName("Test if getTimeMode returns DISCRETE_TIME")
        void testGetTimeMode() {
            Scheduler.TimeMode timeMode = discreteTimeMultiThreadScheduler.getTimeMode();
            assertThat(timeMode).isEqualTo(Scheduler.TimeMode.DISCRETE_TIME);
        }
        
    }
    
}
