package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class SizeValidation extends ParameterizedValidation {
    private int min = 0;
    private int max = Integer.MAX_VALUE;

    public SizeValidation(@NotNull String arg) {
        super(arg);
        String[] parts = arg.split("\\|");
        if (parts.length == 1) {
            int num = Integer.parseInt(parts[0]);
            min = Math.max(0, num);
            max = Math.max(0, num);
        } else if (parts.length == 2) {
            if (!parts[0].isEmpty())
                min = Math.max(0, Integer.parseInt(parts[0]));
            if (!parts[1].isEmpty())
                max = Math.max(0, Integer.parseInt(parts[1]));
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
        return (number == -1) || (number >= min && number <= max);
    }

    @Override
    public @NotNull String message() {
        return String.format("must be between %d and %d", min, max);
    }
}
