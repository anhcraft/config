package dev.anhcraft.config.configdoc;

import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.configdoc.entity.SchemaEntity;
import dev.anhcraft.config.configdoc.internal.ConfigDocGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigDocBuilder {
  private final List<SchemaEntity> schemaEntities = new ArrayList<>();
  private final Map<Pattern, String> javaDocs = new HashMap<>();
  private int unnamedSchemaCounter = 1;

  public ConfigDocBuilder() {
    addJavadoc("(org\\.bukkit.+)|(org\\.spigotmc.+)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(com\\.destroystokyo\\.paper.+)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(io\\.papermc\\.paper.+)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(java\\..+)", "https://docs.oracle.com/en/java/javase/22/docs/api/");
  }

  @Contract("_, _ -> this")
  public ConfigDocBuilder withSchema(@Nullable String name, @NotNull Schema<?> schema) {
    String entityName = schema.getName();
    if (entityName == null)
      entityName = name != null ? name : "UnnamedSchema" + (unnamedSchemaCounter++);
    return withSchema(new SchemaEntity(entityName, schema));
  }

  @Contract("_ -> this")
  public ConfigDocBuilder withSchema(@NotNull Schema<?> schema) {
    return withSchema(null, schema);
  }

  @Contract("_ -> this")
  public ConfigDocBuilder withSchema(@NotNull SchemaEntity schema) {
    schemaEntities.add(schema);
    return this;
  }

  @Contract("_, _ -> this")
  public ConfigDocBuilder addJavadoc(@NotNull String classPattern, @NotNull String link) {
    return addJavadoc(Pattern.compile(classPattern), link);
  }

  @Contract("_, _ -> this")
  public ConfigDocBuilder addJavadoc(@NotNull Pattern classPattern, @NotNull String link) {
    if (!link.endsWith("/")) link = link + '/';
    javaDocs.put(classPattern, link);
    return this;
  }

  @Contract("_ -> this")
  public ConfigDocBuilder generate(@NotNull File output) {
    new ConfigDocGenerator(schemaEntities, javaDocs, output).generate();
    return this;
  }
}
