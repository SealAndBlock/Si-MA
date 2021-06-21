package sima.core.simulation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimaSimulationUtils {
    
    // Variables.
    
    private final long seed = 90;
    private final int repetitions = 150;
    
    // Tests.
    
    @Nested
    @Tag("SimaSimulationUtils.resetRandom")
    @DisplayName("SimaSimulationUtils resetRandom tests")
    class ResetRandomTest {
        
        @Test
        @DisplayName("Test if reset random reset correctly the random")
        void testResetRandom() {
            List<Integer> firstGeneration = new ArrayList<>();
            List<Integer> SecondGeneration = new ArrayList<>();
            
            SimaSimulationUtils.setRandomSeed(seed);
            for (int i = 0; i < repetitions; i++) {
                firstGeneration.add(SimaSimulationUtils.randomInt());
            }
            
            SimaSimulationUtils.resetRandom();
            SimaSimulationUtils.setRandomSeed(seed);
            for (int i = 0; i < repetitions; i++) {
                SecondGeneration.add(SimaSimulationUtils.randomInt());
            }
            
            assertThat(SecondGeneration).containsExactlyElementsOf(firstGeneration);
        }
        
    }
    
    @Nested
    @Tag("SimaSimulationUtils.setRandomSeed")
    @DisplayName("SimaSimulationUtils setRandomSeed test")
    class SetRandomSeedTest {
        
        @Test
        @DisplayName("Test if setRandomSeed set a new seed for the random and the generation of random is the same that a java.util.Random " +
                             "generate")
        void testSetRandomSeed() {
            List<Integer> expectedGeneration = new ArrayList<>();
            List<Integer> generation = new ArrayList<>();
            
            Random r = new Random(seed);
            for (int i = 0; i < repetitions; i++) {
                expectedGeneration.add(r.nextInt());
            }
            
            SimaSimulationUtils.setRandomSeed(seed);
            for (int i = 0; i < repetitions; i++) {
                generation.add(SimaSimulationUtils.randomInt());
            }
            
            assertThat(generation).containsExactlyElementsOf(expectedGeneration);
        }
        
    }
    
    @Nested
    @Tag("SimaSimulationUtils.randomLong")
    @DisplayName("SimaSimulationUtils randomLong tests")
    class RandomLongTest {
        
        @Nested
        @Tag("SimaSimulationUtils.randomLong(long, long)")
        @DisplayName("SimaSimulationUtils randomLong(long, long) tests")
        class RandomLongTwoArgsTest {
            
            @Test
            @DisplayName("Test if randomLong(long, long) throws an IllegalArgumentException if max < min")
            void testRandomLongWithMaxLessThanMin() {
                int min = 90;
                int max = 5;
                assertThrows(IllegalArgumentException.class, () -> SimaSimulationUtils.randomLong(min, max));
            }
            
            @Test
            @DisplayName("Test if randomLong(long, long) generates random long which is in the bound")
            void testRandomLong() {
                int min = 10;
                int max = 10;
                
                // Same bounds
                long r0 = SimaSimulationUtils.randomLong(min, max);
                assertThat(r0).isEqualTo(min);
                
                // Different positives bounds.
                min = 15;
                max = 25;
                long r1 = SimaSimulationUtils.randomLong(min, max);
                assertThat(r1).isGreaterThanOrEqualTo(min).isLessThanOrEqualTo(max);
                
                // Different negatives bounds.
                min = -55;
                max = -35;
                long r2 = SimaSimulationUtils.randomLong(min, max);
                assertThat(r2).isGreaterThanOrEqualTo(min).isLessThanOrEqualTo(max);
                
                // Different bounds with negative and positive.
                min = -5;
                max = 90;
                long r3 = SimaSimulationUtils.randomLong(min, max);
                assertThat(r3).isGreaterThanOrEqualTo(min).isLessThanOrEqualTo(max);
                
            }
            
        }
        
        @Nested
        @Tag("SimaSimulationUtils.randomLong()")
        @DisplayName("SimaSimulationUtils randomLong() tests")
        class RandomLongNoArgsTest {
            
            @Test
            @DisplayName("Test if randomLong(l) does not throws exception")
            void testRandomLong() {
                assertDoesNotThrow((ThrowingSupplier<Long>) SimaSimulationUtils::randomLong);
            }
            
        }
        
    }
    
    @Nested
    @Tag("SimaSimulationUtils.randomInt")
    @DisplayName("SimaSimulationUtils randomInt tests")
    class RandomIntTest {
        
        @Nested
        @Tag("SimaSimulationUtils.randomInt(long, long)")
        @DisplayName("SimaSimulationUtils randomInt(long, long) tests")
        class RandomIntTwoArgsTest {
            
            @Test
            @DisplayName("Test if randomLong(long, long) throws an IllegalArgumentException if max < min")
            void testRandomIntWithMaxLessThanMin() {
                int min = 90;
                int max = 5;
                assertThrows(IllegalArgumentException.class, () -> SimaSimulationUtils.randomInt(min, max));
            }
            
            @Test
            @DisplayName("Test if randomLong(long, long) generates random long which is in the bound")
            void testRandomInt() {
                int min = 10;
                int max = 10;
                
                // Same bounds
                long r0 = SimaSimulationUtils.randomInt(min, max);
                assertThat(r0).isEqualTo(min);
                
                // Different positives bounds.
                min = 15;
                max = 25;
                long r1 = SimaSimulationUtils.randomInt(min, max);
                assertThat(r1).isGreaterThanOrEqualTo(min).isLessThanOrEqualTo(max);
                
                // Different negatives bounds.
                min = -55;
                max = -35;
                long r2 = SimaSimulationUtils.randomInt(min, max);
                assertThat(r2).isGreaterThanOrEqualTo(min).isLessThanOrEqualTo(max);
                
                // Different bounds with negative and positive.
                min = -5;
                max = 90;
                long r3 = SimaSimulationUtils.randomInt(min, max);
                assertThat(r3).isGreaterThanOrEqualTo(min).isLessThanOrEqualTo(max);
                
            }
            
        }
        
        @Nested
        @Tag("SimaSimulationUtils.randomInt()")
        @DisplayName("SimaSimulationUtils randomInt() tests")
        class RandomIntNoArgsTest {
            
            @Test
            @DisplayName("Test if randomLong(l) does not throws exception")
            void testRandomInt() {
                assertDoesNotThrow((ThrowingSupplier<Long>) SimaSimulationUtils::randomLong);
            }
            
        }
        
    }
}
