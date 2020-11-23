package sima.core.scheduler.multithread;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestDiscreteTimeMultiThreadScheduler extends TestMultiThreadScheduler {

    // Static.

    protected static DiscreteTimeMultiThreadScheduler DISCRETE_TIME_MULTI_THREAD_SCHEDULER;

    // Setup.

    @Override
    protected void verifyAndSetup() {
        END_SIMULATION = 1_000;
        NB_EXECUTOR_THREADS = 5;
        DISCRETE_TIME_MULTI_THREAD_SCHEDULER = new DiscreteTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREADS);
        MULTI_THREAD_SCHEDULER = DISCRETE_TIME_MULTI_THREAD_SCHEDULER;
        TIME_EXECUTION_TOLERANCE = 0;

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructorThrowsExceptionIfEndSimulationIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscreteTimeMultiThreadScheduler(0, NB_EXECUTOR_THREADS));
        assertThrows(IllegalArgumentException.class,
                () -> new DiscreteTimeMultiThreadScheduler(-1, NB_EXECUTOR_THREADS));
    }

    @Test
    public void constructorThrowsExceptionIfNbExecutorThreadIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new DiscreteTimeMultiThreadScheduler(END_SIMULATION, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new DiscreteTimeMultiThreadScheduler(END_SIMULATION, -1));
    }
}
