package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.bukkit.adapter.PotionEffectAdapter;
import dev.anhcraft.config.bukkit.adapter.VectorAdapter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class BukkitBridge {
  public static void adaptTypes(ConfigFactory.Builder builder) {
    builder.adaptType(Vector.class, VectorAdapter.INSTANCE);
    builder.adaptType(PotionEffect.class, PotionEffectAdapter.INSTANCE);
  }
}
