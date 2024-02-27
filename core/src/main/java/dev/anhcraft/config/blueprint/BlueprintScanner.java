package dev.anhcraft.config.blueprint;

import dev.anhcraft.config.ConfigFactory;
import dev.anhcraft.config.error.UnsupportedSchemaException;
import dev.anhcraft.config.type.ComplexTypes;
import org.jetbrains.annotations.NotNull;

/**
 * A blueprint scanner.
 */
public interface BlueprintScanner {
  /**
   * Scans the schema of the specified class.<br>
   * The schema is tested against {@link ComplexTypes#isNormalClassOrAbstract(Class)}. If fails, this method
   * throws {@link UnsupportedSchemaException}.<br>
   * The result is not cached. It is recommended to use {@link ConfigFactory#getSchema(Class)} instead which has
   * an in-memory schema cache to prevent repeating scanning.
   * @param type the class
   * @return the schema
   * @see Schema
   */
  @NotNull Schema scanSchema(@NotNull Class<?> type);
}
