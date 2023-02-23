package dev.anhcraft.config.struct;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface ConfigSection {
    boolean isEmpty();

    void set(@NotNull String path, @Nullable SimpleForm value);

    default void del(@NotNull String path) throws Exception {
        set(path, SimpleForm.of(null));
    }

    default void set(@NotNull String path, ConfigSection value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, int value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, boolean value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, long value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, byte value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, short value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, float value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, double value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, char value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, String value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, int[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, boolean[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, long[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, byte[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, short[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, float[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, double[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default void set(@NotNull String path, char[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    default <T> void set(@NotNull String path, T[] value) throws Exception {
        set(path, SimpleForm.of(value));
    }

    @Nullable
    SimpleForm get(@NotNull String path) throws Exception;

    @NotNull
    Set<String> getKeys(boolean deep);

    @NotNull
    ConfigSection deepClone() throws Exception;

    @NotNull
    String stringify();
}
