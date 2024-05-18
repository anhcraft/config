package dev.anhcraft.config.configdoc.entity;

import dev.anhcraft.config.blueprint.Schema;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class SchemaEntity {
  private final String name;
  private final Schema<?> schema;

  public SchemaEntity(@NotNull String name, @NotNull Schema<?> schema) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SchemaEntity)) return false;
    SchemaEntity entity = (SchemaEntity) o;
    return Objects.equals(schema, entity.schema);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(schema);
  }
}
