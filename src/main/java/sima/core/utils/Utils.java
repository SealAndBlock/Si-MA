package sima.core.utils;

import org.jetbrains.annotations.NotNull;
import sima.core.exception.FailInstantiationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Utils {
    
    // Constructors.
    
    private Utils() {
    }
    
    // Methods.
    
    @SuppressWarnings("unchecked")
    public static @NotNull <T> Class<? extends T> extractClassForName(String className) throws ClassNotFoundException {
        return (Class<? extends T>) Class.forName(className);
    }
    
    @NotNull
    public static <T> T instantiate(Class<? extends T> classToInstantiate, Class<?>[] argumentClasses,
                                    Object... args) throws FailInstantiationException {
        Constructor<? extends T> constructor;
        try {
            constructor = classToInstantiate.getConstructor(argumentClasses);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new FailInstantiationException(e);
        }
    }
    
    public static <T> T instantiate(Class<? extends T> classToInstantiate) throws FailInstantiationException {
        Constructor<? extends T> constructor;
        try {
            constructor = classToInstantiate.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new FailInstantiationException(e);
        }
    }
    
}
