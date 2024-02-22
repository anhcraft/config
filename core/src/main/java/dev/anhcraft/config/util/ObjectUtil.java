package dev.anhcraft.config.util;

import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class ObjectUtil {
    private static Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {}
    }

    @NotNull
    public static Object newInstance(@NotNull Class<?> clazz) throws InstantiationException {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            try {
                return unsafe.allocateInstance(clazz);
            } catch (InstantiationException q) { // TODO make a separate exception type
                throw new RuntimeException("Failed to create instance of " + clazz);
            }
        }
    }
}
