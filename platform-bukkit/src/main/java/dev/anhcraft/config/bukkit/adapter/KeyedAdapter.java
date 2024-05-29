package dev.anhcraft.config.bukkit.adapter;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.type.ComplexTypes;
import java.lang.reflect.Type;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KeyedAdapter implements TypeAdapter<Keyed> {
  public static final KeyedAdapter INSTANCE = new KeyedAdapter();

  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx, @NotNull Class<? extends Keyed> sourceType, @NotNull Keyed value)
      throws Exception {
    return ctx.simplify(ctx, NamespacedKey.class, value.getKey());
  }

  @Override
  public @Nullable Keyed complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof String) {
      //noinspection unchecked
      Class<? extends Keyed> clazz = (Class<? extends Keyed>) ComplexTypes.erasure(targetType);
      NamespacedKey key = (NamespacedKey) ctx.complexify(ctx, value, NamespacedKey.class);
      if (key != null) {
        Registry<? extends Keyed> registry = Bukkit.getRegistry(clazz);
        return registry == null ? null : registry.get(key);
      }
    }
    return null;
  }
}
