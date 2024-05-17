package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.error.UnsupportedSchemaException;
import java.util.*;
import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a dictionary schema.
 */
public class DictionarySchema extends AbstractSchema<DictionaryProperty> {
  private final String name;

  public DictionarySchema(
      @NotNull List<DictionaryProperty> properties,
      @NotNull Map<String, DictionaryProperty> lookup,
      @Nullable String name) {
    super(properties, lookup);
    this.name = name;
  }

  /**
   * Creates a new {@link DictionarySchema} builder.
   * @return a new builder
   */
  public static @NotNull Builder create() {
    return new Builder();
  }

  @Override
  public @Nullable String getName() {
    return name;
  }

  /**
   * A builder for {@link DictionarySchema}.
   */
  public static class Builder {
    private final List<DictionaryProperty> propertyList = new ArrayList<>();
    private String name;

    /**
     * Sets the name.
     * @param name the name
     * @return this
     */
    public @NotNull Builder withName(String name) {
      this.name = name;
      return this;
    }

    /**
     * Adds multiple properties.
     * @param properties the properties
     * @return this
     */
    @Contract("_ -> this")
    public @NotNull Builder addProperty(@NotNull Collection<DictionaryProperty> properties) {
      this.propertyList.addAll(properties);
      return this;
    }

    /**
     * Adds multiple properties.
     * @param properties the properties
     * @return this
     */
    @Contract("_ -> this")
    public @NotNull Builder addProperty(@NotNull DictionaryProperty... properties) {
      this.propertyList.addAll(Arrays.asList(properties));
      return this;
    }

    /**
     * Adds a property.
     * @param name the primary name
     * @param builder the property builder
     * @return this
     */
    @Contract("_, _ -> this")
    public @NotNull Builder addProperty(
        String name, @NotNull Consumer<DictionaryProperty.Builder> builder) {
      var property = new DictionaryProperty.Builder();
      property.withNames(name);
      builder.accept(property);
      this.propertyList.add(property.build());
      return this;
    }

    /**
     * Builds the schema.
     * @return the schema
     */
    @NotNull public DictionarySchema build() {
      Map<String, DictionaryProperty> lookup = new LinkedHashMap<>();
      List<DictionaryProperty> properties = new ArrayList<>();
      for (DictionaryProperty property : this.propertyList) {
        if (lookup.containsKey(property.name())) {
          throw new UnsupportedSchemaException("Duplicate property name: " + property.name());
        }
        lookup.put(property.name(), property);
        properties.add(property);
      }
      for (DictionaryProperty property : this.propertyList) {
        for (String alias : property.aliases()) {
          lookup.putIfAbsent(alias, property);
        }
      }
      return new DictionarySchema(properties, lookup, name);
    }
  }
}
