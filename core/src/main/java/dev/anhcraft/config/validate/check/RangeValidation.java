package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class RangeValidation extends ParameterizedValidation {
    private static final DecimalFormat FORMAT = new DecimalFormat("0.#");
    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;

    public RangeValidation(@NotNull String arg) {
        super(arg);
        String[] parts = arg.split("\\|");
        if (parts.length == 1) {
            double value = Double.parseDouble(parts[0]);
            min = value;
            max = value;
        } else if (parts.length == 2) {
            if (!parts[0].isEmpty())
                min = Double.parseDouble(parts[0]);
            if (!parts[1].isEmpty())
                max = Double.parseDouble(parts[1]);
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
