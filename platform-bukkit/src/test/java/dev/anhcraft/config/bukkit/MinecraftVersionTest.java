package dev.anhcraft.config.bukkit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class MinecraftVersionTest {
  private static List<Object[]> testData() {
    return Arrays.asList(
        new Object[][] {
          {"1.20.6-SNAPSHOT", 1, 20, 6},
          {"1.19.4", 1, 19, 4},
          {"1.18-beta", 1, 18, 0},
          {"1.17.1-git-3bdec5f-SNAPSHOT", 1, 17, 1},
          {"1.16", 1, 16, 0},
          {"1", 1, 0, 0},
        });
  }

  @ParameterizedTest(name = "Parsing version string {0}")
  @MethodSource("testData")
  public void testParseVersionString(
      @NotNull String versionString, int expectedMajor, int expectedMinor, int expectedPatch) {
    try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
      bukkit.when(Bukkit::getBukkitVersion).thenReturn(versionString);
      MinecraftVersion version = MinecraftVersion.parse(versionString);
      assertEquals(expectedMajor, version.getMajor(), "Major version mismatch");
      assertEquals(expectedMinor, version.getMinor(), "Minor version mismatch");
      assertEquals(expectedPatch, version.getPatch(), "Patch version mismatch");
    }
  }
}
