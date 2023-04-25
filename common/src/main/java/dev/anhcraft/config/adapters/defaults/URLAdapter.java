package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.exceptions.InvalidValueException;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class URLAdapter implements TypeAdapter<URL> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull URL value) throws Exception {
        return SimpleForm.of(value.toString());
    }

    @Override
    public @Nullable URL complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws InvalidValueException {
        if (value.isString()) {
            try {
                return new URL(Objects.requireNonNull(value.asString()));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        throw new InvalidValueException("Failed to convert to URL");
    }
}
