package dev.anhcraft.config.meta;

import dev.anhcraft.config.error.InvalidValueException;
import dev.anhcraft.config.validate.check.Validation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates the property's value during denormalization.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validate {
  /**
   * A list of validations, separated by comma(s).<br>
   * For example: {@code not-null, size=3|}<br>
   * Checks out the wiki or the javadoc of each {@link Validation} for further information.
   * @return the list of validations
   * @see Validation
   */
  String value();

  /**
   * By default, raise {@link InvalidValueException} when a validation fails. Set this to true to suppress the
   * exception and simply ignore the denormalization for the property.
   * @return true if the exception should be suppressed
   */
  boolean silent() default false;
}
