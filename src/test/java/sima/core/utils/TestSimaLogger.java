package sima.core.utils;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestSimaLogger extends SimaTest {
    
    // Constants.
    
    protected SimaLogger LOGGER = new SimaLogger("TEST");
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
    }
    
    // Tests.
    
    @Test
    void infoDoesNotThrowException() {
        assertDoesNotThrow(() -> LOGGER.info("TEST INFO"));
    }
    
    @Test
    void errorWithoutThrowableDoesNotThrowException() {
        assertDoesNotThrow(() -> LOGGER.error("TEST ERROR"));
    }
    
    @Test
    void errorWithThrowableDoesNotThrowException() {
        assertDoesNotThrow(() -> LOGGER.error("TEST ERROR", new Exception("EXCEPTION TEST")));
    }
    
    @Test
    void getLoggerDoesNotThrowException() {
        assertDoesNotThrow(() -> assertNotNull(LOGGER.getLogger()));
    }
}
