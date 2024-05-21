package dev.anhcraft.config.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;

/**
 * During denormalization, the property with {@link Fallback} catches all remaining unmapped settings including
 * its corresponding setting in the configuration.<br>
 * The property type must be {@link LinkedHashMap} or any of its supertypes.<br>
 * A schema can have at most one {@link Fallback} property.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Fallback {
  /**
   * Controls the behaviour when a property has aliases.
   * @return what to be used for distinction
   */
  Distinct distinctBy() default Distinct.NAME;

  enum Distinct {
    /**
     * If one of many names of a property is found, remaining names will be caught by the {@link Fallback} property.
     */
    NAME,

    /**
     * If one of many names of a property is found, remaining names will be ignored by the {@link Fallback} property.
     */
    PROPERTY
  }
}
