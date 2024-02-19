package dev.anhcraft.config.validate.check;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class NotEmptyValidation implements Validation {
    @Override
    public boolean check(Object value) {
        if (value instanceof String) return !((String) value).isEmpty();
        else if (value instanceof Collection) return !((Collection<?>) value).isEmpty();
        else if (value instanceof Iterable) return ((Iterable<?>) value).iterator().hasNext();
        else if (value instanceof Map) return !((Map<?, ?>) value).isEmpty();
        return true;
    }

    @Override
    public @NotNull String message() {
        return "must be not-empty";
    }
}
