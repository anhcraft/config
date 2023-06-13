package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigHandler;
import dev.anhcraft.config.bukkit.adapters.*;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BukkitAdapters {
    public static void registerFor(@NotNull ConfigHandler handler) {
        handler.registerTypeAdapter(Location.class, new LocationAdapter());
        handler.registerTypeAdapter(Vector.class, new VectorAdapter());
        handler.registerTypeAdapter(AttributeModifier.class, new AttributeModifierAdapter());
        handler.registerTypeAdapter(BlockVector.class, new BlockVectorAdapter());
        handler.registerTypeAdapter(Pattern.class, new PatternAdapter());
        handler.registerTypeAdapter(BoundingBox.class, new BoundingBoxAdapter());
        handler.registerTypeAdapter(Color.class, new ColorAdapter());
        handler.registerTypeAdapter(PotionEffect.class, new PotionEffectAdapter());
        handler.registerTypeAdapter(FireworkEffect.class, new FireworkEffectAdapter());
        handler.registerTypeAdapter(Enchantment.class, new EnchantmentAdapter());
        handler.registerTypeAdapter(NamespacedKey.class, new NamespacedKeyAdapter());
        handler.registerTypeAdapter(ItemStack.class, new ItemStackAdapter());
    }
}
