package sima.core.simulation;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSimaSimulationUtils extends SimaTest {

    // Methods.

    @Override
    protected void verifyAndSetup() {
    }

    // Tests.

    @Test
    public void randomLongWithBoundAlwaysReturnsValueInDefineInterval() {
        Random r = new Random();
        long min;
        long max;

        do {
            min = Math.abs(r.nextLong());
            max = Math.abs(r.nextLong());
        } while (min > max);


        for (int i = 0; i < 1_000_000; i++) {
            long randomLong = SimaSimulationUtils.randomLong(min, max);
            assertTrue(min <= randomLong && randomLong <= max);
        }
    }

    @Test
    public void randomLongWithBoundAlwaysReturnsAlwaysMinIfMaxAndMinAreEquals() {
        Random r = new Random();
        long min = Math.abs(r.nextLong());

        for (int i = 0; i < 1_000_000; i++) {
            long randomLong = SimaSimulationUtils.randomLong(min, min);
            assertEquals(randomLong, min);
        }
    }

    @Test
    public void randomIntWithBoundAlwaysReturnsValueInDefineInterval() {
        Random r = new Random();
        int min;
        int max;

        do {
            min = Math.abs(r.nextInt());
            max = Math.abs(r.nextInt());
        } while (min > max);


        for (int i = 0; i < 1_000_000; i++) {
            long randomInt = SimaSimulationUtils.randomInt(min, max);
            assertTrue(min <= randomInt && randomInt <= max);
        }
    }

    @Test
    public void randomIntWithBoundAlwaysReturnsAlwaysMinIfMaxAndMinAreEquals() {
        Random r = new Random();
        int min = Math.abs(r.nextInt());

        for (int i = 0; i < 1_000_000; i++) {
            int randomInt = SimaSimulationUtils.randomInt(min, min);
            assertEquals(randomInt, min);
        }
    }
}
