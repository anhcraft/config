package dev.anhcraft.config.bukkit;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum NMSVersion {
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    v1_9_R1,
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
    v1_19_R1;

    private static final NMSVersion current;

    static {
        //noinspection ConstantConditions
        if(Bukkit.getServer() != null) {
            current = NMSVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3]);
        } else {
            current = NMSVersion.values()[0];
        }
    }

    public int compare(@NotNull NMSVersion another){
        return ordinal() - another.ordinal();
    }

    @NotNull
    public static NMSVersion current(){
        return current;
    }
}
