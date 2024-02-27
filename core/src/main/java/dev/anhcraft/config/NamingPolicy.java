package dev.anhcraft.config;

import dev.anhcraft.config.util.StringUtil;

import java.util.function.UnaryOperator;

public final class NamingPolicy {
    public static final UnaryOperator<String> DEFAULT = UnaryOperator.identity();
    public static final UnaryOperator<String> PASCAL_CASE = s -> Character.toUpperCase(s.charAt(0)) + s.substring(1);
    public static final UnaryOperator<String> SNAKE_CASE = s -> String.join("_", StringUtil.splitCamelCase(s));
    public static final UnaryOperator<String> KEBAB_CASE = s -> String.join("-", StringUtil.splitCamelCase(s));
}
