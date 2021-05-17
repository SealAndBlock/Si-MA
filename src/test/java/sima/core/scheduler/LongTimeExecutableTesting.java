package sima.core.scheduler;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Sleep current during 100ms.
 */
public class LongTimeExecutableTesting implements Executable {
    
    // Static.
    
    public static final long WAITING_TIME = 100L;
    
    // Methods.
    
    @Override
    public void execute() {
        await().atLeast(WAITING_TIME, TimeUnit.MILLISECONDS).until(() -> true);
    }
}
