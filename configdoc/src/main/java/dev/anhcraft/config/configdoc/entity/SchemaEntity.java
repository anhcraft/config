package dev.anhcraft.config.configdoc.entity;

import dev.anhcraft.config.blueprint.Schema;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SchemaEntity {
  private final String id;
  private final String name;
  private final Schema<?> schema;

  public SchemaEntity(@Nullable String id, @NotNull String name, @NotNull Schema<?> schema) {
    if (id != null && !id.matches("[A-Za-z0-9]+"))
      throw new IllegalArgumentException("Invalid identifier: " + id);
    this.id = id;
    this.name = name;
    this.schema = schema;
  }

  @Nullable public String getId() {
    return id;
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
