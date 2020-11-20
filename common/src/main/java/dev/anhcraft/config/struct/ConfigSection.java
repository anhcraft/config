package dev.anhcraft.config.struct;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ConfigSection {
    boolean isEmpty();

    void set(@NotNull String path, @Nullable SimpleForm value);

    @Nullable
    SimpleForm get(@NotNull String path) throws Exception;

    @NotNull
    Set<String> getKeys(boolean deep);

    @NotNull
    ConfigSection deepClone() throws Exception;

    @NotNull
    String stringify();
}
