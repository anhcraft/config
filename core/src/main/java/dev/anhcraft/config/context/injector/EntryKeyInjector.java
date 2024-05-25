package dev.anhcraft.config.context.injector;

import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.Scope;
import dev.anhcraft.config.context.ValueScope;
import dev.anhcraft.config.error.ContextInjectionException;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * An injector that copies the key of an entry in a {@link Map} into the injection point within the value.<br>
 * For example: In the following code, after denormalizing the {@code items} property, the injector sets the key of
 * each entry in {@code items} into the {@code id} property of the respective {@code Item} object.
 * <pre>
 * {@code
 *   public static class Config {
 *     @Fallback
 *     public Map<String, Item> items;
 *   }
 *   public static class Item {
 *     public String id; // <-- injection point
 *     public String name;
 *   }
 * }
 * </pre>
 */
public class EntryKeyInjector implements Injector {
  private final String injectionPoint;

  public EntryKeyInjector(@NotNull String injectionPoint) {
    this.injectionPoint = injectionPoint;
  }

  public EntryKeyInjector() {
    this("id");
  }

  @Override
  public void afterExitScope(@NotNull Context context, @NotNull Scope scope) {
    if (scope instanceof ValueScope) {
      Object v = ((ValueScope) scope).getValue();
      if (v instanceof Map) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) v).entrySet()) {
          String key = entry.getKey().toString();
          Object val = entry.getValue();
          ClassSchema schema = context.getFactory().getSchema(val.getClass());
          ClassProperty property = schema.property(injectionPoint);
          if (property == null) continue;
          try {
            property.field().set(val, key);
          } catch (IllegalAccessException e) {
            throw new ContextInjectionException(context, "Failed to access injection point", e);
          }
        }
      }
    }
  }
}
