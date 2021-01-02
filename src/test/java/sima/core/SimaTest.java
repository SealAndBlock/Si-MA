package sima.core;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class SimaTest {

    // Setup.

    @BeforeEach
    public void setup() {
        this.verifyAndSetup();
    }

    // Initialization.

    /**
     * Initialize all fields that the tests need.
     * <p>
     * This method is called before each unit tests.
     */
    protected abstract void verifyAndSetup();

    // Methods.

    /**
     * Verify the precondition of the test and if the precondition is true, execute the test.
     *
     * @param preConditionTest the pre condition to verify before execute the test
     * @param testRunnable     the test to run after verify the pre condition
     */
    public void verifyPreConditionAndExecuteTest(PreConditionTest preConditionTest, TestRunnable testRunnable) {
        if (preConditionTest.verifyPreConditionTest()) {
            testRunnable.runTest();
        } else {
            fail("Pre condition tests not verified");
        }
    }

}
