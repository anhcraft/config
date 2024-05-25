package dev.anhcraft.config.context;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.context.injector.Injector;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Context} that can be extended by adding {@link Injector}s.
 */
public class InjectableContext extends Context {
  private final List<Injector> injectors = new ArrayList<>(1);

  /**
   * Constructs an {@link InjectableContext} with the specified {@link ConfigFactory}.
   *
   * @param factory the ConfigFactory used to configure this context
   */
  public InjectableContext(@NotNull ConfigFactory factory) {
    super(factory);
  }

  /**
   * Adds an {@link Injector} to this context.
   *
   * @param injector the {@link Injector} to be added
   * @return this {@link InjectableContext}
   */
  public InjectableContext inject(@NotNull Injector injector) {
    injectors.add(injector);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void enterScope(@NotNull Scope scope) {
    for (Injector injector : injectors) {
      injector.beforeEnterScope(this, scope);
    }
    super.enterScope(scope);
    for (Injector injector : injectors) {
      injector.afterEnterScope(this, scope);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Scope exitScope() {
    if (getDepth() == 0) return null;
    Scope s = getScope(0);
    for (Injector injector : injectors) {
      injector.beforeExitScope(this, s);
    }
    super.exitScope();
    for (Injector injector : injectors) {
      injector.afterExitScope(this, s);
    }
    return s;
  }
}
