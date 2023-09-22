package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnchantmentAdapter implements TypeAdapter<Enchantment> {
    private static final Map<String, Enchantment> INDEXES = new HashMap<>();

    static {
        for (Enchantment e : Enchantment.values()) {
            String i = e.getName().toLowerCase();
            INDEXES.put(i, e);
            INDEXES.put(i.replace("_", ""), e);
            INDEXES.put(i.replace("_", " "), e);
            INDEXES.put(i.replace("_", "-"), e);
        }
        INDEXES.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        INDEXES.put("fire protection", Enchantment.PROTECTION_FIRE);
        INDEXES.put("feather falling", Enchantment.PROTECTION_FALL);
        INDEXES.put("blast protection", Enchantment.PROTECTION_EXPLOSIONS);
        INDEXES.put("projectile protection", Enchantment.PROTECTION_PROJECTILE);
        INDEXES.put("respiration", Enchantment.OXYGEN);
        INDEXES.put("aqua affinity", Enchantment.WATER_WORKER);
        INDEXES.put("sharpness", Enchantment.DAMAGE_ALL);
        INDEXES.put("smite", Enchantment.DAMAGE_UNDEAD);
        INDEXES.put("bane of arthropods", Enchantment.DAMAGE_ARTHROPODS);
        INDEXES.put("looting", Enchantment.LOOT_BONUS_MOBS);
        INDEXES.put("efficiency", Enchantment.DIG_SPEED);
        INDEXES.put("unbreaking", Enchantment.DURABILITY);
        INDEXES.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
        INDEXES.put("power", Enchantment.ARROW_DAMAGE);
        INDEXES.put("punch", Enchantment.ARROW_KNOCKBACK);
        INDEXES.put("flame", Enchantment.ARROW_FIRE);
        INDEXES.put("infinity", Enchantment.ARROW_INFINITE);
        INDEXES.put("luck of the sea", Enchantment.LUCK);
        INDEXES.put("curse of binding", Enchantment.BINDING_CURSE);
        INDEXES.put("curse of vanishing", Enchantment.VANISHING_CURSE);
    }

    @Override
    @Nullable
    public SimpleForm simplify(@NotNull ConfigSerializer configSerializer, @NotNull Type type, @NotNull Enchantment enchantment) throws Exception {
        return SimpleForm.of(enchantment.getName().toLowerCase());
    }

    @Override
    @Nullable
    public Enchantment complexify(@NotNull ConfigDeserializer configDeserializer, @NotNull Type type, @NotNull SimpleForm simpleForm) throws Exception {
        if (simpleForm.isString()) {
            String id = Objects.requireNonNull(simpleForm.asString()).trim();
            Enchantment enc = INDEXES.get(id.toLowerCase());
            if (enc == null)
                enc = Enchantment.getByName(id.toUpperCase());
            return enc;
        }
        return null;
    }
}
