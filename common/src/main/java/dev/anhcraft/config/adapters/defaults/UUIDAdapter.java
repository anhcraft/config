package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.exceptions.InvalidValueException;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.UUID;

public class UUIDAdapter implements TypeAdapter<UUID> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull UUID value) throws Exception {
        return SimpleForm.of(value.toString());
    }

    @Override
    public @Nullable UUID complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws InvalidValueException {
        if (value.isString()) {
            return UUID.fromString(Objects.requireNonNull(value.asString()));
        }
        throw new InvalidValueException("Failed to convert to UUID");
    }
}
