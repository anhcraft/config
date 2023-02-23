package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

public class FloatAdapter implements TypeAdapter<Float> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Float value) throws Exception {
        return SimpleForm.of(value);
    }

    @Override
    public @Nullable Float complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isNumber()) {
            return value.asFloat();
        } else if (value.isString()) {
            return Float.parseFloat(Objects.requireNonNull(value.asString()));
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
