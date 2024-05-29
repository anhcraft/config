package dev.anhcraft.config.bukkit.adapter;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NamespacedKeyAdapter implements TypeAdapter<NamespacedKey> {
  public static final NamespacedKeyAdapter INSTANCE = new NamespacedKeyAdapter();

  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx,
      @NotNull Class<? extends NamespacedKey> sourceType,
      @NotNull NamespacedKey value)
      throws Exception {
    return value.toString();
  }

  @Override
  public @Nullable NamespacedKey complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    return NamespacedKey.fromString(value.toString());
  }
}
