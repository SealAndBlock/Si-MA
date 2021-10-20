package sima.core.simulation.configuration;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestConfigurationParser {
    
    //Constants.
    
    static final String PREFIX_CONFIG_PATH = "src/test/resources/config/";
    
    // Tests.
    
    /*@Nested
    @Tag("ConfigurationParser.parseConfiguration")
    @DisplayName("ConfigurationParser parseConfiguration tests")
    class ParseConfigurationTest {
        
        @Test
        @DisplayName("Test if parseConfiguration throws a NullPointerException if configurationJsonPath is null")
        void testParseConfigurationWithNullPath() {
            assertThrows(NullPointerException.class, () -> ConfigurationParser.parseToSimaSimulationJson(null));
        }
        
        @Test
        @DisplayName("Test if parseConfiguration throws an IOException if file path does not exist")
        void testParseConfigurationWithNotExistingPath() {
            assertThrows(IOException.class, () -> ConfigurationParser.parseToSimaSimulationJson("PATH_DOES_NOT_EXISTS"));
        }
        
        @Test
        @DisplayName("Test if parseConfiguration throws an JsonSyntaxException if config file as a Json syntax error")
        void testParseConfigurationWithJsonSyntaxErrorFile() {
            assertThrows(JsonSyntaxException.class,
                    () -> ConfigurationParser.parseToSimaSimulationJson(PREFIX_CONFIG_PATH + "configJsonSyntaxError.json"));
        }
        
    }*/
    
}
