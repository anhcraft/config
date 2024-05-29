package dev.anhcraft.config.bukkit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bukkit.Bukkit;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ColorUtilTest {
  @ParameterizedTest
  @ValueSource(strings = {"1.12-R0.1-SNAPSHOT", "1.16.5-R0.1-SNAPSHOT"})
  public void test(String ver) {
    try (MockedStatic<Bukkit> bukkit = Mockito.mockStatic(Bukkit.class)) {
      bukkit.when(Bukkit::getBukkitVersion).thenReturn(ver);

      assertEquals("", ColorUtil.colorize(""));
      assertEquals("§aHello §x§0§0§0§0§0§0World!", ColorUtil.colorize("&aHello &#000World!"));
      assertEquals(
          "§x§7§0§f§b§4§0§o§nR§x§8§1§e§0§5§e§o§nG§x§9§2§c§5§7§c§o§nB"
              + " §x§a§3§a§a§9§a§o§nL§x§b§3§8§f§b§8§o§ni§x§c§4§7§4§d§6§o§nn§x§d§5§5§9§f§4§o§ne\n",
          ColorUtil.colorize(
              "&#70fb40&o&nR&#81e05e&o&nG&#92c57c&o&nB"
                  + " &#a3aa9a&o&nL&#b38fb8&o&ni&#c474d6&o&nn&#d559f4&o&ne\n"));
      assertEquals(
          "§x§f§f§0§0§0§0§lR§x§f§f§4§c§0§0§la§x§f§f§9§9§0§0§li§x§f§f§e§5§0§0§ln§x§9§9§f§f§0§0§lb§x§0§0§f§f§0§0§lo§x§0§0§6§6§9§9§lw"
              + " §x§0§f§0§0§e§6§lL§x§3§c§0§0§9§b§li§x§6§8§0§0§a§2§ln§x§9§4§0§0§d§3§le",
          ColorUtil.colorize(
              "&#ff0000&lR&#ff4c00&la&#ff9900&li&#ffe500&ln&#99ff00&lb&#00ff00&lo&#006699&lw"
                  + " &#0f00e6&lL&#3c009b&li&#6800a2&ln&#9400d3&le"));
    }
  }
}
