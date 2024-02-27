package dev.anhcraft.config.validate.check;

import dev.anhcraft.config.error.ValidationParseException;
import dev.anhcraft.config.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Validates the size of containers.<br>
 * Support: {@link String}, {@link Collection}, {@link Map} (including dictionary)
 */
public class SizeValidation extends ParameterizedValidation {
    private Integer min;
    private Integer max;

    public SizeValidation(@NotNull String arg) {
        super(arg);
        List<String> parts = StringUtil.fastSplit(arg, '|');
        if (parts.size() == 1) {
            int num = parseInt(parts.get(0));
            min = num;
            max = num;
        } else if (parts.size() == 2) {
            if (!parts.get(0).isEmpty())
                min = parseInt(parts.get(0));
            if (!parts.get(1).isEmpty())
                max = parseInt(parts.get(1));
            if (max != null && min != null && min > max) {
                throw new ValidationParseException("Invalid validation argument: " + arg);
            }
        } else {
            throw new ValidationParseException("Invalid validation argument: " + arg);
        }
    }

    private int parseInt(String s) {
        int value = Integer.parseInt(s);
        if (value < 0) {
            throw new ValidationParseException("Invalid validation argument: " + s);
        }
        return value;
    }

    @Override
    public boolean check(Object value) {
        int number = -1;
        if (value instanceof String)
            number = ((String) value).length();
        else if (value instanceof Collection)
            number = ((Collection<?>) value).size();
        else if (value instanceof Map)
            number = ((Map<?, ?>) value).size();
        else if (value.getClass().isArray())
            number = Array.getLength(value);
        if (number == -1)
            return true;
        if (min != null && number < min) return false;
        return max == null || !(number > max);
    }

    @Override
    public @NotNull String message() {
        if (min != null && max == null)
            return String.format("must be at least %d", min);
        if (min == null && max != null)
            return String.format("must be at most %d", max);
        if (min != null)
            return String.format("must be between %d and %d", min, max);
        return "";
    }
}
