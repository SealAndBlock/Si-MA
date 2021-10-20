package sima.core.simulation.configuration.json;

import java.util.List;

public class EnvironmentJson extends ObjectIdJson implements ArgumentativeObjectJson {
    
    // Variables.
    
    private String name;
    private String environmentClass;
    private List<PCLChainJson> physicalConnectionLayerChains;
    private List<List<String>> args;

    // Methods.

    /**
     *
     * @return true if {@link #physicalConnectionLayerChains} is not null and not empty, else false.
     */
    public boolean containsPhysicalLayerChain() {
        return physicalConnectionLayerChains != null && !physicalConnectionLayerChains.isEmpty();
    }

    // Getters.
    
    public String getName() {
        return name;
    }
    
    public String getEnvironmentClass() {
        return environmentClass;
    }
    
    public List<PCLChainJson> getPhysicalConnectionLayerChains() {
        return physicalConnectionLayerChains;
    }
    
    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
