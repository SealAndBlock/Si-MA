package sima.core.environment;

import org.junit.jupiter.api.Test;
import sima.core.agent.AgentIdentifier;
import sima.core.agent.AgentTesting;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class TestEnvironmentTesting extends GlobalTestEnvironment {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
        List<AgentIdentifier> notAcceptedAgent = new ArrayList<>();

        ACCEPTED_AGENT = new AgentTesting("ACCEPT_AGENT", 0, null).getAgentIdentifier();
        NOT_ACCEPTED_AGENT = new AgentTesting("NOT_ACCEPTED_AGENT", 1, null).getAgentIdentifier();

        notAcceptedAgent.add(NOT_ACCEPTED_AGENT);

        ENVIRONMENT = new EnvironmentTesting(0, notAcceptedAgent);

        super.verifyAndSetup();
    }

    // Tests.

    @Test
    public void constructEventTestingNotFailWithoutList() {
        try {
            new EnvironmentTesting(0);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void constructEventTestingNotFailWithNullList() {
        try {
            new EnvironmentTesting(0, null);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    public void constructEventTestingNotFailWithNotNullList() {
        try {
            new EnvironmentTesting(0, new ArrayList<>());
        } catch (Exception e) {
            fail(e);
        }
    }
}
