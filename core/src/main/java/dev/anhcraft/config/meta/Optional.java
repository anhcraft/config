package dev.anhcraft.config.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When {@link Optional} associates with a property, the denormalizer keeps the default field's value if the value
 * denormalized is {@code null}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Optional {}
