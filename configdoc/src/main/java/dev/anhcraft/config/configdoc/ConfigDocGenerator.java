package dev.anhcraft.config.configdoc;

import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.blueprint.DictionarySchema;
import dev.anhcraft.config.blueprint.Schema;
import dev.anhcraft.config.configdoc.entity.SchemaEntity;
import dev.anhcraft.config.type.ComplexTypes;
import dev.anhcraft.config.type.TypeResolver;
import dev.anhcraft.jvmkit.utils.FileUtil;
import java.io.File;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ConfigDocGenerator {
  private static final ResourceLoader resourceLoader = new ResourceLoader();
  private final List<SchemaEntity> schemaEntities = new ArrayList<>();
  private final Map<Pattern, String> javaDocs = new HashMap<>();
  private int unnamedSchemaCounter = 1;

  public ConfigDocGenerator() {
    addJavadoc("(org\\.bukkit.+)|(org\\.spigotmc.+)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(com\\.destroystokyo\\.paper.+)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(io\\.papermc\\.paper.+)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(java\\..+)", "https://docs.oracle.com/en/java/javase/22/docs/api/");
  }

  @Contract("_ -> this")
  public ConfigDocGenerator withSchema(@NotNull ClassSchema schema) {
    return withSchema(new SchemaEntity(schema.type().getSimpleName(), schema));
  }

  @Contract("_ -> this")
  public ConfigDocGenerator withSchema(@NotNull DictionarySchema schema) {
    return withSchema(new SchemaEntity("UnnamedSchema" + (unnamedSchemaCounter++), schema));
  }

  @Contract("_, _ -> this")
  public ConfigDocGenerator withSchema(@NotNull String name, @NotNull Schema<?> schema) {
    return withSchema(new SchemaEntity(name, schema));
  }

  @Contract("_ -> this")
  public ConfigDocGenerator withSchema(@NotNull SchemaEntity schema) {
    schemaEntities.add(schema);
    return this;
  }

  @Contract("_, _ -> this")
  public ConfigDocGenerator addJavadoc(@NotNull String classPattern, @NotNull String link) {
    return addJavadoc(Pattern.compile(classPattern), link);
  }

  @Contract("_, _ -> this")
  public ConfigDocGenerator addJavadoc(@NotNull Pattern classPattern, @NotNull String link) {
    if (!link.endsWith("/")) link = link + '/';
    javaDocs.put(classPattern, link);
    return this;
  }

  @Contract("_ -> this")
  public ConfigDocGenerator generate(@NotNull File output) {
    output.mkdirs();

    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode("HTML5");
    templateResolver.setCacheable(true);

    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    Map<String, Object> globalVars = new HashMap<>();
    globalVars.put("schemaEntities", schemaEntities);
    globalVars.put("generator", this);

    for (SchemaEntity entity : schemaEntities) {
      Context context = new Context(Locale.US, globalVars);
      context.setVariable("title", entity.getName());
      context.setVariable("schema", entity.getSchema());
      String text = templateEngine.process("template", context);
      FileUtil.write(new File(output, entity.getPageFileName()), text);
    }

    FileUtil.write(new File(output, "main.css"), resourceLoader.get("main.css"));
    FileUtil.write(new File(output, "tooltip.css"), resourceLoader.get("tooltip.css"));
    FileUtil.write(new File(output, "main.js"), resourceLoader.get("main.js"));
    FileUtil.write(new File(output, "search.js"), generateSearchModule());
    return this;
  }

  private String generateSearchModule() {
    Map<String, List<Integer>> keywordMap = new HashMap<>();
    for (int i = 0; i < schemaEntities.size(); i++) {
      SchemaEntity entity = schemaEntities.get(i);
      keywordMap.computeIfAbsent(entity.getName().toLowerCase(), key -> new ArrayList<>()).add(i);
      for (String propertyName : entity.getSchema().propertyNames()) {
        keywordMap.computeIfAbsent(propertyName.toLowerCase(), key -> new ArrayList<>()).add(i);
      }
    }
    StringJoiner keywordJoiner = new StringJoiner(",");
    for (Map.Entry<String, List<Integer>> entry : keywordMap.entrySet()) {
      keywordJoiner.add(
          String.format(
              "\"%s\":[%s]",
              entry.getKey(),
              entry.getValue().stream().map(Object::toString).collect(Collectors.joining(","))));
    }
    String schemaIndex =
        schemaEntities.stream()
            .map(
                schem ->
                    "{\"name\":\""
                        + schem.getName()
                        + "\",\"path\":\""
                        + schem.getPageFileName()
                        + "\"}")
            .collect(Collectors.joining(","));
    return resourceLoader
        .get("search.js")
        .replace("/*__SCHEMA_INDEX__*/", schemaIndex)
        .replace("/*__KEYWORD_INDEX__*/", keywordJoiner.toString());
  }

  // Copy from ComplexTypes#describe
  public String generateInteractiveType(Type type, boolean simple) {
    if (type instanceof GenericArrayType) {
      GenericArrayType arrayType = (GenericArrayType) type;
      return String.format(
          "%s[]", generateInteractiveType(arrayType.getGenericComponentType(), simple));
    } else if (type instanceof ParameterizedType) {
      ParameterizedType paramType = (ParameterizedType) type;
      String args =
          Arrays.stream(paramType.getActualTypeArguments())
              .map(a -> generateInteractiveType(a, simple))
              .collect(Collectors.joining(","));
      if (paramType.getOwnerType() != null)
        return String.format(
            "%s.%s&lt;%s&gt;",
            generateInteractiveType(paramType.getOwnerType(), simple),
            generateInteractiveType(paramType.getRawType(), simple),
            args);
      else
        return String.format(
            "%s&lt;%s&gt;", generateInteractiveType(paramType.getRawType(), simple), args);
    } else if (type instanceof TypeVariable) {
      return ((TypeVariable<?>) type).getName();
    } else if (type instanceof TypeResolver) {
      return generateInteractiveType(((TypeResolver) type).provideType(), simple);
    } else if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;
      if (clazz.isArray()) return generateInteractiveType(clazz.getComponentType(), simple) + "[]";

      String link = getJavadocLinkForClass(clazz);
      String full = clazz.getName();
      String name = simple ? clazz.getSimpleName() : full;

      if (link == null) return String.format("<span tooltip=\"%s\">%s</span>", full, name);
      return String.format("<a href=\"%s\" target=\"_blank\" tooltip=\"%s\">%s</a>", link, full, name);
    }

    return type.getTypeName();
  }

  private String getJavadocLinkForClass(Class<?> clazz) {
    String path = clazz.getName();
    String module = clazz.getModule().getName();
    for (Map.Entry<Pattern, String> jd : javaDocs.entrySet()) {
      if (jd.getKey().matcher(path).matches()) {
        String link = jd.getValue();
        link += module == null ? "" : module + "/";
        link += path.replace('.', '/').replace('$', '.');
        link += ".html";
        return link;
      }
    }
    return null;
  }
}
