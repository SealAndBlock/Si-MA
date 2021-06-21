package sima.core.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sima.core.exception.FailInstantiationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {
    
    // Tests.
    
    @Nested
    @Tag("Utils.extractClassForName")
    @DisplayName("Utils extractClassForName tests")
    class ExtractClassForNameTest {
        
        @Test
        @DisplayName("Test if extractClassForName throws an ClassNotFoundException if the class does not exists")
        void testExtractClassForNameWithNotExistingClass() {
            assertThrows(ClassNotFoundException.class, () -> Utils.extractClassForName("NotExistingClass"));
        }
        
        @Test
        @DisplayName("Test if extractClassForName returns a correct class with a name of an existing class")
        void testExtractClassForNameWithExistingClass() {
            try {
                Class<? extends SimpleClass> simpleAgentClass = Utils.extractClassForName(SimpleClass.class.getName());
                assertThat(simpleAgentClass).isEqualTo(SimpleClass.class);
            } catch (ClassNotFoundException e) {
                fail(e);
            }
        }
        
    }
    
    @Nested
    @Tag("Utils.instantiate")
    @DisplayName("Utils instantiate tests")
    class InstantiateTest {
        
        @Nested
        @Tag("Utils.instantiate(Class)")
        @DisplayName("Utils instantiate(Class) test")
        class InstantiateOneArgumentTest {
            
            @Test
            @DisplayName("Test if instantiate(class) throws FailInstantiationException if the class does not have a default constructor")
            void testInstantiateWithClassWithoutDefaultConstructor() {
                assertThrows(FailInstantiationException.class, () -> Utils.instantiate(ClassWithoutDefaultConstructor.class));
            }
            
            @Test
            @DisplayName("Test if instantiate(class) throws FailInstantiationException if the default constructor throws an Exception")
            void testInstantiateWithDefaultClassConstructorWhichThrowsException() {
                assertThrows(FailInstantiationException.class, () -> Utils.instantiate(ClassWhichThrowsExceptionInConstructors.class));
            }
            
            @Test
            @DisplayName("Test if instantiate(class) throws FailInstantiationException if the default constructor is not accessible")
            void testInstantiateWithDefaultClassConstructorWhichIsNotAccessible() {
                assertThrows(FailInstantiationException.class, () -> Utils.instantiate(ClassWithNotAccessibleConstructor.class));
            }
            
            @Test
            @DisplayName("Test if instantiate(class) throws FailInstantiationException if the class cannot be instantiate (ex -> abstract " +
                                 "class, interface")
            void testInstantiateWithNotInstantiableClass() {
                assertThrows(FailInstantiationException.class, () -> Utils.instantiate(AbstractClass.class));
            }
            
            @Test
            @DisplayName("Test if instantiate(class) create a new instance with a correct class")
            void testInstantiateWithCorrectClass() {
                try {
                    SimpleClass simpleClass = Utils.instantiate(SimpleClass.class);
                    assertThat(simpleClass).isNotNull();
                } catch (FailInstantiationException e) {
                    fail(e);
                }
            }
            
        }
        
        @Nested
        @Tag("Utils.instantiate(class, class[], Object...)")
        @DisplayName("Utils instantiate(class, class[], Object...) tests")
        class InstantiateSeveralArgumentsTest {
            
            @Test
            @DisplayName("Test if instantiate(class, class[], Object...) throws FailInstantiationException if the class does not have a " +
                                 "corresponding constructor")
            void testInstantiateWithClassWithoutDefaultConstructor() {
                assertThrows(FailInstantiationException.class, () -> Utils.instantiate(ClassWithoutDefaultConstructor.class,
                        new Class[]{Integer.class}, 5));
            }
            
            @Test
            @DisplayName("Test if instantiate(class, class[], Object...) throws FailInstantiationException if the constructor throws " +
                                 "an Exception")
            void testInstantiateWithDefaultClassConstructorWhichThrowsException() {
                assertThrows(FailInstantiationException.class,
                        () -> Utils.instantiate(ClassWhichThrowsExceptionInConstructors.class, new Class[]{String.class}, "IGNORED"));
            }
            
            @Test
            @DisplayName("Test if instantiate(class, class[], Object...) throws FailInstantiationException if the constructor is not accessible")
            void testInstantiateWithDefaultClassConstructorWhichIsNotAccessible() {
                assertThrows(FailInstantiationException.class,
                        () -> Utils.instantiate(ClassWithNotAccessibleConstructor.class, new Class[]{String.class}, "IGNORED"));
            }
            
            @Test
            @DisplayName(
                    "Test if instantiate(class, class[], Object...) throws FailInstantiationException if the class cannot be instantiate (ex " +
                            "-> abstract class, interface")
            void testInstantiateWithNotInstantiableClass() {
                assertThrows(FailInstantiationException.class,
                        () -> Utils.instantiate(AbstractClass.class, new Class[]{String.class}, "IGNORED"));
            }
            
            @Test
            @DisplayName("Test if instantiate(class, class[], Object...) create a new instance with a correct class")
            void testInstantiateWithCorrectClass() {
                try {
                    SimpleClass simpleClass = Utils.instantiate(SimpleClass.class, new Class[]{String.class}, "IGNORED");
                    assertThat(simpleClass).isNotNull();
                } catch (FailInstantiationException e) {
                    fail(e);
                }
            }
            
        }
        
    }
    
    // Inner classes.
    
    private static abstract class AbstractClass {
        
        public AbstractClass() {
        }
        
        public AbstractClass(String ignored) {
        }
    }
    
    private static class ClassWithNotAccessibleConstructor {
        
        private ClassWithNotAccessibleConstructor() {
        }
        
        private ClassWithNotAccessibleConstructor(String ignored) {
        }
        
    }
    
    private static class ClassWhichThrowsExceptionInConstructors {
        
        public ClassWhichThrowsExceptionInConstructors() {
            throw new RuntimeException();
        }
        
        public ClassWhichThrowsExceptionInConstructors(String ignored) {
            throw new RuntimeException();
        }
        
    }
    
    private static class ClassWithoutDefaultConstructor {
        
        public ClassWithoutDefaultConstructor(String ignored) {
        }
        
    }
    
    private static class SimpleClass {
        
        // Constructors.
        
        public SimpleClass() {
        }
        
        public SimpleClass(String ignored) {
        }
        
    }
    
}
