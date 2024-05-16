package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.type.SimpleTypes;
import dev.anhcraft.config.validate.DisabledValidator;
import dev.anhcraft.config.validate.ValidationRegistry;
import dev.anhcraft.config.validate.Validator;
import java.util.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a property in a {@link DictionarySchema}.
 */
public class DictionaryProperty extends AbstractProperty {
  private final Class<?> type;
  private final DictionarySchema schema;

  public DictionaryProperty(
      @NotNull PropertyNaming naming,
      @NotNull List<String> description,
      @NotNull Validator validator,
      @Nullable Class<?> type,
      @Nullable DictionarySchema schema) {
    super(naming, description, validator);
    if (type != null && !SimpleTypes.validate(type))
      throw new IllegalArgumentException(
          "The type must be a simple type (got " + ComplexTypes.describe(type) + ")");
    if (schema != null
        && (type == null
            || (!type.isArray() && !Dictionary.class.isAssignableFrom(type))
            || (type.isArray() && !Dictionary.class.isAssignableFrom(type.getComponentType())))) {
      String t = type == null ? "<null>" : ComplexTypes.describe(type);
      throw new IllegalArgumentException(
          "The schema is allowed only if the type is a dictionary (got " + t + ")");
    }
    this.type = type;
    this.schema = schema;
  }

  /**
   * Gets the type of this property.
   * @return the type
   */
  @Nullable public Class<?> type() {
    return type;
  }

  @Override
  public String describeType(boolean simple) {
    return type == null ? "" : ComplexTypes.describe(type, simple);
  }

  /**
   * Gets the schema of this property.<br>
   * The schema is optional and only available if the type is a {@link Dictionary}.
   * @return the schema
   */
  @Nullable public DictionarySchema schema() {
    return schema;
  }

  /**
   * Checks whether the given value is compatible to this property.<br>
   * If the property type is {@code null}, it can hold any kind of value.<br>
   * <b>Note: </b> A {@code null} value is always compatible even if the property type is primitive, this
   * is due to the restriction of Dictionary that only permits non-null values.<br>
   * If the type and the value is Dictionary, the value must be compatible with the schema.
   * @param value the value
   * @return {@code true} if the value is compatible
   */
  public boolean isCompatible(@Nullable Object value) {
    if (value == null || type == null) return true;
    boolean b = ComplexTypes.isCompatible(value.getClass(), type);
    if (b && value instanceof Dictionary && !((Dictionary) value).isCompatibleWith(schema))
      b = false;
    return b;
  }

  /**
   * Creates a new {@link DictionaryProperty} builder.
   * @return a new builder
   */
  public static @NotNull Builder create() {
    return new Builder();
  }

  /**
   * A builder for {@link DictionaryProperty}.
   */
  public static class Builder {
    private String primaryName;
    private LinkedHashSet<String> aliases;
    private List<String> description;
    private Validator validator;
    private Class<?> type;
    private DictionarySchema schema;

    /**
     * Adds multiple names including aliases.
     * @param names the names
     * @return this
     */
    @Contract("_ -> this")
    public Builder withNames(@NotNull String... names) {
      if (names.length > 0) {
        this.primaryName = names[0];
        if (names.length > 1)
          this.aliases = new LinkedHashSet<>(Arrays.asList(names).subList(1, names.length));
      }
      return this;
    }

    /**
     * Adds multiple aliases.
     * @param aliases the aliases
     * @return this
     */
    @Contract("_ -> this")
    public Builder withAliases(@Nullable Collection<String> aliases) {
      this.aliases = aliases == null ? null : new LinkedHashSet<>(aliases);
      return this;
    }

    /**
     * Adds multiple aliases.
     * @param aliases the aliases
     * @return this
     */
    @Contract("_ -> this")
    public Builder withAliases(@NotNull String... aliases) {
      return withAliases(Arrays.asList(aliases));
    }

    /**
     * Adds multiple descriptions.
     * @param description the descriptions
     * @return this
     */
    @Contract("_ -> this")
    public Builder withDescription(@Nullable List<String> description) {
      this.description = description;
      return this;
    }

    /**
     * Adds multiple descriptions.
     * @param description the descriptions
     * @return this
     */
    @Contract("_ -> this")
    public Builder withDescription(@NotNull String... description) {
      return withDescription(Arrays.asList(description));
    }

    /**
     * Sets the validator of this property.
     * @param validator the validator
     * @return this
     */
    @Contract("_ -> this")
    public Builder withValidator(@Nullable Validator validator) {
      this.validator = validator;
      return this;
    }

    /**
     * Sets the validation of this property.
     * @param validation the validation
     * @return this
     * @see dev.anhcraft.config.meta.Validate
     */
    @Contract("_ -> this")
    public Builder withValidator(@NotNull String validation) {
      this.validator = ValidationRegistry.DEFAULT.parseString(validation, false);
      return this;
    }

    /**
     * Sets the type of this property.
     * @param type the type
     * @return this
     */
    @Contract("_ -> this")
    public Builder withType(@Nullable Class<?> type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the schema of this property.
     * @param schema the schema
     * @return this
     */
    public Builder withSchema(@Nullable DictionarySchema schema) {
      this.schema = schema;
      return this;
    }

    /**
     * Sets the type to {@link Dictionary} and the schema of this property.
     * @param schema the schema
     * @return this
     */
    public Builder isDictionary(@Nullable DictionarySchema schema) {
      this.type = Dictionary.class;
      this.schema = schema;
      return this;
    }

    /**
     * Sets the type to {@code Dictionary[]} and the schema of this property.
     * @param schema the schema
     * @return this
     */
    public Builder isDictionaryArray(@Nullable DictionarySchema schema) {
      this.type = Dictionary[].class;
      this.schema = schema;
      return this;
    }

    /**
     * Builds the property.
     * @return the property
     */
    @NotNull public DictionaryProperty build() {
      if (primaryName == null) throw new IllegalArgumentException("Primary name is required");
      return new DictionaryProperty(
          new PropertyNaming(primaryName, aliases == null ? new LinkedHashSet<>() : aliases),
          description == null ? Collections.emptyList() : description,
          validator == null ? DisabledValidator.INSTANCE : validator,
          type,
          schema);
    }
  }
}
