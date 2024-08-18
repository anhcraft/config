package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.type.ComplexTypes;
import org.jetbrains.annotations.NotNull;

/**
 * A class schema scanner.<br>
 * The implementation is required to maintain a cache of schema to facilitate {@link #getOrScanSchema(Class)}.
 */
public interface ClassSchemaScanner {
  /**
   * Scans the schema of the specified class.<br>
   * The schema is tested against {@link ComplexTypes#isNormalClassOrAbstract(Class)}. If fails, this method
   * throws {@link UnsupportedSchemaException}.<br>
   * <b>The result is never cached.</b>
   * @param type the class
   * @return the schema
   */
  @NotNull ClassSchema scanSchema(@NotNull Class<?> type);

  /**
   * Gets the schema of the specified class or scans it if not exists in the cache.
   * @param type the class
   * @return the schema
   * @see #scanSchema(Class)
   * @see ClassSchema
   */
  @NotNull ClassSchema getOrScanSchema(@NotNull Class<?> type);
}
