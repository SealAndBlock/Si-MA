package sima.core;

import org.junit.jupiter.api.BeforeEach;

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

}
