package dev.anhcraft.config.utils;

import dev.anhcraft.config.struct.ConfigSection;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.function.UnaryOperator;

public class ObjectUtil {
    private static Unsafe unsafe;
    private static Method cloneMethod;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            cloneMethod = Object.class.getDeclaredMethod("clone");
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public static Object newInstance(@NotNull Class<?> clazz) throws InstantiationException {
        return unsafe.allocateInstance(clazz);
    }

    @NotNull
    public static Object shallowCopy(@NotNull Object object) throws Exception {
        if (object instanceof Number
                || object instanceof Boolean
                || object instanceof Character
                || object instanceof String) {
            return object;
        } else if (object instanceof ConfigSection) {
            return ((ConfigSection) object).deepClone();
        } else if (object instanceof List<?>) {
            // TODO better way for cloning list
            if (object instanceof ArrayList<?>) {
                return ((ArrayList<?>) object).clone();
            } else if (object instanceof LinkedList<?>) {
                return ((LinkedList<?>) object).clone();
            } else if (object instanceof Vector<?>) {
                return ((Vector<?>) object).clone();
            } else {
                return new ArrayList<>((List<?>) object);
            }
        } else if (object.getClass().isArray()) {
            return replaceAll(object, UnaryOperator.identity());
        } else if (object instanceof Cloneable) {
            return cloneMethod.invoke(object);
        } else {
            return object;
        }
    }

    public static Object replaceAll(Object array, UnaryOperator<Object> operator) {
        return replaceAll(array.getClass().getComponentType(), array, operator);
    }

    public static Object replaceAll(Class<?> type, Object array, UnaryOperator<Object> operator) {
        int len = Array.getLength(array);
        Object newArray = Array.newInstance(type, len);
        for (int i = 0; i < len; i++) {
            Array.set(newArray, i, operator.apply(Array.get(array, i)));
        }
        return newArray;
    }
}
