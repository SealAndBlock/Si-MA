package sima.standard.broadcast.basic;

import org.junit.jupiter.api.*;
import sima.core.exception.SimaSimulationFailToStartRunningException;
import sima.core.simulation.SimaSimulation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static sima.core.simulation.SimaSimulation.SimaLog;
import static sima.util.DataManager.*;
import static sima.util.DataManager.DataReader.*;

public class BasicBroadcastIT {

    // Static.

    public static final String PREFIX_CONFIG_PATH = "src/integration-test/resources/config/broadcast/basic/";
    public static final String PREFIX_VALIDITY_CONFIG_PATH = PREFIX_CONFIG_PATH + "validity/";
    public static final String PREFIX_NO_DUPLICATION_CONFIG_PATH = PREFIX_CONFIG_PATH + "no_duplication/";

    public static final String ENVIRONMENT_NAME = "SimpleEnvironment";
    public static final int NB_AGENT = 10;

    // Before.

    @AfterEach
    @BeforeEach
    void clearFile() {
        clearDataFile();
    }

    // Tests.

    @Nested
    @Tag("BasicBroadcast.validity")
    @DisplayName("BasicBroadcast validity tests")
    class ValidityTest {

        @Test
        @DisplayName("Test if the BasicBroadcast satisfies validity with a correct broadcaster and all agents corrects / MONO-THREAD")
        void testBasicBroadcastValidityWithAllAgentsCorrectsMonoThread() {
            try {
                SimaSimulation.runSimulation(PREFIX_VALIDITY_CONFIG_PATH + "validity_with_all_agents_corrects_mono_thread.json");
                SimaSimulation.waitEndSimulation();
                verifyAllAgentsDeliverMessage();
            } catch (SimaSimulationFailToStartRunningException e) {
                fail(e);
            }
        }

        @Test
        @DisplayName("Test if the BasicBroadcast satisfies validity with a correct broadcaster and all agents corrects / MULTI-THREAD")
        void testBasicBroadcastValidityWithAllAgentsCorrectsMultiThread() {
            try {
                SimaSimulation.runSimulation(PREFIX_VALIDITY_CONFIG_PATH + "validity_with_all_agents_corrects_multi_thread.json");
                SimaSimulation.waitEndSimulation();
                verifyAllAgentsDeliverMessage();
            } catch (SimaSimulationFailToStartRunningException e) {
                fail(e);
            }
        }

        private void verifyAllAgentsDeliverMessage() {
            try {
                List<Integer> agent = getAgentFromFile(AGENT_DELIVER_MESSAGE_PATH);
                assertThat(agent.size()).isEqualByComparingTo(NB_AGENT);
            } catch (IOException e) {
                SimaLog.error("Error during read agent which haas delivered message", e);
                fail(e);
            }
        }

        @Test
        @DisplayName("Test if the BasicBroadcast satisfies validity with a correct broadcaster and some agents faulty / MONO-THREAD")
        void testBasicBroadcastValidityWithFaultyAgentMonoThread() {
            try {
                SimaSimulation.runSimulation(PREFIX_VALIDITY_CONFIG_PATH + "validity_with_faulty_agents_killed_after_broadcast_mono_thread.json");
                SimaSimulation.waitEndSimulation();
                verifyCorrectAgentsDeliverMessage();
            } catch (SimaSimulationFailToStartRunningException e) {
                fail(e);
            }
        }

        @Test
        @DisplayName("Test if the BasicBroadcast satisfies validity with a correct broadcaster and some agents faulty / MULTI-THREAD")
        void testBasicBroadcastValidityWithFaultyAgentMultiThread() {
            try {
                SimaSimulation.runSimulation(PREFIX_VALIDITY_CONFIG_PATH + "validity_with_faulty_agents_killed_after_broadcast_multi_thread.json");
                SimaSimulation.waitEndSimulation();
                verifyCorrectAgentsDeliverMessage();
            } catch (SimaSimulationFailToStartRunningException e) {
                fail(e);
            }
        }

        private void verifyCorrectAgentsDeliverMessage() {
            try {
                List<Integer> correctAgents = getCorrectAgent();
                List<Integer> agentWhichDeliverMessage = getAgentFromFile(AGENT_DELIVER_MESSAGE_PATH);

                assertThat(agentWhichDeliverMessage).containsAll(correctAgents);
            } catch (IOException e) {
                SimaLog.error("Error during read agent infos", e);
                fail(e);
            }
        }

    }

    @Nested
    @Tag("BasicBroadcast.noDuplication")
    @DisplayName("BasicBroadcast no duplication test")
    class NoDuplicationTest {

        @Test
        @DisplayName("Test if the BasicBroadcast does not duplicate message / MONO-THREAD")
        void testBasicBroadcastDoesNotDuplicateMessageMonoThread() {
            try {
                SimaSimulation.runSimulation(PREFIX_NO_DUPLICATION_CONFIG_PATH + "no_duplication_with_all_agents_corrects_mono_thread.json");
                SimaSimulation.waitEndSimulation();
                verifyNumberOfCorrectDelivery();
            } catch (SimaSimulationFailToStartRunningException e) {
                fail(e);
            }
        }

        @Test
        @DisplayName("Test if the BasicBroadcast does not duplicate message / MULTI-THREAD")
        void testBasicBroadcastDoesNotDuplicateMessageMultiThread() {
            try {
                SimaSimulation.runSimulation(PREFIX_NO_DUPLICATION_CONFIG_PATH + "no_duplication_with_all_agents_corrects_multi_thread.json");
                SimaSimulation.waitEndSimulation();
                verifyNumberOfCorrectDelivery();
            } catch (SimaSimulationFailToStartRunningException e) {
                fail(e);
            }
        }

        private void verifyNumberOfCorrectDelivery() {
            try {
                Map<String, Integer> map = new HashMap<>();
                List<String> lines = getFileLines(AGENT_NB_DELIVERY_PATH);
                List<Integer> correctAgent = getCorrectAgent();
                for (String id : lines) {
                    if (correctAgent.contains(Integer.parseInt(id)))
                        if (!map.containsKey(id)) {
                            map.put(id, 1);
                        } else {
                            map.put(id, map.get(id) + 1);
                        }
                }

                map.forEach((k, v) -> {
                    if (v > 1)
                        fail("The agent with the unique id " + k + " has deliver more than once the message (exactly " + v + " times)");
                });
            } catch (IOException e) {
                SimaLog.error("Error during read line", e);
                fail(e);
            }
        }

    }

}
