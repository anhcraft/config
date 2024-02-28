package dev.anhcraft.config.meta;

import dev.anhcraft.config.adapter.TypeAdapter;
import dev.anhcraft.config.context.Context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a normalization processor.<br>
 * A processor is an annotated instance method that takes the responsibility of type adapter to transform values
 * in the context of a specific property. A property can be bound to a single normalization processor. The processor
 * is not inheritable from superclasses or interfaces.<br>
 * The normalization processor has two strategies:
 * <ul>
 *   <li>{@link Normalizer.Strategy#REPLACE} (default)
 *    <ul>
 *      <li>Replaces the automatic type-adapting</li>
 *      <li>The processor is responsible to take the value from the corresponding field and return the simplified value.
 *      The normalizer is only responsible to write it into the corresponding setting</li>
 *    </ul>
 *   </li>
 *   <li>{@link Normalizer.Strategy#BEFORE}
 *    <ul>
 *      <li>Executes before the automatic type-adapting</li>
 *      <li>The processor can return a different complex value than the one existing in the field. The normalizer will
 *      take that value, perform automatic type-adapting and write into the corresponding setting.
 *      </li>
 *    </ul>
 *   </li>
 * </ul>
 * The method using this annotation must be bound to instance. It is recommended to set it private for encapsulation.
 * The method may have none or one parameter. If a parameter exists, the type of the parameter must be {@link Context}.
 * The return type must be non-void, otherwise, the method is discarded.
 * @see Normalizer.Strategy
 * @see TypeAdapter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Normalizer {
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
   * Normalizer strategy.
   * @see Normalizer
   */
  enum Strategy {
    REPLACE,
    BEFORE
  }
}
