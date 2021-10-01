package sima.core;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import sima.core.simulation.SimaSimulation;

public abstract class TestSima {
    
    // Setup.
    
    @BeforeEach
    public void setup() {
        this.verifyAndSetup();
    }
    
    // Initialization.
    
    /**
     * Initialize all fields that the tests need.
     * <p>
     * This method is called before each unit tests.
     */
    protected abstract void verifyAndSetup();
    
    // Methods.
    
    /**
     * @return an instance of MockedStatic of a SimaSimulation
     */
    public static MockedStatic<SimaSimulation> mockSimaSimulation() {
        return Mockito.mockStatic(SimaSimulation.class);
    }
    
}
