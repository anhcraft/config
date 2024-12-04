package dev.anhcraft.config.meta;

import java.lang.annotation.*;
import org.jetbrains.annotations.NotNull;

/**
 * A shape is a subtype of a type shaped by a discriminator.
 * @see Discriminator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Shapes.class)
public @interface Shape {
  /**
   * Returns the name of the discriminator.
   * @return the discriminator name
   */
  @NotNull String discriminator();

  /**
   * Returns the value of the discriminator.
   * @return the discriminator value
   */
  @NotNull String value();
}
