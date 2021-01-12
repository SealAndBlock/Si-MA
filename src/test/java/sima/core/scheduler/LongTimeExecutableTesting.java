package sima.core.scheduler;

/**
 * Sleep current during 3000ms.
 */
public class LongTimeExecutableTesting implements Executable {

    // Static.

    public static final long WAITING_TIME = 3_000L;

    // Methods.

    @Override
    public void execute() {
        try {
            Thread.sleep(WAITING_TIME);
        } catch (InterruptedException ignored) {
        }
    }
}
