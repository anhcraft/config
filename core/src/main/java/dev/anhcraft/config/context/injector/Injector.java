package dev.anhcraft.config.context.injector;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.context.Scope;
import org.jetbrains.annotations.NotNull;

/**
 * The Injector interface provides default methods to perform actions before and after
 * entering and exiting scopes. It allows for hooks that can be used to manage resources,
 * perform logging, or execute any other necessary pre- and post-context and scope activities.
 */
public interface Injector {

  /**
   * Invoked before entering a scope within the given context.
   * This method has a default empty implementation.
   *
   * @param context the context in which the scope is being entered
   * @param scope   the scope that will be entered
   */
  default void beforeEnterScope(@NotNull Context context, @NotNull Scope scope) {}

  /**
   * Invoked after entering a scope within the given context.
   * This method has a default empty implementation.
   *
   * @param context the context in which the scope has been entered
   * @param scope   the scope that has been entered
   */
  default void afterEnterScope(@NotNull Context context, @NotNull Scope scope) {}

  /**
   * Invoked before exiting a scope within the given context.
   * This method has a default empty implementation.
   *
   * @param context the context in which the scope is being exited
   * @param scope   the scope that will be exited
   */
  default void beforeExitScope(@NotNull Context context, @NotNull Scope scope) {}

  /**
   * Invoked after exiting a scope within the given context.
   * This method has a default empty implementation.
   *
   * @param context the context in which the scope has been exited
   * @param scope   the scope that has been exited
   */
  default void afterExitScope(@NotNull Context context, @NotNull Scope scope) {}
}
