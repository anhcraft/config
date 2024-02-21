package dev.anhcraft.config.validate.check;

import dev.anhcraft.config.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class RangeValidation extends ParameterizedValidation {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;

    public RangeValidation(@NotNull String arg) {
        super(arg);
        List<String> parts = StringUtil.fastSplit(arg, '|');
        if (parts.size() == 1) {
            double value = Double.parseDouble(parts.get(0));
            min = value;
            max = value;
        } else if (parts.size() == 2) {
            if (!parts.get(0).isEmpty())
                min = Math.max(0, Double.parseDouble(parts.get(0)));
            if (!parts.get(1).isEmpty())
                max = Math.max(0, Double.parseDouble(parts.get(1)));
        }
    }

    @Override
    public boolean check(Object value) {
        if (value instanceof Number) {
            double number = ((Number) value).doubleValue();
            return number >= min && number <= max;
        }
        return true;
    }

    @Override
    public @NotNull String message() {
        return String.format("must be between %s and %s", FORMAT.format(min), FORMAT.format(max));
    }
}
