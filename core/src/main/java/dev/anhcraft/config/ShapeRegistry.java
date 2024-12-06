package dev.anhcraft.config;

import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.ShapeLinkingAmbiguityException;
import dev.anhcraft.config.meta.Shape;
import dev.anhcraft.config.meta.Shapes;
import dev.anhcraft.config.type.ComplexTypes;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The shape registry is used to manage shapes including registration/linking and resolution.
 * @see Shape
 */
public final class ShapeRegistry {
  private final Map<ClassProperty, ShapeCollection> discriminatorPropertyShapes = new HashMap<>();
  private final ConfigFactory factory;

  public ShapeRegistry(@NotNull ConfigFactory factory) {
    this.factory = factory;
  }

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
  public void register(@NotNull Class<?> clazz) {
    Shape[] shapes = clazz.getAnnotationsByType(Shape.class);
    Map<String, String> shapeLookup = new HashMap<>();
    for (Shape shape : shapes) {
      shapeLookup.put(shape.discriminator(), shape.value());
    }

    /*
      SHAPE - DISCRIMINATOR LINKING ALGORITHM:
      - Type T is permitted to have discriminator X whose corresponding field either belongs to T or the lowest ancestor
      of T having X (In other words, going down from the ancestor to T, there exists only one discriminator X)
      - Type T is permitted to have multiple discriminators
      - Shape S belongs to type T if T is the lowest ancestor of S having discriminator X (In other words, going down from
      T to S, there exists only one discriminator X)
      - Type T is permitted to have multiple shapes
      - Shape S can belong to type T1 and T2 (either they are siblings or child-parent) if T1 and T2 share the same
      discriminator name (X) but different corresponding properties (X1, X2)
    */

    Class<?> node = clazz.getSuperclass();
    while (node != Object.class
        && ComplexTypes.isNormalClassOrAbstract(node)
        && !shapeLookup.isEmpty()) {
      ClassSchema schema = factory.getSchema(node);

      for (String fieldName : shapeLookup.keySet()) {
        if (!schema.effectiveDiscriminatorNames().contains(fieldName)) continue;

        ClassProperty discriminatorProperty = schema.declaredPropertyByField(fieldName);
        if (discriminatorProperty == null) continue;

        String discriminatorVal = shapeLookup.get(fieldName);
        if (discriminatorVal == null) continue;

        // When we link shape S to property P for node N, P might not belong to N but to one of its
        // ancestors. If we continue, property P might appear again; doing so, does not cause
        // duplication
        // Once we go past the node having P, we must continue since it is possible to have another
        // property Q with the same discriminator name as P

        discriminatorPropertyShapes
            .computeIfAbsent(discriminatorProperty, k -> new ShapeCollection())
            .discriminatorValueToSubtype
            .compute(
                discriminatorVal,
                (k, v) -> {
                  // If there is no shape linked yet, or the existing shape is a supertype of the
                  // new shape
                  if (v == null || v.isAssignableFrom(clazz)) return clazz;
                  if (clazz.isAssignableFrom(v)) return v;
                  throw new ShapeLinkingAmbiguityException(
                      "Ambiguous shape linking: "
                          + v.getName()
                          + " [existing] and "
                          + clazz.getName()
                          + " [new] ---> "
                          + fieldName
                          + " [discriminator] from "
                          + discriminatorProperty.field().getDeclaringClass().getName());
                });
      }

      node = node.getSuperclass();
    }
  }

  /**
   * Solves the shape for the given base.<br>
   * In this process, the registry looks at all effective discriminator properties in the base instance and tries to
   * look up a compatible shape with matched value.<br>
   * The first result is always chosen. The process is expected to be deterministic during the runtime unless there is
   * new shape linking.
   * @param base the base
   * @return the shape class
   */
  public @Nullable Class<?> solve(@NotNull Context ctx, @NotNull Object base) throws Exception {
    Class<?> baseClass = base.getClass();
    ClassSchema classSchema = factory.getSchema(baseClass);

    // the following iteration is expected to be deterministic
    for (ClassProperty discriminator : classSchema.effectiveDiscriminators()) {
      ShapeCollection collection = discriminatorPropertyShapes.get(discriminator);
      if (collection == null) continue;

      Object val = discriminator.field().get(base);
      String valStr = (String) factory.getDenormalizer().denormalize(ctx, val, String.class);
      Class<?> clazz = collection.discriminatorValueToSubtype.get(valStr);

      // An edge case is that: P is a subtype of GP; P is the shape of GP for discriminator D
      // declared in GP.
      // As such, D is inherited into P, and solving base=P for D will return P --> invalid
      if (clazz != null && baseClass != clazz && baseClass.isAssignableFrom(clazz)) {
        return clazz; // return the first shape
      }
    }

    return null;
  }

  private static class ShapeCollection {
    private final Map<String, Class<?>> discriminatorValueToSubtype = new HashMap<>();
  }
}
