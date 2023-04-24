package dev.anhcraft.config.bukkit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum NMSVersion {
    v1_14_R1,
    v1_15_R1,
    v1_16_R1,
    v1_16_R2,
    v1_16_R3,
    v1_17_R1,
    v1_18_R1,
    v1_18_R2,
    v1_19_R1,
    v1_19_R2,
    v1_19_R3;

    private static final NMSVersion current;

    static {
        //noinspection ConstantConditions
        if (Bukkit.getServer() != null) {
            current = NMSVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
        } else {
            current = NMSVersion.values()[0];
        }
    }

    @NotNull
    public static NMSVersion current() {
        return current;
    }

    public int compare(@NotNull NMSVersion another) {
        return ordinal() - another.ordinal();
    }
}
