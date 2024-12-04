package dev.anhcraft.config;

import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.meta.Shape;
import dev.anhcraft.config.type.ComplexTypes;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The shape registry is used to manage shapes.
 * @see Shape
 */
public final class ShapeRegistry {
  private static class ShapeCollection {
    private final Map<String, Class<?>> discriminatorValueToSubtype = new HashMap<>();
  }

  private final Map<ClassProperty, ShapeCollection> discriminatorPropertyShapes = new HashMap<>();
  private final ConfigFactory factory;

  ShapeRegistry(ConfigFactory factory) {
    this.factory = factory;
  }

  /**
   * Registers a class to the shape registry.
   * @param clazz the class
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
      Map<String, ClassProperty> discriminatorProperties = schema.getDiscriminators();

      for (Map.Entry<String, ClassProperty> entry : discriminatorProperties.entrySet()) {
        String discriminatorName = entry.getKey();
        if (!shapeLookup.containsKey(discriminatorName)) continue;

        String discriminatorVal = shapeLookup.get(entry.getKey());
        ClassProperty discriminatorProperty = entry.getValue();

        // When we link shape S to property P for node N, P might not belong to N but to one of its
        // ancestors
        // as such, if we continue, property P might appear again; doing so, does not create
        // duplication
        // Once we go past the node having P, we must continue since it is possible to have another
        // property Q
        // with the same discriminator name as P

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
                  throw new IllegalArgumentException(
                      "Ambiguous shape linking: "
                          + v.getName()
                          + " [existing] and "
                          + clazz.getName()
                          + " [new] ---> "
                          + discriminatorName
                          + " [discriminator] from "
                          + discriminatorProperty.field().getDeclaringClass().getName());
                });
      }

      node = node.getSuperclass();
    }
  }

  /**
   * Solves the shape of the given base class.
   * @param base the base
   * @return the shape
   */
  public @Nullable Class<?> solve(@NotNull Context ctx, @NotNull Object base) throws Exception {
    ClassSchema classSchema = factory.getSchema(base.getClass());
    for (Map.Entry<String, ClassProperty> entry : classSchema.getDiscriminators().entrySet()) {
      ShapeCollection collection = discriminatorPropertyShapes.get(entry.getValue());
      if (collection == null) continue;
      Object val = entry.getValue().field().get(base);
      String valStr = (String) factory.getDenormalizer().denormalize(ctx, val, String.class);
      Class<?> clazz = collection.discriminatorValueToSubtype.get(valStr);
      // TODO current shape selection is indeterministic
      if (clazz != null) return clazz;
    }
    return null;
  }
}
