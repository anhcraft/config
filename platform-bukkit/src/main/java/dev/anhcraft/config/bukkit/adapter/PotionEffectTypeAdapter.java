package dev.anhcraft.config.bukkit.adapter;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class PotionEffectTypeAdapter implements TypeAdapter<PotionEffectType> {
  public static final PotionEffectTypeAdapter INSTANCE = new PotionEffectTypeAdapter();

  @Override
  public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<? extends PotionEffectType> sourceType, @NotNull PotionEffectType value) throws Exception {
    return value.getName();
  }

  @Override
  public @Nullable PotionEffectType complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof String)
      return PotionEffectType.getByName(String.valueOf(value));
    return null;
  }
}
