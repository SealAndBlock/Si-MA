package sima.core.utils;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import sima.core.agent.SimaAgent;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestSimaLogger {
    
    // Variables.
    
    protected SimaLogger simaLogger;
    
    // Init.
    
    @BeforeEach
    void setUp() {
        simaLogger = new SimaLogger("SIMA_LOGGER");
    }
    
    // Tests.
    
    @Nested
    @Tag("SimaLogger.constructor")
    @DisplayName("SimaLogger constructor tests")
    class ConstructorTest {
        
        @Nested
        @Tag("SimaLogger.constructor(String)")
        @DisplayName("SimaLogger constructor(String) tests")
        class ConstructorStringTest {
            
            @Test
            @DisplayName("Test if constructor(String) create correct logger")
            void testConstructorWithString() {
                String simaLoggerName = "LOGGER";
                SimaLogger simaLogger = new SimaLogger(simaLoggerName);
                Logger logger = simaLogger.getLogger();
                assertThat(logger.getName()).isEqualTo(simaLoggerName);
            }
            
        }
        
        @Nested
        @Tag("SimaLogger.constructor(Class)")
        @DisplayName("SimaLogger constructor(Class) tests")
        class ConstructorClassTest {
            
            @Test
            @DisplayName("Test if constructor(Class) create correct logger")
            void testConstructorWithString() {
                SimaLogger simaLogger = new SimaLogger(SimaAgent.class);
                Logger logger = simaLogger.getLogger();
                assertThat(logger.getName()).isEqualTo(SimaAgent.class.getName());
            }
            
        }
        
    }
    
    @Nested
    @Tag("SimaLogger.info")
    @DisplayName("SimaLogger info tests")
    class InfoTest {
        
        @Test
        @DisplayName("Test if info does not throw exception")
        void testInfo() {
            assertDoesNotThrow(() -> simaLogger.info("info"));
        }
        
    }
    
    @Nested
    @Tag("SimaLogger.error")
    @DisplayName("SimaLogger error tests")
    class ErrorTest {
        
        @Test
        @DisplayName("Test if error without throwable does not throw exception")
        void testErrorWithoutThrowable() {
            assertDoesNotThrow(() -> simaLogger.error("error"));
        }
        
        @Test
        @DisplayName("Test if error with throwable does not throw exception")
        void testErrorWithThrowable() {
            assertDoesNotThrow(() -> simaLogger.error("error", new Exception()));
        }
        
    }
    
    
}
