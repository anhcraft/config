package dev.anhcraft.config.type;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeToken<T> extends TypeResolver {

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
