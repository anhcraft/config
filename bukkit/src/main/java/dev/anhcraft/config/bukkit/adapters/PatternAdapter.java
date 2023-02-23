package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ClassUtil;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class PatternAdapter implements TypeAdapter<Pattern> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Pattern value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("color", value.getColor().name());
        cs.set("pattern", value.getPattern().name());
        return SimpleForm.of(cs);
    }

    @Override
    public @Nullable Pattern complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return new Pattern(
                    Objects.requireNonNull(Optional.ofNullable(cs.get("color"))
                            .map(SimpleForm::asString)
                            .map(s -> (DyeColor) ClassUtil.findEnum(DyeColor.class, s.toUpperCase()))
                            .orElse(null)),
                    Objects.requireNonNull(Optional.ofNullable(cs.get("pattern"))
                            .map(SimpleForm::asString)
                            .map(s -> (PatternType) ClassUtil.findEnum(PatternType.class, s.toUpperCase()))
                            .orElse(null))
            );
        }
        return null;
    }
}
