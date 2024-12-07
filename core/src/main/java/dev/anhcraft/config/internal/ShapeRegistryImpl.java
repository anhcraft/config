package dev.anhcraft.config.internal;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.ShapeRegistry;
import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.context.Context;
import dev.anhcraft.config.error.ShapeLinkingAmbiguityException;
import dev.anhcraft.config.error.ShapeResolutionException;
import dev.anhcraft.config.meta.Shape;
import dev.anhcraft.config.type.ComplexTypes;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ShapeRegistryImpl implements ShapeRegistry {
  private final Map<ClassProperty, ShapeCollection> discriminatorPropertyShapes = new HashMap<>();
  private final ConfigFactory factory;

  public ShapeRegistryImpl(@NotNull ConfigFactory factory) {
    this.factory = factory;
  }

  @Override
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

  @Override
  public @Nullable Class<?> solve(@NotNull Context ctx, @NotNull Object base) {
    Class<?> baseClass = base.getClass();
    ClassSchema classSchema = factory.getSchema(baseClass);

    // the following iteration is expected to be deterministic
    for (ClassProperty discriminator : classSchema.effectiveDiscriminators()) {
      ShapeCollection collection = discriminatorPropertyShapes.get(discriminator);
      if (collection == null) continue;

      Object val;
      try {
        val = discriminator.field().get(base);
      } catch (IllegalAccessException e) {
        throw new ShapeResolutionException(
            ctx, "Cannot solve shape for type " + baseClass.getName(), e);
      }
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
