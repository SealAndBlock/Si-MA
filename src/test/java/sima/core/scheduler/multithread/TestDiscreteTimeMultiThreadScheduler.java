package sima.core.scheduler.multithread;

public class TestDiscreteTimeMultiThreadScheduler extends TestMultiThreadScheduler {

    // Setup.

    @Override
    protected void initialize() {
        END_SIMULATION = 1_000;
        NB_EXECUTOR_THREADS = 5;
        SCHEDULER = new DiscreteTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREADS);
        TIME_EXECUTION_TOLERANCE = 0;

        super.initialize();
    }
}
