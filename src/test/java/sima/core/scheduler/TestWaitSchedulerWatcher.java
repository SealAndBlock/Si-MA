package sima.core.scheduler;

import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

public class TestWaitSchedulerWatcher {
    
    // Variables.
    
    protected WaitSchedulerWatcher waitSchedulerWatcher;
    
    private final long timeOut = 50L;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        waitSchedulerWatcher = new WaitSchedulerWatcher();
    }
    
    // Tests.
    
    @Nested
    @Tag("WaitSchedulerWatcher.schedulerStarted")
    @DisplayName("WaitSchedulerWatcher schedulerStarted tests")
    class SchedulerStartedTest {
        
        @Test
        @DisplayName("Test if schedulerStarted does not throw an exception")
        void testSchedulerStarted() {
            assertDoesNotThrow(() -> waitSchedulerWatcher.schedulerStarted());
        }
        
    }
    
    @Nested
    @Tag("WaitSchedulerWatcher.schedulerSimulationEndTimeReach")
    @DisplayName("WaitSchedulerWatcher schedulerSimulationEndTimeReach tests")
    class SchedulerSimulationEndTimeReachTest {
        
        @Test
        @DisplayName("Test if schedulerSimulationEndTimeReach does not throw an exception")
        void testSchedulerSimulationEndTimeReach() {
            assertDoesNotThrow(() -> waitSchedulerWatcher.simulationEndTimeReach());
        }
        
    }
    
    @Nested
    @Tag("WaitSchedulerWatcher.noExecutableToExecute")
    @DisplayName("WaitSchedulerWatcher noExecutableToExecute tests")
    class NoExecutableToExecuteTest {
        
        @Test
        @DisplayName("Test if noExecutableToExecute does not throw an exception")
        void testNoExecutableToExecute() {
            assertDoesNotThrow(() -> waitSchedulerWatcher.noExecutableToExecute());
        }
        
    }
    
    @Nested
    @Tag("WaitSchedulerWatcher.schedulerKilled")
    @DisplayName("WaitSchedulerWatcher schedulerKilled tests")
    class SchedulerKilledTest {
        
        @Test
        @DisplayName("Test if schedulerKilled does not throw an exception")
        void testNoExecutableToExecute() {
            assertDoesNotThrow(() -> waitSchedulerWatcher.schedulerKilled());
        }
        
    }
    
    @Nested
    @Tag("WaitSchedulerWatcher.waitUntilKilled")
    @DisplayName("WaitSchedulerWatcher waitUntilKilled tests")
    class WaitUntilKilledTest {
        
        @Test
        @DisplayName("Test if the method waitUntilKilled stop the current thread until the method schedulerKilled is called")
        void testWaitUntilKilledWaitUntilSchedulerKilledCall() {
            final AtomicBoolean threadFinish = new AtomicBoolean(false);
            final Thread runningThread = new Thread(() -> {
                await().timeout(timeOut, TimeUnit.MILLISECONDS);
                threadFinish.set(true);
                waitSchedulerWatcher.schedulerKilled();
            });
            runningThread.start();
            waitSchedulerWatcher.waitUntilKilled();
            assertThat(threadFinish.get()).isTrue();
        }
        
        @Test
        @DisplayName("Test if the method waitUntilKilled does not block if schedulerKilled is called before waitUntilKilled")
        void testWaitUntilKilledWithSchedulerKilledCallBefore() {
            final AtomicBoolean passed = new AtomicBoolean(false);
            final Thread testThread = new Thread(() -> {
                waitSchedulerWatcher.schedulerKilled();
                waitSchedulerWatcher.waitUntilKilled();
                passed.set(true);
            });
            testThread.start();
            try {
                testThread.join();
                assertThat(passed.get()).isTrue();
            } catch (InterruptedException e) {
                fail(e);
            }
        }
        
        @Test
        @DisplayName("test if the method waitUntilKilled unlock the current thread if the current thread is interrupt")
        void testWaitUntilKilledWithSchedulerWithInterruptedThread() {
            final AtomicBoolean passed = new AtomicBoolean(false);
            final Thread waitingThread = new Thread(() -> {
                waitSchedulerWatcher.waitUntilKilled();
                passed.set(true);
            });
            waitingThread.start();
            await().timeout(timeOut, TimeUnit.MILLISECONDS);
            waitingThread.interrupt();
            try {
                waitingThread.join();
            } catch (InterruptedException e) {
                // Thread always interrupted because we make waitingThread.interrupt.
                assertThat(passed.get()).isTrue();
            }
        }
        
    }
    
}
