package dev.anhcraft.config.bukkit.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BukkitUtil {
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");

  static {
    DECIMAL_FORMAT.setRoundingMode(RoundingMode.FLOOR);
  }

  public static String format(double value) {
    return DECIMAL_FORMAT.format(value);
  }
}
