package sima.basic.environment;

import org.junit.jupiter.api.Test;
import sima.core.agent.AgentTesting;
import sima.core.environment.GlobalTestEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static sima.basic.environment.FullyConnectedNetworkEnvironment.*;

class TestFullyConnectedNetworkEnvironment extends GlobalTestEnvironment {
    
    // Initialisation.
    
    @Override
    protected void verifyAndSetup() {
        
        ACCEPTED_AGENT = new AgentTesting("ACCEPT_AGENT", 0, 0, null);
        ACCEPTED_AGENT_IDENTIFIER = ACCEPTED_AGENT.getAgentIdentifier();
        NOT_ACCEPTED_AGENT_IDENTIFIER = null;
        NOT_EVOLVING_AGENT_IDENTIFIER = new AgentTesting("NOT_EVOLVING", 2, 2, null).getAgentIdentifier();
        
        ENVIRONMENT = new FullyConnectedNetworkEnvironment("ENV", null);
        ENVIRONMENT_EQUAL = new FullyConnectedNetworkEnvironment("ENV", null);
        
        super.verifyAndSetup();
    }
    
    // Tests.
    
    @Test
    void constructFullyConnectedNetworkEnvironmentWithNullNameThrowsException() {
        Map<String, String> hashMap = new HashMap<>();
        assertThrows(NullPointerException.class, () -> new FullyConnectedNetworkEnvironment(null, hashMap));
    }
    
    @Test
    void constructFullyConnectedNetworkEnvironmentWithNotNullNameThrowsException() {
        assertDoesNotThrow(() -> new FullyConnectedNetworkEnvironment("ENV", new HashMap<>()));
    }
    
    @Test
    void constructFullyConnectedNetworkEnvironmentWithNotNullArgsNotFail() {
        assertDoesNotThrow(() -> new FullyConnectedNetworkEnvironment("ENV", new HashMap<>()));
    }
    
    @Test
    void constructFullyConnectedNetworkEnvironmentWithNullArgsNotFail() {
        assertDoesNotThrow(() -> new FullyConnectedNetworkEnvironment("ENV", null));
    }
    
    @Test
    void sendDelayReturnsDefaultValueIfNoArgsDuringConstruction() {
        FullyConnectedNetworkEnvironment fullyConnectedNetworkEnvironment =
                new FullyConnectedNetworkEnvironment("ENV", null);
        
        assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MIN_SEND_DELAY,
                fullyConnectedNetworkEnvironment.getMinSendDelay());
        assertEquals(FullyConnectedNetworkEnvironment.DEFAULT_MAX_SEND_DELAY,
                fullyConnectedNetworkEnvironment.getMaxSendDelay());
    }
    
    @Test
    void sendDelayReturnsValuePassInArgsDuringConstruction() {
        Map<String, String> args = new HashMap<>();
        args.put(MIN_SEND_DELAY_ARGS, "50");
        args.put(MAX_SEND_DELAY_ARGS, "70");
        
        FullyConnectedNetworkEnvironment env = new FullyConnectedNetworkEnvironment("ENV", args);
        assertEquals(50, env.getMinSendDelay());
        assertEquals(70, env.getMaxSendDelay());
    }
    
    @Test
    void maxDelayAlwaysGreaterOrEqualsToMinDelayEvenIfItIsNotTheCaseInArgs() {
        Map<String, String> args = new HashMap<>();
        args.put(MIN_SEND_DELAY_ARGS, "70");
        args.put(MAX_SEND_DELAY_ARGS, "50");
        
        FullyConnectedNetworkEnvironment env = new FullyConnectedNetworkEnvironment("ENV", args);
        assertEquals(50, env.getMinSendDelay());
        assertEquals(70, env.getMaxSendDelay());
    }
    
    @Test
    void ifSpecifiedDelayAreNegativeTheDelayIsEqualToOne() {
        Map<String, String> args = new HashMap<>();
        args.put(MIN_SEND_DELAY_ARGS, "-50");
        args.put(MAX_SEND_DELAY_ARGS, "-70");
        
        FullyConnectedNetworkEnvironment env = new FullyConnectedNetworkEnvironment("ENV", args);
        assertEquals(1, env.getMinSendDelay());
        assertEquals(1, env.getMaxSendDelay());
    }
    
    @Test
    void ifSpecifiedDelayAreNotNumberTheDelayIsEqualToDefault() {
        Map<String, String> args = new HashMap<>();
        args.put(MIN_SEND_DELAY_ARGS, "-Adf50");
        args.put(MAX_SEND_DELAY_ARGS, "-70FSQ");
        
        FullyConnectedNetworkEnvironment env = new FullyConnectedNetworkEnvironment("ENV", args);
        assertEquals(DEFAULT_MIN_SEND_DELAY, env.getMinSendDelay());
        assertEquals(DEFAULT_MAX_SEND_DELAY, env.getMaxSendDelay());
    }
}
