package sima.core.simulation;

import org.junit.jupiter.api.BeforeEach;

public class TestLocalAgentManager extends TestAgentManager {

    @BeforeEach
    public void setup() {
        AGENT_MANAGER = new LocalAgentManager();
    }

}
