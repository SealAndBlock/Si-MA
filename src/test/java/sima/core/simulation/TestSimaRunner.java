package sima.core.simulation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sima.core.exception.SimaSimulationFailToStartRunningException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimaRunner {
    
    //Constants.
    
    static final String PREFIX_CONFIG_PATH = "src/test/resources/config/";
    
    // Tests.
    
    @Nested
    @Tag("SimaRunner.main")
    @DisplayName("SimaRunner main tests")
    class MainTest {
        
        @Test
        @DisplayName("Test if the simulation is started with no arguments, no simulation is started and throws an IllegalArgumentException")
        void testMainWithNoArgument() {
            assertThrows(IllegalArgumentException.class, () -> SimaRunner.main(new String[]{}));
        }
        
        @Test
        @DisplayName("Test if the simulation is started with wrong config pass, throws a SimaSimulationFailToStartRunningException")
        void testMainWithWrongConfigPathArg() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaRunner.main(new String[]{"WrongPath"}));
        }
        
        @Test
        @DisplayName("Test if the simulation is started with wrong config, throws a SimaSimulationFailToStartRunningException")
        void testMainWithWrongConfigArg() {
            assertThrows(SimaSimulationFailToStartRunningException.class, () -> SimaRunner.main(new String[]{PREFIX_CONFIG_PATH +
                    "configJsonSyntaxError.json"}));
        }
        
        @Test
        @DisplayName("Test if the simulation is started with correct config, the simulation is started and no exception are thrown")
        void testMainWithCorrectConfigArg() {
            assertDoesNotThrow(() -> SimaRunner.main(new String[]{PREFIX_CONFIG_PATH + "correctConfig.json"}));
        }
    }
    
}
