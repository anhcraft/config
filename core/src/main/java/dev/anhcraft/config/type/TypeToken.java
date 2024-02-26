package dev.anhcraft.config.type;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A type token captures an arbitrary type using Super Type Token technique.<br>
 * For example: {@code new TypeToken<List<String>>(){}} creates an anonymous subclass of {@link TypeToken}. Since
 * type information retains when extending a generic superclass, the type can be captured using {@link #capture()}
 * @param <T>
 * @see TypeResolver
 */
public abstract class TypeToken<T> extends TypeResolver {

    /**
     * Captures the given type.<br>
     * For example: {@code new TypeToken<List<String>>(){}.capture()} returns {@code List<String>}
     * @return the captured type
     */
    @NotNull
    public final Type capture() {
        Type superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType))
            throw new UnsupportedOperationException("Extending TypeResolver requires a parameterized type");
        return ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    @Override
    public final @NotNull Type provideType() {
        return capture();
    }
}
