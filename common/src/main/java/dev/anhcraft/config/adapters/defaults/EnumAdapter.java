package dev.anhcraft.config.adapters.defaults;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

public class EnumAdapter implements TypeAdapter<Enum<?>> {
    private boolean lowerCaseOnSerialize = true;
    private boolean upperCaseOnDeserialize = true;

    public void lowerCaseOnSerialize(boolean lowerCaseOnSerialize) {
        this.lowerCaseOnSerialize = lowerCaseOnSerialize;
    }

    public void uppercaseOnDeserialize(boolean upperCaseOnDeserialize) {
        this.upperCaseOnDeserialize = upperCaseOnDeserialize;
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Enum<?> value) throws Exception {
        return SimpleForm.of(lowerCaseOnSerialize ? value.name().toLowerCase() : value.name());
    }

    @Override
    public @Nullable Enum<?> complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) {
        if (value.isString()) {
            String str = Objects.requireNonNull(value.asString());
            if (upperCaseOnDeserialize) str = str.toUpperCase();
            // noinspection unchecked
            return (Enum<?>) ClassUtil.findEnum((Class<? extends Enum>) targetType, str);
        } else {
            return null;
        }
    }
}
