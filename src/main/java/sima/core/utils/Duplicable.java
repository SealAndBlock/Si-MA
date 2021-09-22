package sima.core.utils;

import org.jetbrains.annotations.NotNull;

public interface Duplicable<T> {
    
    @NotNull T duplicate();
    
}
