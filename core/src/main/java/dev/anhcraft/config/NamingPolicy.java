package dev.anhcraft.config;

import dev.anhcraft.config.util.StringUtil;
import java.util.function.UnaryOperator;

/**
 * Built-in naming policies.
 */
public final class NamingPolicy {
  /**
   * Default naming policy, does nothing.
   */
  public static final UnaryOperator<String> DEFAULT = UnaryOperator.identity();

  /**
   * Converts camelCase to PascalCase
   */
  public static final UnaryOperator<String> PASCAL_CASE =
      s -> Character.toUpperCase(s.charAt(0)) + s.substring(1);

  /**
   * Converts camelCase to snake_case
   */
  public static final UnaryOperator<String> SNAKE_CASE =
      s -> String.join("_", StringUtil.splitCamelCase(s));

  /**
   * Converts camelCase to kebab-case
   */
  public static final UnaryOperator<String> KEBAB_CASE =
      s -> String.join("-", StringUtil.splitCamelCase(s));
}
