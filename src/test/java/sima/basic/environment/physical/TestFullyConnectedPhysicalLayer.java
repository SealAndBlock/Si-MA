package sima.basic.environment.physical;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.environment.Environment;
import sima.core.environment.physical.TestPhysicalConnectionLayer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TestFullyConnectedPhysicalLayer extends TestPhysicalConnectionLayer {
    
    // Variables.
    
    protected FullyConnectedPhysicalLayer fullyConnectedPhysicalLayer;
    
    @Mock
    private Environment mockEnvironment;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment, createMapArgs("10", "15"));
        physicalConnectionLayer = fullyConnectedPhysicalLayer;
    }
    
    // Tests.
    
    @Nested
    @Tag("FullyConnectedPhysicalLayer.constructor")
    @DisplayName("FullyConnectedPhysicalLayer constructor tests")
    class ConstructorTest {
        
        @Test
        @DisplayName("Test if constructor throws a IllegalArgumentException with null environment")
        void testConstructorWithNullName() {
            Map<String, String> args = new HashMap<>();
            assertThrows(IllegalArgumentException.class, () -> new FullyConnectedPhysicalLayer(null, args));
        }
        
        @Test
        @DisplayName("Test if constructor with null args map set the min and max delays to default values")
        void testConstructorWithNullArgsMap() {
            FullyConnectedPhysicalLayer fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment, null);
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY, fullyConnectedPhysicalLayer.getMinSendDelay());
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY, fullyConnectedPhysicalLayer.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with miss args delay set the missing delay to default value")
        void testConstructorWithMissingDelayArgs() {
            FullyConnectedPhysicalLayer fullyConnectedPhysicalLayer;
            
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment, createMapArgs(null,
                    String.valueOf(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY)));
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY, fullyConnectedPhysicalLayer.getMinSendDelay());
            
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment,
                    createMapArgs(String.valueOf(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY), null));
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY, fullyConnectedPhysicalLayer.getMaxSendDelay());
            
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment, createMapArgs(null, null));
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY, fullyConnectedPhysicalLayer.getMinSendDelay());
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY, fullyConnectedPhysicalLayer.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with min or max delay string args has not number format then set the wrong args to default value")
        void testConstructorWithArgsWithNotNumberFormat() {
            FullyConnectedPhysicalLayer fullyConnectedPhysicalLayer;
            
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment, createMapArgs("WRONG",
                    String.valueOf(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY)));
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY, fullyConnectedPhysicalLayer.getMinSendDelay());
            
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment,
                    createMapArgs(String.valueOf(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY), "WRONG"));
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY, fullyConnectedPhysicalLayer.getMaxSendDelay());
            
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment, createMapArgs("WRONG", "WRONG"));
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY, fullyConnectedPhysicalLayer.getMinSendDelay());
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY, fullyConnectedPhysicalLayer.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with negative min and max delay set them to default values")
        void testConstructorWithLessThanOneDelay() {
            var fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment, createMapArgs("0", "0"));
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY, fullyConnectedPhysicalLayer.getMinSendDelay());
            assertEquals(FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY, fullyConnectedPhysicalLayer.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor set a min delay always less or equals to max delay in any case")
        void testConstructorAlwaysSetMinDelayLessOrEqualToMaxDelay() {
            FullyConnectedPhysicalLayer fullyConnectedPhysicalLayer;
            long minSendDelay;
            long maxSendDelay;
            
            // WRONG min delay and max delay less than default min delay
            maxSendDelay = FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY - 1;
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment,
                    createMapArgs("WRONG", String.valueOf(maxSendDelay)));
            assertTrue(fullyConnectedPhysicalLayer.getMinSendDelay() <= fullyConnectedPhysicalLayer.getMaxSendDelay());
            
            // WRONG max delay and min delay greater than default max delay
            minSendDelay = FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY + 1;
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment,
                    createMapArgs(String.valueOf(minSendDelay), "WRONG"));
            assertTrue(fullyConnectedPhysicalLayer.getMinSendDelay() <= fullyConnectedPhysicalLayer.getMaxSendDelay());
            
            // Set correct format but min > max
            minSendDelay = FullyConnectedPhysicalLayer.DEFAULT_MIN_SEND_DELAY;
            maxSendDelay = FullyConnectedPhysicalLayer.DEFAULT_MAX_SEND_DELAY;
            fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment,
                    createMapArgs(String.valueOf(maxSendDelay), String.valueOf(minSendDelay)));
            assertTrue(fullyConnectedPhysicalLayer.getMinSendDelay() <= fullyConnectedPhysicalLayer.getMaxSendDelay());
        }
        
        @Test
        @DisplayName("Test if constructor with correct min and max args set correct values")
        void testConstructorWithCorrectMapArgs() {
            long minSendDelay = 5;
            long maxSendDelay = 10;
            var fullyConnectedPhysicalLayer = new FullyConnectedPhysicalLayer(mockEnvironment,
                    createMapArgs(String.valueOf(minSendDelay), String.valueOf(maxSendDelay)));
            assertEquals(minSendDelay, fullyConnectedPhysicalLayer.getMinSendDelay());
            assertEquals(maxSendDelay, fullyConnectedPhysicalLayer.getMaxSendDelay());
        }
        
    }
    
    private Map<String, String> createMapArgs(String minSendDelay, String maxSendDelay) {
        Map<String, String> args = new HashMap<>();
        if (minSendDelay != null)
            args.put(FullyConnectedPhysicalLayer.MIN_SEND_DELAY_ARGS, minSendDelay);
        
        if (maxSendDelay != null)
            args.put(FullyConnectedPhysicalLayer.MAX_SEND_DELAY_ARGS, maxSendDelay);
        
        return args;
    }
    
}
