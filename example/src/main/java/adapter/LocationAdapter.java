package adapter;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import model.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class LocationAdapter implements TypeAdapter<Location> {
  @Override
  public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<? extends Location> sourceType, @NotNull Location value) throws Exception {
    return value.x + "," + value.y;
  }

  @Override
  public @Nullable Location complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    if (value instanceof String) {
      String[] xy = ((String) value).split(",");
      return new Location(Integer.parseInt(xy[0]), Integer.parseInt(xy[1]));
    }
    return null;
  }
}
