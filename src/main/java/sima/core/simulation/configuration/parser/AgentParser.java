package sima.core.simulation.configuration.parser;

import org.jetbrains.annotations.NotNull;
import sima.core.agent.SimaAgent;
import sima.core.environment.Environment;
import sima.core.exception.ConfigurationException;
import sima.core.exception.FailInstantiationException;
import sima.core.protocol.Protocol;
import sima.core.simulation.configuration.json.AgentJson;
import sima.core.simulation.configuration.json.BehaviorJson;
import sima.core.simulation.configuration.json.ProtocolJson;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.beans.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static sima.core.simulation.configuration.parser.ConfigurationParser.extractSetterName;
import static sima.core.simulation.configuration.parser.ConfigurationParser.parseArgs;
import static sima.core.utils.Utils.*;

public class AgentParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;

    private final EnvironmentParser envParser;
    private final BehaviorParser behaviorParser;
    private final ProtocolParser protocolParser;

    private final Set<SimaAgent> allAgents;

    private int agentCounter;

    // Constructors.

    public AgentParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = notNullOrThrows(simaSimulationJson, new IllegalArgumentException("The simaSimulationJson cannot be null"));

        envParser = new EnvironmentParser(this.simaSimulationJson);
        behaviorParser = new BehaviorParser(this.simaSimulationJson);
        protocolParser = new ProtocolParser(this.simaSimulationJson);

        allAgents = new HashSet<>();

        agentCounter = 0;
    }

    // Methods.

    public void parseAgents() throws ConfigurationException, FailInstantiationException, ClassNotFoundException, NoSuchMethodException {
        allAgents.clear();
        parseEnvironment();
        parseBehavior();
        parseProtocol();
        fillSetAgents();
    }

    private void parseEnvironment() throws ConfigurationException, FailInstantiationException, ClassNotFoundException {
        envParser.parseEnvironments();
    }

    private void parseBehavior() throws ConfigurationException {
        behaviorParser.parseBehaviors();
    }

    private void parseProtocol() throws ConfigurationException, ClassNotFoundException {
        protocolParser.parseProtocols();
    }

    private void fillSetAgents() throws ConfigurationException, FailInstantiationException, ClassNotFoundException, NoSuchMethodException {
        if (simaSimulationJson.hasAgents()) {
            for (AgentJson agentJson : simaSimulationJson.getAgents())
                createAllAgents(agentJson);
        }
    }


    /**
     * @param agentJson the json agent configuration
     */
    private void createAllAgents(AgentJson agentJson)
            throws ConfigurationException, FailInstantiationException, ClassNotFoundException, NoSuchMethodException {
        verifyAgentNumberToCreate(agentJson.getNumberToCreate());
        for (var i = 0; i < agentJson.getNumberToCreate(); i++) {
            SimaAgent agent = createAgent(agentJson, i);
            associateAgentAndEnvironments(agent, agentJson);
            associateAgentAndBehaviors(agent, agentJson);
            associateAgentAndProtocol(agent, agentJson);
        }
    }

    /**
     * Verifies if the numberToCreate specified is greater or equal to 1. If it is not the case, throws an {@link ConfigurationException}, else
     * nothing is done.
     *
     * @param numberToCreate the numberToCreate to verify
     *
     * @throws ConfigurationException if numberToCreate is equal or less to 0.
     */
    private void verifyAgentNumberToCreate(int numberToCreate) throws ConfigurationException {
        if (numberToCreate < 1)
            throw new ConfigurationException("Cannot have a number of agent to create less or equal to 0");
    }

    @NotNull
    private SimaAgent createAgent(AgentJson agentJson, int agentSequenceId)
            throws ClassNotFoundException, ConfigurationException, FailInstantiationException {
        Class<? extends SimaAgent> agentClass = extractClassForName(agentJson.getAgentClass());
        String agentName = String.format(agentJson.getNamePattern(), agentSequenceId);
        Map<String, String> agentArgs = parseArgs(agentJson);

        SimaAgent agent = instantiateAgent(agentClass, agentName, agentSequenceId, agentCounter++, agentArgs);
        addAgent(agent);
        return agent;
    }

    private void addAgent(SimaAgent agent) throws ConfigurationException {
        if (!allAgents.add(agent))
            throw new ConfigurationException("Fail to add agent is agent set -> Two agent with same hashCode. Agent not added = " + agent);
    }

    private void associateAgentAndEnvironments(SimaAgent agent, AgentJson agentJson) throws ConfigurationException {
        if (agentJson.hasEnvironment())
            for (String environmentId : agentJson.getEnvironments()) {
                var mapEnv = envParser.getMapEnvironments();
                var env = notNullOrThrows(mapEnv.get(environmentId), new ConfigurationException("EnvironmentId " + environmentId + " not found"));
                agentJoinEnvironment(agent, env);
            }
    }

    private void agentJoinEnvironment(SimaAgent agent, Environment env) throws ConfigurationException {
        if (!agent.joinEnvironment(env))
            throw new ConfigurationException("Agent " + agent + " unable to join the Environment " + env);
    }

    private void associateAgentAndBehaviors(SimaAgent agent, AgentJson agentJson) throws ConfigurationException, ClassNotFoundException {
        if (agentJson.hasBehavior())
            for (String behaviorId : agentJson.getBehaviors()) {
                Map<String, BehaviorJson> mapB = behaviorParser.getMapBehaviors();
                var behaviorJson = notNullOrThrows(mapB.get(behaviorId), new ConfigurationException("BehaviorId " + behaviorId + " not " + "exists"));
                addBehaviorToAgent(agent, behaviorJson);
            }
    }

    private void addBehaviorToAgent(SimaAgent agent, BehaviorJson behaviorJson) throws ClassNotFoundException, ConfigurationException {
        if (!agent.addBehavior(extractClassForName(behaviorJson.getBehaviorClass()), parseArgs(behaviorJson)))
            throw new ConfigurationException("Unable to add behavior " + behaviorJson.getBehaviorClass() + " to agent " + agent);
    }

    private void associateAgentAndProtocol(SimaAgent agent, AgentJson agentJson)
            throws ConfigurationException, ClassNotFoundException, NoSuchMethodException {
        if (agentJson.getProtocols() != null) {
            addAllProtocolsToAgent(agent, agentJson);
            linkProtocolDependencies(agent, agentJson);
        }
    }

    private void addAllProtocolsToAgent(SimaAgent agent, AgentJson agentJson) throws ConfigurationException, ClassNotFoundException {
        for (String protocolId : agentJson.getProtocols()) {
            Map<String, ProtocolJson> mapP = protocolParser.getMapProtocols();
            var protocolJson = notNullOrThrows(mapP.get(protocolId), new ConfigurationException("ProtocolId " + protocolId + " not found"));
            addProtocolToAgent(agent, protocolJson);
        }
    }

    private void addProtocolToAgent(SimaAgent agent, ProtocolJson protocolJson) throws ConfigurationException, ClassNotFoundException {
        if (!agent.addProtocol(extractClassForName(protocolJson.getProtocolClass()), protocolJson.getTag(), parseArgs(protocolJson)))
            throw new ConfigurationException("Unable to add protocol " + protocolJson.getProtocolClass() + " to agent " + agent);
    }

    private void linkProtocolDependencies(SimaAgent agent, AgentJson agentJson) throws ClassNotFoundException, NoSuchMethodException {
        for (String protocolId : agentJson.getProtocols()) {
            Map<String, ProtocolJson> mapP = protocolParser.getMapProtocols();

            var protocolJson = mapP.get(protocolId);
            if (protocolJson.hasProtocolDependencies())
                for (Map.Entry<String, String> entry : protocolJson.getProtocolDependencies().entrySet()) {
                    String attributeName = entry.getKey();
                    String dependenceId = entry.getValue();
                    Object dependenceInstance = simaSimulationJson.getInstanceFromId(dependenceId, agent);
                    var protocol = agent.getProtocol(protocolJson.extractProtocolIdentifier());
                    linkProtocolAttributeDependencies(protocol, attributeName, dependenceInstance);
                }
        }
    }

    /**
     * Try to set the value to the attribute.
     *
     * @param protocol           the protocol to set attribute value
     * @param fieldName          the protocol attribute name
     * @param dependenceInstance the value to set to the protocol attribute
     *
     * @throws NoSuchMethodException if the setter for the field is not found
     */
    private void linkProtocolAttributeDependencies(Protocol protocol, String fieldName, Object dependenceInstance) throws NoSuchMethodException {
        try {
            var statement = new Statement(protocol, extractSetterName(fieldName), new Object[]{dependenceInstance});
            statement.execute();
        } catch (Exception e) {
            throw new NoSuchMethodException("No setter found for the fieldName " + fieldName + " in the class " + dependenceInstance.getClass());
        }
    }

    // Static.

    public static @NotNull SimaAgent instantiateAgent(Class<? extends SimaAgent> agentClass, String agentName,
                                                      int agentSequenceId, int agentUniqueId, Map<String, String> args)
            throws FailInstantiationException {
        return instantiate(agentClass, new Class[]{String.class, int.class, int.class, Map.class}, agentName,
                           agentSequenceId, agentUniqueId, args);
    }


    // Getters.

    public Set<SimaAgent> getAllAgents() {
        return allAgents;
    }

    public Set<Environment> getAllEnvironments() {
        return envParser.getAllEnvironments();
    }
}
