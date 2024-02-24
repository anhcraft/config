package dev.anhcraft.config.validate.check;

import dev.anhcraft.config.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SizeValidation extends ParameterizedValidation {
    private Integer min;
    private Integer max;

    public SizeValidation(@NotNull String arg) {
        super(arg);
        List<String> parts = StringUtil.fastSplit(arg, '|');
        if (parts.size() == 1) {
            int num = Integer.parseInt(parts.get(0));
            min = Math.max(0, num);
            max = Math.max(0, num);
        } else if (parts.size() == 2) {
            if (!parts.get(0).isEmpty())
                min = Math.max(0, Integer.parseInt(parts.get(0)));
            if (!parts.get(1).isEmpty())
                max = Math.max(0, Integer.parseInt(parts.get(1)));
        }
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
        if ((number == -1))
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
