package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class CharacterAdapter implements TypeAdapter<Character> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Character value) throws Exception {
        return SimpleForm.of(value);
    }

    @Override
    public @Nullable Character complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if(value.isCharacter()) {
            return value.asCharacter();
        } else if(value.isString()) {
            return Objects.requireNonNull(value.asString()).charAt(0);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
