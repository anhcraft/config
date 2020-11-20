package dev.anhcraft.config.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassUtil {
    private static final Map<String, List<String>> ENUM_MAP = new HashMap<>();

    @NotNull
    public static String hashClass(@NotNull Class<?> clazz) {
        // TODO hash class loader too (if possible)
        return clazz.getName();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Nullable
    public static <T extends Enum> Object findEnum(@NotNull Class<T> clazz, @NotNull String name) {
        String hash = hashClass(clazz);
        List<String> list = ENUM_MAP.get(hash);
        if (list == null) {
            if (clazz.isEnum()) {
                list = Arrays.stream(clazz.getEnumConstants()).map(Enum::toString).collect(Collectors.toList());
                ENUM_MAP.put(hash, list);
            } else {
                return null;
            }
        }

        if (list.contains(name)) {
            return Enum.valueOf(clazz, name);
        } else {
            return null;
        }
    }

    public static boolean isAssignable(@NotNull Class<?> upper, @NotNull Class<?> clazz) {
        if (upper.equals(Byte.TYPE)) {
            return clazz.equals(Byte.TYPE) || clazz.equals(Byte.class);
        } else if (upper.equals(Short.TYPE)) {
            return clazz.equals(Short.TYPE) || clazz.equals(Short.class);
        } else if (upper.equals(Integer.TYPE)) {
            return clazz.equals(Integer.TYPE) || clazz.equals(Integer.class);
        } else if (upper.equals(Long.TYPE)) {
            return clazz.equals(Long.TYPE) || clazz.equals(Long.class);
        } else if (upper.equals(Float.TYPE)) {
            return clazz.equals(Float.TYPE) || clazz.equals(Float.class);
        } else if (upper.equals(Double.TYPE)) {
            return clazz.equals(Double.TYPE) || clazz.equals(Double.class);
        } else if (upper.equals(Boolean.TYPE)) {
            return clazz.equals(Boolean.TYPE) || clazz.equals(Boolean.class);
        } else if (upper.equals(Character.TYPE)) {
            return clazz.equals(Character.TYPE) || clazz.equals(Character.class);
        } else if (upper.equals(Byte.class)) {
            return clazz.equals(Byte.class) || clazz.equals(Byte.TYPE);
        } else if (upper.equals(Short.class)) {
            return clazz.equals(Short.class) || clazz.equals(Short.TYPE);
        } else if (upper.equals(Integer.class)) {
            return clazz.equals(Integer.class) || clazz.equals(Integer.TYPE);
        } else if (upper.equals(Long.class)) {
            return clazz.equals(Long.class) || clazz.equals(Long.TYPE);
        } else if (upper.equals(Float.class)) {
            return clazz.equals(Float.class) || clazz.equals(Float.TYPE);
        } else if (upper.equals(Double.class)) {
            return clazz.equals(Double.class) || clazz.equals(Double.TYPE);
        } else if (upper.equals(Boolean.class)) {
            return clazz.equals(Boolean.class) || clazz.equals(Boolean.TYPE);
        } else if (upper.equals(Character.class)) {
            return clazz.equals(Character.class) || clazz.equals(Character.TYPE);
        } else {
            return upper.isAssignableFrom(clazz);
        }
    }
}
