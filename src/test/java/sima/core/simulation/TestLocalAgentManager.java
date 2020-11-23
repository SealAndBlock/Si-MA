package sima.core.simulation;

public class TestLocalAgentManager extends TestAgentManager {

    @Override
    protected void initialize() {
        AGENT_MANAGER = new LocalAgentManager();

        super.initialize();
    }

}
