package dev.anhcraft.config.configdoc.internal;

import dev.anhcraft.config.configdoc.entity.SchemaEntity;
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
import org.jetbrains.annotations.ApiStatus;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ConfigDocGenerator {
  private static final ResourceLoader resourceLoader = new ResourceLoader();
  private final List<SchemaEntity> schemaEntities;
  private final Map<Pattern, String> javaDocs;
  private final File output;

  public ConfigDocGenerator(
      List<SchemaEntity> schemaEntities, Map<Pattern, String> javaDocs, File output) {
    this.schemaEntities = schemaEntities;
    this.javaDocs = javaDocs;
    this.output = output;
  }

  public ConfigDocGenerator generate() {
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
      context.setVariable("fileName", entity.getPageFileName());
      context.setVariable("schema", entity.getSchema());
      String text = templateEngine.process("schema", context);
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
      Set<String> keywords = new HashSet<>(entity.getSchema().propertyNames());
      keywords.add(entity.getName());
      keywords.addAll(tokenize(entity.getName()));
      Set<String> tokens = new HashSet<>();
      for (String keyword : keywords) {
        tokens.add(keyword);
        tokens.addAll(tokenize(keyword));
      }
      for (String token : tokens) {
        keywordMap.computeIfAbsent(token.toLowerCase(), key -> new ArrayList<>()).add(i);
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

  private List<String> tokenize(String str) {
    List<String> tokens = new ArrayList<>();
    StringBuilder buffer = new StringBuilder();
    int consecutiveUppercase = 0;
    char[] charArray = str.toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      char ch = charArray[i];
      if (Character.isLetterOrDigit(ch)) {
        if (Character.isUpperCase(ch)) {
          consecutiveUppercase++;
        }
        buffer.append(ch);
        boolean hasNextWord =
            i < charArray.length - 1
                && Character.isLowerCase(charArray[i])
                && Character.isUpperCase(charArray[i + 1]);
        boolean endOfAbbr =
            i < charArray.length - 1
                && Character.isUpperCase(charArray[i])
                && Character.isLowerCase(charArray[i + 1])
                && consecutiveUppercase > 1;
        if (!(hasNextWord || endOfAbbr)) continue;
      }
      if (buffer.length() > 0) {
        tokens.add(buffer.toString().toLowerCase());
        buffer.setLength(0);
        consecutiveUppercase = 0;
      }
    }
    if (buffer.length() > 0) tokens.add(buffer.toString().toLowerCase());
    return tokens;
  }

  // Copy from ComplexTypes#describe
  // TO BE CALLED BY THYMELEAF
  // TODO Isolate this method in a separate class only available to the Thymeleaf template
  @ApiStatus.Internal
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
      return String.format(
          "<a href=\"%s\" target=\"_blank\" tooltip=\"%s\">%s</a>", link, full, name);
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