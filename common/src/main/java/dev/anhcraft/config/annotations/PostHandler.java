package dev.anhcraft.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a handler that will be executed when an object was deserialized from a config.<br>
 * The following parameters can be present in the involving method:<br>
 * The first one should be {@link dev.anhcraft.config.ConfigDeserializer}.<br>
 * The second one should be {@link dev.anhcraft.config.schema.ConfigSchema}.<br>
 * The third one should be {@link dev.anhcraft.config.struct.ConfigSection}.<br>
 * All post handlers will be executed in order, and before {@link dev.anhcraft.config.ConfigDeserializer.Callback}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostHandler {
}
