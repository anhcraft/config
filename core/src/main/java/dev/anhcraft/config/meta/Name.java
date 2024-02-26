package dev.anhcraft.config.meta;

import dev.anhcraft.config.NamingPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides a list of names for the associated property.<br>
 * {@link Name} and {@link Alias} can be used together (neither of them is required) to provide the names. They must
 * be unique and non-blank. If a name is invalid, it is ignored implicitly.<br>
 * <ul>
 *     <li>If both {@link Name} and {@link Alias} exist, the first one {@link Name} provided is the primary, following
 *     with the aliases, and finally following aliases from {@link Alias}</li>
 *     <li>If only {@link Name} exists, the first name is the primary, following aliases</li>
 *     <li>If only {@link Alias} exists, the primary name is auto-generated, {@link Alias} provides aliases</li>
 * </ul>
 * When the primary name is auto-generated, it is taken from the associated field's name. The name may be transformed
 * using {@link NamingPolicy} such as to transform from camelCase to snake_case.
 * @see Alias
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Name {
    String[] value();
}
