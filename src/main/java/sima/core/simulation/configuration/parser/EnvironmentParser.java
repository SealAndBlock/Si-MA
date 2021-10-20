package sima.core.simulation.configuration.parser;

import org.jetbrains.annotations.NotNull;
import sima.core.environment.Environment;
import sima.core.environment.physical.PhysicalConnectionLayer;
import sima.core.exception.ConfigurationException;
import sima.core.exception.FailInstantiationException;
import sima.core.simulation.configuration.json.EnvironmentJson;
import sima.core.simulation.configuration.json.PCLChainJson;
import sima.core.simulation.configuration.json.SimaSimulationJson;

import java.util.*;

import static sima.core.simulation.configuration.parser.ConfigurationParser.parseArgs;
import static sima.core.simulation.configuration.parser.PhysicalConnectionLayerParser.instantiatePCL;
import static sima.core.utils.Utils.*;

public class EnvironmentParser {

    // Variables.

    private final SimaSimulationJson simaSimulationJson;

    private final PhysicalConnectionLayerParser pclParser;

    private final Map<String, Environment> mapEnvironments;
    private final Set<Environment> allEnvironments;

    // Constructors.

    public EnvironmentParser(SimaSimulationJson simaSimulationJson) {
        this.simaSimulationJson = notNullOrThrows(simaSimulationJson, new IllegalArgumentException("The simaSimulationJson cannot be null"));
        pclParser = new PhysicalConnectionLayerParser(this.simaSimulationJson);
        mapEnvironments = new HashMap<>();
        allEnvironments = new HashSet<>();
    }

    // Methods.

    public void parseEnvironments() throws ConfigurationException, ClassNotFoundException, FailInstantiationException {
        allEnvironments.clear();
        mapEnvironments.clear();
        parsePCL();
        fillSetEnvironments();
    }

    private void parsePCL() throws ConfigurationException {
        pclParser.parsePhysicalConnectionLayers();
    }

    private void fillSetEnvironments() throws ConfigurationException, ClassNotFoundException, FailInstantiationException {
        verifyIfSimulationContainsEnvironment();
        createAllEnvironments();
    }

    private void verifyIfSimulationContainsEnvironment() throws ConfigurationException {
        if (simaSimulationJson.getEnvironments() == null || simaSimulationJson.getEnvironments().isEmpty())
            throw new ConfigurationException("The simulation need at least one environment");
    }

    private void createAllEnvironments() throws ConfigurationException, ClassNotFoundException, FailInstantiationException {
        for (EnvironmentJson envJson : simaSimulationJson.getEnvironments()) {
            var env = createEnvironment(envJson);
            mapEnvironments.put(notNullOrThrows(envJson.getId(), new ConfigurationException("EnvironmentId cannot be null")), env);
            simaSimulationJson.linkIdAndObject(envJson.getId(), env);
        }
    }

    @NotNull
    private Environment createEnvironment(EnvironmentJson environmentJson)
            throws ConfigurationException, ClassNotFoundException, FailInstantiationException {
        Environment env = instantiateEnvironmentFromJson(environmentJson);
        linkPCL(environmentJson, env);
        return addEnvironmentInSet(env);
    }

    private Environment instantiateEnvironmentFromJson(EnvironmentJson envJson)
            throws ConfigurationException, ClassNotFoundException, FailInstantiationException {
        Environment env;
        Map<String, String> envArgs = parseArgs(envJson);

        if (!envArgs.isEmpty())
            env = instantiateEnvironment(extractClassForName(envJson.getEnvironmentClass()), envJson.getName(), envArgs);
        else
            env = instantiateEnvironment(extractClassForName(envJson.getEnvironmentClass()), envJson.getName(), null);

        return env;
    }

    private void linkPCL(EnvironmentJson environmentJson, Environment environment)
            throws ConfigurationException, FailInstantiationException, ClassNotFoundException {
        if (!pclParser.isEmpty() && environmentJson.containsPhysicalLayerChain())
            for (PCLChainJson pclChainJson : environmentJson.getPhysicalConnectionLayerChains()) {
                addPCLChainInEnvironment(environment, pclChainJson);
            }
    }

    private void addPCLChainInEnvironment(Environment env, PCLChainJson pclChainJson)
            throws ConfigurationException, FailInstantiationException, ClassNotFoundException {
        var pclChain = createPCLChain(env, pclChainJson);
        var pclChainHead = linkPCLTogether(pclChain);
        addPCLChain(env, pclChainJson.getName(), pclChainHead);
    }

    private PhysicalConnectionLayer linkPCLTogether(List<PhysicalConnectionLayer> pclChain) {
        PhysicalConnectionLayer ite = null;

        // Browse the chain from the end to the start.
        for (int i = pclChain.size() - 1; i >= 0; i--) {
            ite = pclChain.get(i);
            if (i != 0)
                ite.setNext(pclChain.get(i - 1));
        }

        return ite; // Never returns null because pclChain is never empty thanks for verifications done before the call of this method.
    }

    private void addPCLChain(Environment env, String pclChainName, PhysicalConnectionLayer pcl) throws ConfigurationException {
        boolean added = env.addPhysicalConnectionLayer(pclChainName, pcl);
        if (!added)
            throw new ConfigurationException(
                    "PCLChain name not unique and already added in Environment" + env.getEnvironmentName() + ". Wrong name = " + pclChainName);
    }

    private List<PhysicalConnectionLayer> createPCLChain(Environment env, PCLChainJson pclChainJson)
            throws FailInstantiationException, ClassNotFoundException, ConfigurationException {
        List<PhysicalConnectionLayer> pclChain = new ArrayList<>();

        for (String pclId : pclChainJson.getChain()) {
            var pclJson = pclParser.getMapPCL().get(pclId);
            var physicalConnectionLayer = instantiatePCL(extractClassForName(pclJson.getPCLClass()), env, parseArgs(pclJson));
            pclChain.add(physicalConnectionLayer);
        }

        return pclChain;
    }

    private Environment addEnvironmentInSet(Environment env) throws ConfigurationException {
        if (allEnvironments.add(env)) {
            return env;
        } else
            throw new ConfigurationException(
                    "Two environments with the same hashCode (Probably due to the fact that they have the same name). Problematic Environment = " +
                            env);
    }

    // Static.

    /**
     * @param environmentClass the environment class
     * @param args             environment args
     *
     * @return a new instance of the specified {@code Environment} class.
     *
     * @throws FailInstantiationException if the instantiation fails
     * @throws NullPointerException       if environmentName is nulls
     */
    public static @NotNull Environment instantiateEnvironment(Class<? extends Environment> environmentClass,
                                                              String environmentName,
                                                              Map<String, String> args)
            throws FailInstantiationException {
        return instantiate(environmentClass, new Class[]{String.class, Map.class}, environmentName, args);
    }

    // Getters.

    public Set<Environment> getAllEnvironments() {
        return allEnvironments;
    }

    public Map<String, Environment> getMapEnvironments() {
        return mapEnvironments;
    }
}
