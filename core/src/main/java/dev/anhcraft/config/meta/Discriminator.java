package dev.anhcraft.config.meta;

import dev.anhcraft.config.ShapeRegistry;
import java.lang.annotation.*;

/**
 * @see Shape
 * @see ShapeRegistry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Discriminator {}
