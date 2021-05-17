package sima.core.scheduler.multithread;

import org.junit.jupiter.api.Disabled;
import sima.core.scheduler.GlobalTestScheduler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
abstract class GlobalTestMultiThreadScheduler extends GlobalTestScheduler {
    
    // Variables.
    
    protected static int NB_EXECUTOR_THREADS = 5;
    
    protected static MultiThreadScheduler MULTI_THREAD_SCHEDULER;
    
    // Setup.
    
    @Override
    protected void verifyAndSetup() {
        SCHEDULER = MULTI_THREAD_SCHEDULER;
        
        assertNotNull(MULTI_THREAD_SCHEDULER, "MULTI_THREAD_SCHEDULER cannot be null for tests");
        assertTrue(NB_EXECUTOR_THREADS > 0, "NB_EXECUTOR_THREADS cannot be less or equal to 0");
        
        super.verifyAndSetup();
    }
}
