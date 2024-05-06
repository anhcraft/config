package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigHandler;
import dev.anhcraft.config.bukkit.adapters.*;
import org.bukkit.*;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class BukkitAdapters {
    public static void registerFor(@NotNull ConfigHandler handler) {
        handler.registerTypeAdapter(Location.class, new LocationAdapter());
        handler.registerTypeAdapter(Vector.class, new VectorAdapter());
        handler.registerTypeAdapter(AttributeModifier.class, new AttributeModifierAdapter());
        handler.registerTypeAdapter(BlockVector.class, new BlockVectorAdapter());
        handler.registerTypeAdapter(Pattern.class, new PatternAdapter());
        handler.registerTypeAdapter(Color.class, new ColorAdapter());
        handler.registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter());
        handler.registerTypeAdapter(FireworkEffect.class, new FireworkEffectAdapter());
        handler.registerTypeAdapter(ItemStack.class, new ItemStackAdapter());

        if (NMSVersion.current().atLeast(NMSVersion.v1_12_R1)) {
            handler.registerTypeAdapter(org.bukkit.NamespacedKey.class, new NamespacedKeyAdapter());
        }

        if (NMSVersion.current().atLeast(NMSVersion.v1_13_R1)) {
            handler.registerTypeAdapter(org.bukkit.util.BoundingBox.class, new BoundingBoxAdapter());
        }

        try {
            // Prevent initialization exception when testing
            Field field = Bukkit.class.getDeclaredField("server");
            field.setAccessible(true);
            if (field.get(null) != null) {
                handler.registerTypeAdapter(Enchantment.class, new EnchantmentAdapter());

                if (NMSVersion.current().atLeast(NMSVersion.v1_20_R1)) {
                    handler.registerTypeAdapter(org.bukkit.inventory.meta.trim.ArmorTrim.class, new ArmorTrimAdapter());
                }
            }
        } catch (Exception ignored) {}
    }
}
