package sima.core.scheduler.multithread;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TestRealTimeMultiThreadScheduler extends GlobalTestMultiThreadScheduler {
    
    // Static.
    
    protected static RealTimeMultiThreadScheduler REAL_TIME_MULTI_THREAD_SCHEDULER;
    
    // Setup.
    
    @Override
    protected void verifyAndSetup() {
        END_SIMULATION = 4_000;
        NB_EXECUTOR_THREADS = 10;
        REAL_TIME_MULTI_THREAD_SCHEDULER = new RealTimeMultiThreadScheduler(END_SIMULATION, NB_EXECUTOR_THREADS);
        MULTI_THREAD_SCHEDULER = REAL_TIME_MULTI_THREAD_SCHEDULER;
        TIME_EXECUTION_TOLERANCE = 350; // ms
        NB_EXECUTION_TOLERANCE = 30;
        REPETITION_STEP = 100;
        
        super.verifyAndSetup();
    }
    
    // Tests.
    
    @Test
    void constructorThrowsExceptionIfEndSimulationIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(0, NB_EXECUTOR_THREADS));
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(-1, NB_EXECUTOR_THREADS));
    }
    
    @Test
    void constructorThrowsExceptionIfNbExecutorThreadIsLessOrEqualToZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(END_SIMULATION, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new RealTimeMultiThreadScheduler(END_SIMULATION, -1));
    }
}
