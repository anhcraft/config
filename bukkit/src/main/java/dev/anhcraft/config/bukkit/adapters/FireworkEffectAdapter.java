package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.ClassUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class FireworkEffectAdapter implements TypeAdapter<FireworkEffect> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull FireworkEffect value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("type", value.getType().name());
        cs.set("flicker", value.hasFlicker());
        cs.set("trail", value.hasTrail());
        cs.set("colors", serializer.transform(Color[].class, value.getColors()));
        cs.set("fadeColors", serializer.transform(Color[].class, value.getFadeColors()));
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
                    .withColor(getColors("colors", deserializer, cs))
                    .withFade(getColors("fadeColors", deserializer, cs)).build();
        }
        return null;
    }

    private static Color[] getColors(String k, ConfigDeserializer deserializer, ConfigSection cs) throws Exception {
        SimpleForm o = cs.get(k);
        return (o == null || !o.isArray()) ? new Color[0] : deserializer.transform(Color[].class, o);
    }
}
