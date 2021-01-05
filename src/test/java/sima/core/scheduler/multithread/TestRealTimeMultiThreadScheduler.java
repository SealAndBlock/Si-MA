package sima.core.scheduler.multithread;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestRealTimeMultiThreadScheduler extends GlobalTestMultiThreadScheduler {

    // Static.

    protected static RealTimeMultiThreadScheduler REAL_TIME_MULTI_THREAD_SCHEDULER;

    // Setup.

    @Override
    protected void verifyAndSetup() {
        END_SIMULATION = 1_000;
        NB_EXECUTOR_THREADS = 10;
        REAL_TIME_MULTI_THREAD_SCHEDULER = new RealTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREADS);
        MULTI_THREAD_SCHEDULER = REAL_TIME_MULTI_THREAD_SCHEDULER;
        TIME_EXECUTION_TOLERANCE = 160; // ms
        NB_EXECUTION_TOLERANCE = 15;

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructorThrowsExceptionIfEndSimulationIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(0, NB_EXECUTOR_THREADS));
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(-1, NB_EXECUTOR_THREADS));
    }

    @Test
    public void constructorThrowsExceptionIfNbExecutorThreadIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(END_SIMULATION, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(END_SIMULATION, -1));
    }
}
