package dev.anhcraft.config.context.injector;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.PropertyScope;
import dev.anhcraft.config.context.Scope;
import dev.anhcraft.config.context.ValueScope;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * An injector that adds property description to {@link Dictionary} before entering a {@link ValueScope}.<br>
 * The injector should be used during normalization.
 */
// TODO: support fixed width
public class PropertyDescriptionInjector implements Injector {
  private final String commentPrefix;

  public PropertyDescriptionInjector(@NotNull String commentPrefix) {
    this.commentPrefix = commentPrefix;
  }

  public PropertyDescriptionInjector() {
    this("#");
  }

  @Override
  public void beforeEnterScope(@NotNull Context context, @NotNull Scope scope) {
    if (scope instanceof ValueScope && context.getDepth() > 0) {
      Scope parent = context.getScope(0);
      if (parent instanceof PropertyScope) {
        PropertyScope ps = (PropertyScope) parent;
        Dictionary dict = ps.getContainer();
        if (dict == null) return;
        List<String> desc = ps.getProperty().description();
        if (desc.isEmpty()) return;
        Object v;
        if (desc.size() == 1) v = ps.getProperty().description().get(0);
        else v = ps.getProperty().description().toArray(String[]::new);
        dict.put(commentPrefix + ps.getProperty().name(), v);
      }
    }
  }
}
