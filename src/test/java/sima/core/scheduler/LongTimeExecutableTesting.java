package sima.core.scheduler;

/**
 * Sleep current during 500ms.
 */
public class LongTimeExecutableTesting implements Executable {

    // Static.

    public static final long WAITING_TIME = 500L;

    // Methods.

    @Override
    public void execute() {
        try {
            Thread.sleep(WAITING_TIME);
        } catch (InterruptedException ignored) {
        }
    }
}
