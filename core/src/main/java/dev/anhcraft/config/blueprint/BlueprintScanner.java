package dev.anhcraft.config.blueprint;

import org.jetbrains.annotations.NotNull;

public interface BlueprintScanner {
    @NotNull Schema scanSchema(@NotNull Class<?> type);
}
