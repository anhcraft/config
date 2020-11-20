package dev.anhcraft.config.annotations;

import java.lang.annotation.*;

/**
 * This special annotation is used only to give examples.<br>
 * This one must be used with either {@link Setting} or {@link Configurable}.<br>
 * <ul>
 *     <li>With {@link Setting}, it gives a specific example to the corresponding setting.</li>
 *     <li>With {@link Configurable}, it gives an overall example.</li>
 * </ul>
 * This annotation is repeatable. It is stacked into {@link Examples}.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Examples.class)
public @interface Example {
    String[] value();
}
