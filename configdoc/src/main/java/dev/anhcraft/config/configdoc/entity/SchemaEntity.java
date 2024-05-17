package dev.anhcraft.config.configdoc.entity;

import dev.anhcraft.config.blueprint.Schema;
import org.jetbrains.annotations.NotNull;

public class SchemaEntity {
  private final String name;
  private final Schema<?> schema;

  public SchemaEntity(@NotNull String name, @NotNull Schema<?> schema) {
    // TODO name must be compliant with URL path name, without whitespace
    this.name = name;
    this.schema = schema;
  }

  @NotNull public String getName() {
    return name;
  }

  @NotNull public Schema<?> getSchema() {
    return schema;
  }

  @NotNull public String getPageFileName() {
    return "schema." + name + ".html";
  }
}
