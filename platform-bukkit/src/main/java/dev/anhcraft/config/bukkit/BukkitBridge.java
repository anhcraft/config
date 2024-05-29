package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.bukkit.adapter.ColorAdapter;
import dev.anhcraft.config.bukkit.adapter.ConfigurationSerializableAdapter;
import dev.anhcraft.config.bukkit.adapter.KeyedAdapter;
import dev.anhcraft.config.bukkit.adapter.NamespacedKeyAdapter;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

/**
 * Contains bridge utilities to integrate Bukkit.
 */
public class BukkitBridge {

  /**
   * Adapts type adapters facilitating compatibility with Bukkit API.
   * @param builder the factory builder
   */
  public static void adaptTypes(@NotNull ConfigFactory.Builder builder) {
    builder.adaptType(ConfigurationSerializable.class, ConfigurationSerializableAdapter.INSTANCE);

    // [Color] Override ConfigurationSerializable due to naming
    builder.adaptType(Color.class, ColorAdapter.INSTANCE);

    if (MinecraftVersion.since(MinecraftVersion.v1_19))
      builder.adaptType(org.bukkit.Keyed.class, KeyedAdapter.INSTANCE);

    if (MinecraftVersion.since(MinecraftVersion.v1_12))
      builder.adaptType(org.bukkit.NamespacedKey.class, NamespacedKeyAdapter.INSTANCE);
  }
}
