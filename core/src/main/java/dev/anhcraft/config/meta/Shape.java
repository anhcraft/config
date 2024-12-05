package dev.anhcraft.config.meta;

import dev.anhcraft.config.ShapeRegistry;
import java.lang.annotation.*;
import org.jetbrains.annotations.NotNull;

/**
 * A shape represents a variant of one or multiple types and is used to support schema variance.<br>
 * When type <b>T</b> is annotated as <code>@Shape(discriminator = D, value = v)</code>:
 * <ul>
 *  <li><b>T</b> becomes the shape of a supertype <b>U</b> if <b>U</b> has an <i>effective</i> discriminator
 *  named <b>D</b></li>
 *  <li>An instance of <b>U</b> has a shape of <b>T</b> if the discriminator field <b>D</b> matches the
 *  value <b>v</b></li>
 * </ul>
 * <b>General rules:</b>
 * <ul>
 *  <li>A shape might be linked to multiple discriminator properties (located in different supertypes)</li>
 *  <li>A type might have multiple {@link Shape} annotated (with different discriminator names)</li>
 *  <li>A type cannot have two {@link Shape} annotated with the same name; or two {@link Shape} with different names
 *  but pointing to the same discriminator property. In either case, the later {@link Shape} will take precedence.
 *  <b>NO</b> exception is thrown.</li>
 *  <li>Two shapes of the same type can be distinct by (a) Two different values of the same discriminator; or (b) Two
 *  different discriminator(s)</li>
 *  <li>Shape is <b>NOT</b> inheritable: If <b>S</b> is a shape of <b>T</b>, any subtypes of <b>S</b> cannot become
 *  shapes of <b>T</b> unless there is explicit annotation and registration</li>
 * </ul>
 * <b>Shape registration:</b> Any class using this annotation must be <b>explicitly</b> registered with {@link ShapeRegistry}.
 * @see Discriminator
 * @see ShapeRegistry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Shapes.class)
public @interface Shape {
  /**
   * Returns the name of the discriminator (either primary name or alias)<br>
   * The target discriminator can exist in any supertypes.
   * @return the discriminator name
   */
  @NotNull String discriminator();

  /**
   * Returns the value of the discriminator.<br>
   * For Character-based and String-based discriminator, the value-matching is case-sensitive.
   * @return the discriminator value
   */
  @NotNull String value();
}
