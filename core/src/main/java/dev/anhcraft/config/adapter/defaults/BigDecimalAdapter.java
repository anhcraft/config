package dev.anhcraft.config.adapter.defaults;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.MathContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BigDecimalAdapter implements TypeAdapter<BigDecimal> {
  public static final BigDecimalAdapter INSTANCE = new BigDecimalAdapter(MathContext.DECIMAL128);
  private final MathContext mathContext;

  public BigDecimalAdapter(@NotNull MathContext mathContext) {
    this.mathContext = mathContext;
  }

  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx,
      @NotNull Class<? extends BigDecimal> sourceType,
      @NotNull BigDecimal value)
      throws Exception {
    return value.toString();
  }

  @Override
  public @Nullable BigDecimal complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof Double || value instanceof Float) {
      //noinspection UnpredictableBigDecimalConstructorCall
      return new BigDecimal(((Number) value).doubleValue(), mathContext);
    } else if (value instanceof Long) {
      return new BigDecimal((Long) value, mathContext);
    } else if (value instanceof Number) {
      return new BigDecimal(((Number) value).intValue(), mathContext);
    } else if (value instanceof String) {
      return new BigDecimal((String) value, mathContext);
    }
    return null;
  }
}
