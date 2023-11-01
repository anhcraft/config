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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class URIAdapter implements TypeAdapter<URI> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull URI value) throws Exception {
        return SimpleForm.of(value.toString());
    }

    @Override
    public @Nullable URI complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws InvalidValueException {
        if (value.isString()) {
            try {
                return new URI(Objects.requireNonNull(value.asString()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        throw new InvalidValueException("Failed to convert to URL");
    }
}
