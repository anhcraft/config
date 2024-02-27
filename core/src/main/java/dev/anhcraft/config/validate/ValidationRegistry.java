package dev.anhcraft.config.validate;

import dev.anhcraft.config.error.ValidationParseException;
import dev.anhcraft.config.validate.check.*;
import java.util.*;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ValidationRegistry {
  // Static validations
  public static final NotNullValidation NOT_NULL = new NotNullValidation();
  public static final NotEmptyValidation NOT_EMPTY = new NotEmptyValidation();
  public static final NotBlankValidation NOT_BLANK = new NotBlankValidation();

  /**
   * The default validation registry.<br>
   * <ul>
   *     <li>{@link NotNullValidation}: {@code not-null, notNull, non-null, nonNull}</li>
   *     <li>{@link NotEmptyValidation}: {@code not-empty, notEmpty, non-empty, nonEmpty}</li>
   *     <li>{@link NotBlankValidation}: {@code not-blank, notBlank, non-blank, nonBlank}</li>
   *     <li>{@link RangeValidation}: {@code range=min|max, range=min|, range=|max}</li>
   *     <li>{@link SizeValidation}: {@code size=min|max, size=min|, size=|max}</li>
   * </ul>
   */
  // Default validator
  public static final ValidationRegistry DEFAULT =
      ValidationRegistry.create()
          .add(Set.of("not-null", "notNull", "non-null", "nonNull"), (s) -> NOT_NULL)
          .add(Set.of("not-empty", "notEmpty", "non-empty", "nonEmpty"), (s) -> NOT_EMPTY)
          .add(Set.of("not-blank", "notBlank", "non-blank", "nonBlank"), (s) -> NOT_BLANK)
          .add(Set.of("range"), RangeValidation::new)
          .add(Set.of("size"), SizeValidation::new)
          .build();

  private final Map<String, ValidationConstructor> validations;

  /**
   * Combines multiple validation registries into one.
   * @param registries the validation registries
   * @return a new validation registry
   */
  public static @NotNull ValidationRegistry composite(@NotNull ValidationRegistry... registries) {
    return new ValidationRegistry(registries);
  }

  ValidationRegistry(ValidationRegistry... compositions) {
    Map<String, ValidationConstructor> mixed = new HashMap<>();
    for (ValidationRegistry composition : compositions) {
      mixed.putAll(composition.validations);
    }
    validations = Collections.unmodifiableMap(mixed);
  }

  ValidationRegistry(Builder builder) {
    validations = Collections.unmodifiableMap(builder.validations);
  }

  /**
   * Gets the function to construct the given type of validation.
   * @param type the type
   * @return the function
   */
  @Nullable public ValidationConstructor getValidationConstructor(@Nullable String type) {
    return validations.get(type);
  }

  /**
   * Constructs a validator from a string.
   * @param str the string
   * @param silent whether this validator is silent
   * @return the validator
   */
  @NotNull public Validator parseString(@NotNull String str, boolean silent) {
    List<Validation> list = new ArrayList<>();
    String[] tuples = str.trim().split("\\s*,\\s*");

    for (String tuple : tuples) {
      if (tuple.isEmpty()) continue;
      String[] args = tuple.split("\\s*=\\s*");
      if (args.length > 2)
        throw new ValidationParseException(
            String.format("Invalid validation syntax at parameter '%s'", tuple));

      String type = args[0];
      Function<String, Validation> validation = validations.get(type);
      if (validation == null)
        throw new ValidationParseException(String.format("Validation type '%s' not found", type));

      list.add(validation.apply(args.length == 2 ? args[1] : ""));
    }

    return new AggregatedValidator(list.toArray(Validation[]::new), silent);
  }

  /**
   * The builder for {@link ValidationRegistry}.
   */
  public static class Builder {
    private final Map<String, ValidationConstructor> validations = new HashMap<>();

    /**
     * Adds a validation constructor.
     * @param type the validation type
     * @param constructor the validation constructor
     * @return this
     */
    @NotNull public Builder add(@NotNull String type, @NotNull ValidationConstructor constructor) {
      validations.put(type, constructor);
      return this;
    }

    /**
     * Adds a validation constructor.
     * @param types a set of types that use this constructor
     * @param constructor the validation constructor
     * @return this
     */
    @NotNull public Builder add(@NotNull Set<String> types, @NotNull ValidationConstructor constructor) {
      for (String type : types) {
        validations.put(type, constructor);
      }
      return this;
    }

    /**
     * Builds the validation registry.
     * @return the validation registry
     */
    @NotNull public ValidationRegistry build() {
      return new ValidationRegistry(this);
    }
  }

  /**
   * Creates a builder for {@link ValidationRegistry}.
   * @return the builder
   */
  @NotNull public static ValidationRegistry.Builder create() {
    return new ValidationRegistry.Builder();
  }
}
