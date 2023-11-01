package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ArmorTrimAdapter implements TypeAdapter<ArmorTrim> {
    private static final Map<String, TrimMaterial> TRIM_MATERIALS = new HashMap<>();
    private static final Map<String, TrimPattern> TRIM_PATTERNS = new HashMap<>();

    static {
        try {
            for (Field field : TrimMaterial.class.getDeclaredFields()) {
                if (field.getType().isAssignableFrom(TrimMaterial.class)) {
                    TRIM_MATERIALS.put(field.getName().toUpperCase(), (TrimMaterial) field.get(null));
                }
            }
            for (Field field : TrimPattern.class.getDeclaredFields()) {
                if (field.getType().isAssignableFrom(TrimPattern.class)) {
                    TRIM_PATTERNS.put(field.getName().toUpperCase(), (TrimPattern) field.get(null));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull ArmorTrim value) throws Exception {
        ConfigSection cs = serializer.getConfigProvider().createSection();
        cs.set("material", value.getMaterial().getKey().toString());
        cs.set("pattern", value.getPattern().getKey().toString());
        return SimpleForm.of(cs);
    }

    @Override
    public @Nullable ArmorTrim complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            ConfigSection cs = requireNonNull(value.asSection());
            SimpleForm material = cs.get("material");
            SimpleForm pattern = cs.get("pattern");

            if (material != null && pattern != null && material.isString() && pattern.isString()) {
                String materialKey = requireNonNull(material.asString());
                String patternKey = requireNonNull(pattern.asString());

                TrimMaterial materialValue = TRIM_MATERIALS.get(materialKey);
                if (materialValue == null)
                    materialValue = requireNonNull(Bukkit.getRegistry(TrimMaterial.class)).get(requireNonNull(NamespacedKey.fromString(materialKey)));

                TrimPattern patternValue = TRIM_PATTERNS.get(patternKey);
                if (patternValue == null)
                    patternValue = requireNonNull(Bukkit.getRegistry(TrimPattern.class)).get(requireNonNull(NamespacedKey.fromString(patternKey)));

                if (materialValue != null && patternValue != null)
                    return new ArmorTrim(materialValue, patternValue);
            }
        }
        return null;
    }
}
