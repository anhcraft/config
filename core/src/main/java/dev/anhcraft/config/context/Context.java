package dev.anhcraft.config.context;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.adapter.TypeAdapter;
import java.lang.reflect.Type;
import java.util.*;

import dev.anhcraft.config.blueprint.ClassProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A context represents a process of normalization and denormalization.<br>
 * The context contains consecutive nested scopes according to the current operation in the pipeline. With scopes, it
 * is possible to know which property and setting is currently being processed. It is also possible to build a full path
 * representing the scopes for debugging, log tracing, error-handling, etc<br>
 * The {@link Context} can be inherited to provide further information to the pipeline. For example, the context may
 * contain the current service involved.<br>
 * <b>The context is not thread-safe</b>. A context can be reused as long as it is on one thread only.
 */
public class Context implements TypeAdapter<Object> {
  private final ConfigFactory factory;
  private final Deque<Scope> scopes = new ArrayDeque<>();
  private String cachedPath;

  /**
   * Use {@link ConfigFactory#createContext()}
   */
  public Context(@NotNull ConfigFactory factory) { // TODO hide this constructor
    this.factory = factory;
  }

  /**
   * Gets the {@link ConfigFactory} associated with this context
   * @return the factory
   */
  public @NotNull ConfigFactory getFactory() {
    return factory;
  }

  /**
   * Enters a new scope.
   * @param scope the scope
   */
  public void enterScope(@NotNull Scope scope) {
    scopes.offerLast(scope);
    cachedPath = null;
  }

  /**
   * Exits the recent scope.
   * @return the exited scope or {@code null} if no scope is currently entered
   */
  public Scope exitScope() {
    cachedPath = null;
    return scopes.pollLast();
  }

  /**
   * Gets the scope at the specified backward index.<br>
   * For example, if {@code backward} is 0, then the current scope is returned.<br>
   * If the scope is out of bound, an {@link IndexOutOfBoundsException} will be thrown.
   * @param backward the backward index (non-negative)
   * @return the scope
   */
  public @NotNull Scope getScope(int backward) {
    if (backward < 0) throw new IllegalArgumentException("backward cannot be negative");
    if (backward >= scopes.size())
      throw new IndexOutOfBoundsException("backward must be smaller than size of scopes");
    if (backward == 0) return scopes.getLast();
    Iterator<Scope> it = scopes.descendingIterator();
    while (it.hasNext()) {
      Scope scope = it.next();
      if (backward-- == 0) return scope;
    }
    throw new IllegalStateException();
  }

  /**
   * Gets the depth of nested scopes in this context.
   * @return the number
   */
  public int getDepth() {
    return scopes.size();
  }

  /**
   * Gets an immutable view of nested scopes in this context.
   * @return the scopes
   */
  public Collection<Scope> getScopes() {
    return Collections.unmodifiableCollection(scopes);
  }

  /**
   * Gets the current path of this context.<br>
   * The path is cached as long as no modification is made to the scopes.
   * @return the path
   */
  public @NotNull String getPath() {
    if (cachedPath == null) cachedPath = buildPath(PathType.FIELD, ".");
    return cachedPath;
  }

  /**
   * Builds the full path representing all the scopes in this context.<br>
   * It is recommended to use {@link #getPath()} for performance reasons.
   * @param type the path type to use
   * @param separator the separator
   * @return the path
   */
  public @NotNull String buildPath(@NotNull PathType type, @NotNull String separator) {
    StringBuilder sb = new StringBuilder();
    boolean wasProperty = false;
    for (Scope scope : scopes) {
      if (scope instanceof ElementScope) {
        sb.append('[').append(((ElementScope) scope).getIndex()).append(']');
      } else if (scope instanceof PropertyScope) {
        if (wasProperty) sb.append(separator);
        PropertyScope ps = (PropertyScope) scope;
        switch (type) {
          case FIELD:
            if (ps.getProperty() instanceof ClassProperty)
              sb.append(((ClassProperty) ps.getProperty()).field().getName());
            break;
          case PRIMARY:
            sb.append(ps.getProperty().name());
            break;
          case SETTING:
            sb.append(ps.getSetting());
            break;
          default:
            throw new UnsupportedOperationException();
        }
        wasProperty = true;
      } else {
        throw new UnsupportedOperationException();
      }
    }
    return sb.toString();
  }

  /**
   * @see #simplify(Context, Class, Object)
   */
  public @Nullable Object simplify(@NotNull Class<?> sourceType, @NotNull Object value)
      throws Exception {
    return simplify(this, sourceType, value);
  }

  /**
   * @see #complexify(Context, Object, Type)
   */
  public @Nullable Object complexify(@NotNull Object value, @NotNull Type targetType)
      throws Exception {
    return complexify(this, value, targetType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable Object simplify(
      @NotNull Context ctx, @NotNull Class<?> sourceType, @NotNull Object value) throws Exception {
    //noinspection unchecked,rawtypes
    return factory.getNormalizer().normalize(ctx, (Class) sourceType, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @Nullable Object complexify(
      @NotNull Context ctx, @NotNull Object value, @NotNull Type targetType) throws Exception {
    return factory.getDenormalizer().denormalize(ctx, value, targetType);
  }
}
