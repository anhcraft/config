package dev.anhcraft.config.validate.check;

import dev.anhcraft.config.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class RangeValidation extends ParameterizedValidation {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
    private Double min;
    private Double max;

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
            if (min != null && number < min) return false;
            return max == null || !(number > max);
        }
        return true;
    }

    @Override
    public @NotNull String message() {
        if (min != null && max == null)
            return String.format("must be at least %s", FORMAT.format(min));
        if (min == null && max != null)
            return String.format("must be at most %s", FORMAT.format(max));
        if (min != null)
            return String.format("must be between %s and %s", FORMAT.format(min), FORMAT.format(max));
        return "";
    }
}
