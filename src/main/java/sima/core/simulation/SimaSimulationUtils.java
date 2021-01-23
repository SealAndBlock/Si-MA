package sima.core.simulation;

import java.util.Random;

public class SimaSimulationUtils {

    // Static.

    private static Random RANDOM;

    // Methods.

    /**
     * Compute a random value in the interval [min;max]. The random computation is done with {@link #RANDOM}.
     *
     * @param min the min value (include)
     * @param max the max value (also include)
     * @return a random value in the interval [min;max]
     */
    public static long randomLong(long min, long max) {
        return min + (Math.abs(randomLong()) % ((max + 1) - min));
    }

    /**
     * @return a random value of long.
     * @see Random#nextLong()
     */
    public static long randomLong() {
        return RANDOM.nextLong();
    }

    /**
     * Compute a random value in the interval [min;max]. The random computation is done with {@link #RANDOM}.
     *
     * @param min the min value (include)
     * @param max the max value (also include)
     * @return a random value in the interval [min;max]
     */
    public static int randomInt(int min, int max) {
        return min + (Math.abs(randomInt()) % ((max + 1) - min));
    }

    /**
     * @return a random value of int.
     * @see Random#nextInt()
     */
    public static int randomInt() {
        return RANDOM.nextInt();
    }

}
