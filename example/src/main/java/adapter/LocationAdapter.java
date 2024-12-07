package adapter;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import java.lang.reflect.Type;
import model.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationAdapter implements TypeAdapter<Location> {
  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx,
      @NotNull Class<? extends Location> sourceType,
      @NotNull Location value) {
    return value.getX() + "," + value.getY();
  }

  @Override
  public @Nullable Location complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) {
    if (value instanceof String) {
      String[] xy = ((String) value).split(",", 2);
      return new Location(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
    }
    return null;
  }
}
