package dev.anhcraft.config.validate;

import dev.anhcraft.config.validate.check.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class ValidationRegistry {
    // Static validations
    public static final NotNullValidation NOT_NULL = new NotNullValidation();
    public static final NotEmptyValidation NOT_EMPTY = new NotEmptyValidation();
    public static final NotBlankValidation NOT_BLANK = new NotBlankValidation();

    // Default validator
    public static final ValidationRegistry DEFAULT = ValidationRegistry.create()
            .add(Set.of("not-null", "notNull", "non-null", "nonNull"), (s) -> NOT_NULL)
            .add(Set.of("not-empty", "notEmpty", "non-empty", "nonEmpty"), (s) -> NOT_EMPTY)
            .add(Set.of("not-blank", "notBlank", "non-blank", "nonBlank"), (s) -> NOT_BLANK)
            .add(Set.of("range"), RangeValidation::new)
            .add(Set.of("size"), SizeValidation::new)
            .build();

    private final Map<String, Function<String, Validation>> validations;

    public static ValidationRegistry composite(ValidationRegistry... validators) {
        return new ValidationRegistry(validators);
    }

    ValidationRegistry(ValidationRegistry... compositions) {
        Map<String, Function<String, Validation>> mixed = new HashMap<>();
        for (ValidationRegistry composition : compositions) {
            mixed.putAll(composition.validations);
        }
        validations = Collections.unmodifiableMap(mixed);
    }

    ValidationRegistry(Builder builder) {
        validations = Collections.unmodifiableMap(builder.validations);
    }

    @Nullable
    public Function<String, Validation> getValidation(@Nullable String type) {
        return validations.get(type);
    }

    @NotNull
    public Validator parseString(@NotNull String str, boolean silent) {
        List<Validation> list = new ArrayList<>();
        String[] tuples = str.trim().split("\\s*,\\s*");

        for (String tuple : tuples) {
            String[] args = tuple.split("=");
            String type = args[0];

            Function<String, Validation> validation = validations.get(type);
            if (validation == null) {
                continue;
            }

            list.add(validation.apply(args.length > 1 ? args[1] : ""));
        }

        return new AggeratedValidator(list.toArray(Validation[]::new), silent);
    }

    public static class Builder {
        private final Map<String, Function<String, Validation>> validations = new HashMap<>();

        @NotNull
        public Builder add(@NotNull String type, @NotNull Function<String, Validation> validation) {
            validations.put(type, validation);
            return this;
        }

        @NotNull
        public Builder add(@NotNull Set<String> types, @NotNull Function<String, Validation> validation) {
            for (String type : types) {
                validations.put(type, validation);
            }
            return this;
        }

        @NotNull
        public ValidationRegistry build() {
            return new ValidationRegistry(this);
        }
    }

    @NotNull
    public static ValidationRegistry.Builder create() {
        return new ValidationRegistry.Builder();
    }
}
