package sima.core.utils;

import org.junit.jupiter.api.Test;
import sima.core.SimaTest;
import sima.core.agent.AgentTesting;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils extends SimaTest {

    // Initialisation.

    @Override
    protected void verifyAndSetup() {
    }

    // Tests.

    @Test
    public void instantiateCanCorrectlyInstantiateAnAgentTesting() {
        try {
            AgentTesting a = Utils.instantiate(AgentTesting.class, new Class<?>[]{String.class, int.class, int.class,
                                                       Map.class},
                                               "AGENT", 0, 0, new HashMap<>());
            assertEquals("AGENT", a.getAgentName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            fail(e);
        }
    }
}
