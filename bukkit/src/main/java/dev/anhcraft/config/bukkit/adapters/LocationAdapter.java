package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.exceptions.InvalidValueException;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import dev.anhcraft.config.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class LocationAdapter implements TypeAdapter<Location> {
    private boolean inlineSerialization;

    public void inlineSerialization(boolean value) {
        this.inlineSerialization = value;
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Location value) throws Exception {
        if (inlineSerialization) {
            return SimpleForm.of(
                    (value.getWorld() != null ? value.getWorld().getName() + " " : "") +
                            value.getX() + " " + value.getY() + " " + value.getZ() + " " + value.getYaw() + " " + value.getPitch()
            );
        } else {
            ConfigSection cs = serializer.getConfigProvider().createSection();
            cs.set("x",value.getX());
            cs.set("y", value.getY());
            cs.set("z", value.getZ());
            cs.set("yaw", value.getYaw());
            cs.set("pitch", value.getPitch());
            if (value.getWorld() != null) {
                cs.set("world", value.getWorld().getName());
            }
            return SimpleForm.of(cs);
        }
    }

    @Override
    public @Nullable Location complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isString()) {
            String[] str = Objects.requireNonNull(value.asString()).split(" ");
            if (str.length < 3) {
                throw new InvalidValueException("Missing required arguments (x, y, z): " + Arrays.toString(str));
            } else {
                World world = null;
                double x, y, z;
                float yaw = 0, pitch = 0;
                if (StringUtil.isNumber(str[0])) {
                    x = Double.parseDouble(str[0]);
                    y = Double.parseDouble(str[1]);
                    z = Double.parseDouble(str[2]);
                    if (str.length >= 4) {
                        yaw = Float.parseFloat(str[3]);
                    }
                    if (str.length >= 5) {
                        pitch = Float.parseFloat(str[4]);
                    }
                } else if (str.length == 3) {
                    throw new InvalidValueException("Missing required arguments (world, x, y, z): " + Arrays.toString(str));
                } else {
                    world = Bukkit.getWorld(str[0]);
                    x = Double.parseDouble(str[1]);
                    y = Double.parseDouble(str[2]);
                    z = Double.parseDouble(str[3]);
                    if (str.length >= 5) {
                        yaw = Float.parseFloat(str[4]);
                    }
                    if (str.length >= 6) {
                        pitch = Float.parseFloat(str[5]);
                    }
                }
                return new Location(world, x, y, z, yaw, pitch);
            }
        } else if (value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return new Location(
                    Optional.ofNullable(cs.get("world")).map(SimpleForm::asString).map(Bukkit::getWorld).orElse(null),
                    Optional.ofNullable(cs.get("x")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("y")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("z")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("yaw")).map(SimpleForm::asFloat).orElse(0f),
                    Optional.ofNullable(cs.get("pitch")).map(SimpleForm::asFloat).orElse(0f)
            );
        }
        return null;
    }
}
