package dev.anhcraft.config.bukkit.adapter;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.SchemalessDictionary;
import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class ColorAdapter implements TypeAdapter<Color> {
  public static final ColorAdapter INSTANCE = new ColorAdapter();

  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx, @NotNull Class<? extends Color> sourceType, @NotNull Color value)
      throws Exception {
    return SchemalessDictionary.create()
      .put("red", value.getRed())
      .put("green", value.getGreen())
      .put("blue", value.getBlue())
      .put("alpha", value.getAlpha())
      .build();
  }

  @Override
  public @Nullable Color complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof String) {
      String[] str = ((String) value).split("\\s+");
      if (str.length != 3 && str.length != 4) {
        throw new InvalidValueException(ctx, "Invalid arguments (red, green, blue, alpha)");
      }
      Integer r = (Integer) ctx.complexify(str[0], Integer.class);
      if (r == null) {
        throw new InvalidValueException(ctx, "Invalid red value: " + str[0]);
      }
      Integer g = (Integer) ctx.complexify(str[1], Integer.class);
      if (g == null) {
        throw new InvalidValueException(ctx, "Invalid green value: " + str[1]);
      }
      Integer b = (Integer) ctx.complexify(str[2], Integer.class);
      if (b == null) {
        throw new InvalidValueException(ctx, "Invalid blue value: " + str[2]);
      }
      Integer a = 255;
      if (str.length == 4) {
        a = (Integer) ctx.complexify(str[3], Integer.class);
        if (a == null) {
          throw new InvalidValueException(ctx, "Invalid alpha value: " + str[3]);
        }
      }
      return Color.fromARGB(a, r, g, b);
    } else if (value instanceof Dictionary) {
      Dictionary dict = (Dictionary) value;
      return Color.fromARGB(
        (Integer) dict.getOrDefault("alpha", 255),
        (Integer) dict.getOrDefault("red", 0),
        (Integer) dict.getOrDefault("green", 0),
        (Integer) dict.getOrDefault("blue", 0)
      );
    }
    return null;
  }
}
