package dev.anhcraft.config.meta;

import dev.anhcraft.config.ShapeRegistry;
import dev.anhcraft.config.error.SchemaCreationException;
import java.lang.annotation.*;

/**
 * Marks a field as a discriminator.<br>
 * A discriminator property declared in type <b>T</b> or inherited into type <b>T</b> is used to distinguish
 * {@link Shape} of <b>T</b>. When an instance of <b>T</b> is deserialized, the denormalizer looks at the value of
 * discriminator properties to determine the compatible shape.<br>
 * A property annotated as {@link Discriminator} must meet following conditions, otherwise, the schema creation process
 * results in {@link SchemaCreationException}:
 * <ul>
 *   <li>Has a deterministic, equitable, scalar type: {@code byte}, {@code short}, {@code int}, {@code long},
 *   {@code char}, {@code boolean}, or their wrapper types; and {@code String}</li>
 *   <li>Must not be a {@link Fallback} property</li>
 * </ul>
 * <b>General rules:</b>
 * <ul>
 *   <li>A type is allowed to have multiple discriminators (either declared or inherited)</li>
 *   <li>Only <i>effective</i> discriminator(s) are considered in the process</li>
 * </ul>
 * <b>Multi-discriminators:</b>
 * <ul>
 *   <li>Disjunction support ({@code OR} operator): For example, {@code S1} is a shape of {@code T} due to discriminator
 *   {@code d1}, and {@code S2} is also a shape of {@code T} due to discriminator {@code d2}.</li>
 *   <li>There is <b>NO</b> conjunction support ({@code AND} operator)</li>
 * </ul>
 * @see Shape
 * @see ShapeRegistry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Discriminator {}
