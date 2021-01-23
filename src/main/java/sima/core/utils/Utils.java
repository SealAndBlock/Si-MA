package sima.core.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Utils {

    // Methods.

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> Class<? extends T> extractClassForName(String className) throws ClassNotFoundException {
        return (Class<? extends T>) Class.forName(className);
    }

    @NotNull
    public static <T> T instantiate(Class<? extends T> classToInstantiate, Class<?>[] argumentClasses,
                                    Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends T> constructor = classToInstantiate.getConstructor(argumentClasses);
        return constructor.newInstance(args);
    }

    public static <T> T instantiate(Class<? extends T> classToInstantiate)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends T> constructor = classToInstantiate.getConstructor();
        return constructor.newInstance();
    }

}
