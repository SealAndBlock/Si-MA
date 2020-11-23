package sima.core.scheduler.multithread;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import sima.core.scheduler.TestScheduler;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public abstract class TestMultiThreadScheduler extends TestScheduler {

    // Variables.

    protected int NB_EXECUTOR_THREADS = 5;

    // Setup.

    @BeforeEach
    @Override
    public void setup() {
        super.setup();

        assertTrue(NB_EXECUTOR_THREADS > 0, "NB_EXECUTOR_THREADS cannot be less or equal to 0");
    }
}
