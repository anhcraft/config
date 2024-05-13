package dev.anhcraft.config.configdoc;

import dev.anhcraft.config.blueprint.ClassProperty;
import dev.anhcraft.config.blueprint.ClassSchema;
import dev.anhcraft.config.configdoc.internal.ResourceLoader;
import dev.anhcraft.config.configdoc.internal.TextReplacer;
import dev.anhcraft.config.validate.AggregatedValidator;
import dev.anhcraft.config.validate.check.Validation;
import dev.anhcraft.jvmkit.utils.FileUtil;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ConfigDocGenerator {
  private static final ResourceLoader resourceLoader = new ResourceLoader();
  private final Map<Class<?>, ClassSchema> schemas = new LinkedHashMap<>();
  private final Map<Pattern, String> javaDocs = new HashMap<>();
  private boolean showFooter = true;

  public ConfigDocGenerator() {
    addJavadoc("(org.bukkit*)|(org.spigotmc*)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(com.destroystokyo.paper*)", "https://jd.papermc.io/paper/1.20/");
    addJavadoc("(io.papermc.paper*)", "https://jd.papermc.io/paper/1.20/");
  }

  @Contract("_ -> this")
  public ConfigDocGenerator withSchema(@NotNull ClassSchema schema) {
    schemas.put(schema.type(), schema);
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
  public ConfigDocGenerator showFooter(boolean showFooter) {
    this.showFooter = showFooter;
    return this;
  }

  private TextReplacer handleText(String[] e) {
    return new TextReplacer(s -> String.join("\n", e));
  }

  private TextReplacer handleText(ClassProperty entry) {
    return new TextReplacer(
        s -> {
          switch (s) {
            case "key":
              return entry.name();
            case "description":
              {
                StringBuilder sb = new StringBuilder(String.join("<br>", entry.description()));
                if (entry.validator() instanceof AggregatedValidator) {
                  sb.append("<br><br><b>Constraints:</b>");
                  AggregatedValidator v = (AggregatedValidator) entry.validator();
                  sb.append("<ul>");
                  for (Validation validation : v.validations()) {
                    sb.append("<li>").append(validation.message()).append("</li>");
                  }
                  sb.append("</ul>");
                }
                return sb.toString();
              }
            case "type":
              Field field = entry.field();
              String fullType = field.getType().toGenericString();
              StringBuilder type = new StringBuilder(field.getType().getSimpleName());
              StringBuilder vb = new StringBuilder(" ");
              if (schemas.containsKey(field.getType())) {
                vb.append("<a href=\"")
                    .append(field.getType().getSimpleName())
                    .append(".schema.html\">")
                    .append(type)
                    .append("</a>");
              } else {
                boolean found = false;
                for (Map.Entry<Pattern, String> jd : javaDocs.entrySet()) {
                  if (jd.getKey().matcher(fullType).matches()) {
                    vb.append("<a href=\"")
                        .append(jd.getValue())
                        .append(fullType.replace('.', '/').replace('$', '.'))
                        .append(".html\">")
                        .append(type)
                        .append("</a>");
                    found = true;
                    break;
                  }
                }
                if (!found) vb.append(type);
              }
              vb.append(" <b>").append(entry.validator().message()).append("</b>");
              if (entry.isOptional()) {
                vb.append(" <b>optional</b>");
              }
              return vb.toString();
          }
          return "";
        });
  }

  private UnaryOperator<String> handleText(ClassSchema schema) {
    return s -> {
      if (s.equals("name")) {
        return schema.type().getSimpleName();
      } /*else if (s.equals("description")) {
            return String.join("<br>", schema.description().value());
        }*/ else if (s.equals("entry_count")) {
        return String.valueOf(schema.properties().size());
      } else if (s.startsWith("entries?")) {
        String file = s.substring("entries?".length()).trim();
        String content = resourceLoader.get(file);
        StringBuilder sb = new StringBuilder();
        for (ClassProperty entry : schema.properties()) {
          if (entry.isTransient() || entry.isConstant()) continue;
          sb.append(handleText(entry).replace(content));
        }
        return sb.toString();
      }
      return handleText().apply(s);
    };
  }

  private UnaryOperator<String> handleText() {
    return s -> {
      if (s.startsWith("schemas?")) {
        String file = s.substring("schemas?".length()).trim();
        String content = resourceLoader.get(file);
        StringBuilder sb = new StringBuilder();
        for (ClassSchema schema : schemas.values()) {
          sb.append(new TextReplacer(handleText(schema)).replace(content));
        }
        return sb.toString();
      } else if (s.equals("footer") && showFooter) {
        return resourceLoader.get("footer.html");
      }
      return "";
    };
  }

  @Contract("_ -> this")
  public ConfigDocGenerator generate(@NotNull File output) {
    output.mkdirs();

    String schemHtml = resourceLoader.get("schema.html");

    for (ClassSchema schem : schemas.values()) {
      FileUtil.write(
          new File(output, schem.type().getSimpleName() + ".schema.html"),
          new TextReplacer(handleText(schem)).replace(schemHtml));
    }

    FileUtil.write(
        new File(output, "index.html"),
        new TextReplacer(handleText()).replace(resourceLoader.get("index.html")));
    FileUtil.write(new File(output, "main.js"), resourceLoader.get("main.js"));
    FileUtil.write(new File(output, "main.css"), resourceLoader.get("main.css"));
    return this;
  }
}
