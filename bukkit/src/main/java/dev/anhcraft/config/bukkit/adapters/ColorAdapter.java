package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class ColorAdapter implements TypeAdapter<Color> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Color value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("red", SimpleForm.of(value.getRed()));
        cs.set("green", SimpleForm.of(value.getGreen()));
        cs.set("blue", SimpleForm.of(value.getBlue()));
        return SimpleForm.of(cs);
    }

    @Override
    public @Nullable Color complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if(value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return Color.fromRGB(
                    Optional.ofNullable(cs.get("red")).map(SimpleForm::asInt).orElse(0),
                    Optional.ofNullable(cs.get("green")).map(SimpleForm::asInt).orElse(0),
                    Optional.ofNullable(cs.get("blue")).map(SimpleForm::asInt).orElse(0)
            );
        }
        return null;
    }
}
