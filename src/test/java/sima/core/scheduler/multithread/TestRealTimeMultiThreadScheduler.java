package sima.core.scheduler.multithread;

public class TestRealTimeMultiThreadScheduler extends TestMultiThreadScheduler {

    // Setup.

    @Override
    protected void verifyAndSetup() {
        END_SIMULATION = 1_000;
        NB_EXECUTOR_THREADS = 5;
        SCHEDULER = new RealTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREADS);
        TIME_EXECUTION_TOLERANCE = 100; // ms

        super.verifyAndSetup();
    }
}
