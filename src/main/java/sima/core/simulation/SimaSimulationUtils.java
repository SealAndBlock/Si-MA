package sima.core.simulation;

import java.security.SecureRandom;
import java.util.Random;

public class SimaSimulationUtils {

    // Static.

    private static final Random RANDOM = new SecureRandom();

    // Constructors.

    private SimaSimulationUtils() {}

    // Methods.

    /**
     * Compute a random value in the interval [min;max[. The random computation is done with {@link #RANDOM}.
     *
     * <strong>WARNING!</strong> only works with positive value
     *
     * @param min the min value (include)
     * @param max the max value (exclude)
     * @return a random value in the interval [min;max[
     */
    public static long randomLong(long min, long max) {
        if (max - min == 0)
            return min;
        return min + (Math.abs(randomLong()) % (max - min));
    }

    /**
     * @return a random value of long.
     * @see Random#nextLong()
     */
    public static long randomLong() {
        return RANDOM.nextLong();
    }

    /**
     * Compute a random value in the interval [min;max[. The random computation is done with {@link #RANDOM}.
     *
     * <strong>WARNING!</strong> only works with positive value
     *
     * @param min the min value (include)
     * @param max the max value (exclude)
     * @return a random value in the interval [min;max[
     */
    public static int randomInt(int min, int max) {
        if (max - min == 0)
            return min;
        return min + (Math.abs(randomInt()) % (max - min));
    }

    /**
     * @return a random value of int.
     * @see Random#nextInt()
     */
    public static int randomInt() {
        return RANDOM.nextInt();
    }

}
