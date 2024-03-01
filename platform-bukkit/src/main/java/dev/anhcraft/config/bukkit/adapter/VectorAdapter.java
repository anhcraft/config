package dev.anhcraft.config.bukkit.adapter;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.InvalidValueException;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

import static dev.anhcraft.config.bukkit.util.BukkitUtil.format;

public class VectorAdapter implements TypeAdapter<Vector> {
    public static final VectorAdapter INSTANCE = new VectorAdapter();

    @Override
    public @Nullable Object simplify(@NotNull Context ctx, @NotNull Class<? extends Vector> sourceType, @NotNull Vector value) throws Exception {
        return format(value.getX()) + " " + format(value.getY()) + " " + format(value.getZ());
    }

    @Override
    public @Nullable Vector complexify(@NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
        if (value instanceof String) {
            String[] str = ((String) value).split("\\s+");
            if (str.length < 3) {
                throw new InvalidValueException(ctx, "Missing required arguments (x, y, z)");
            }
            Double x = (Double) ctx.complexify(str[0], Double.class);
            if (x == null) {
                throw new InvalidValueException(ctx, "Invalid x value: " + str[0]);
            }
            Double y = (Double) ctx.complexify(str[1], Double.class);
            if (y == null) {
                throw new InvalidValueException(ctx, "Invalid y value: " + str[1]);
            }
            Double z = (Double) ctx.complexify(str[2], Double.class);
            if (z == null) {
                throw new InvalidValueException(ctx, "Invalid z value: " + str[2]);
            }
            return new Vector(x, y, z);
        }
        return null;
    }
}
