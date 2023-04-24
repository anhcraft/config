package dev.anhcraft.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the field associated with this annotation as final/read-only, thus {@link dev.anhcraft.config.ConfigDeserializer} will not be able to change the value.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constant {
}
