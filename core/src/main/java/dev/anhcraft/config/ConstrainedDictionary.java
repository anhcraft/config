package dev.anhcraft.config;

import dev.anhcraft.config.blueprint.DictionaryProperty;
import dev.anhcraft.config.blueprint.DictionarySchema;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.type.SimpleTypes;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConstrainedDictionary extends AbstractDictionary {
  /**
   * Makes a copy of the given map as a {@link ConstrainedDictionary}.<br>
   * The map is shallow-copied. Changes to the dictionary does not reflect in the original map, however, indirect
   * changes made to the underlying values does reflect.<br>
   * The given map must contain simple values only, otherwise, {@link IllegalArgumentException} throws.<br>
   * The given map must be compatible with the given schema, otherwise, {@link IllegalArgumentException} throws.
   * @param map the map
   * @return the dictionary
   */
  public static @NotNull ConstrainedDictionary copyOf(
      @NotNull Map<String, Object> map, @Nullable DictionarySchema schema) {
    ConstrainedDictionary container = new ConstrainedDictionary(schema);
    container.putAll(map);
    return container;
  }

  private final DictionarySchema schema;

  public ConstrainedDictionary(@Nullable DictionarySchema schema) {
    this.schema = schema;
  }

  @Override
  protected void onPut(String name, Object value) {
    if (schema == null || value == null) return;
    DictionaryProperty property = schema.property(name);
    if (property == null) return;
    Class<?> type = property.type();
    if (type == null) return;
    if (!ComplexTypes.isCompatible(value.getClass(), type))
      throw new IllegalArgumentException(
          "Property '" + name + "' is not of type " + ComplexTypes.describe(type));
    DictionarySchema subSchema = property.schema();
    if (subSchema != null) {
      if (
        value instanceof Dictionary
          && !((Dictionary) value).isCompatibleWith(subSchema))
        throw new IllegalArgumentException("Property '" + name + "' does not have compatible schema");
      else if (value.getClass().isArray()) {
        int length = Array.getLength(value);
        for (int i = 0; i < length; i++) {
          Object elem = Array.get(value, i);
          if (elem instanceof Dictionary
              && !((Dictionary) elem).isCompatibleWith(subSchema))
            throw new IllegalArgumentException(
                "Property '" + name + "' has an element at index " + i + " not have compatible schema");
        }
      }
    }
  }

  @Override
  public boolean isCompatibleWith(@Nullable DictionarySchema schema) {
    return this.schema == schema || super.isCompatibleWith(schema); // TODO optimize
  }

  @Override
  public @NotNull Dictionary duplicate(boolean deepCopy) {
    if (deepCopy) {
      LinkedHashMap<String, Object> backend = unwrap();
      for (Map.Entry<String, Object> entry : backend.entrySet()) {
        entry.setValue(SimpleTypes.deepClone(entry.getValue()));
      }
      return copyOf(backend, schema);
    }
    return copyOf(this, schema);
  }
}
