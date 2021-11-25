package sima.core.scheduler.executor;

import org.junit.jupiter.api.*;
import sima.core.exception.ExecutorShutdownException;
import sima.core.exception.ForcedWakeUpException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

class TestMultiThreadExecutor {

    // Constants.

    private static final int NB_THREAD = 1;

    private static final int SLEEP_TIME = 75;

    private static final int NB_EXECUTIONS = NB_THREAD * 3;

    // Variables.

    private MultiThreadExecutor mte;

    // SetUp.

    @BeforeEach
    private void setUp() {
        mte = new MultiThreadExecutor(NB_THREAD);
    }

    // Methods.

    private void createAndExecute(List<ExecutableTest> exec) {
        for (int i = 0; i < NB_EXECUTIONS; i++) {
            ExecutableTest e = new ExecutableTest();
            exec.add(e);
            mte.execute(e);
        }
    }

    private static void verifyAllExecutions(List<ExecutableTest> exec) {
        for (ExecutableTest e : exec) {
            assertThat(e.executed).isTrue();
        }
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Inner class

    private static class AwaitableExecutable implements Executable {

        // Variables.

        private boolean finished = false;

        private MultiThreadExecutor.ExecutorThread executorThread;

        // Constructors.

        public AwaitableExecutable() {
        }

        // Methods.

        @Override
        public void execute() {
            getExecutorThread();
            await();
            sleep();
            finished = true;
        }

        private void getExecutorThread() {
            executorThread = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
        }

        public void await() {
            try {
                executorThread.await();
            } catch (InterruptedException | ForcedWakeUpException e) {
                fail(e);
            }
        }

        public void wakeUp() {
            executorThread.wakeUp();
        }
    }

    private static class ExecutableTest implements Executable {

        // Variables.

        private boolean executed = false;

        // Methods.

        @Override
        public void execute() {
            sleep();
            executed = true;

        }
    }

    // Tests.

    @Nested
    @Tag("MultiThreadExecutor.constructor")
    @DisplayName("MultiThreadExecutor constructor")
    class Constructor {

        @Test
        @DisplayName("Test if constructor throw IllegalArgumentException if maxT is less or equal than 0")
        void testConstructorWithWrongMaxT() {
            assertThrows(IllegalArgumentException.class, () -> new MultiThreadExecutor(0));
            assertThrows(IllegalArgumentException.class, () -> new MultiThreadExecutor(-1));
        }

        @Test
        @DisplayName("Test if constructor does not throw exception with greater than 0 maxT")
        void testConstructorWithCorrectMaxT() {
            assertDoesNotThrow(() -> new MultiThreadExecutor(1));
            assertDoesNotThrow(() -> new MultiThreadExecutor(2));
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.execute")
    @DisplayName("MultiThreadExecutor execute")
    class Execute {

        @Test
        @DisplayName("Test if execute throws NullPointerException if the executable is null")
        void testExecuteWithNullExecutable() {
            assertThrows(NullPointerException.class, () -> mte.execute(null));
        }

        @Test
        @DisplayName("Test if execute throws RejectedExecutionException if the MultiThreadExecutor is shutdown")
        void testExecuteAfterShutdown() {
            mte.shutdown();
            assertThrows(RejectedExecutionException.class, () -> mte.execute(() -> System.out.println("Hello")));
            assertThrows(RejectedExecutionException.class, () -> mte.execute(null));
        }

        @Test
        @DisplayName("Test if execute executes all Executables")
        void testExecuteExecutesAllExecutables() {
            List<ExecutableTest> exec = new ArrayList<>();
            createAndExecute(exec);

            await().until(mte::isQuiescence);

            verifyAllExecutions(exec);
        }

        @Test
        @DisplayName("Test if execute does not fail with several execution which throws exception")
        void testExecuteWithFailedExecutable() {
            List<ExecutableTest> exec = new ArrayList<>();
            for (int i = 0; i < NB_EXECUTIONS; i++) {
                if (i % 2 == 0) {
                    ExecutableTest e = new ExecutableTest();
                    exec.add(e);
                    mte.execute(e);
                } else {
                    mte.execute(() -> {
                        sleep();
                        throw new RuntimeException("Runtime exception in Executable");
                    });
                }
            }

            await().until(mte::isQuiescence);

            verifyAllExecutions(exec);
        }

        @Test
        @DisplayName("Test if execute execute Executable which use await and wakeUp primitive")
        void testExecuteWithAwaitAndWakeUp() {
            AwaitableExecutable e = new AwaitableExecutable();
            Executable wakeUpper = new Executable() {
                @Override
                public void execute() {
                    e.wakeUp();
                }

                @Override
                public Object getLockMonitor() {
                    return null;
                }
            };

            mte.execute(e);
            sleep(100);
            mte.execute(wakeUpper);

            await().until(mte::isQuiescence);

            assertThat(e.finished).isTrue();
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.isShutdown")
    @DisplayName("MultiThreadExecutor isShutdown")
    class isShutDown {

        @Test
        @DisplayName("Test if isShutdown returns false if the MultiThreadExecutor is not shutdown")
        void testIsShutdownWithNotShutdownMultiThreadExecutor() {
            assertThat(mte.isShutdown()).isFalse();
        }

        @Test
        @DisplayName("Test if isShutdown returns true if the MultiThreadExecutor is shutdown")
        void testIsShutdownWithShutdownMultiThreadExecutor() {
            mte.shutdown();
            assertThat(mte.isShutdown()).isTrue();
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.shutdown")
    @DisplayName("MultiThreadExecutor shutdown")
    class Shutdown {

        @Test
        @DisplayName("Test if all executables are executed after a shutdown")
        void testShutdownExecuteAllExecutables() {
            final boolean[] executed = new boolean[NB_EXECUTIONS];

            for (int i = 0; i < NB_EXECUTIONS; i++) {
                int finalI = i;
                mte.execute(() -> executed[finalI] = true);
            }

            mte.shutdown();

            await().until(mte::isTerminated);

            assertThat(mte.isTerminated()).isTrue();

            for (Boolean b : executed) {
                assertThat(b).isTrue();
            }
        }

        @Test
        @DisplayName("Test if shutdown forced wakeup executable that are waiting if there is no more running executable after a shutdown if no " +
                "running executable")
        void testShutdownForcedWakeup() {
            final AtomicBoolean forced = new AtomicBoolean(false);

            mte.execute(() -> {
                MultiThreadExecutor.ExecutorThread eT = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
                try {
                    System.out.println("HERE");
                    eT.await();
                    System.out.println("Finish");
                } catch (InterruptedException | ExecutorShutdownException e) {
                    fail(e);
                } catch (ForcedWakeUpException e) {
                    forced.set(true);
                }
            });

            sleep(50);
            mte.shutdown();

            await().until(mte::isTerminated);

            assertThat(mte.isTerminated()).isTrue();
            assertThat(forced).isTrue();
        }

        @Test
        @DisplayName("Test shutdown execute all executable even if a executable is wake up by an executable after a shutdown")
        void testShutdownExecuteAllCorrectly() {
            AtomicReference<MultiThreadExecutor.ExecutorThread> eT = new AtomicReference<>();

            final boolean[] executed = new boolean[NB_EXECUTIONS + 1];

            mte.execute(() -> {
                eT.set((MultiThreadExecutor.ExecutorThread) Thread.currentThread());
                try {
                    eT.get().await();
                    executed[NB_EXECUTIONS] = true;
                } catch (InterruptedException | ForcedWakeUpException | ExecutorShutdownException e) {
                    fail(e);
                }
            });

            for (int i = 0; i < NB_EXECUTIONS; i++) {
                int finalI = i;
                if (i != 0) {
                    mte.execute(() -> {
                        sleep(50);
                        executed[finalI] = true;
                    });
                } else {
                    mte.execute(() -> {
                        sleep(200);
                        eT.get().wakeUp();
                        executed[finalI] = true;
                    });
                }
            }

            sleep(25);
            mte.shutdown();

            await().until(mte::isTerminated);

            assertThat(mte.isTerminated()).isTrue();

            for (Boolean b : executed) {
                assertThat(b).isTrue();
            }
        }

        @Test
        @DisplayName("Test shutdown execute all executable and force wakeup if necessary")
        void testShutdownExecuteAndForcedWakeUp() {
            final boolean[] executed = new boolean[NB_EXECUTIONS];

            final AtomicBoolean forced = new AtomicBoolean(false);

            mte.execute(() -> {
                MultiThreadExecutor.ExecutorThread eT = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
                try {
                    eT.await();
                } catch (InterruptedException | ExecutorShutdownException e) {
                    fail(e);
                } catch (ForcedWakeUpException e) {
                    forced.set(true);
                }
            });

            for (int i = 0; i < NB_EXECUTIONS; i++) {
                int finalI = i;
                mte.execute(() -> {
                    sleep(50);
                    executed[finalI] = true;
                });
            }

            sleep(25);
            mte.shutdown();

            await().until(mte::isTerminated);

            assertThat(mte.isTerminated()).isTrue();

            for (Boolean b : executed) {
                assertThat(b).isTrue();
            }


            assertThat(forced).isTrue();
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.isQuiescence")
    @DisplayName("MultiThreadExecutor isQuiescence")
    class IsQuiescence {

        @Test
        @DisplayName("Test if isQuiescence returns true if the MultiThreadExecutor has no Executable to execute")
        void testIsQuiescenceWithNoExecutableToExecute() {
            boolean isQuiescence = mte.isQuiescence();
            assertThat(isQuiescence).isTrue();
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.awaitQuiescence")
    @DisplayName("MultiThreadExecutor awaitQuiescence")
    class AwaitQuiescence {

        @Test
        @DisplayName("Test if awaitQuiescence wait until all ExecutorThread has been executed")
        void testAwaitQuiescence() {
            List<ExecutableTest> exec = new ArrayList<>();
            createAndExecute(exec);

            try {
                boolean isQuiescence = mte.awaitQuiescence();
                assertThat(isQuiescence).isTrue();
            } catch (InterruptedException e) {
                fail(e);
            }
        }

        @Test
        @DisplayName("Test if awaitQuiescence does not block if the MultiThread scheduler does not have Executable to execute")
        void testAwaitQuiescenceWithNoExecutable() {
            try {
                boolean isQuiescence = mte.awaitQuiescence();
                assertThat(isQuiescence).isTrue();
            } catch (InterruptedException e) {
                fail(e);
            }
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.isTerminated")
    @DisplayName("MultiThreadExecutor isTerminated")
    class IsTerminated {

        @Test
        @DisplayName("Test if isTerminated returns false if the MultiThreadExecutor is not shutdown")
        void testIsTerminatedWithNotShutdownMultiThreadExecutor() {
            boolean isTerminated = mte.isTerminated();
            assertThat(isTerminated).isFalse();
        }

        @Test
        @DisplayName("Test if isTerminated returns true if the MultiThreadExecutor is shutdown and has no executable to execute")
        void testIsTerminatedWithShutdownMultiThreadExecutor() {
            mte.shutdown();
            boolean isTerminated = mte.isTerminated();
            assertThat(isTerminated).isTrue();
        }

        @Test
        @DisplayName("Test if isTerminated returns true after shutdown and execute all Executable")
        void testIsTerminatedWithShutdownAndALlExecutableExecuted() {
            List<ExecutableTest> exec = new ArrayList<>();
            createAndExecute(exec);
            mte.shutdown();
            await().until(mte::isQuiescence);
            boolean isTerminated = mte.isTerminated();
            assertThat(isTerminated).isTrue();
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.awaitTermination")
    @DisplayName("MultiThreadExecutor awaitTermination")
    class AwaitTermination {

        @Test
        @DisplayName("Test if awaitTermination does not wait and returns false if the MultiThreadExecutor is not shutdown")
        void testAwaitTerminationWithNotShutdown() {
            try {
                boolean isTerminated = mte.awaitTermination(50);
                assertThat(isTerminated).isFalse();
            } catch (InterruptedException e) {
                fail(e);
            }
        }

        @Test
        @DisplayName("Test if awaitTermination returns false after wait if the MultiThreadExecutor does not finish to execute some Executables")
        void testAwaitTerminationWithNotFinishExecution() {
            mte.execute(() -> sleep(250));

            try {
                boolean isTerminated = mte.awaitTermination(50);
                assertThat(isTerminated).isFalse();
            } catch (InterruptedException e) {
                fail(e);
            }
        }

        @Test
        @DisplayName("Test if awaitTermination returns true after wait if the MultiThreadExecutor has finish to execute all Executables")
        void testAwaitTerminationWithAllExecutionsFinish() {
            mte.execute(() -> sleep(250));
            mte.shutdown();

            try {
                boolean isTerminated = mte.awaitTermination(500);
                assertThat(isTerminated).isTrue();
            } catch (InterruptedException e) {
                fail(e);
            }
        }

    }

    @Nested
    @Tag("MultiThreadExecutor.shutdownNow")
    @DisplayName("MultiThreadExecutor shutdownNow")
    class ShutdownNow {

        @Test
        @DisplayName("Test if shutdownNow returns empty list if the MultiThreadExecutor does not have any executions")
        void testShutdownNowWithNoExecutions() {
            List<Executable> executables = mte.shutdownNow();
            assertThat(executables).isEmpty();
            assertThat(mte.isTerminated()).isTrue();
        }

        @Test
        @DisplayName("Test if shutdownNow returns non empty list if MultiThreadExecutor is running and Executable are waiting to be executed")
        void testShutdownNowWithWaitingToBeExecutedExecutable() {
            long waitingTime = 100;
            for (int i = 0; i < NB_EXECUTIONS; i++) {
                mte.execute(() -> sleep(waitingTime));
            }
            List<Executable> notExecuted = mte.shutdownNow();
            assertThat(notExecuted).isNotEmpty();
        }

        @Test
        @DisplayName("Test if after shutdownNow a waiting Executable catch a ForcedWakeUpException")
        void testShutdownNowWithWaitingExecutable() {
            long waitingTime = 100;

            AtomicBoolean forced = new AtomicBoolean(false);

            mte.execute(() -> {
                MultiThreadExecutor.ExecutorThread eT = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
                try {
                    eT.await();
                } catch (InterruptedException e) {
                    fail(e);
                } catch (ForcedWakeUpException e) {
                    sleep(waitingTime);
                    forced.set(true);
                }
            });

            await().until(mte::isQuiescence);

            List<Executable> notExecuted = mte.shutdownNow();
            assertThat(notExecuted).isEmpty();

            await().until(mte::isTerminated);

            assertThat(forced).isTrue();
        }

        @Test
        @DisplayName("Test if after shutdownNow call await throws an ExecutorShutdownException")
        void testShutdownNowAwaitThrowsException() {
            AtomicBoolean executorShutdown = new AtomicBoolean(false);

            mte.execute(() -> {
                MultiThreadExecutor.ExecutorThread eT = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
                try {
                    sleep(150);
                    eT.await();
                } catch (InterruptedException | ForcedWakeUpException e) {
                    fail(e);
                } catch (ExecutorShutdownException e) {
                    executorShutdown.set(true);
                }
            });

            List<Executable> notExecuted = mte.shutdownNow();
            assertThat(notExecuted).isEmpty();

            await().until(mte::isTerminated);

            assertThat(executorShutdown).isTrue();
        }

    }

    @Test
    @DisplayName("Test if after shutdownNow call, all running Executables are correctly executed and wakeup Executable correctly finish")
    void testShutdownNowCorrectlyExecutedRunningExecutable() {
        AtomicBoolean finished = new AtomicBoolean(false);

        final AtomicReference<MultiThreadExecutor.ExecutorThread> eT = new AtomicReference<>(null);

        mte.execute(() -> {
            MultiThreadExecutor.ExecutorThread executorThread = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
            eT.set(executorThread);
            try {
                executorThread.await();
                System.out.println("FINISH");
                finished.set(true);
            } catch (InterruptedException | ForcedWakeUpException | ExecutorShutdownException e) {
                fail(e);
            }
        });

        for (int i = 0; i < NB_EXECUTIONS; i++) {
            if (i != 0) {
                mte.execute(() -> sleep(75));
            } else {
                mte.execute(() -> {
                    sleep(150);
                    eT.get().wakeUp();
                });
            }
        }

        sleep(50);
        List<Executable> notExecuted = mte.shutdownNow();
        assertThat(notExecuted).isNotEmpty();

        await().until(mte::isTerminated);

        assertThat(finished).isTrue();
    }

    @Nested
    @Tag("MultiThreadExecutor.ExecutorThread.await.wakeup")
    @DisplayName("MultiThreadExecutor.ExecutorThread await and wakeup")
    class AwaitWakeup {

        @Test
        @DisplayName("Test if await wait even if a wakeup has been called before the call of the wait")
        void testAwaitAfterAWakeup() {
            if (NB_THREAD > 1) {
                final AtomicReference<MultiThreadExecutor.ExecutorThread> eT = new AtomicReference<>(null);

                final AtomicBoolean forced = new AtomicBoolean(false);

                mte.execute(() -> {
                    eT.set((MultiThreadExecutor.ExecutorThread) Thread.currentThread());
                    try {
                        sleep(150);
                        eT.get().await();
                    } catch (InterruptedException | ExecutorShutdownException e) {
                        fail(e);
                    } catch (ForcedWakeUpException e) {
                        forced.set(true);
                    }
                });

                mte.execute(() -> eT.get().wakeUp());

                sleep(250);
                List<Executable> notExecuted = mte.shutdownNow();
                assertThat(notExecuted).isEmpty();

                await().until(mte::isTerminated);

                assertThat(forced).isTrue();
            }
        }

    }

}
