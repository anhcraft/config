package dev.anhcraft.config.bukkit.util;

import dev.anhcraft.config.bukkit.internal.ChatColorStub;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class ColorUtil {
  private static final Pattern HEX_PATTERN =
      Pattern.compile("&#(([0-9a-fA-F]{6})|([0-9a-fA-F]{3}))");

  /**
   * Translates the color codes in the given string, support HEX format such as: {@code &#ffffff} or {@code &#fff}
   * @param str the string to translate
   * @return the translated string
   */
  @Contract("null -> null; !null -> !null")
  public static String colorize(@Nullable String str) {
    if (str == null) return null;
    StringBuilder sb = new StringBuilder(str.length());
    Matcher matcher = HEX_PATTERN.matcher(str);
    while (matcher.find()) {
      String hex = matcher.group();
      if (hex.length() == 5) {
        hex += hex.substring(2);
      }
      matcher.appendReplacement(sb, ChatColorStub.of(hex.substring(1)).toString());
    }
    matcher.appendTail(sb);
    return ChatColor.translateAlternateColorCodes('&', sb.toString());
  }
}
