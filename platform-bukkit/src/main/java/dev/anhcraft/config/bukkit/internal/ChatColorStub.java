package dev.anhcraft.config.bukkit.internal;

import com.google.common.base.Preconditions;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus;

// https://github.com/SpigotMC/BungeeCord/blob/master/chat/src/main/java/net/md_5/bungee/api/ChatColor.java
@ApiStatus.Internal
public final class ChatColorStub {
  public static final char COLOR_CHAR = '§';
  public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
  public static final Pattern STRIP_COLOR_PATTERN =
      Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-ORX]");
  private static final Map<Character, ChatColorStub> BY_CHAR = new HashMap<>();
  private static final Map<String, ChatColorStub> BY_NAME = new HashMap<>();
  private static int count = 0;
  private final String toString;
  private final String name;
  private final int ordinal;

  private ChatColorStub(char code, String name) {
    this(code, name, null);
  }

  private ChatColorStub(char code, String name, Color color) {
    this.name = name;
    this.toString = new String(new char[] {COLOR_CHAR, code});
    this.ordinal = count++;

    BY_CHAR.put(code, this);
    BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
  }

  private ChatColorStub(String name, String toString, int rgb) {
    this.name = name;
    this.toString = toString;
    this.ordinal = -1;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.toString);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ChatColorStub other = (ChatColorStub) obj;

    return Objects.equals(this.toString, other.toString);
  }

  @Override
  public String toString() {
    return toString;
  }

  public static String stripColor(final String input) {
    if (input == null) {
      return null;
    }

    return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
  }

  public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
    char[] b = textToTranslate.toCharArray();
    for (int i = 0; i < b.length - 1; i++) {
      if (b[i] == altColorChar && ALL_CODES.indexOf(b[i + 1]) > -1) {
        b[i] = ChatColorStub.COLOR_CHAR;
        b[i + 1] = Character.toLowerCase(b[i + 1]);
      }
    }
    return new String(b);
  }

  public static ChatColorStub getByChar(char code) {
    return BY_CHAR.get(code);
  }

  public static ChatColorStub of(Color color) {
    return of("#" + String.format("%08x", color.getRGB()).substring(2));
  }

  public static ChatColorStub of(String string) {
    Preconditions.checkArgument(string != null, "string cannot be null");
    if (string.length() == 7 && string.charAt(0) == '#') {
      int rgb;
      try {
        rgb = Integer.parseInt(string.substring(1), 16);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Illegal hex string " + string);
      }

      StringBuilder magic = new StringBuilder(COLOR_CHAR + "x");
      for (char c : string.substring(1).toCharArray()) {
        magic.append(COLOR_CHAR).append(c);
      }

      return new ChatColorStub(string, magic.toString(), rgb);
    }

    ChatColorStub defined = BY_NAME.get(string.toUpperCase(Locale.ROOT));
    if (defined != null) {
      return defined;
    }

    throw new IllegalArgumentException("Could not parse ChatColor " + string);
  }

  @Deprecated
  public static ChatColorStub valueOf(String name) {
    Preconditions.checkNotNull(name, "Name is null");

    ChatColorStub defined = BY_NAME.get(name);
    Preconditions.checkArgument(
        defined != null, "No enum constant " + ChatColorStub.class.getName() + "." + name);

    return defined;
  }

  @Deprecated
  public static ChatColorStub[] values() {
    return BY_CHAR.values().toArray(new ChatColorStub[0]);
  }

  @Deprecated
  public String name() {
    return name.toUpperCase(Locale.ROOT);
  }

  @Deprecated
  public int ordinal() {
    Preconditions.checkArgument(ordinal >= 0, "Cannot get ordinal of hex color");
    return ordinal;
  }
}
