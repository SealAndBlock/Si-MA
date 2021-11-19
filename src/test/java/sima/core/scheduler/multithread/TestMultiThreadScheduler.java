package sima.core.scheduler.multithread;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import sima.core.scheduler.Scheduler;
import sima.core.scheduler.TestScheduler;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public abstract class TestMultiThreadScheduler extends TestScheduler {

    // Variables.

    protected MultiThreadScheduler multiThreadScheduler;

    // Init.

    @BeforeEach
    protected void setUp() {
        scheduler = multiThreadScheduler;
    }

    // Tests.

    @Nested
    @Tag("MultiThreadScheduler.toString")
    @DisplayName("MultiThreadScheduler toString tests")
    class ToStringTest {

        @Test
        @DisplayName("Test if the method toString returns a correct String")
        void testToString() {
            String toString = multiThreadScheduler.toString();
            assertThat(toString).isNotNull();
        }

    }

    @Nested
    @Tag("MultiThreadScheduler.getSchedulerType")
    @DisplayName("MultiThreadScheduler getSchedulerType tests")
    class GetSchedulerTypeTest {

        @Test
        @DisplayName("Test if getSchedulerType returns SchedulerType.MULTI_THREAD")
        void testGetSchedulerTypeReturns() {
            var schedulerType = multiThreadScheduler.getSchedulerType();
            assertThat(schedulerType).isEqualTo(Scheduler.SchedulerType.MULTI_THREAD);
        }

    }
}
