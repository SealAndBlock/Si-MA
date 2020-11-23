package sima.core;

import org.junit.jupiter.api.BeforeEach;

public abstract class TestInitializer {

    // Setup.

    @BeforeEach
    public void setup() {
        this.initialize();
    }

    // Initialization.

    /**
     * Initialize all fields that the tests need.
     */
    protected abstract void initialize();

}
