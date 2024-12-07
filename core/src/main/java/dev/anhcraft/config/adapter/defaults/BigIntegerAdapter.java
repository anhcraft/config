package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import java.math.BigInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BigIntegerAdapter implements TypeAdapter<BigInteger> {
  public static final BigIntegerAdapter INSTANCE = new BigIntegerAdapter();

  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx,
      @NotNull Class<? extends BigInteger> sourceType,
      @NotNull BigInteger value) {
    return value.toString();
  }

  @Override
  public @Nullable BigInteger complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) {
    if (value instanceof Number) {
      return BigInteger.valueOf(((Number) value).longValue());
    } else if (value instanceof String) {
      return new BigInteger((String) value);
    }
    return null;
  }
}
