package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.exceptions.InvalidValueException;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class BoundingBoxAdapter implements TypeAdapter<BoundingBox> {
    private boolean inlineSerialization;

    public void inlineSerialization(boolean value) {
        this.inlineSerialization = value;
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull BoundingBox value) throws Exception {
        if (inlineSerialization) {
            return SimpleForm.of(
                    value.getMinX() + " " + value.getMinY() + " " + value.getMinZ() + " " +
                            value.getMaxX() + " " + value.getMaxY() + " " + value.getMaxZ() + " "
            );
        } else {
            ConfigSection cs = serializer.getConfigProvider().createSection();
            cs.set("minX", SimpleForm.of(value.getMinX()));
            cs.set("minY", SimpleForm.of(value.getMinY()));
            cs.set("minZ", SimpleForm.of(value.getMinZ()));
            cs.set("maxX", SimpleForm.of(value.getMaxX()));
            cs.set("maxY", SimpleForm.of(value.getMaxY()));
            cs.set("maxZ", SimpleForm.of(value.getMaxZ()));
            return SimpleForm.of(cs);
        }
    }

    @Override
    public @Nullable BoundingBox complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isString()) {
            String[] str = Objects.requireNonNull(value.asString()).split(" ");
            if (str.length == 6) {
                return new BoundingBox(
                        Double.parseDouble(str[0]),
                        Double.parseDouble(str[1]),
                        Double.parseDouble(str[2]),
                        Double.parseDouble(str[3]),
                        Double.parseDouble(str[4]),
                        Double.parseDouble(str[5])
                );
            } else {
                throw new InvalidValueException("Missing required arguments (x, y, z): " + Arrays.toString(str));
            }
        } else if (value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return new BoundingBox(
                    Optional.ofNullable(cs.get("minX")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("minY")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("minZ")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("maxX")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("maxY")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("maxZ")).map(SimpleForm::asDouble).orElse(0d)
            );
        }
        return null;
    }
}
