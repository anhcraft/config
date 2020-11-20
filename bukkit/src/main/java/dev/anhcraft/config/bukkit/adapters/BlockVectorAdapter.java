package dev.anhcraft.config.bukkit.adapters;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.exceptions.InvalidValueException;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class BlockVectorAdapter implements TypeAdapter<BlockVector> {
    private boolean inlineSerialization;

    public void inlineSerialization(boolean value) {
        this.inlineSerialization = value;
    }

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull BlockVector value) throws Exception {
        if(inlineSerialization) {
            return SimpleForm.of(value.getX() + " " + value.getY() + " " + value.getZ());
        } else {
            ConfigSection cs = serializer.getConfigProvider().createSection();
            cs.set("x", SimpleForm.of(value.getX()));
            cs.set("y", SimpleForm.of(value.getY()));
            cs.set("z", SimpleForm.of(value.getZ()));
            return SimpleForm.of(cs);
        }
    }

    @Override
    public @Nullable BlockVector complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isString()) {
            String[] str = Objects.requireNonNull(value.asString()).split(" ");
            if(str.length < 3) {
                throw new InvalidValueException("Missing required arguments (x, y, z): " + Arrays.toString(str));
            } else {
                return new BlockVector(
                        Double.parseDouble(str[0]),
                        Double.parseDouble(str[1]),
                        Double.parseDouble(str[2])
                );
            }
        } else if(value.isSection()) {
            ConfigSection cs = Objects.requireNonNull(value.asSection());
            return new BlockVector(
                    Optional.ofNullable(cs.get("x")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("y")).map(SimpleForm::asDouble).orElse(0d),
                    Optional.ofNullable(cs.get("z")).map(SimpleForm::asDouble).orElse(0d)
            );
        }
        return null;
    }
}
