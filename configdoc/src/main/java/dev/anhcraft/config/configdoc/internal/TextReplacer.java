package dev.anhcraft.config.configdoc.internal;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class TextReplacer {
  public static final Pattern INFO_PLACEHOLDER_PATTERN = Pattern.compile("\\{[a-zA-Z0-9?_. ]+}");
  private final UnaryOperator<String> handler;

  public TextReplacer(UnaryOperator<String> handler) {
    this.handler = handler;
  }

  @NotNull public String replace(@NotNull String str) {
    Matcher m = INFO_PLACEHOLDER_PATTERN.matcher(str);
    StringBuilder sb = new StringBuilder(str.length());
    while (m.find()) {
      String p = m.group();
      String s = p.substring(1, p.length() - 1).trim();
      m.appendReplacement(sb, handler.apply(s));
    }
    m.appendTail(sb);
    return sb.toString();
  }
}
