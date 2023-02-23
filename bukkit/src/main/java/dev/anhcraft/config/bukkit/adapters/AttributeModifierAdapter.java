package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ClassUtil;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AttributeModifierAdapter implements TypeAdapter<AttributeModifier> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull AttributeModifier value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("id", value.getUniqueId().toString());
        cs.set("name", value.getName());
        cs.set("amount", value.getAmount());
        cs.set("operation", value.getOperation().name());
        return SimpleForm.of(cs);
    }

    @Override
    public @Nullable AttributeModifier complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return new AttributeModifier(
                    Objects.requireNonNull(
                            Optional.ofNullable(cs.get("id"))
                                    .map(SimpleForm::asString)
                                    .map(UUID::fromString)
                                    .orElse(null)
                    ),
                    Objects.requireNonNull(
                            Optional.ofNullable(cs.get("name"))
                                    .map(SimpleForm::asString)
                                    .orElse(null)
                    ),
                    Optional.ofNullable(cs.get("amount"))
                            .map(SimpleForm::asDouble)
                            .orElse(0d),
                    Objects.requireNonNull(
                            Optional.ofNullable(cs.get("operation"))
                                    .map(SimpleForm::asString)
                                    .map(s -> (AttributeModifier.Operation) ClassUtil.findEnum(AttributeModifier.Operation.class, s.toUpperCase()))
                                    .orElse(null)
                    )
            );
        }
        return null;
    }
}
