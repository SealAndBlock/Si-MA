package sima.basic.transport;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import sima.core.environment.Environment;
import sima.core.protocol.TestProtocol;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class TestTransportProtocol extends TestProtocol {
    
    // Variables.
    
    protected TransportProtocol transportProtocol;
    
    @Mock
    private Environment mockEnvironment;
    
    @Mock
    private Environment mockEnvironmentOther;
    
    // Inits.
    
    @BeforeEach
    void setUp() {
        protocol = transportProtocol;
    }
    
    // Tests.
    
    @Nested
    @Tag("TransportProtocol.getEnvironment")
    @DisplayName("TransportProtocol getEnvironment tests")
    class GetEnvironmentTest {
        
        @Test
        @DisplayName("Test if the method getEnvironment returns null if the environment field has not been set")
        void testGetEnvironmentWithNotSetEnvironment() {
            assertThat(transportProtocol.getEnvironment()).isNull();
        }
        
        
        @Test
        @DisplayName("Test if the method getEnvironment return not null if the environment field has been set")
        void testGetEnvironmentWithSetEnvironment() {
            transportProtocol.setEnvironment(mockEnvironment);
            assertThat(transportProtocol.getEnvironment()).isNotNull();
        }
    }
    
    @Nested
    @Tag("TransportProtocol.setEnvironment")
    @DisplayName("TransportProtocol setEnvironment tests")
    class SetEnvironmentTest {
        
        @Test
        @DisplayName("Test if the method setEnvironment set the environment if it is the first time set")
        void testSetEnvironmentFirstTimeSet() {
            transportProtocol.setEnvironment(mockEnvironment);
            assertThat(transportProtocol.getEnvironment()).isEqualTo(mockEnvironment);
        }
        
        @Test
        @DisplayName("Test if the method setEnvironment does not set any value until the first set is a make with a null value")
        void testSetEnvironmentWithFirstTimeSetNull() {
            transportProtocol.setEnvironment(null);
            transportProtocol.setEnvironment(mockEnvironment);
            assertThat(transportProtocol.getEnvironment()).isEqualTo(mockEnvironment);
        }
        
        @Test
        @DisplayName("Test if the method setEnvironment does not set an other value after that the environment has alreay been set")
        void testSetEnvironmentWithSecondSet() {
            transportProtocol.setEnvironment(mockEnvironment);
            transportProtocol.setEnvironment(mockEnvironmentOther);
            assertThat(transportProtocol.getEnvironment()).isEqualTo(mockEnvironment);
        }
        
    }
}
