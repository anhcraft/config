package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

public class IntegerAdapter implements TypeAdapter<Integer> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Integer value) throws Exception {
        return SimpleForm.of(value);
    }

    @Override
    public @Nullable Integer complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isNumber()) {
            return value.asInt();
        } else if (value.isString()) {
            return Integer.parseInt(Objects.requireNonNull(value.asString()));
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
