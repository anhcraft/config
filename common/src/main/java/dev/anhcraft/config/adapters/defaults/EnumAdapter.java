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
    private boolean preferUppercase = true;

    /**
     * Automatic uppercase enum names.
     *
     * @param value {@code true} or {@code false}
     */
    public void preferUppercase(boolean value) {
        this.preferUppercase = value;
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Enum<?> value) throws Exception {
        return SimpleForm.of(value.name());
    }

    @Override
    public @Nullable Enum<?> complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) {
        if (value.isString()) {
            String str = Objects.requireNonNull(value.asString());
            if (preferUppercase) str = str.toUpperCase();
            // noinspection unchecked
            return (Enum<?>) ClassUtil.findEnum((Class<? extends Enum>) targetType, str);
        } else {
            return null;
        }
    }
}
