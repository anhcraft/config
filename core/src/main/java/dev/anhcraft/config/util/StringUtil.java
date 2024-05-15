package dev.anhcraft.config.util;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities to work with strings.
 */
public final class StringUtil {
  /**
   * Splits the given string into fragments. The difference of this compared to {@link String#split(String)} is:
   * <ul>
   *     <li>This works fast without pattern matching</li>
   *     <li>The fragment can be empty</li>
   * </ul>
   * For example, using {@code "3|".split("|")} returns {@code ["3"]}, while this method returns {@code ["3", ""]}
   * @param str the string
   * @param separator the separator
   * @return the list
   */
  public static @NotNull List<String> fastSplit(@NotNull String str, char separator) {
    if (str.isEmpty()) return List.of();
    List<String> list = new ArrayList<>();
    StringBuilder buffer = new StringBuilder();
    for (char c : str.toCharArray()) {
      if (c == separator) {
        list.add(buffer.toString());
        buffer.setLength(0);
        continue;
      }
      buffer.append(c);
    }
    if (str.charAt(str.length() - 1) == separator) {
      list.add("");
    } else {
      list.add(buffer.toString());
    }
    return list;
  }

  /**
   * Splits a string in camelCase into lowercase fragments.
   * @param camelCase the string
   * @return the list
   */
  public static @NotNull List<String> splitCamelCase(@NotNull String camelCase) {
    List<String> parts = new ArrayList<>();
    StringBuilder buffer = new StringBuilder();
    for (char c : camelCase.toCharArray()) {
      if (Character.isUpperCase(c)) {
        if (buffer.length() > 0) {
          parts.add(buffer.toString());
          buffer.setLength(0);
        }
        buffer.append(Character.toLowerCase(c));
      } else {
        buffer.append(c);
      }
    }
    if (buffer.length() > 0) {
      parts.add(buffer.toString());
    }
    return parts;
  }
}
