package adapters;

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
        return SimpleForm.of("UUID:" + value.toString());
    }

    @Override
    public @Nullable UUID complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws InvalidValueException {
        if (value.isString()) {
            String str = Objects.requireNonNull(value.asString());
            if (str.startsWith("UUID:")) {
                return UUID.fromString(str.substring("UUID:".length()));
            }
        }
        throw new InvalidValueException("Failed to convert to UUID");
    }
}
