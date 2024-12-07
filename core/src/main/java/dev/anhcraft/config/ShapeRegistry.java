package dev.anhcraft.config;

import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.ShapeLinkingAmbiguityException;
import dev.anhcraft.config.meta.Shape;
import dev.anhcraft.config.meta.Shapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The shape registry is used to manage shapes including registration/linking and resolution.
 * @see Shape
 */
public interface ShapeRegistry {

  /**
   * Registers a shape to the shape registry.<br>
   * In this process, the registry attempts to link the given shape with one or many effective
   * discriminators existing in the ancestor(s) of the shape class.<br>
   * If there exists an existing shape linked to a discriminator for a specific discriminator value:
   * <ul>
   *   <li>Prefer more detailed shape: If the new shape is a subtype of the existing shape because the new shape
   *   is more detailed.</li>
   *   <li>Ambiguity: If S1, S2 are eligible shapes and S1, S2 are siblings in the type hierarchy, there is no exact
   *   decision on which one to keep; as such, {@link ShapeLinkingAmbiguityException} is thrown.</li>
   * </ul>
   * @param clazz the shape class annotated with {@link Shape} or {@link Shapes}
   */
  void register(@NotNull Class<?> clazz);

  /**
   * Solves the shape for the given base.<br>
   * In this process, the registry looks at all effective discriminator properties in the base instance and tries to
   * look up a compatible shape with matched value.<br>
   * The first result is always chosen. The process is expected to be deterministic during the runtime unless there is
   * new shape linking.
   * @param base the base
   * @return the shape class
   */
  @Nullable Class<?> solve(@NotNull Context ctx, @NotNull Object base);
}
