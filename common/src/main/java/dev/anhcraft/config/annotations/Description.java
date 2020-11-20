package dev.anhcraft.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used with {@link Setting} or {@link Configurable} to describe a setting or a configuration.<br>
 * The description should be clear to give users good understanding of the setting(s).<br>
 * However, it must not contain any examples. For more details, take a look at {@link Example}.<br>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String[] value();
}
