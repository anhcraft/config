package dev.anhcraft.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class NamingStrategy {
    public static final UnaryOperator<String> DEFAULT = UnaryOperator.identity();
    public static final UnaryOperator<String> PASCAL_CASE = s -> {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    };
    public static final UnaryOperator<String> SNAKE_CASE = s -> {
        return String.join("_", split(s));
    };
    public static final UnaryOperator<String> KEBAB_CASE = s -> {
        return String.join("-", split(s));
    };

    private static List<String> split(String camelCase) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (buffer.length() > 0) {
                    parts.add(buffer.toString());
                    buffer = new StringBuilder();
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
