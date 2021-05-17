package sima.core.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimaLogger {
    
    // Variables.
    
    private final Logger logger;
    
    // Constructors.
    
    public SimaLogger(String loggerName) {
        logger = LoggerFactory.getLogger(loggerName);
    }
    
    public SimaLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }
    
    // Methods.
    
    public void info(String info) {
        if (logger.isInfoEnabled())
            logger.info(info);
    }
    
    public void error(String error) {
        if (logger.isErrorEnabled())
            logger.error(error);
    }
    
    public void error(String error, Throwable e) {
        if (logger.isErrorEnabled())
            logger.error(error, e);
    }
    
    // Getters and Setters.
    
    public @NotNull Logger getLogger() {
        return logger;
    }
    
}
