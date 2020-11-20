package dev.anhcraft.config.annotations;

import dev.anhcraft.config.exceptions.InvalidValueException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used with {@link Setting} to validate the value before modify the corresponding field.<br>
 * If not pass the validation, {@link InvalidValueException} will be thrown, unless silent mode is enabled.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {
    /**
     * The value must not be null.<br>
     * Not work with primitive entries.
     *
     * @return true/false
     */
    boolean notNull() default false;

    /**
     * The value must not be empty.<br>
     * For instances, list, array, string, section, etc
     *
     * @return true/false
     */
    boolean notEmpty() default false;

    /**
     * Enable the silent mode.
     *
     * @return true/false
     */
    boolean silent() default false;
}
