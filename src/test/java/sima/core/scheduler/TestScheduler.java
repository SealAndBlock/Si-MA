package sima.core.scheduler;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.agent.AgentIdentifier;
import sima.core.environment.event.Event;
import sima.core.exception.*;
import sima.core.scheduler.executor.Executable;
import sima.core.scheduler.executor.MultiThreadExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class TestScheduler {

    // Variables.

    protected Scheduler scheduler;

    protected long nbRepetitions = 150L;
    protected long executionTimeStep = 10L;
    protected long simulationSpecificTimeCorrect = 5L;
    protected long simulationSpecificTimeInCorrect = 2L;

    private WaitSchedulerWatcher waitSchedulerWatcher;

    @Mock
    private Scheduler.SchedulerWatcher mockSchedulerWatcher;

    @Mock
    private Executable mockExecutable;

    @Mock
    private Event mockEvent;

    @Mock
    private AgentIdentifier mockAgentIdentifier;

    // Init.

    @BeforeEach
    void setUpWaitSchedulerWatcher() {
        waitSchedulerWatcher = new WaitSchedulerWatcher();
    }

    // Tests.

    private void waitSchedulerKill() {
        waitSchedulerWatcher.waitUntilKilled();
    }

    private void prepareSchedulerForWatchingItsKill() {
        scheduler.addSchedulerWatcher(waitSchedulerWatcher);
    }

    private void configureMockExecutableForOneExecution(AtomicLong executionTime) {
        doAnswer(invocation -> {
            executionTime.set(scheduler.getCurrentTime());
            return null;
        }).when(mockExecutable).execute();
    }

    private @NotNull List<Long> generateExpectedExecutionTimesForRepeatedExecution() {
        final List<Long> expectedExecutionTimes = new ArrayList<>();
        for (long i = Scheduler.NOW, j = 0; j < nbRepetitions; j++) {
            expectedExecutionTimes.add(i);
            i += executionTimeStep;
        }
        return expectedExecutionTimes;
    }

    private void configureMockExecutableForRepeatedExecution(List<Long> executionTimes) {
        doAnswer(invocation -> {
            executionTimes.add(scheduler.getCurrentTime());
            return null;
        }).when(mockExecutable).execute();
    }

    private void verifyEndSimulationMinimum(long minimumEndSimulation) {
        assertThat(scheduler.getEndSimulation()).isGreaterThanOrEqualTo(minimumEndSimulation);
    }

    @NotNull
    private List<Long> generateExpectedExecutionTimesForInfiniteExecution() {
        final List<Long> expectedExecutionTimes = new ArrayList<>();
        for (long time = Scheduler.NOW; time <= scheduler.getEndSimulation(); time += executionTimeStep) {
            expectedExecutionTimes.add(time);
        }
        return expectedExecutionTimes;
    }

    private void configureMockExecutableToTryToScheduleExecutableAtAlreadyPassedSimulationTime() {
        doAnswer(invocation -> {
            assertThrows(NotScheduleTimeException.class, () -> scheduler.scheduleExecutableAtSpecificTime(mockExecutable,
                                                                                                          simulationSpecificTimeInCorrect));
            return null;
        }).when(mockExecutable).execute();
    }

    private void configureMockExecutableToThrowAnExceptionDuringItsExecution() {
        doAnswer(invocation -> {
            throw new Exception();
        }).when(mockExecutable).execute();
    }

    @Nested
    @Tag("Scheduler.schedulerWatcher")
    @DisplayName("Scheduler schedulerWatcher methods tests")
    class SchedulerWatcherTest {

        @Nested
        @Tag("Scheduler.addSchedulerWatcher")
        @DisplayName("Scheduler addSchedulerWatcher tests")
        class AddSchedulerWatcherTest {

            @Test
            @DisplayName("Test if addSchedulerWatcher returns false with null parameters")
            void testAddSchedulerWatcherWithNull() {
                boolean added = scheduler.addSchedulerWatcher(null);
                assertFalse(added);
            }

            @Test
            @DisplayName("Test if addSchedulerWatcher returns true with a not already added SchedulerWatcher")
            void testAddSchedulerWatcherWithNotAlreadyAddedSchedulerWatcher() {
                boolean added = scheduler.addSchedulerWatcher(mockSchedulerWatcher);
                assertTrue(added);
            }

            @Test
            @DisplayName("Test if addSchedulerWatcher returns false with an already added SchedulerWatcher")
            void testAddSchedulerWatcherWithAlreadyAddedSchedulerWatcher() {
                scheduler.addSchedulerWatcher(mockSchedulerWatcher);
                boolean secondAdd = scheduler.addSchedulerWatcher(mockSchedulerWatcher);
                assertFalse(secondAdd);
            }

        }

        @Nested
        @Tag("Scheduler.removeSchedulerWatcher")
        @DisplayName("Scheduler removeSchedulerWatcher tests")
        class RemoveSchedulerWatcherTest {

            @Test
            @DisplayName("Test if removeSchedulerWatcher does not throw exception")
            void testRemoveSchedulerWatcherDoesNotThrowException() {
                // Null args
                assertDoesNotThrow(() -> scheduler.removeSchedulerWatcher(null));

                // With not added SchedulerWatcher
                assertDoesNotThrow(() -> scheduler.removeSchedulerWatcher(mockSchedulerWatcher));

                // With added SchedulerWatcher
                scheduler.addSchedulerWatcher(mockSchedulerWatcher);
                assertDoesNotThrow(() -> scheduler.removeSchedulerWatcher(mockSchedulerWatcher));
            }

        }
    }

    @Nested
    @Tag("Scheduler.getEndSimulation")
    @DisplayName("Scheduler getEndSimulation tests")
    class getEndSimulationTest {

        @Test
        @DisplayName("Test if getEndSimulation returns a long greater or equals to 1")
        void testGetEndSimulationReturns() {
            long endSimulation = scheduler.getEndSimulation();
            long minimum = 1L;
            assertThat(endSimulation).isGreaterThanOrEqualTo(minimum);
        }

    }

    @Nested
    @Tag("Scheduler.isRunning")
    @DisplayName("Scheduler isRunning tests")
    class IsRunningTest {

        @Test
        @DisplayName("Test if isRunning returns false if the Scheduler is not started")
        void testIsRunningWithNotStartedScheduler() {
            boolean isRunning = scheduler.isRunning();
            assertFalse(isRunning);
        }

    }

    @Nested
    @Tag("Scheduler.isKilled")
    @DisplayName("Scheduler isKilled tests")
    class IsKilledTest {

        @Test
        @DisplayName("Test if isKilled returns false if the Scheduler is not killed")
        void testIsKilledWithNotKilledScheduler() {
            boolean isKilled = scheduler.isKilled();
            assertFalse(isKilled);
        }

    }

    @Nested
    @Tag("Scheduler.start")
    @DisplayName("Scheduler start tests")
    class StartTest {

        @Test
        @DisplayName("Test if start returns true if the scheduler is not started and not killed. Also verify if isRunning returns true after " +
                "the start")
        void testStartWithNotStartedAndNotKilledScheduler() {
            boolean started = scheduler.start();
            assertThat(started).isTrue();
        }

        @Test
        @DisplayName("Test if start returns false if the scheduler is killed. Also verify if isRunning returns false")
        void testStartAfterKillScheduler() {
            scheduler.kill();
            boolean started = scheduler.start();
            boolean isRunning = scheduler.isRunning();
            assertThat(started).isFalse();
            assertThat(isRunning).isFalse();
        }

        @Test
        @DisplayName("Test if start notify the SchedulerWatcher if start returns true")
        void testStartNotifiesSchedulerWatcher() {
            scheduler.addSchedulerWatcher(mockSchedulerWatcher);
            scheduler.start();
            verify(mockSchedulerWatcher, times(1)).schedulerStarted();
        }

        @Test
        @DisplayName("Test if start a scheduler with no executable make it directly killed and notify watcher that the scheduler kill by no " +
                "executable to execute")
        void testStartWithNoExecutableToExecute() {
            scheduler.addSchedulerWatcher(mockSchedulerWatcher);
            scheduler.start();
            verify(mockSchedulerWatcher, times(1)).noExecutableToExecute();
            verify(mockSchedulerWatcher, times(1)).schedulerKilled();
        }

    }

    @Nested
    @Tag("Scheduler.kill")
    @DisplayName("Scheduler kill tests")
    class KillTest {

        @Test
        @DisplayName("Test if kill returns true if the Scheduler is not already killed. Also verifies if isKilled returns true.")
        void testKillWithNotAlreadyKilledScheduler() {
            boolean killed = scheduler.kill();
            boolean isKilled = scheduler.isKilled();
            assertThat(killed).isTrue();
            assertThat(isKilled).isTrue();
        }

        @Test
        @DisplayName("Test if kill returns false if the Scheduler is already killed. Also verifies if isKilled returns true.")
        void testKillWithAlreadyKilledScheduler() {
            scheduler.kill();
            boolean secondKill = scheduler.kill();
            boolean isKilled = scheduler.isKilled();
            assertThat(secondKill).isFalse();
            assertThat(isKilled).isTrue();
        }

        @Test
        @DisplayName("Test if when kill returns true the SchedulerWatcher is notified")
        void testKillNotifiesSchedulerWatcher() {
            scheduler.addSchedulerWatcher(mockSchedulerWatcher);
            scheduler.kill();
            verify(mockSchedulerWatcher, times(1)).schedulerKilled();
        }
    }

    @Nested
    @Tag("Scheduler.getCurrentTime")
    @DisplayName("Scheduler getCurrentTime tests")
    class GetCurrentTimeTest {

        @Test
        @DisplayName("Test if getCurrentTime returns 0 if the Scheduler is not started but not killed")
        void testGetCurrentTimeWithNotStartedScheduler() {
            long currentTime = scheduler.getCurrentTime();
            long expectedCurrentTime = 0L;
            assertThat(currentTime).isEqualTo(expectedCurrentTime);
        }

        @Test
        @DisplayName("Test if getCurrentTime returns -1 if the Scheduler is killed")
        void testGetCurrentTimeWithKilledScheduler() {
            scheduler.kill();
            long currentTime = scheduler.getCurrentTime();
            long expectedCurrentTime = -1L;
            assertThat(currentTime).isEqualTo(expectedCurrentTime);
        }
    }

    @Nested
    @Tag("Scheduler.schedule")
    @DisplayName("Scheduler schedule methods tests")
    class ScheduleTest {

        @Nested
        @Tag("Scheduler.scheduleExecutable")
        @DisplayName("Scheduler scheduleExecutable tests")
        class ScheduleExecutableTest {

            @Test
            @DisplayName("Test if scheduleExecutable throws an NullPointerException with null Executable")
            void testScheduleExecutableWithNullExecutable() {
                assertThrows(NullPointerException.class, () -> scheduler.scheduleExecutable(null, Scheduler.NOW,
                                                                                            Scheduler.ScheduleMode.ONCE, 1, 1));
                assertThrows(NullPointerException.class, () -> scheduler.scheduleExecutable(null, Scheduler.NOW,
                                                                                            Scheduler.ScheduleMode.REPEATED, 1, 1));
                assertThrows(NullPointerException.class, () -> scheduler.scheduleExecutable(null, Scheduler.NOW,
                                                                                            Scheduler.ScheduleMode.INFINITE, 1, 1));
            }

            @Test
            @DisplayName("Test if scheduleExecutable throws an IllegalArgumentException if waitingTime is less than 1")
            void testScheduleExecutableWithLessThanOneWaitingTime() {
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutable(mockExecutable, -1,
                                                                                                Scheduler.ScheduleMode.ONCE, 1, 1));
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutable(mockExecutable, -1,
                                                                                                Scheduler.ScheduleMode.REPEATED, 1, 1));
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutable(mockExecutable, -1,
                                                                                                Scheduler.ScheduleMode.INFINITE, 1, 1));
            }

            @Test
            @DisplayName("Test if scheduleExecutable does not throws exception with schedule mode equal to ONCE and nbRepetition and " +
                    "executionTimeStep less than 1")
            void testScheduleExecutableIgnoreNbRepetitionAndExecutionTimeStepInScheduleModeOnce() {
                assertDoesNotThrow(() -> scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW, Scheduler.ScheduleMode.ONCE,
                                                                      -1, -1));
            }

            @Test
            @DisplayName("Test if scheduleExecutable throws an IllegalArgumentException if nbRepetition is less than 1 in scheduleMode is " +
                    "equal to REPEATED")
            void testScheduleExecutableThrowsExceptionWithNbRepetitionLessThanOneWitheScheduleModeDifferentThanOnce() {
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW,
                                                                                                Scheduler.ScheduleMode.REPEATED, -1, 1));
            }

            @Test
            @DisplayName("Test if scheduleExecutable ignore nbRepetitions in INFINITE scheduleMode.")
            void testScheduleExecutableIgnoreNbRepetitionWithInfiniteScheduleMode() {
                assertDoesNotThrow(() -> scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW,
                                                                      Scheduler.ScheduleMode.INFINITE, -1, 1));
            }

            @Test
            @DisplayName("Test if scheduleExecutable throws an IllegalArgumentException if executionTimeStep is less than 1 for different than" +
                    " ONCE scheduleMode")
            void testScheduleExecutableThrowsExceptionWithExecutionTimeStepLessThanOne() {
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW,
                                                                                                Scheduler.ScheduleMode.REPEATED, 1, -1));
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW,
                                                                                                Scheduler.ScheduleMode.INFINITE, 1, -1));
            }

            @Test
            @DisplayName("Test if scheduleExecutable in once schedule mode executes correctly the Executable")
            void testScheduleExecutableInOnceScheduleModeExecuteCorrectlyExecutable() {
                // GIVEN
                final AtomicLong executionTime = new AtomicLong();
                configureMockExecutableForOneExecution(executionTime);

                // WHEN
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
                scheduler.start();

                // THEN
                waitSchedulerKill();
                verify(mockExecutable, times(1)).execute();
                assertThat(executionTime.get()).isEqualTo(Scheduler.NOW);
            }

            @Test
            @DisplayName("Test if scheduleExecutable in repeated schedule mode executes correctly the executable")
            void testScheduleExecutableInRepeatedScheduleModeExecuteCorrectlyExecutable() {
                // GIVEN
                final List<Long> executionTimes = new ArrayList<>();
                configureMockExecutableForRepeatedExecution(executionTimes);
                final List<Long> expectedExecutionTimes = generateExpectedExecutionTimesForRepeatedExecution();
                verifyEndSimulationMinimum(Scheduler.NOW + ((nbRepetitions - 1) * executionTimeStep) + 1);

                // WHEN
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW, Scheduler.ScheduleMode.REPEATED, nbRepetitions, executionTimeStep);
                scheduler.start();

                // THEN
                System.out.println("WAIT");
                waitSchedulerKill();
                verify(mockExecutable, times((int) nbRepetitions)).execute();
                assertThat(executionTimes.size()).isEqualTo(nbRepetitions);
                assertThat(executionTimes).containsExactlyElementsOf(expectedExecutionTimes);
            }

            @Test
            @DisplayName("Test if scheduleExecutable in infinite schedule mode executes correctly the executable and verifies that watcher are" +
                    " notified by the end simulation has been reach")
            void testScheduleExecutableInInfiniteScheduleModeExecuteCorrectlyExecutable() {
                // GIVEN
                final List<Long> executionTimes = new ArrayList<>();
                configureMockExecutableForRepeatedExecution(executionTimes);
                final List<Long> expectedExecutionTimes = generateExpectedExecutionTimesForInfiniteExecution();
                verifyEndSimulationMinimum(1L);

                // WHEN
                scheduler.addSchedulerWatcher(mockSchedulerWatcher);
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW, Scheduler.ScheduleMode.INFINITE, -1, executionTimeStep);
                scheduler.start();

                // THEN
                waitSchedulerKill();
                assertThat(executionTimes).containsExactlyElementsOf(expectedExecutionTimes);
                verify(mockSchedulerWatcher, times(1)).simulationEndTimeReach();
            }

            @Test
            @DisplayName("Test ScheduleExecutable with an Executable which throws an exception during its execution does not " +
                    "block the scheduler")
            void testScheduleExecutableWithExecutableWhichThrowsAnException() {
                // GIVEN
                configureMockExecutableToThrowAnExceptionDuringItsExecution();
                verifyEndSimulationMinimum(Scheduler.NOW + 1);

                // WHEN
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutable(mockExecutable, Scheduler.NOW, Scheduler.ScheduleMode.ONCE, -1, -1);
                scheduler.start();

                // THEN
                waitSchedulerKill();
                verify(mockExecutable, times(1)).execute();
            }
        }

        @Nested
        @Tag("Scheduler.scheduleExecutableAtSpecificTime")
        @DisplayName("Scheduler scheduleExecutableAtSpecificTime tests")
        class ScheduleExecutableAtSpecificTimeTest {

            @Test
            @DisplayName("Test if scheduleExecutableAtSpecificTime throws a NullPointerException with null Executable")
            void testScheduleExecutableAtSpecificTimeWithNullExecutable() {
                assertThrows(NullPointerException.class, () -> scheduler.scheduleExecutableAtSpecificTime(null, Scheduler.NOW));
            }

            @Test
            @DisplayName("Test if scheduleExecutableAtSpecificTime throws IllegalArgumentException with less than 1 simulationSpecificTime")
            void testScheduleExecutableAtSpecificTimeWithLessThanOneSimulationSpecificTime() {
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutableAtSpecificTime(mockExecutable, -1));
            }

            @Test
            @DisplayName("Test if scheduleExecutableAtSpecificTime executes correctly the Executable")
            void testScheduleExecutableAtSpecificTimeExecutesCorrectlyExecutable() {
                // GIVEN
                final AtomicLong executionTime = new AtomicLong();
                configureMockExecutableForOneExecution(executionTime);

                // WHEN
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutableAtSpecificTime(mockExecutable, simulationSpecificTimeCorrect);
                scheduler.start();

                // THEN
                waitSchedulerKill();
                verify(mockExecutable, times(1)).execute();
                assertThat(executionTime.get()).isEqualTo(simulationSpecificTimeCorrect);
            }

            @Test
            @DisplayName("Test if an Executable has been scheduled with the method scheduleExecutableAtSpecificTime with a simulation time " +
                    "greater than the endSimulation, therefore the Executable is not execute")
            void testScheduleExecutableAtSpecificTimeWithGreaterExecutionTimeThanEndSimulation() {
                // GIVEN
                // Nothing to do with mockExecutable

                // WHEN
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutableAtSpecificTime(mockExecutable, scheduler.getEndSimulation() + 1);
                scheduler.start();

                // THEN
                waitSchedulerKill();
                verify(mockExecutable, times(0)).execute();
            }

            @Test
            @DisplayName("Test scheduleExecutableAtSpecificTime throws an NotScheduleTimeException if the simulationTime specified is already " +
                    "pass in the simulation")
            void testScheduleExecutableAtSpecificTimeWithAlreadyPassedSimulationTime() {
                // GIVEN
                configureMockExecutableToTryToScheduleExecutableAtAlreadyPassedSimulationTime();
                verifyEndSimulationMinimum(simulationSpecificTimeCorrect + 1);

                // WHEN
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutableAtSpecificTime(mockExecutable, simulationSpecificTimeCorrect);
                scheduler.start();

                // THEN
                waitSchedulerKill();
                verify(mockExecutable, times(1)).execute();
            }

            @Test
            @DisplayName("Test scheduleExecutableAtSpecificTime with an Executable which throws an exception during its execution does not " +
                    "block the scheduler")
            void testScheduleExecutableAtSpecificTimeWithExecutableWhichThrowsAnException() {
                // GIVEN
                configureMockExecutableToThrowAnExceptionDuringItsExecution();
                verifyEndSimulationMinimum(simulationSpecificTimeCorrect + 1);

                // WHEN
                prepareSchedulerForWatchingItsKill();
                scheduler.scheduleExecutableAtSpecificTime(mockExecutable, simulationSpecificTimeCorrect);
                scheduler.start();

                // THEN
                waitSchedulerKill();
                verify(mockExecutable, times(1)).execute();
            }

        }

        @Nested
        @Tag("Scheduler.scheduleExecutableOnce")
        @DisplayName("Scheduler scheduleExecutableOnce tests")
        class ScheduleExecutableOnceTest {

            @Test
            @DisplayName("Test scheduleExecutableOnce throws a NullPointerException with null Executable")
            void testScheduleExecutableOnceWithNullExecutable() {
                assertThrows(NullPointerException.class, () -> scheduler.scheduleExecutableOnce(null, Scheduler.NOW));
            }

            @Test
            @DisplayName("Test scheduleExecutableOnce throws an IllegalArgumentException with less than 1 waitingTime")
            void testScheduleExecutableOnceWithLessThanOneWaitingTime() {
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutableOnce(mockExecutable, -1));
            }

        }

        @Nested
        @Tag("Scheduler.scheduleExecutableRepeated")
        @DisplayName("Scheduler scheduleExecutableRepeated tests")
        class ScheduleExecutableRepeatedTest {

            @Test
            @DisplayName("Test if scheduleExecutableRepeated throws a NullPointerException with null Executable")
            void testScheduleExecutableRepeatedWithNullExecutable() {
                assertThrows(NullPointerException.class, () -> scheduler.scheduleExecutableRepeated(null, Scheduler.NOW, 1, 1));
            }

            @Test
            @DisplayName("Test if scheduleExecutableRepeated throws an IllegalArgumentException with less than 1 waitingTime or nbRepetition " +
                    "or executionTimeStep")
            void testScheduleExecutableRepeatedWithIllegalArgument() {
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutableRepeated(mockExecutable, -1, 1, 1));
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutableRepeated(mockExecutable, Scheduler.NOW, -1, 1));
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutableRepeated(mockExecutable, Scheduler.NOW, 1, -1));
            }

            @Test
            @DisplayName("Test if scheduleExecutableRepeated does not throw exception if all arguments are correct")
            void testScheduleExecutableRepeatedWithCorrectArguments() {
                assertDoesNotThrow(() -> scheduler.scheduleExecutableRepeated(mockExecutable, Scheduler.NOW, 1, 1));
            }

        }

        @Nested
        @Tag("Scheduler.scheduleExecutableInfinitely")
        @DisplayName("Scheduler scheduleExecutableInfinitely tests")
        class ScheduleExecutableInfinitelyTest {

            @Test
            @DisplayName("Test if scheduleExecutableInfinitely throws a NullPointerException with null Executable")
            void testScheduleExecutableInfinitelyWithNullExecutable() {
                assertThrows(NullPointerException.class, () -> scheduler.scheduleExecutableInfinitely(null, Scheduler.NOW, 1));
            }

            @Test
            @DisplayName("Test if scheduleExecutableInfinitely throws an IllegalArgumentException with less than one waitingTime or " +
                    "executionTimeStep")
            void testScheduleExecutableInfinitelyWithIllegalArgument() {
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutableInfinitely(mockExecutable, -1, 1));
                assertThrows(IllegalArgumentException.class, () -> scheduler.scheduleExecutableInfinitely(mockExecutable, Scheduler.NOW, -1));
            }

            @Test
            @DisplayName("Test if scheduleExecutableInfinitely does not throw exception with correct arguments")
            void testScheduleExecutableInfinitelyWithCorrectArguments() {
                assertDoesNotThrow(() -> scheduler.scheduleExecutableInfinitely(mockExecutable, Scheduler.NOW, 1));
            }
        }

        @Nested
        @Tag("Scheduler.scheduleEvent")
        @DisplayName("Scheduler scheduleEvent tests")
        class SchedulerEventTest {

            @Test
            @DisplayName("Test if scheduleEvent throws a NullPointerException with null agent Target")
            void testScheduleEventWithNullTarget() {
                assertThrows(NullPointerException.class, () -> scheduler.scheduleEvent(null, mockEvent, Scheduler.NOW));
            }

            @Test
            @DisplayName("Test if scheduleEvent throws a NullPointerException with null Event")
            void testScheduleEventWithNullEvent() {
                assertThrows(NullPointerException.class, () -> scheduler.scheduleEvent(mockAgentIdentifier, null, Scheduler.NOW));
            }

        }

        @Nested
        @Tag("Scheduler.scheduleAwait")
        @DisplayName("Scheduler.scheduleAwait")
        class ScheduleAwait {

            @Nested
            @Tag("ScheduleAwait.infinite")
            @DisplayName("SchedulerAwait infinite")
            class Infinite {

                @Test
                @DisplayName("Test scheduleAwait with null condition throws NullPointerException")
                void testScheduleAwaitWithNullCondition() {
                    final AtomicBoolean thrown = new AtomicBoolean(false);

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(null);
                        } catch (ForcedWakeUpException | InterruptedException | NotPreparedConditionException e) {
                            fail(e);
                        } catch (NullPointerException e) {
                            thrown.set(true);
                        }
                    }, Scheduler.NOW);

                    scheduler.start();

                    waitSchedulerKill();

                    assertThat(thrown).isTrue();
                }

                @Test
                @DisplayName("Test scheduleAwait outside an Executable executed in the MultiThreadExecutor")
                void testScheduleAwaitOutsideCorrectContext() {
                    Scheduler.Condition c = new Scheduler.Condition();
                    assertThrows(NotCorrectContextException.class, () -> scheduler.scheduleAwait(c));
                }

                @Test
                @DisplayName("Test scheduleAwait throws ForcedWakeUpException if the thread is forced to wakeup")
                void testScheduleAwaitForcedWakeup() {
                    final Scheduler.Condition c = new Scheduler.Condition();
                    final AtomicBoolean forced = new AtomicBoolean(false);

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(c);
                        } catch (InterruptedException e) {
                            fail(e);
                        } catch (ForcedWakeUpException e) {
                            forced.set(true);
                        }
                    }, Scheduler.NOW);

                    scheduler.start();

                    waitSchedulerKill();

                    assertThat(forced).isTrue();
                }

                @Test
                @DisplayName("Test scheduleAwait can be wakeup with the condition at the correct time")
                void testScheduleAwaitWakeup() {
                    final Scheduler.Condition c = new Scheduler.Condition();
                    final AtomicLong currentTime = new AtomicLong(-1);

                    long wakeUpTime = 5;

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(c);
                            currentTime.set(scheduler.getCurrentTime());
                        } catch (ForcedWakeUpException | InterruptedException e) {
                            fail(e);
                        }
                    }, Scheduler.NOW);

                    scheduler.scheduleExecutableOnce(c::wakeup, wakeUpTime);

                    scheduler.start();

                    waitSchedulerKill();

                    assertThat(currentTime.get()).isEqualByComparingTo(wakeUpTime);
                }

            }


            @Nested
            @Tag("ScheduleAwait.timeout")
            @DisplayName("ScheduleAwait timeout")
            class Timeout {

                @Test
                @DisplayName("Test scheduleAwait with null condition throws NullPointerException")
                void testScheduleAwaitWithNullCondition() {
                    final AtomicBoolean thrown = new AtomicBoolean(false);

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(null, Scheduler.NOW);
                        } catch (ForcedWakeUpException | InterruptedException | NotPreparedConditionException | IllegalArgumentException e) {
                            fail(e);
                        } catch (NullPointerException e) {
                            thrown.set(true);
                        }
                    }, Scheduler.NOW);

                    scheduler.start();

                    waitSchedulerKill();

                    assertThat(thrown).isTrue();
                }

                @Test
                @DisplayName("Test scheduleAwait with non correct timeout throws IllegalArgumentException")
                void testScheduleAwaitWithNotCorrectTimeout() {
                    final Scheduler.Condition c = new Scheduler.Condition();
                    final AtomicBoolean thrown = new AtomicBoolean(false);

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(c, 0);
                        } catch (ForcedWakeUpException | InterruptedException | NotPreparedConditionException | NullPointerException e) {
                            fail(e);
                        } catch (IllegalArgumentException e) {
                            thrown.set(true);
                        }
                    }, Scheduler.NOW);

                    scheduler.start();

                    waitSchedulerKill();

                    assertThat(thrown).isTrue();
                }

                @Test
                @DisplayName("Test scheduleAwait outside an Executable executed in the MultiThreadExecutor")
                void testScheduleAwaitOutsideCorrectContext() {
                    Scheduler.Condition c = new Scheduler.Condition();
                    assertThrows(NotCorrectContextException.class, () -> scheduler.scheduleAwait(c, Scheduler.NOW));
                }

                /*@Test
                @DisplayName("Test scheduleAwait throws ForcedWakeUpException if the thread is forced to wakeup")
                void testScheduleAwaitForcedWakeup() {
                    final Scheduler.Condition c = new Scheduler.Condition();
                    final AtomicBoolean forced = new AtomicBoolean(false);

                    final long wakeupTime = 5;

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(c, wakeupTime);
                            System.out.println("FINISH");
                        } catch (InterruptedException e) {
                            fail(e);
                        } catch (ForcedWakeUpException e) {
                            System.out.println("FORCED");
                            forced.set(true);
                        }
                    }, Scheduler.NOW);

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            fail(e);
                        }
                    }, 2);

                    scheduler.start();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        fail(e);
                    }
                    scheduler.kill();

                    waitSchedulerKill();

                    assertThat(forced).isTrue();
                }*/

                @Test
                @DisplayName("Test scheduleAwait can be wakeup before the timeout")
                void testScheduleAwaitWakeupBeforeTimeout() {
                    final Scheduler.Condition c = new Scheduler.Condition();
                    final AtomicLong wakeupTime = new AtomicLong(-1);

                    final long timeout = 5;

                    final long wakeup = 3;

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(c, timeout);
                            wakeupTime.set(scheduler.getCurrentTime());
                        } catch (ForcedWakeUpException | InterruptedException e) {
                            fail(e);
                        }
                    }, Scheduler.NOW);

                    scheduler.scheduleExecutableOnce(c::wakeup, wakeup);

                    scheduler.start();

                    waitSchedulerKill();

                    assertThat(wakeupTime.get()).isEqualByComparingTo(wakeup);
                }

                @Test
                @DisplayName("Test scheduleAwait is wakeup at the timeout")
                void testScheduleAwaitWakeupAtTimeout() {
                    final Scheduler.Condition c = new Scheduler.Condition();
                    final AtomicLong wakeupTime = new AtomicLong(-1);

                    final long timeout = 5;

                    prepareSchedulerForWatchingItsKill();

                    scheduler.scheduleExecutableOnce(() -> {
                        try {
                            scheduler.scheduleAwait(c, timeout);
                            wakeupTime.set(scheduler.getCurrentTime());
                        } catch (ForcedWakeUpException | InterruptedException e) {
                            fail(e);
                        }
                    }, Scheduler.NOW);

                    scheduler.start();

                    System.out.println("WAIT");
                    waitSchedulerKill();

                    assertThat(wakeupTime.get()).isEqualByComparingTo(timeout + 1);
                }

            }
        }

    }

    @Nested
    @Tag("Scheduler.endSimulationReach")
    @DisplayName("Scheduler endSimulationReach tests")
    class EndSimulationReachTest {

        @Test
        @DisplayName("Test if endSimulationReach returns false for a not started and not killed Scheduler")
        void testEndSimulationReachForNotStartedAndNotKilledScheduler() {
            boolean endReach = scheduler.endSimulationReach();
            assertThat(endReach).isFalse();
        }

        @Test
        @DisplayName("Test if endSimulationReach returns true for an killed Scheduler")
        void testEndSimulationReachForKilledScheduler() {
            scheduler.kill();
            boolean endReach = scheduler.endSimulationReach();
            assertThat(endReach).isTrue();
        }

    }

    @Nested
    @Tag("Scheduler.Condition")
    @DisplayName("Scheduler Condition")
    class Condition {

        @Nested
        @Tag("Condition.prepare")
        @DisplayName("Condition prepare")
        class Prepare {

            @Test
            @DisplayName("Test if prepare throws an ClassCastException if the current thread is not an instance of ExecutorThread")
            void testPrepareInNotExecutorThread() {
                Scheduler.Condition c = new Scheduler.Condition();
                assertThrows(ClassCastException.class, c::prepare);
            }

            @Test
            @DisplayName("Test if prepare does not throw Exception if the context is correct and the condition is not already prepared")
            void testPrepareWithCorrectContext() {
                MultiThreadExecutor mte = new MultiThreadExecutor(8);

                final AtomicBoolean prepared = new AtomicBoolean(false);
                final Scheduler.Condition c = new Scheduler.Condition();

                mte.execute(() -> {
                    try {
                        c.prepare();
                        prepared.set(true);
                    } catch (Exception e) {
                        fail(e);
                    }
                });

                await().until(mte::isQuiescence);

                assertThat(prepared).isTrue();
            }

            @Test
            @DisplayName("Test if prepare throws an AlreadyPreparedConditionException if the condition is already prepared")
            void testPrepareWithAlreadyPreparedCondition() {
                MultiThreadExecutor mte = new MultiThreadExecutor(8);

                final AtomicBoolean thrown = new AtomicBoolean(false);
                final Scheduler.Condition c = new Scheduler.Condition();

                mte.execute(() -> {
                    c.prepare();
                    try {
                        c.prepare();
                        fail("Not thrown exception");
                    } catch (ClassCastException e) {
                        fail(e);
                    } catch (AlreadyPreparedConditionException e) {
                        thrown.set(true);
                    }
                });

                await().until(mte::isQuiescence);

                assertThat(thrown).isTrue();
            }

            @Test
            @DisplayName("Test if prepare does not throw Exception if prepare is recalled after the call of wakeup")
            void testPrepareAfterWakeup() {
                MultiThreadExecutor mte = new MultiThreadExecutor(8);

                final AtomicBoolean success = new AtomicBoolean(false);
                final Scheduler.Condition c = new Scheduler.Condition();

                mte.execute(() -> {
                    try {
                        c.prepare();
                        c.wakeup();
                        c.prepare();
                        success.set(true);
                    } catch (AlreadyPreparedConditionException e) {
                        fail(e);
                    }
                });

                await().until(mte::isQuiescence);

                assertThat(success).isTrue();
            }

        }

        @Nested
        @Tag("Condition.wakeup")
        @DisplayName("Condition wakeup")
        class Wakeup {

            @Test
            @DisplayName("Test if wakeup throws NotPreparedConditionException if the condition is not prepared")
            void testWakeupWithNotPreparedCondition() {
                Scheduler.Condition c = new Scheduler.Condition();
                assertThrows(NotPreparedConditionException.class, c::wakeup);
            }

            @Test
            @DisplayName("Test if wakeup can wakeup a thread waiting on the condition")
            void testWakeupCorrectlyWakeup() {
                MultiThreadExecutor mte = new MultiThreadExecutor(8);

                final Scheduler.Condition c = new Scheduler.Condition();
                final AtomicBoolean finish = new AtomicBoolean(false);

                mte.execute(() -> {
                    c.prepare();
                    MultiThreadExecutor.ExecutorThread eT = (MultiThreadExecutor.ExecutorThread) Thread.currentThread();
                    try {
                        eT.await();
                        finish.set(true);
                    } catch (InterruptedException | ForcedWakeUpException e) {
                        fail(e);
                    }
                });

                mte.execute(() -> {
                    try {
                        Thread.sleep(150);
                        c.wakeup();
                    } catch (InterruptedException e) {
                        fail(e);
                    }
                });

                await().until(mte::isQuiescence);

                assertThat(finish).isTrue();
            }

        }

        @Nested
        @Tag("Condition.hashBeenPrepared")
        @DisplayName("Condition hasBeenPrepared")
        class HasBeenPrepared {

            @Test
            @DisplayName("Test if hasBeenPrepared returns false if the condition has never been prepared")
            void testHasBeenPreparedWithNotPreparedCondition() {
                Scheduler.Condition c = new Scheduler.Condition();
                assertThat(c.hasBeenPrepared()).isFalse();
            }


            @Test
            @DisplayName("Test if hasBeenPrepared returns true if the condition has been prepared")
            void testHasBeenPreparedWithPreparedCondition() {
                MultiThreadExecutor mte = new MultiThreadExecutor(8);

                final AtomicBoolean hasBeenPrepared = new AtomicBoolean(false);
                final Scheduler.Condition c = new Scheduler.Condition();

                mte.execute(() -> {
                    c.prepare();
                    hasBeenPrepared.set(c.hasBeenPrepared());
                });

                await().until(mte::isQuiescence);

                assertThat(hasBeenPrepared).isTrue();
            }

            @Test
            @DisplayName("Test if hasBeenPrepared returns false after the call of wakeup")
            void testHasBeenPreparedAfterWakeup() {

                MultiThreadExecutor mte = new MultiThreadExecutor(8);

                final AtomicBoolean hasBeenPrepared = new AtomicBoolean(true); // true because we want change to false
                final Scheduler.Condition c = new Scheduler.Condition();

                mte.execute(() -> {
                    c.prepare();
                    c.wakeup();
                    hasBeenPrepared.set(c.hasBeenPrepared());
                });

                await().until(mte::isQuiescence);

                assertThat(hasBeenPrepared).isFalse();

            }
        }

    }

}
