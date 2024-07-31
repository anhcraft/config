package dev.anhcraft.config.bukkit;

import dev.anhcraft.config.util.StringUtil;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a version of Minecraft with major, minor, and patch components.
 */
public class MinecraftVersion implements Comparable<MinecraftVersion> {
  public static final MinecraftVersion CURRENT;

  static {
    CURRENT = parse(Bukkit.getBukkitVersion());
  }

  // START: generated using config/tools
  public static final MinecraftVersion v1_21 = new MinecraftVersion(1, 21, 0);
  public static final MinecraftVersion v1_20_6 = new MinecraftVersion(1, 20, 6);
  public static final MinecraftVersion v1_20_5 = new MinecraftVersion(1, 20, 5);
  public static final MinecraftVersion v1_20_4 = new MinecraftVersion(1, 20, 4);
  public static final MinecraftVersion v1_20_3 = new MinecraftVersion(1, 20, 3);
  public static final MinecraftVersion v1_20_2 = new MinecraftVersion(1, 20, 2);
  public static final MinecraftVersion v1_20_1 = new MinecraftVersion(1, 20, 1);
  public static final MinecraftVersion v1_20 = new MinecraftVersion(1, 20, 0);
  public static final MinecraftVersion v1_19_4 = new MinecraftVersion(1, 19, 4);
  public static final MinecraftVersion v1_19_3 = new MinecraftVersion(1, 19, 3);
  public static final MinecraftVersion v1_19_2 = new MinecraftVersion(1, 19, 2);
  public static final MinecraftVersion v1_19_1 = new MinecraftVersion(1, 19, 1);
  public static final MinecraftVersion v1_19 = new MinecraftVersion(1, 19, 0);
  public static final MinecraftVersion v1_18_2 = new MinecraftVersion(1, 18, 2);
  public static final MinecraftVersion v1_18_1 = new MinecraftVersion(1, 18, 1);
  public static final MinecraftVersion v1_18 = new MinecraftVersion(1, 18, 0);
  public static final MinecraftVersion v1_17_1 = new MinecraftVersion(1, 17, 1);
  public static final MinecraftVersion v1_17 = new MinecraftVersion(1, 17, 0);
  public static final MinecraftVersion v1_16_5 = new MinecraftVersion(1, 16, 5);
  public static final MinecraftVersion v1_16_4 = new MinecraftVersion(1, 16, 4);
  public static final MinecraftVersion v1_16_3 = new MinecraftVersion(1, 16, 3);
  public static final MinecraftVersion v1_16_2 = new MinecraftVersion(1, 16, 2);
  public static final MinecraftVersion v1_16_1 = new MinecraftVersion(1, 16, 1);
  public static final MinecraftVersion v1_16 = new MinecraftVersion(1, 16, 0);
  public static final MinecraftVersion v1_15_2 = new MinecraftVersion(1, 15, 2);
  public static final MinecraftVersion v1_15_1 = new MinecraftVersion(1, 15, 1);
  public static final MinecraftVersion v1_15 = new MinecraftVersion(1, 15, 0);
  public static final MinecraftVersion v1_14_4 = new MinecraftVersion(1, 14, 4);
  public static final MinecraftVersion v1_14_3 = new MinecraftVersion(1, 14, 3);
  public static final MinecraftVersion v1_14_2 = new MinecraftVersion(1, 14, 2);
  public static final MinecraftVersion v1_14_1 = new MinecraftVersion(1, 14, 1);
  public static final MinecraftVersion v1_14 = new MinecraftVersion(1, 14, 0);
  public static final MinecraftVersion v1_13_2 = new MinecraftVersion(1, 13, 2);
  public static final MinecraftVersion v1_13_1 = new MinecraftVersion(1, 13, 1);
  public static final MinecraftVersion v1_13 = new MinecraftVersion(1, 13, 0);
  public static final MinecraftVersion v1_12_2 = new MinecraftVersion(1, 12, 2);
  public static final MinecraftVersion v1_12_1 = new MinecraftVersion(1, 12, 1);
  public static final MinecraftVersion v1_12 = new MinecraftVersion(1, 12, 0);

  // END: generated using config/tools

  /**
   * Parses the given version string into a {@code MinecraftVersion}.<br>
   * The string must have the following prefix: {@code X.Y.Z}. Release channels, commit hash and other
   * build identifiers must come after the first hyphen and are ignored, for example: {@code 1.18.2-SNAPSHOT}.
   * @param version the version string
   * @return the {@code MinecraftVersion} that was parsed
   */
  public static @NotNull MinecraftVersion parse(@NotNull String version) {
    String ver = StringUtil.fastSplit(version, '-').get(0);
    List<String> parts = StringUtil.fastSplit(ver, '.');
    return new MinecraftVersion(
        !parts.isEmpty() ? Byte.parseByte(parts.get(0)) : 0,
        parts.size() > 1 ? Byte.parseByte(parts.get(1)) : 0,
        parts.size() > 2 ? Byte.parseByte(parts.get(2)) : 0);
  }

  /**
   * Checks if the current version is newer than or equal to the given version.
   *
   * @param other the other version
   * @return {@code true} if the current version is newer than or equal to the given version
   */
  public static boolean since(@NotNull MinecraftVersion other) {
    return CURRENT.compareTo(other) >= 0;
  }

  private final int major;
  private final int minor;
  private final int patch;

  /**
   * Constructs a new {@code MinecraftVersion} with the specified major, minor, and patch versions.
   *
   * @param major the major version
   * @param minor the minor version
   * @param patch the patch version
   */
  public MinecraftVersion(int major, int minor, int patch) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  /**
   * Returns the major version.
   *
   * @return the major version
   */
  public int getMajor() {
    return major;
  }

  /**
   * Returns the minor version.
   *
   * @return the minor version
   */
  public int getMinor() {
    return minor;
  }

  /**
   * Returns the patch version.
   *
   * @return the patch version
   */
  public int getPatch() {
    return patch;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    MinecraftVersion that = (MinecraftVersion) obj;
    return major == that.major && minor == that.minor && patch == that.patch;
  }

  @Override
  public int hashCode() {
    return Objects.hash(major, minor, patch);
  }

  @Override
  public int compareTo(MinecraftVersion other) {
    int majorComparison = Integer.compare(this.major, other.major);
    if (majorComparison != 0) return majorComparison;

    int minorComparison = Integer.compare(this.minor, other.minor);
    if (minorComparison != 0) return minorComparison;

    return Integer.compare(this.patch, other.patch);
  }

  @Override
  public String toString() {
    return major + "." + minor + "." + patch;
  }
}
