package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class PotionEffectAdapter implements TypeAdapter<PotionEffect> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull PotionEffect value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("type", SimpleForm.of(value.getType().getName()));
        cs.set("duration", SimpleForm.of(value.getDuration()));
        cs.set("amplifier", SimpleForm.of(value.getAmplifier()));
        cs.set("ambient", SimpleForm.of(value.isAmbient()));
        cs.set("particles", SimpleForm.of(value.hasParticles()));
        cs.set("icon", SimpleForm.of(value.hasIcon()));
        return SimpleForm.of(cs);
    }

    @Override
    public @Nullable PotionEffect complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return new PotionEffect(
                    Objects.requireNonNull(Optional.ofNullable(cs.get("type"))
                            .map(SimpleForm::asString)
                            .map(s -> PotionEffectType.getByName(s.toUpperCase()))
                            .orElse(null)),
                    Optional.ofNullable(cs.get("duration")).map(SimpleForm::asInt).orElse(0),
                    Optional.ofNullable(cs.get("amplifier")).map(SimpleForm::asInt).orElse(0),
                    Optional.ofNullable(cs.get("ambient")).map(SimpleForm::asBoolean).orElse(false),
                    Optional.ofNullable(cs.get("particles")).map(SimpleForm::asBoolean).orElse(false),
                    Optional.ofNullable(cs.get("icon")).map(SimpleForm::asBoolean).orElse(false)
            );
        }
        return null;
    }
}
