package sima.core.simulation;

import java.util.Random;

public class SimaSimulationUtils {
    
    // Static.
    
    private static Random random = createRandom();
    
    // Constructors.
    
    private SimaSimulationUtils() {
    }
    
    // Methods.
    
    private static Random createRandom() {
        return new Random();
    }
    
    /**
     * Reset the Random.
     */
    public static void resetRandom() {
        random = createRandom();
    }
    
    /**
     * Set the seed to the Random.
     *
     * @param seed the seed
     */
    public static void setRandomSeed(long seed) {
        random.setSeed(seed);
    }
    
    /**
     * Compute a random value in the interval [min;max[. The random computation is done with {@link #random}.
     *
     * <strong>WARNING!</strong> only works with positive value
     *
     * @param min the min value (include)
     * @param max the max value (exclude)
     *
     * @return a random value in the interval [min;max[
     */
    public static long randomLong(long min, long max) {
        verifyBounds(min, max);
        
        if (max == min)
            return min;
        
        return min + (Math.abs(randomLong()) % (max - min));
    }
    
    /**
     * @return a random value of long.
     *
     * @see Random#nextLong()
     */
    public static long randomLong() {
        return random.nextLong();
    }
    
    /**
     * Compute a random value in the interval [min;max[. The random computation is done with {@link #random}.
     *
     * <strong>WARNING!</strong> only works with positive value
     *
     * @param min the min value (include)
     * @param max the max value (exclude)
     *
     * @return a random value in the interval [min;max[
     */
    public static int randomInt(int min, int max) {
        verifyBounds(min, max);
        
        if (max == min)
            return min;
        
        return min + (Math.abs(randomInt()) % (max - min));
    }
    
    private static void verifyBounds(long min, long max) {
        if (max < min)
            throw new IllegalArgumentException("Max bound cannot be less than min bound");
    }
    
    /**
     * @return a random value of int.
     *
     * @see Random#nextInt()
     */
    public static int randomInt() {
        return random.nextInt();
    }
    
}
