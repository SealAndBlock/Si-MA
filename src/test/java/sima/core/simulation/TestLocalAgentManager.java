package sima.core.simulation;

public class TestLocalAgentManager extends GlobalTestAgentManager {

    @Override
    protected void verifyAndSetup() {
        AGENT_MANAGER = new LocalAgentManager();

        super.verifyAndSetup();
    }

}
