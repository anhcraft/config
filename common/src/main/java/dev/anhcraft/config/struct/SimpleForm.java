package dev.anhcraft.config.struct;

import dev.anhcraft.config.utils.ObjectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;

/**
 * SimpleForm is a safe wrapper for storing "simple" objects.<br>
 * Accepts number, character, boolean, array, {@link String}, {@link ConfigSection}, {@link List}.<br>
 * With collection types like array or {@link List}, their members also must have "simple" types.
 */
public class SimpleForm {
    private final Object object;

    private SimpleForm(@NotNull Object object) throws Exception {
        this.object = ObjectUtil.shallowCopy(object);
    }

    private static boolean isAllowed(@Nullable Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Number
                || object instanceof Boolean
                || object instanceof Character
                || object instanceof String
                || object instanceof ConfigSection) {
            return true;
        } else if (object instanceof List<?>) {
            List<?> list = (List<?>) object;
            return list.isEmpty() || isAllowed(list.get(0));
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0 || isAllowed(Array.get(object, 0));
        }
        return false;
    }

    @Nullable
    public static SimpleForm of(@NotNull Object object) throws Exception {
        return object instanceof SimpleForm ? (SimpleForm) object : (isAllowed(object) ? new SimpleForm(object) : null);
    }

    public boolean isNumber() {
        return object instanceof Number;
    }

    public boolean isBoolean() {
        return object instanceof Boolean;
    }

    public boolean isCharacter() {
        return object instanceof Character;
    }

    public boolean isPrimitive() {
        return isNumber() || isBoolean() || isCharacter();
    }

    public boolean isString() {
        return object instanceof String;
    }

    public boolean isSection() {
        return object instanceof ConfigSection;
    }

    public boolean isList() {
        return object instanceof List<?>;
    }

    public boolean isArray() {
        return object.getClass().isArray();
    }

    public byte asByte() {
        return isNumber() ? ((Number) object).byteValue() : 0;
    }

    public short asShort() {
        return isNumber() ? ((Number) object).shortValue() : 0;
    }

    public int asInt() {
        return isNumber() ? ((Number) object).intValue() : 0;
    }

    public long asLong() {
        return isNumber() ? ((Number) object).longValue() : 0;
    }

    public float asFloat() {
        return isNumber() ? ((Number) object).floatValue() : 0;
    }

    public double asDouble() {
        return isNumber() ? ((Number) object).doubleValue() : 0;
    }

    public boolean asBoolean() {
        return isBoolean() ? (Boolean) object : false;
    }

    public char asCharacter() {
        return isCharacter() ? (Character) object : 0;
    }

    @Nullable
    public String asString() {
        return isString() ? (String) object : null;
    }

    @Nullable
    public ConfigSection asSection() {
        return isSection() ? (ConfigSection) object : null;
    }

    @Nullable
    public List<?> asList() {
        return isList() ? (List<?>) object : null;
    }

    public boolean isEmpty() {
        if (isList()) {
            return Objects.requireNonNull(asList()).isEmpty();
        } else if (isSection()) {
            return Objects.requireNonNull(asSection()).isEmpty();
        } else if (isString()) {
            return Objects.requireNonNull(asString()).isEmpty();
        } else if (isArray()) {
            return Array.getLength(object) == 0;
        } else {
            return false;
        }
    }

    @NotNull
    public Object getObject() {
        return object;
    }
}
