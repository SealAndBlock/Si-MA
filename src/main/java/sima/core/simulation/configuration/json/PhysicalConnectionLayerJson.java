package sima.core.simulation.configuration.json;

import java.util.List;

public class PhysicalConnectionLayerJson extends ObjectIdJson implements ArgumentativeObjectJson {
    
    
    // Variables
    
    private String physicalConnectionLayerClass;
    private List<List<String>> args;
    
    // Methods.
    
    public String getPCLClass() {
        return physicalConnectionLayerClass;
    }
    
    @Override
    public List<List<String>> getArgs() {
        return args;
    }
}
