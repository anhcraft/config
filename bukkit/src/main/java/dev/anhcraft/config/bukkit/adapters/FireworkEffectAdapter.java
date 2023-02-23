package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ClassUtil;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class FireworkEffectAdapter implements TypeAdapter<FireworkEffect> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull FireworkEffect value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("type", SimpleForm.of(value.getType().name()));
        cs.set("flicker", SimpleForm.of(value.hasFlicker()));
        cs.set("trail", SimpleForm.of(value.hasTrail()));
        cs.set("colors", SimpleForm.of(value.getColors()));
        cs.set("fadeColors", SimpleForm.of(value.getFadeColors()));
        return SimpleForm.of(cs);
    }

    @Override
    public @Nullable FireworkEffect complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return FireworkEffect.builder().with(Objects.requireNonNull(Optional.ofNullable(cs.get("type"))
                            .map(SimpleForm::asString)
                            .map(s -> (FireworkEffect.Type) ClassUtil.findEnum(FireworkEffect.Type.class, s.toUpperCase()))
                            .orElse(null)))
                    .flicker(Optional.ofNullable(cs.get("flicker")).map(SimpleForm::asBoolean).orElse(false))
                    .trail(Optional.ofNullable(cs.get("trail")).map(SimpleForm::asBoolean).orElse(false))
                    .withColor(Optional.ofNullable(cs.get("colors")).map(SimpleForm::asList).orElse(new ArrayList<>()))
                    .withFade(Optional.ofNullable(cs.get("fadeColors")).map(SimpleForm::asList).orElse(new ArrayList<>()))
                    .build();
        }
        return null;
    }
}
