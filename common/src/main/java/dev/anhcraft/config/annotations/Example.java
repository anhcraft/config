package dev.anhcraft.config.annotations;

import java.lang.annotation.*;

/**
 * This special annotation is used only to give examples.<br>
 * This one must be used with either a configurable field or a {@link Configurable} class.<br>
 * <ul>
 *     <li>With a configurable field, it gives a specific example to the corresponding setting.</li>
 *     <li>With a {@link Configurable} class, it gives an overall example.</li>
 * </ul>
 * This annotation is repeatable. It is stacked into {@link Examples}.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Examples.class)
public @interface Example {
    String[] value();
}
