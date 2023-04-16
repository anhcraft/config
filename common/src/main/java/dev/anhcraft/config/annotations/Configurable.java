package dev.anhcraft.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation puts at top of any class represent configurable data.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {
    /**
     * When {@link Path} is not specified for a setting, the field name will be used to make up the key.<br>
     * This option is responsible for changing the key's naming style automatically.<br>
     * <b>Field names must be in lower camel case - which is the standard naming convention in Java</b>
     * <ul>
     *     <li><b>Train Case</b>: e.g <code>this-is-a-path</code></li>
     *     <li><b>Snake Case</b>: e.g <code>this_is_a_path</code></li>
     *     <li><b>None</b>: no change to the name</li>
     * </ul>
     * @return naming style
     */
    NamingStyle keyNamingStyle() default NamingStyle.NONE;

    enum NamingStyle {
        TRAIN_CASE,
        SNAKE_CASE,
        NONE
    }
}
