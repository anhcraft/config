package dev.anhcraft.config.bukkit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum NMSVersion {
    v1_9_R2,
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
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
    v1_19_R3,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3,
    v1_21_R1;

    private static NMSVersion current;

    static {
        current = NMSVersion.values()[NMSVersion.values().length - 1];
        //noinspection ConstantConditions
        try {
          //noinspection ConstantValue
          if (Bukkit.getServer() != null) { // pass test
                String[] args = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");
                if (args.length >= 4)
                    current = NMSVersion.valueOf(args[3]);
            }
        } catch (Exception ignored) {} // use default if not found
    }

    @NotNull
    public static NMSVersion current() {
        return current;
    }

    public int compare(@NotNull NMSVersion another) {
        return ordinal() - another.ordinal();
    }

    public boolean atLeast(@NotNull NMSVersion another) {
        return compare(another) >= 0;
    }
}
