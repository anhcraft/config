package dev.anhcraft.config.meta;

import dev.anhcraft.config.Dictionary;
import dev.anhcraft.config.adapter.TypeAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a denormalization processor.<br>
 * A processor is an annotated instance method that takes the responsibility of type adapter to transform values
 * in the context of a specific property. A property can be bound to a single denormalization processor. The processor
 * is not inheritable from superclasses or interfaces.<br>
 * The denormalization processor has two strategies:
 * <ul>
 *   <li>{@link Strategy#REPLACE} (default)
 *    <ul>
 *      <li>Replaces the automatic type-adapting</li>
 *      <li>If the return type is {@code void}, the processor is responsible to take the simple value, complexify it
 *      and set the value into the corresponding field. No validation or post-filtering is performed.</li>
 *      <li>If the return type is non-void, the processor is responsible to take the simple value, transform it
 *      and return. Validation, post-filtering and applying value will be done implicitly.</li>
 *    </ul>
 *   </li>
 *   <li>{@link Strategy#AFTER}
 *    <ul>
 *      <li>Executes after the automatic type-adapting</li>
 *      <li>If the return type is {@code void}, the processor can take the complex value, validate it, and may be able
 *      to change it if that is a mutable object. Everything else is done implicitly.</li>
 *      <li>If the return type is non-void, the processor is responsible to take the complex value, transform it
 *      and return. Validation, post-filtering and applying value will be done implicitly.</li>
 *    </ul>
 *   </li>
 * </ul>
 * The method using this annotation must be bound to instance. It is recommended to set it private for encapsulation.
 * The method must take at least one single parameter as the input. The type of the parameter is determined in the
 * source code. For example, if it replaces the automatic type adapter, the input is a simple value from the
 * {@link Dictionary}, otherwise, it is a complex value after type-adapting. The second parameter is the context,
 * which is optional. The return type can be either void or non-void. Using {@code void}, the processor is responsible
 * to set the value to the corresponding field. Using non-void, the denormalizer continues the execution including
 * validation, post-filtering and applying value.
 * @see Strategy
 * @see TypeAdapter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Denormalizer {
  /**
   * Returns a list of <b>field name</b> that this processor bounds to.
   * @return the field names
   */
  String[] value() default {};

  /**
   * Returns the strategy of this processor.
   * @return the strategy
   */
  Strategy strategy() default Strategy.REPLACE;

  /**
   * Denormalizer strategy.
   * @see Denormalizer
   */
  enum Strategy {
    REPLACE,
    AFTER
  }
}
